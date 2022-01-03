package me.ayunami2000.ayunAudioStreamer;

import com.sun.net.httpserver.HttpServer;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Main {
    public static int port=8698;
    public static String audioDeviceOrFile="";
    public static Mixer mixer=null;
    public static PitchDetection pitchDetection = new PitchDetection();
    public static String currData="";

    public static void main(String[] args) {
        if(args.length==0||args.length==1){
            System.out.println("Usage: port audiodevice|midifilepath|nbsfilepath");
            System.out.println("Audio Devices:");
            int ind=0;
            for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
                System.out.println(ind+"~ "+mixerInfo.getName());
                ind++;
            }
            if(ind==0)System.out.println("(there were no audio devices...)");
            return;
        }
        try{
            port=Integer.parseInt(args[0]);
            audioDeviceOrFile=String.join(" ",Arrays.copyOfRange(args,1,args.length));
            Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
            mixer = null;
            for (int i = 0; i < mixerInfos.length; i++) {
                if (mixerInfos[i].getName().trim().equals(audioDeviceOrFile)) {
                    mixer = AudioSystem.getMixer(mixerInfos[i]);
                    break;
                }
            }
            if(mixer==null){
                for (int i = 0; i < mixerInfos.length; i++) {
                    try {
                        int index = Integer.parseInt(audioDeviceOrFile);
                        if (index==i) {
                            mixer = AudioSystem.getMixer(mixerInfos[i]);
                            break;
                        }
                    }catch(NumberFormatException e){
                        break;
                    }
                }
            }
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/ayunaud",t -> {
                t.sendResponseHeaders(200,0);
                OutputStream os = t.getResponseBody();
                while(true){
                    os.write(("\n"+(currData.startsWith(";")?currData.substring(1):currData)).getBytes(StandardCharsets.US_ASCII));
                    os.flush();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {}
                }
            });
            server.setExecutor(null); // creates a default executor
            server.start();
            if(mixer==null){
                System.out.println("Error: Audio device not found! Using file...");
                String songText=ConvertNBS.doConvert(audioDeviceOrFile);
                if(songText==null||songText.equals("")){
                    //errmsg
                }else{
                    try {
                        System.out.println("Starting stream on port "+port);
                        Thread fard=(new Thread(() -> {
                            while(Thread.currentThread().isAlive()){
                                currData="";
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {}
                            }
                        }));
                        fard.start();
                        PlaySong.playSong(songText.split("\n"));//hopefully this keeps it from exiting
                        fard.stop();
                        System.exit(0);
                    } catch (InterruptedException e) {}
                }
            }else{
                pitchDetection.beginPitchDetection();
                System.out.println("Starting stream on port "+port);
                while(true){
                    currData="";
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {}
                }
            }
        } catch(NumberFormatException | IOException e){
            //errmsg
            e.printStackTrace();
            return;
        }
    }
}
