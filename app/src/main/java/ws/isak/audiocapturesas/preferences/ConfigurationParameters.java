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


    public static final int BUFFER_ELEMENTS_TO_RECORD = 1024;       //playback 2048 (2k) but since
    public static final int BYTES_PER_ELEMENT = 2;                  //2 bytes per elements need half the elements
    //-------DYNAMIC - TODO set up user input for changing these if necessary

    public static final int RECORDER_SAMPLERATE = 8000;             //Max on emulator,  TODO variable
    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;           //TODO Stereo?!
    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
}
