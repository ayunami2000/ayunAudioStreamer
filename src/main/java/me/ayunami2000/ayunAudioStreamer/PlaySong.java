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
            int vol = songInfo.length == 4 ? Integer.parseInt(songInfo[3]) : 127;
            Thread.sleep((tick - ticks) * 50);
            ticks = tick;
            Main.currData+="\n"+instr+","+(((double) vol) / 127.0)+","+(.5 * (Math.pow(2, ((double) note) / 12.0)));
            Main.updData=true;
        }
    }
}
