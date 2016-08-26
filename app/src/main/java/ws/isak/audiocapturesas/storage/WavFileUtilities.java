package ws.isak.audiocapturesas.storage;

import ws.isak.audiocapturesas.preferences.ConfigurationParameters;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


/**
 * Created by isakherman on 8/16/16.  This class contains two methods, the first constructs the
 * WAV header file of 44 bytes.  The second is used to construct a .wav file from this header and
 * the raw audio in a .pcm file recorded from the microphone.  WAV file descriptor comes from Craig.
 * http://soundfile.sapp.org/doc/WaveFormat/
 */
public class WavFileUtilities {

    private ConfigurationParameters configParams;

    /*
    This method constructs the static WAV file header taking as input from the call to it in method
    wavToPCM the configuration parameters for sample rate, bits per sample and number of channels
    along with the length in bytes of the pcmFile generated during live recording.
    */

    public static byte[] getWAVHeader(int sRate, int bitsPerSample, int audioDataLength, int numChans) {
        byte[] header = new byte[44];
        //NOTE all fields are little-endian unless they contain characters

        /* RIFF HEADER */
        //ChunkID:
        header[0] = 'R';
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        //ChunkSize = size of entire file - 8 bytes for ChunkID and ChunkSize
        int totalLength = audioDataLength + 36; //44 bytes for header - 8 bytes
        header[4] = (byte) (totalLength & 0xff); //mask all but LSB
        header[5] = (byte) ((totalLength >> 8) & 0xff); //shift right and mask all but LSB (now 2nd LSB)
        header[6] = (byte) ((totalLength >> 16) & 0xff); //etc
        header[7] = (byte) ((totalLength >> 24) & 0xff);
        //Format:
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';

        /* "fmt " (note space) SUBCHUNK */
        //SubChunk1ID:
        header[12] = 'f';
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        //SubChunk1Size:
        header[16] = 16;  //16 for PCM (remember little-endian)
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //AudioFormat:
        header[20] = 1;  //1 for PCM (linear quantization; no compression)
        header[21] = 0;
        //NumChannels:
        header[22] = (byte) numChans;
        header[23] = 0;
        //SampleRate:
        header[24] = (byte) (sRate & 0xff);
        header[25] = (byte) ((sRate >> 8) & 0xff);
        header[26] = (byte) ((sRate >> 16) & 0xff);
        header[27] = (byte) ((sRate >> 24) & 0xff);
        //ByteRate = SampleRate*NumChannels*BitsPerSample/8:
        int byteRate = numChans * sRate * bitsPerSample / 8;
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        //BlockAlign = NumChannels*BitsPerSample/8;
        header[32] = (byte) (2 * 16 / 8);
        header[33] = 0;
        //BitsPerSample:
        header[34] = (byte) bitsPerSample;
        header[35] = 0;

        /* "data" SUBCHUNK */
        //SubChunk2ID:
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        //SubChunk2Size:
        header[40] = (byte) (audioDataLength & 0xff);
        header[41] = (byte) ((audioDataLength >> 8) & 0xff);
        header[42] = (byte) ((audioDataLength >> 16) & 0xff);
        header[43] = (byte) ((audioDataLength >> 24) & 0xff);
        //actual data will go here
        return header;
    }


    /*
    The wavFromPCM method returns a byte [] array of a complete WAV file from the audio byte []
    stored in file.pcm with the appropriate WAV header.
    */

    public byte [] wavFromPCM (File pcmFile) {

        byte[] wavHeader;
        byte[] pcmBytes = null;
        byte[] wavBytes = null;
        int dataLength = 0;

        try {
            pcmBytes = FileUtils.readFileToByteArray(pcmFile);
            System.out.println("WavFileUtilities: wavFromPCM: pcmFile.length is: " + pcmBytes.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dataLength = pcmBytes.length;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        wavHeader = getWAVHeader(configParams.SAMPLE_RATE,
                configParams.BITS_PER_SAMPLE,
                dataLength,
                configParams.NUM_CHANNELS);
        wavBytes = new byte[configParams.AUDIO_HEADER_LENGTH + pcmBytes.length];
        System.arraycopy(wavHeader, 0, wavBytes, 0, configParams.AUDIO_HEADER_LENGTH);
        System.arraycopy(pcmBytes, 0, wavBytes, configParams.AUDIO_HEADER_LENGTH, dataLength);

        return wavBytes;
    }
}