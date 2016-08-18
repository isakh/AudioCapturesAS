package ws.isak.audiocapturesas.preferences;

import android.media.AudioFormat;

//import ws.isak.audiocapturesas.R;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.preference.PreferenceManager;

/**
 * Created by isakherman on 8/11/16.
 * A class to hold the audio parameters for preferences etc.
 */
public class ConfigurationParameters {

    //-------STATIC

    public static final int AUDIO_HEADER_LENGTH = 44;               //number of bytes in the WAV header; variable in case other container format used
    public static final int BITS_PER_SAMPLE = 16;                   //bits per sample in PCM_16BIT encode//public static final int BUFFER_ELEMENTS_TO_RECORD = 1024;     //playback 2048 (2k) but since
    //public static final int BYTES_PER_ELEMENT = 2;                //2 bytes per elements need half the elements

    //-------DYNAMIC - TODO set up user input for changing these if necessary

    public final int SAMPLERATE = 8000;                             //Max on emulator,  TODO variable
    public final int NUM_CHANNELS = AudioFormat.CHANNEL_IN_MONO;    //TODO Stereo?!
    public final int WINDOW_LIMIT = 1000;                           //max windows to process (e.g. time limit for recording)
    public final int SAMPLES_PER_WINDOW = 300;                      //can vary depending on processing efficacy ~300 is good
    public final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
}
