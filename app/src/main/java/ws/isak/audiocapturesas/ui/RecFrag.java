package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;
import ws.isak.audiocapturesas.audioProcessing.RecordAudioData;
import ws.isak.audiocapturesas.preferences.ConfigurationParameters;
import ws.isak.audiocapturesas.storage.DataStorageUtilities;
import ws.isak.audiocapturesas.storage.WavFileUtilities;

import android.support.v4.app.Fragment;
import android.content.Context;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

/**
 * Created by @isakherman on 7/19/16. .
 * The fragment where audio can be recorded/saved/played back.
 * TODO add a background of a scrolling bitmap of the spectrogram
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class RecFrag extends Fragment implements View.OnClickListener {

    Context context = getActivity().getApplicationContext();

    ConfigurationParameters configParams;
    DataStorageUtilities dataStore;
    WavFileUtilities wavUtils;
    RecordAudioData recAudio;

    private Button record, stop, play, save;        //Control buttons

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("=================================");
        System.out.println("In RecFrag: STARTING onCreateView");

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
                System.out.println("RecFrag: onClick: Record Button Pressed");
                Toast.makeText(getActivity(), "Record Button Pressed", Toast.LENGTH_SHORT).show();
                //startAudioRecording();
                recAudio.startAudioRecording();
                setButtonsStates(false, true, false, false);
                break;
            case R.id.stopButtonRecFrag:
                System.out.println("RecFrag: onClick: Stop Button Pressed");
                Toast.makeText(getActivity(), "stopButton: Stop Button Pressed", Toast.LENGTH_SHORT).show();
                recAudio.stopAudioRecording();
                setButtonsStates(true, false, true, true);
                break;
            case R.id.playButtonRecFrag:
                System.out.println("RecFrag: onClick: Play Button Pressed");
                Toast.makeText(getActivity(), "Play Button Pressed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.saveButtonRecFrag:
                System.out.println("RecFrag: onClick: Save Button Pressed");
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
        System.out.println("RecFrag: setButtonHandlers: creating Buttons");
        record = (Button) v.findViewById(R.id.recButtonRecFrag);
        stop = (Button) v.findViewById(R.id.stopButtonRecFrag);
        play = (Button) v.findViewById(R.id.playButtonRecFrag);
        save = (Button) v.findViewById(R.id.saveButtonRecFrag);
        System.out.println("RecFrag: setButtonHandlers: setting on-click listeners");
        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        save.setOnClickListener(this);
    }

    /*
    A private function that sets the initial state of the buttons suitable for the interaction
    */
    private void setButtonsStates(boolean recState, boolean stopState, boolean playState, boolean saveState) {
        System.out.println("RecFrag: setButtonStates: buttons states set");
        record.setEnabled(recState);
        stop.setEnabled(stopState);
        play.setEnabled(playState);          //if this is to be true, we would need library access (jump to frag)
        save.setEnabled(saveState);
    }
}