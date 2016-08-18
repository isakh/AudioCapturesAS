package ws.isak.audiocapturesas.storage;

import ws.isak.audiocapturesas.R;
import android.content.res.Resources;
import android.content.Context;

import android.os.Environment;
//import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by isakherman on 8/16/16. Moving Storage utility methods to separate class as not only
 * relevant to RecFrag.
 */

public class DataStorageUtilities {

    private Context context;

    private String storageState;            //the state of external storage as returned by environment
    private String storageDirectory;        //set/get by methods, set as composite of user and path variables

    private File storageDir;                //File used to build directory path to FieldRecordings
    private boolean isStorageDirMade;
    private File fieldRecordingDir;         //File for newFieldRecording directory - there can only be one at a time
    private boolean isFieldRecordingDirMade;
    private File fieldRecordingFile;        //File for newFieldRecording - this is where the wav data is written
    private boolean isFieldRecordingFileMade;

    private String timeStamp;               //used in constructing newFieldRecording directory name

    //==============================================================================================
    //Constructor

    public DataStorageUtilities (Context curContext) {
        this.context = curContext;
    }


    /*
    Public function that sets the external storage directory where data is to be collecte once it has been saved.
    Check the state of the external storage location to verify it's availability.  This function only
    creates the folder structure to the point of ~/storage/sdcard/FieldRecordings (or variation thereof.)
    The subsequent subfolder for the data collected at a given point in time will be created in
    another function.
     */
    public  void setExternalStorageDir() {
        storageState = android.os.Environment.getExternalStorageState();
        System.out.println("DataStorageUtilities: setExternalStorageDir: storageState is: " + storageState);
        //in case of mounting problem, notify user that storage is not available
        if (!storageState.equals(Environment.MEDIA_MOUNTED)) {
            //send error message to logcat
            System.out.println("DataStorageUtilities: setExternalStorageDir: ERROR: External Storage is " + storageState);
        }
        //Define the storage directory we want to use and check that it exists
        System.out.println("DataStorageUtilities: setExternalStorageDir: setting storageDirectory ...");
        System.out.println( "... Environment.getExternalStorageDirectory().getAbsolutePath() is: "
                + Environment.getExternalStorageDirectory().getAbsolutePath());
        System.out.println ("... context.getString(R.string.storage_directory) is:  "
                + context.getString(R.string.storage_directory));
        storageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator
                + context.getString(R.string.storage_directory)
                + File.separator;
        storageDir = new File(storageDirectory);
        isStorageDirMade = storageDir.mkdirs();
        if (storageDir.exists()) {
            System.out.println("DataStorageUtilities: setExternalStorageDir: storageDir exists is: " + storageDir.exists());
        }
        if (!isStorageDirMade) {
            System.out.println("DataStorageUtilities: setExternalStorageDir: failed to make storageDir");
        } else {
            try {
                String path = storageDir.getCanonicalPath();
                System.out.println("DataStorageUtilities: setExternalStorageDir: Storage Directory is: " + path);
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /* TODO add this back in?
    A private method that returns the File object (for the Directory) at storageDir

    private File getExternalStorageDir () {
        System.out.println("RecFrag: getExternalStorageDir: returning storage directory");
        return storageDir;
    } */


    /*
    A private method that returns the time stamp of the current time as a string.  This is to be
    used as a UID for file creation even if the user doesn't provide additional unique information
    */
    private static String getCurrentTimeStamp() {
        System.out.println("DataStorageUtilities: getCurrentTimeStamp: initialized");
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
    [TODO: this will not work if networked and multiple instances of the programme are simultaneously unless each user has UID]
    */
    public void setFieldRecordingFile (String fileFormat ) {
        System.out.println("DataStorageUtilities: setFieldRecordingFile: initialized");
        timeStamp = getCurrentTimeStamp();
        System.out.println("DataStorageUtilities: setFieldRecordingFile: Timestamp is: " + timeStamp);

        fieldRecordingDir = new File(storageDir,
                context.getString(R.string.test_file_folder)
                        + timeStamp);
        isFieldRecordingDirMade = fieldRecordingDir.mkdirs();
        if (fieldRecordingDir.exists()) {
            System.out.println("DataStorageUtilities: setFieldRecordingFile: fieldRecordingDir exists is: " + fieldRecordingDir.exists());
        }
        if (!isFieldRecordingDirMade) {
            System.out.println("DataStorageUtilities: setFieldRecordingFile: failed to make fieldRecordingDir");
        } else {
            try {
                String path = fieldRecordingDir.getCanonicalPath();
                System.out.println("DataStorageUtilities: setFieldRecordingFile: Field Recording Directory is: " + path);
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
        fieldRecordingFile = new File(fieldRecordingDir, context.getString(R.string.test_file_name) + "_" + timeStamp + fileFormat);
        try{
            isFieldRecordingFileMade = fieldRecordingFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (fieldRecordingFile.exists()) {
            System.out.println("DataStorageUtilities: setFieldRecordingFile: fieldRecordingFile exists is: " + fieldRecordingFile.exists());
        }
        if (!isFieldRecordingFileMade) {
            System.out.println("DataStorageUtilities: setFieldRecordingFile: failed to make fieldRecordingFile");
        } else {
            try {
                String path = fieldRecordingFile.getCanonicalPath();
                System.out.println("DataStorageUtilities: setFieldRecordingFile: Field Recording File is: " + path);
            } catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    A public function that returns the file object stored in fieldRecordingFile
     */

    public File getFieldRecordingFile() {
        try {
            System.out.println("DataStorageUtilities: getFieldRecordingFile: file name is: " + fieldRecordingFile.getCanonicalPath());
            return fieldRecordingFile;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }
}
