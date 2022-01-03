package me.ayunami2000.ayunAudioStreamer;

import me.ayunami2000.ayunAudioStreamer.nbsapi.Instrument;
import me.ayunami2000.ayunAudioStreamer.nbsapi.Layer;
import me.ayunami2000.ayunAudioStreamer.nbsapi.Note;
import me.ayunami2000.ayunAudioStreamer.nbsapi.Song;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ConvertNBS {
    public static List instruList= Arrays.asList("HARP","DRUM","SNARE","CLICK","BASS","FLUTE","BELL","GUITAR","CHIME","XYLOPHONE");
    public static String doConvert(String fileName){
        File nbsFile=new File(fileName);
        if(nbsFile.exists()&&!nbsFile.isDirectory()) {
            String flName=nbsFile.getName().toLowerCase(Locale.ROOT);
            String resStr=flName.endsWith(".nbs")?doLiveConvert(nbsFile):(flName.endsWith(".txt")?readFile(nbsFile.getAbsolutePath(),StandardCharsets.US_ASCII):MidiConverter.midiToTxt(nbsFile));
            if (resStr==null||resStr.equals("")) {
                System.out.println("There was an error while converting your file.");
            } else {
                System.out.println("File converted successfully!");
                return resStr;
            }
        }else{
            System.out.println("There was an error while finding your file.");
        }
        return null;
    }
    public static String readFile(String path, Charset encoding) {
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }catch(IOException e){
            return null;
        }
    }
    public static String doLiveConvert(File nbsFile){
        try{
            String resSongFile="";
            Map<Integer,ArrayList<String>> songLines=new HashMap<>();
            Song nbsSong = new Song(nbsFile);
            List<Layer> nbsSongBoard = nbsSong.getSongBoard();
            for (int i = 0; i < nbsSongBoard.size(); i++) {
                HashMap<Integer, Note> noteList = nbsSongBoard.get(i).getNoteList();
                for (Map.Entry note : noteList.entrySet()) {
                    Note noteInfo = (Note) note.getValue();
                    Integer noteKey=(int)((double)(int)note.getKey()/(5.0*((double)nbsSong.getTempo()/10000.0)));
                    if(!songLines.containsKey(noteKey))songLines.put(noteKey,new ArrayList<>());
                    ArrayList<String> tickLines=songLines.get(noteKey);
                    //keep notes within 2-octave range
                    Integer notePitch=Math.max(33,Math.min(57,noteInfo.getPitch()))-33;
                    tickLines.add(noteKey + ":" + notePitch + ":" + convertInstrument(noteInfo.getInstrument()) + "\n");
                    songLines.put(noteKey,tickLines);
                }
            }
            SortedSet<Integer> ticks = new TreeSet<>(songLines.keySet());
            for (Integer tick : ticks) {
                ArrayList<String> tickLines = songLines.get(tick);
                for(int i=0;i<tickLines.size();i++){
                    resSongFile+=tickLines.get(i);
                }
            }
            if(resSongFile.endsWith("\n"))resSongFile=resSongFile.substring(0,resSongFile.length()-1);
            return resSongFile;
        }catch(Exception e){
            System.out.println("There was an error while converting your NBS.");
            return null;
        }
    }
    private static Integer convertInstrument(Instrument instr){
        Integer instrId=instruList.indexOf(instr.name());
        return instrId==-1?0:instrId;
    }
}