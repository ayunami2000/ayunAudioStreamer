package me.ayunami2000.ayunAudioStreamer;

public class PlaySong {
    private static String[] instruments=new String[]{"harp","basedrum","snare","hat","bass","flute","bell","guitar","chime","xylophone","iron_xylophone","cow_bell","didgeridoo","bit","banjo","pling"};
    public static void playSong(String[] songLines) throws InterruptedException {
        int ticks=0;
        for (String songLine : songLines) {
            String[] songInfo = songLine.split(":");
            int tick = Integer.parseInt(songInfo[0]);
            int note = Integer.parseInt(songInfo[1]);
            int instr = Integer.parseInt(songInfo[2]);
            if(instr!=-1) {
                int vol = songInfo.length == 4 ? Integer.parseInt(songInfo[3]) : 127;
                int panning = songInfo.length >= 5 ? Integer.parseInt(songInfo[4]) : 100;
                int precisePitch = songInfo.length >= 6 ? Integer.parseInt(songInfo[5]) : 0;
                Thread.sleep((tick - ticks) * 50);
                ticks = tick;
                Main.currData += instr + "," + (((double) vol) / 127.0) + "," + (.5 * (Math.pow(2, (note + (precisePitch%100) / 100.0) / 12.0))) + "," + ((100 - panning) / 50.0) + "\n";
                Main.updData = true;
            }
        }
    }
}
