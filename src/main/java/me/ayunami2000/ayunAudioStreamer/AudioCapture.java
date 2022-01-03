package me.ayunami2000.ayunAudioStreamer;

import me.ayunami2000.ayunAudioStreamer.AudioParser.Analysis.Analysis;

public class AudioCapture {
    private static String[] instruments=new String[]{"harp","basedrum","snare","hat","bass","flute","bell","guitar","chime","xylophone","iron_xylophone","cow_bell","didgeridoo","bit","banjo","pling"};

    public static void pitchDetect(Analysis analysis){
        if(analysis.maximum<1)return;
        double volumeToUsable = 12.0 * (Math.log(analysis.maximum / 440.0f) / Math.log(2)) + 69.0;
        for (Double f0 : analysis.klapuri.f0s) {
            //440 Hz as the pitch of A4
            double pitchToMidi = 12.0 * (Math.log(f0 / 440.0f) / Math.log(2)) + 69.0;
            int[] midiToNote = MidiConverter.noteConv(0,(int)pitchToMidi);
            int noteToGame = (midiToNote[1]-MidiConverter.instrument_offsets[midiToNote[0]]) + midiToNote[0]*25;

            Main.currData+=";"+((int)Math.floor(noteToGame / 25))+","+(volumeToUsable/127.0)+","+(.5*(Math.pow(2,((double)(noteToGame%25))/12.0)));
        }
    }
}