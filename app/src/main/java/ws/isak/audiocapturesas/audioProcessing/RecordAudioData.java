package ws.isak.audiocapturesas.audioProcessing;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import ws.isak.audiocapturesas.preferences.ConfigurationParameters;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;

import java.util.concurrent.Semaphore;

/**
 * Created by isakherman on 8/17/16.  This class is the thread which pulls in audio samples from
 * the microphone.  They are stored in a 2D short array audioWindowShortArr which is initialized in
 * the RecFrag and passed to the constructor here
 */
public class RecordAudioData extends Thread {

    private AudioRecord audioRec = null;            //AudioRecorder object triggered for record

    private Thread recordingThread = null;          //initialize to null
    private Semaphore audioToProcess;               //semaphore indicates when window ready for processing
    private ConfigurationParameters configParams;
    private boolean isRecording = true;             //initialize to false

    private short[][] audioWindowShortArr;          //short array of audio sample windows store AudioRecorder.read() output
    private int audioWindowCurIndex;                //an integer index into the audioWindowShortArr

    private byte[] audioByteArr;                    //byte array for FileOutputStream.write()
    private FileOutputStream fileOutStream;
    private BufferedOutputStream buffRecOutStream;
    private DataOutputStream dataRecOutStream;

    //Constructor
    public RecordAudioData(short[][] audioWindowShortArr, ConfigurationParameters configParams, Semaphore audioToProcess) {
        this.audioWindowShortArr = audioWindowShortArr;
        this.audioToProcess = audioToProcess;
        this.configParams = configParams;

        int readSize = AudioRecord.getMinBufferSize(configParams.SAMPLE_RATE,
                AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        audioRec = new AudioRecord(MediaRecorder.AudioSource.MIC,
                configParams.SAMPLE_RATE,
                configParams.NUM_CHANNELS,
                configParams.AUDIO_ENCODING,
                readSize * 2);
    }

    /*
    Private function called when the record button is pressed, it starts by instantiating a new
    AudioRecorder object, and sets up a thread writing the audio data to a file.
   */
    public void startAudioRecording() {

        System.out.println("RecordAudioData: startAudioRecording(): initialized");
        System.out.println("RecordAudioData: startAudioRecording: audioRec Created ... ");
        System.out.println(" ... Sample Rate is: " + configParams.SAMPLE_RATE);
        System.out.println(" ... Num Channels is: " + configParams.NUM_CHANNELS);
        System.out.println(" ... Audio Encoding is: " + configParams.AUDIO_ENCODING);

        recordingThread = new Thread(new Runnable() {
            @Override
            public void run() {

                System.out.println("RecordAudioData: startAudioRecording: recordingThread initialized");
                try {
                    audioRec.startRecording();
                    System.out.println("RecordAudioData: startAudioRecording: audioRec.startRecording() called");
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                //acquire semaphore for 1 audioRecord Thread
                try {
                    audioToProcess.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while (isRecording && !audioToProcess.tryAcquire()) {   //isRecording will be true while stopRecording hasn't been pressed
                                                                        //audioToProcess.tryAcquire will be false until the mutex has been released
                    fillAudioArray();
                }
                //when isRecording is false, stop data from mic and release the AudioRecord object
                System.out.println("RecordAudioData: statAudioRecording: after: while (isRecording) ... ");
                System.out.println("... boolean isRecording is: " + isRecording);
                System.out.println("... Semaphore audioToProcess.tryAcquire() is: " + audioToProcess.tryAcquire());
                System.out.println("... Thread RecordingThread is " + recordingThread);
                audioRec.stop();
                System.out.println("RecordAudioData: startAudioRecording: audioRec.stop() called");
                audioRec.release();
                System.out.println("RecordAudioData: startAudioRecording: audioRec.release() called");

            }
        }, "AudioRecorder Thread");
        recordingThread.start();
        // TODO writeAudioData();
        System.out.println("RecordAudioData: startAudioRecording: recordingThread.start() called");
    }

    /*
    Private function called when the stop button is pressed.  This stops the recording activity
    and releases the recording thread.
     */
    public void stopAudioRecording() {
        System.out.println("RecordAudioData: stopAudioRecording(): isRecording is currently: "
                + isRecording
                + " :recordingThread is currently: "
                + recordingThread);
        isRecording = false;
        System.out.println("RecordAudioData: stopAudioRecording: isRecording set to false");

        //TODO this may be duplicating the process that occurs in startAudioRecording once isRecording is false...
        if (null != audioRec) {
            audioRec = null;
            System.out.println("RecordAudioData: stopAudioRecording: audioRecorder object audioRec set to null");
            recordingThread = null;
            System.out.println("RecordAudioData: stopAudioRecording:  Thread object recordingThread set to null ");
        }
    }

    /*
    Private method fillAudioArray - this takes audio data as it becomes available from the microphone
    and stores it in a 2D array keeping it available for the user to replay certain sections before saving
    NOTE that there is no locking on audioWindowShortArr.
     */
    public void fillAudioArray() {
        //request samplesPerWindow shorts be written into the next free microphone buffer
        readBuffUntilFull(audioWindowShortArr[audioWindowCurIndex], 0, configParams.SAMPLES_PER_WINDOW);
        audioWindowCurIndex++;
        audioToProcess.release();
        if (audioWindowCurIndex == audioWindowShortArr.length) {
            //if the window array has been filled, loop and fill from start of next window
            audioWindowCurIndex = 0;
        }
    }

    /*
    Private method readBuffUntilFull takes care of the fact that the audioRecord.read method will not
    necessarily fill the buffer with samples if there is not enough data available.  This method always
    returns a full array by repeatedly calling the .read() method until there is no space left
     */
    private void readBuffUntilFull(short[] buff, int offset, int spaceLeftInBuff) {
        while (spaceLeftInBuff > 0) {
            int samplesRead = audioRec.read(buff, offset, spaceLeftInBuff);
            spaceLeftInBuff -= samplesRead;
            offset += samplesRead;
        }
    }
}

/*
  ====================================================================
    /*
    Private method writes the audio bytes as they are collected by the microphone to a buffered output
    stream.  The buffered output stream provides an internal buffer unlike file output stream which
    needs to write to disk for each byte, thus reducing the number of system calls.

    private void writeAudioData () {
        System.out.println("RecordAudioData: writeAudioDataToBuffer");
        audioShortArr = new short [ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD];
        System.out.println("RecFrag: writeAudioDataToBuffer: audioShortArr Array allocated: Size is: "
                + audioShortArr.length);

        fileOutStream = null;
        buffRecOutStream = null;
        dataRecOutStream = null;

        System.out.println("RecordAudioData: writeAudioData: ");

    }


    /*
    Private function that writes the recording input stream audio data to a temporary (?) file
    TODO need to figure out how this file relates to the permanent one in external storage

    private void writeAudioDataToFile() {
        System.out.println("RecordAudioData: writeAudioDataToFile: initializing");
        audioShortArr = new short[ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD];
        System.out.println("RecordAudioData: writeAudioDataToFile: audioShortArr Array allocated: Size is: " + audioShortArr.length);

        fileOutStream = null;

        System.out.println("RecordAudioData: writeAudioDataToFile: file fileOutStream initialized");
        dataStore.setFieldRecordingFile(getActivity().toString(R.string.pcm_file_format);
        System.out.println("RecordAudioData: writeAudioDataToFile: called setFieldRecordingFile()");
        File fieldRecordingFilePCM = dataStore.getFieldRecordingFile();
        try {
            String path = fieldRecordingFilePCM.getCanonicalPath();
            System.out.println("RecordAudioData: writeAudioDataToFile: fieldRecordingFilePCM is at: " + path);
            fileOutStream = new FileOutputStream(path);
            System.out.println("RecordAudioData: writeAudioDataToFile: fileOutStream initialized");
        }
        catch (IOException | SecurityException | NullPointerException e) {
            e.printStackTrace();
        }
        while (isRecording && recordingThread != null) {
            //keep collecting microphone output in short format
            try {
                audioRec.read(audioWindowShortArr, 0, ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD);
                System.out.println("RecFrag: writeAudioDataToFile: audioRec.read: isRecording: "
                        + isRecording
                        + " : recordingThread: "
                        + recordingThread);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                //store mic buffer and write the data to file from buffer
                audioByteArr = shortToByte(audioShortArr);
                fileOutStream.write(audioByteArr,
                        0,
                        ConfigurationParameters.BUFFER_ELEMENTS_TO_RECORD * ConfigurationParameters.BYTES_PER_ELEMENT);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        try {
            fileOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO once the PCM stream has stopped, construct an (additional?) WAV file
        //wavUtils.wavFromPCM(fieldRecordingFilePCM);
    }

    /*
    Private function that converts shorts to bytes.  Used to convert the audioShortArr array of
    shorts that is constructed to read in the data from audioRec into a audioByteArr array of bytes
    for output written to file.  The bitwise & 0x00FF ensures that the least significant byte is
    read as is (the cast to (byte) means that only the first 8 bits are read); the bitshift >> 8 then
    moves the most significant byte right to be copied.

    private byte[] shortToByte(short[] sData) {
        //System.out.println("RecordAudioData: shortToByte: audioShortArr sData length is: " + sData.length);
        int shortArraySize = sData.length;
        byte[] outputBytes = new byte[shortArraySize * 2];
        //System.out.println("RecordAudioData: shortToByte: allocated outputBytes.length: " + outputBytes.length);
        for (int i = 0; i < shortArraySize; i++) {
            outputBytes[i * 2] = (byte) (sData[i] & 0x00FF);
            outputBytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        //System.out.println("RecordAudioData: shortToByte: done iterating over short array");
        return outputBytes;
    }
}
*/