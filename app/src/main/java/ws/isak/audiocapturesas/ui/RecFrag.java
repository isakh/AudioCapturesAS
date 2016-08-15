package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;
import ws.isak.audiocapturesas.preferences.ConfigurationParameters;

import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.os.Environment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.Toast;

import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Created by @isakherman on 7/19/16. .
 * The fragment where audio can be recorded/saved/played back.
 * TODO add a background of a scrolling bitmap of the spectrogram
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class RecFrag extends Fragment implements View.OnClickListener {


    private Button record, stop, play, save;        //Control buttons

    private AudioRecord audioRec = null;    //MediaRecorder object triggered during record

    private String storageState;            //the state of external storage as returned by environment
    private String storageDirectory;        //set/get by methods, set as composite of user and path variables

    private File storageDir;                //File used to build directory path to FieldRecordings
    private boolean isStorageDirMade;
    private File fieldRecordingDir;         //File for newFieldRecording directory - there can only be one at a time
    private boolean isFieldRecordingDirMade;
    private File fieldRecordingFile;        //File for newFieldRecording - this is where the wav data is written
    private boolean isFieldRecordingFileMade;

    private String timeStamp;               //used in constructing newFieldRecording directory name

    private int bufferSize;                 //buffer for storing live audio input for processing prior to writing to file
    private Thread recordingThread = null;  //initialize to null
    private boolean isRecording = false;    //initialize to false

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        System.out.println("=================================");
        System.out.println("In RecFrag: STARTING onCreateView");

        //create a view from the UI described in record_fragment.xml
        View v = inflater.inflate(R.layout.fragment_record, container, false);

        //create objects for each of the buttons described in the UI and set their initial state
        setButtonHandlers(v);
        setButtonsStates(true, false, false, false);

        //set up storage directory in external storage for
        setExternalStorageDir();

        int bufferSize = AudioRecord.getMinBufferSize(ConfigurationParameters.RECORDER_SAMPLERATE,
                ConfigurationParameters.RECORDER_CHANNELS, ConfigurationParameters.RECORDER_AUDIO_ENCODING);

        return v;
    }

    @Override
    public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
        switch (v.getId()) {
            case R.id.recButtonRecFrag:
                System.out.println("RecFrag: onClick: Record Button Pressed");
                Toast.makeText(getActivity(), "Record Button Pressed", Toast.LENGTH_SHORT).show();
                startRecording();
                setButtonsStates(false, true, false, false);
                break;
            case R.id.stopButtonRecFrag:
                System.out.println("RecFrag: onClick: Stop Button Pressed");
                Toast.makeText(getActivity(), "stopButton: Stop Button Pressed", Toast.LENGTH_SHORT).show();
                stopRecording();
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

    /*
    Private functions that set and get up the external storage directory where data is to be collected
    once it has been saved. (TODO serialize all data ??)
    Check the state of the external storage location to verify it's availability.  This function only
    creates the folder structure to the point of ~/storage/sdcard/FieldRecordings (or variation thereof.)
    The subsequent subfolder for the data collected at a given point in time will be created in
    another function.
     */
    private void setExternalStorageDir () {
        storageState = android.os.Environment.getExternalStorageState();
        System.out.println("RecFrag: setExternalStorageDir: storageState is: " + storageState);
        //in case of mounting problem, notify user that storage is not available
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            //send error message to logcat
            System.out.println("RecFrag: setExternalStorageDir: ERROR: External Storage (SD CARD) Is Not Mounted. It is " + storageState + ".");
            //and also to user on device
            Toast.makeText(getActivity(),
                    "ERROR: External Storage (SD CARD) Is Not Mounted. It is " + storageState + ".",
                    Toast.LENGTH_SHORT).show();
        }
        //Define the storage directory we want to use and check that it exists
        storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + getActivity().getString(R.string.storage_directory)
                + File.separator;
        System.out.println("RecFrag: setExternalStorageDir: Environment.getExternalStorageDirecory().getAbsolutePath() is :"
                + Environment.getExternalStorageDirectory().getAbsolutePath());
        storageDir = new File(storageDirectory);
        isStorageDirMade = storageDir.mkdirs();
        if (storageDir.exists()) {
            System.out.println("RecFrag: setExternalStorageDir: storageDir exists is: " + storageDir.exists());
        }
        if (!isStorageDirMade) {
            System.out.println("RecFrag: setExternalStorageDir: failed to make storageDir");
        } else {
            try {
                String path = storageDir.getCanonicalPath();
                System.out.println("RecFrag: setExternalStorageDir: Storage Directory is: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }
    }

    /*
    A private method that returns the File object (for the Directory) at storageDir
     */
    private File getExternalStorageDir () {
        System.out.println("RecFrag: getExternalStorageDir: returning storage directory");
        return storageDir;
    }


    /*
    A private method that returns the time stamp of the current time as a string.  This is to be
    used as a UID for file creation even if the user doesn't provide additional unique information
    */
    private static String getCurrentTimeStamp() {
        System.out.println("RecFrag: getCurrentTimeStamp: initialized");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            String currentTimeStamp = dateFormat.format(new Date());
            return currentTimeStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
    A private method that takes the current timestamp and creates a file for storing a recording
    in the storage directory with test_file_name and the time appended as a unique identifier
    [TODO: this will not work if networked and multiple instances of the programme are simultaneously
    unless each user has UID]
    */
    private void setFieldRecordingFile () {
        System.out.println("RecFrag: setFieldRecordingFile: initialized");
        timeStamp = getCurrentTimeStamp();
        System.out.println("RecFrag: setFieldRecordingFile: Timestamp is: " + timeStamp);

        fieldRecordingDir = new File(storageDir,
                getActivity().getString(R.string.test_file_folder)
                + timeStamp);
        isFieldRecordingDirMade = fieldRecordingDir.mkdirs();
        if (fieldRecordingDir.exists()) {
            System.out.println("RecFrag: setFieldRecordingFile: fieldRecordingDir exists is: " + fieldRecordingDir.exists());
        }
        if (!isFieldRecordingDirMade) {
            System.out.println("RecFrag: setFieldRecordingFile: failed to make fieldRecordingDir");
        }
        else {
            try {
                String path = fieldRecordingDir.getCanonicalPath();
                System.out.println("RecFrag: setFieldRecordingFile: Field Recording Directory is: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch  (SecurityException ex) {
                ex.printStackTrace();
            }
        }
        fieldRecordingFile = new File (fieldRecordingDir,
                getActivity().getString(R.string.test_file_name)
                + "_"
                + timeStamp
                + getActivity().getString(R.string.audio_file_format));
        isFieldRecordingFileMade = fieldRecordingFile.mkdir();
        if (fieldRecordingFile.exists()) {       //Tell this to the user so keep as Toast
            System.out.println("RecFrag: setFieldRecordingFile: fieldRecordingFile exists is: " + fieldRecordingFile.exists());
        }
        if (!isFieldRecordingFileMade){
            System.out.println("RecFrag: setFieldRecordingFile: failed to make fieldRecordingFile");
        }
        else {
            try {
                String path = fieldRecordingFile.getCanonicalPath();
                System.out.println("RecFrag: setFieldRecordingFile: Field Recording File is: " + path);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFieldRecordingFileName () {
        try {
            System.out.println("RecFrag: getFieldRecordingFileName: file name is: " + fieldRecordingFile.getCanonicalPath());
            return fieldRecordingFile.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
    Private function called when the record button is pressed, it starts by instantiating a new
    AudioRecorder object, and sets up a thread writing the audio data to a file.
     */
    private void startRecording () {
        System.out.println("RecFrag: startRecording()");
        audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC,
                ConfigurationParameters.RECORDER_SAMPLERATE,
                ConfigurationParameters.RECORDER_CHANNELS,
                ConfigurationParameters.RECORDER_AUDIO_ENCODING,
                ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD * ConfigurationParameters.BYTES_PER_ELEMENT);
        audioRec.startRecording();
        isRecording = true;
        recordingThread = new Thread (new Runnable () {
           public void run () {
               writeAudioDataToFile();
           }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    /*
    Private function called when the stop button is pressed.  This stops the recording activity
    and releases the recording thread.
     */
    private void stopRecording () {
        System.out.println("RecFrag: stopRecording()");
        if (null != audioRec) {
            isRecording = false;
            audioRec.stop();
            audioRec.release();
            audioRec = null;
            recordingThread = null;
        }
    }

    /*
    Private function that writes the recording input stream audio data to a temporary (?) file
    TODO need to figure out how this file relates to the permanent one in external storage
     */
    private void writeAudioDataToFile () {
        System.out.println("RecFrag: writeAudioDataToFile: initializing");
        short sData[] = new short [ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD];

        FileOutputStream outputStream = null;
        try {
            setFieldRecordingFile();
            System.out.println("RecFrag: writeAudioDataToFile: called setFieldRecordingFile()");
            outputStream = new FileOutputStream(getFieldRecordingFileName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            //keep collecting microphone output in byte format
            audioRec.read (sData, 0 , ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD);
            System.out.println("Writing Audio Shorts to File: " + sData.toString());
            try {
                //store mic buffer and write the data to file from buffer
                byte bData[] = shortToByte (sData);
                outputStream.write(bData,
                        0,
                        ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD * ConfigurationParameters.BYTES_PER_ELEMENT);
            } catch (IOException e) {
                e.printStackTrace();;
            }
        }
        try {
            outputStream.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    /*
    Private function that converts shorts to bytes.  Used to convert the sData array of shorts that is
    constructed to read in the data from audioRec into a bData array of bytes for output writen to file.
     */
    private byte[] shortToByte (short[] sData) {
        System.out.println("RecFrag: shortToByte: initializing");
        int shortArraySize = sData.length;
        byte[] outputBytes = new byte [shortArraySize * 2];
        for (int i = 0; i < shortArraySize; i++) {
            outputBytes[i * 2] = (byte) (sData[i] & 0x00FF);          //TODO Remind what this does? bit mask
            outputBytes[(i * 2) + 1] = (byte) (sData[i] >> 8);        //TODO Remind what this does? bit shift
            sData[i] = 0;
        }
        return outputBytes;
    }
}