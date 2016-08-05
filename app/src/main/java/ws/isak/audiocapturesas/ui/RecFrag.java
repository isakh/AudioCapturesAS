package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
//import android.support.v4.os.EnvironmentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.IOException;
import java.io.File;


/**
 * Created by isakherman on 7/19/16. .
 * test for a rec frag with only one button
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class RecFrag extends Fragment implements View.OnClickListener{

    Button record, stop, play, save;
    private MediaRecorder audioRecorder;
    private File outputFile;
    private String outputFileName = null;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        //create a view from the UI described in record fragment xml
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        //create objects for each of the buttons described in the UI
        record = (Button) v.findViewById(R.id.recButtonRecFrag);
        stop = (Button) v.findViewById(R.id.stopButtonRecFrag);
        play = (Button) v.findViewById(R.id.playButtonRecFrag);
        save = (Button) v.findViewById(R.id.saveButtonRecFrag);

        //set the state of the buttons suitable for the initial interaction
        record.setEnabled(true);
        stop.setEnabled(false);
        play.setEnabled(false);
        save.setEnabled(false);

        //check the state of the external storage location to verify it's availability
        String storageState = android.os.Environment.getExternalStorageState();
        Toast.makeText(getActivity(), storageState, Toast.LENGTH_LONG).show();
        if (!storageState.equals (Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(), "### External Storage (SD CARD) Is Not Mounted. It is " + storageState + ".", Toast.LENGTH_LONG).show();
        }
        //make sure that the storage directory we want to use exists
        String path = Environment.getDataDirectory().getAbsolutePath();
        Toast.makeText(getActivity(), path, Toast.LENGTH_LONG).show();              //this shows /data as path
        File directory = new File (path).getParentFile();
        if (!directory.exists() && !directory.mkdirs()) {
            Toast.makeText(getActivity(), "### Path To File Could Not Be Created", Toast.LENGTH_LONG).show();
        }
        try {
            String storageDirName = getResources().getString(R.string.storage_directory);
            Toast.makeText(getActivity(), "storageDirName is: " + storageDirName, Toast.LENGTH_LONG).show();
            File storageDir = new File (Environment.getExternalStorageDirectory(), storageDirName);
            storageDir.mkdir();
            outputFile = File.createTempFile("audioFile", ".mp4", storageDir);
            outputFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audioFile.mp4";
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.DEFAULT);
        audioRecorder.setOutputFile(outputFileName);

        record.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        save.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick (View v) throws IllegalArgumentException, SecurityException, IllegalStateException{
        switch (v.getId()) {
            case R.id.recButtonRecFrag:
                Toast.makeText(getActivity(), "Record Button Pressed", Toast.LENGTH_SHORT).show();
                try {
                    audioRecorder.prepare();
                    audioRecorder.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getActivity(), "Record In Progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stopButtonRecFrag:
                Toast.makeText(getActivity(), "Stop Button Pressed", Toast.LENGTH_SHORT).show();
                //audioRecorder.stop();
                //audioRecorder.release();
                //audioRecorder = null;

                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getActivity(), "Recording has Stopped", Toast.LENGTH_SHORT).show();

                break;
            case R.id.playButtonRecFrag:
                Toast.makeText(getActivity(), "Play Button Pressed", Toast.LENGTH_SHORT).show();
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    mPlayer.setDataSource (outputFileName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.start();
                Toast.makeText(getActivity(), "Playing back Audio", Toast.LENGTH_SHORT).show();

                break;
            case R.id.saveButtonRecFrag:
                Toast.makeText(getActivity(), "Save Button Pressed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}