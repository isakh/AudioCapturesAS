package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
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
 * The fragment where audio can be recorded/saved/played back.
 * TODO add a background of a scrolling bitmap of the spectrogram
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class RecFrag extends Fragment implements View.OnClickListener{

    Button record, stop, play, save;        //Control buttons

    private MediaRecorder audioRecorder;    //MediaRecorder object triggered during record

    private File newFieldRecordingFile;     //File to hold a new field recording

    private String storageState;        //the state of external storage as returned by environment
    private String storageDirectory;    //the storage directory - currently from strings.xml as FieldRecordings
    private String outputFileName;      //TODO initially set as strings.xml TestFile, need to make naming dynamic

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
        play.setEnabled(false);          //if this is to be true, we would need library access (jump to frag)
        save.setEnabled(false);


        /* CREATE DIRECTORY HEIRARCHY */
        //check the state of the external storage location to verify it's availability
        storageState = android.os.Environment.getExternalStorageState();
        Toast.makeText(getActivity(),
                "storageState is: " + storageState,
                Toast.LENGTH_SHORT).show();
        //in case of problem, notify user that storage is not available
        if (!storageState.equals (Environment.MEDIA_MOUNTED)) {
            Toast.makeText(getActivity(),
                    "### External Storage (SD CARD) Is Not Mounted. It is " + storageState + ".",
                    Toast.LENGTH_SHORT).show();
        }
        //Define the storage directory we want to use and check that it exists
        storageDirectory = Environment.getExternalStorageDirectory()
                + File.separator
                + getActivity().getString(R.string.storage_directory)
                + File.separator;
        Toast.makeText(getActivity(),
                "Storage Directory: " + storageDirectory + " SUCCESS",
                Toast.LENGTH_SHORT).show();
        //create a recording file in the storage directory with test_file_name [TODO make naming dynamic]
        try {
            File newFieldRecordingFile = new File(storageDirectory
                    + getActivity().getString(R.string.test_file_name)
                    + getActivity().getString(R.string.audio_file_format));

            if (newFieldRecordingFile.exists()) {
                Toast.makeText(getActivity(),
                        "onCreate: newFiledRecordingFile ALREADY EXISTS... Overwrite?",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(),
                        "Creating File:"
                                + storageDirectory
                                + getActivity().getString(R.string.test_file_name)
                                + getActivity().getString(R.string.audio_file_format),
                        Toast.LENGTH_LONG).show();
                newFieldRecordingFile.createNewFile();
            }

            //define outputFileName
            outputFileName = getActivity().getString(R.string.test_file_name);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //set up the audio recorder - TODO should this be passed to the class for the record button etc?
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
                try {
                    Toast.makeText(getActivity(),
                            "Record Button Pressed",
                            Toast.LENGTH_SHORT).show();
                    audioRecorder.prepare();
                    /*
                    Toast.makeText(getActivity(),
                            "recButton: audioRecorder.prepare() ",
                            Toast.LENGTH_SHORT).show();
                    */
                    audioRecorder.start();
                    /*
                    Toast.makeText(getActivity(),
                            "recButton: audioRecorder.start() ",
                            Toast.LENGTH_SHORT).show();
                    */
                } catch (IOException e) {
                    e.printStackTrace();
                }
                record.setEnabled(false);
                stop.setEnabled(true);
                //Toast.makeText(getActivity(), "recButton: Record In Progress", Toast.LENGTH_SHORT).show();
                break;
            case R.id.stopButtonRecFrag:
                Toast.makeText(getActivity(), "stopButton: Stop Button Pressed", Toast.LENGTH_SHORT).show();
                //audioRecorder.stop();
                //audioRecorder.release();
                //audioRecorder = null;

                stop.setEnabled(false);
                play.setEnabled(true);
                Toast.makeText(getActivity(), "stopButton: Recording has Stopped", Toast.LENGTH_SHORT).show();

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