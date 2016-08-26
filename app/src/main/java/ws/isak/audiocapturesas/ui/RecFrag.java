package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;
import ws.isak.audiocapturesas.audioProcessing.RecordAudioData;
import ws.isak.audiocapturesas.preferences.ConfigurationParameters;
import ws.isak.audiocapturesas.storage.DataStorageUtilities;
import ws.isak.audiocapturesas.storage.WavFileUtilities;

import android.support.v4.app.Fragment;
import android.content.Context;

import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.Semaphore;

/**
 * Created by @isakherman on 7/19/16. .
 * The fragment where audio can be recorded/saved/played back.
 * TODO add a background of a scrolling bitmap of the spectrogram
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class RecFrag extends Fragment implements View.OnClickListener {
    
    private final String TAG = "";

    ConfigurationParameters configParams = new ConfigurationParameters();
    DataStorageUtilities dataStore;
    WavFileUtilities wavUtils;
    RecordAudioData recAudio;

    private Button record, stop, play, save;        //Control buttons
    private Context context;

    private Semaphore audioToProcess = new Semaphore(1);
    private short[][] audioWindows;

    //==============================================================================================


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.v(TAG, "=================================");
        Log.v(TAG, "STARTING onCreateView");
        //get context from activity
        Log.v(TAG, "onCreateView: context = getActivity()");
        context = getActivity();
        //create a view from the UI described in record_fragment.xml
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        //create objects for each of the buttons described in the UI and set their initial states
        setButtonHandlers(v);
        setButtonsStates(true, false, false, false);

        //set up storage directory in external storage for
        dataStore = new DataStorageUtilities(context);
        dataStore.setExternalStorageDir();

        return v;
    }

    @Override
    public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
        switch (v.getId()) {
            case R.id.recButtonRecFrag:
                Log.v(TAG, "onClick: Record Button Pressed");
                Toast.makeText(getActivity(), "Record Button Pressed", Toast.LENGTH_SHORT).show();

                dataStore.setFieldRecordingFile(getActivity().getString(R.string.wav_file_format));
                Log.v(TAG, "onClick: Record: instantiate new RecordAudioData object");

                //allocate 2D short array for storage of audioRecord data
                audioWindows = new short[configParams.WINDOW_LIMIT][configParams.SAMPLES_PER_WINDOW];
                //call constructor on RecordAudioData object
                recAudio = new RecordAudioData(audioWindows, configParams, audioToProcess);
                recAudio.startAudioRecording();

                //set the states of the rest of the buttons on the UI to limit interactions
                setButtonsStates(false, true, false, false);
                break;
            case R.id.stopButtonRecFrag:
                Log.v(TAG, "onClick: Stop Button Pressed");
                Toast.makeText(getActivity(), "stopButton: Stop Button Pressed", Toast.LENGTH_SHORT).show();
                //TODO only stop recording if recording is going, otherwise stop needs to stop playback
                /*if (??????) {
                    recAudio.stopAudioRecording();
                }*/
                setButtonsStates(true, false, true, true);
                break;
            case R.id.playButtonRecFrag:
                Log.v(TAG, "onClick: Play Button Pressed");
                Toast.makeText(getActivity(), "Play Button Pressed", Toast.LENGTH_SHORT).show();
                //TODO this button on this screen should only be available when there is a buffer of audio to play, not play from library
                break;
            case R.id.saveButtonRecFrag:
                Log.v(TAG, "onClick: Save Button Pressed");
                Toast.makeText(getActivity(), "Save Button Pressed", Toast.LENGTH_SHORT).show();
                //TODO copy contents of record buffer to library file for long term storage/manipulation
                //createFieldRecordingFile();
                break;
        }
    }

    /*
    A private method that creates the button objects for the buttons in the current view  and sets
    their on-click listeners for further interaction.
     */
    private void setButtonHandlers(View v) {
        Log.v(TAG, "setButtonHandlers: creating Buttons");
        record = (Button) v.findViewById(R.id.recButtonRecFrag);
        stop = (Button) v.findViewById(R.id.stopButtonRecFrag);
        play = (Button) v.findViewById(R.id.playButtonRecFrag);
        save = (Button) v.findViewById(R.id.saveButtonRecFrag);
        Log.v(TAG, "setButtonHandlers: setting on-click listeners");
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    /*
    A private function that sets the initial state of the buttons suitable for the interaction
    */
    private void setButtonsStates(boolean recState, boolean stopState, boolean playState, boolean saveState) {
        Log.v(TAG, "setButtonStates: buttons states set");
        record.setEnabled(recState);
        stop.setEnabled(stopState);
        play.setEnabled(playState);          //if this is to be true, we would need library access (jump to frag)
        save.setEnabled(saveState);
    }
}