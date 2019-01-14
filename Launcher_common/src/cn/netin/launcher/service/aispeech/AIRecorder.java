package cn.netin.launcher.service.aispeech;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

/**
 * FIXME (441000, 1, 16) is the only format that is guaranteed to work on all
 * devices
 * 
 * @author shun.zhang
 * 
 */
public class AIRecorder {

    private static String TAG = "EL AIRecorder";

    private static int CHANNELS = 1;
    private static int BITS = 16;
    private static int FREQUENCY = 16000; // sample rate
    private static int INTERVAL = 100; // callback interval

    private String latestPath = null; // latest wave file path

    private volatile boolean running = false;

    private ExecutorService workerThread;
    private Future<?> future = null;

    public static interface Callback {
        public void run(byte[] data, int size);
    }

    public AIRecorder() {
        workerThread = Executors.newFixedThreadPool(1);
    }

    /*
    public void release() {
        stop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        Log.d(TAG, "released");
    }
    */

    public int start(final String path, final Callback callback, final Context ctx) {

        stop();

        this.latestPath = path;

        Log.d(TAG, "starting");

        running = true;
        future = workerThread.submit(new Runnable() {

            @Override
            public void run() {

               RandomAccessFile file = null;

                int bufferSize = CHANNELS * FREQUENCY * BITS * INTERVAL / 1000 / 8;
                int minBufferSize = AudioRecord.getMinBufferSize(FREQUENCY, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (minBufferSize > bufferSize) {
                    bufferSize = minBufferSize;
                }

                byte buffer[] = new byte[bufferSize];

                AudioRecord recorder = null;

                try {
                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, FREQUENCY, AudioFormat.CHANNEL_IN_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, bufferSize);
                    
                    /* add by jili.gong try to adjust the Microphone volume  --begin*/
                    /*
                    AudioManager mAudioManager = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE); 
                                       
                    //int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, 0, 0);
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    //mAudioManager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
                    
                    //通话音量 
                    int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_VOICE_CALL ); 
                    int current = mAudioManager.getStreamVolume( AudioManager.STREAM_VOICE_CALL ); 
                    Log.d("VIOCE_CALL", "max : " + max + " current : " + current);
                    
                    //系统音量 
                    max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_SYSTEM ); 
                    current = mAudioManager.getStreamVolume( AudioManager.STREAM_SYSTEM ); 
                    Log.d("SYSTEM", "max : " + max + " current : " + current); 
                    
                    //铃声音量 
                    max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_RING ); 
                    current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING ); 
                    Log.d("RING", "max : " + max + " current : " + current); 
                    
                    //音乐音量 
                    max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ); 
                    current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC ); 
                    Log.d("MUSIC", "max : " + max + " current : " + current); 
                    
                    //提示声音音量 
                    max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM ); 
                    current = mAudioManager.getStreamVolume( AudioManager.STREAM_ALARM ); 
                    Log.d("ALARM", "max : " + max + " current : " + current);
                    
                    */
                    /* add by jili.gong try to adjust the Microphone volume  --end*/                    
                    
                    recorder.startRecording();

                    if (path != null) {
                        file = fopen(path);
                    }

                    Log.d(TAG, "started");

                    /*
                     * discard the beginning 100ms for fixing the transient
                     * noise bug shun.zhang, 2013-07-08
                     */
                    int discardBytes = CHANNELS * FREQUENCY * BITS * 100 / 1000 / 8;
                    while (discardBytes > 0) {
                        int requestBytes = buffer.length < discardBytes ? buffer.length : discardBytes;
                        int readBytes = recorder.read(buffer, 0, requestBytes);
                        if (readBytes > 0) {
                            discardBytes -= readBytes;
                            Log.d(TAG, "discard: " + readBytes);
                        } else {
                            break;
                        }
                    }
                    Log.d(TAG, "discardBytes=" + discardBytes);
                    while (running) {

                        if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                            break;
                        }

                        int size = recorder.read(buffer, 0, buffer.length);
                        if (size > 0) {
                            if (callback != null) {
                                callback.run(buffer, size);
                            }
                            if (file != null) {
                                fwrite(file, buffer, 0, size);
                            }
                        }

                    }

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    try {
                        running = false;
                        if (recorder != null) {
                            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                                recorder.stop(); // FIXME elapse 400ms
                            }
                            recorder.release();
                        }

                        Log.d(TAG, "record stoped");

                        if (file != null) {
                            fclose(file);
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        });

        return 0;
    }

    public int stop() {
        if (!running)
            return 0;

        Log.d(TAG, "stopping");
        running = false;
        if (future != null) {
            try {
                future.get();
            } catch (Exception e) {
                Log.e(TAG, "stop exception", e);
            }
        }

        return 0;
    }

    public int playback() {

        stop();

        if (this.latestPath == null) {
            return -1;
        }

        Log.d(TAG, "playback starting");

        running = true;
        future = workerThread.submit(new Runnable() {

            @Override
            public void run() {

                RandomAccessFile file = null;

                int bufferSize = CHANNELS * FREQUENCY * BITS * INTERVAL / 1000 / 8;
                int minBufferSize = AudioTrack.getMinBufferSize(FREQUENCY, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (minBufferSize > bufferSize) {
                    bufferSize = minBufferSize;
                }

                byte buffer[] = new byte[bufferSize];

                AudioTrack player = null;

                try {
                    
                    player = new AudioTrack(AudioManager.STREAM_MUSIC, FREQUENCY, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, buffer.length, AudioTrack.MODE_STREAM);

                    file = new RandomAccessFile(latestPath, "r");
                    file.seek(44);

                    player.play();

                    Log.d(TAG, "playback started");

                    while (running) {

                        int size = file.read(buffer, 0, buffer.length);
                        if (size == -1) {
                            break;
                        }

                        player.write(buffer, 0, size);
                    }

                    player.flush();

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    try {
                        running = false;

                        if (player != null) {
                            if (player.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
                                player.stop();
                            }
                            player.release();
                        }

                        Log.d(TAG, "playback stoped");

                        if (file != null) {
                            file.close();
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }

            }
        });

        return 0;
    }

    private RandomAccessFile fopen(String path) throws IOException {
        File f = new File(path);

		if (f.exists()) {
			f.delete();
		} else {
			File parentDir = f.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
		}

        RandomAccessFile file = new RandomAccessFile(f, "rw");

        /* RIFF header */
        file.writeBytes("RIFF"); // riff id
        file.writeInt(0); // riff chunk size *PLACEHOLDER*
        file.writeBytes("WAVE"); // wave type

        /* fmt chunk */
        file.writeBytes("fmt "); // fmt id
        file.writeInt(Integer.reverseBytes(16)); // fmt chunk size
        file.writeShort(Short.reverseBytes((short) 1)); // format: 1(PCM)
        file.writeShort(Short.reverseBytes((short) CHANNELS)); // channels: 1
        file.writeInt(Integer.reverseBytes(FREQUENCY)); // samples per second
        file.writeInt(Integer.reverseBytes((int) (CHANNELS * FREQUENCY * BITS / 8))); // BPSecond
        file.writeShort(Short.reverseBytes((short) (CHANNELS * BITS / 8))); // BPSample
        file.writeShort(Short.reverseBytes((short) (CHANNELS * BITS))); // bPSample

        /* data chunk */
        file.writeBytes("data"); // data id
        file.writeInt(0); // data chunk size *PLACEHOLDER*

        Log.d(TAG, "wav path: " + path);
        return file;
    }

    private void fwrite(RandomAccessFile file, byte[] data, int offset, int size) throws IOException {
        file.write(data, offset, size);
        //Log.d(TAG, "fwrite: " + size);
    }

    private void fclose(RandomAccessFile file) throws IOException {
        try {
            file.seek(4); // riff chunk size
            file.writeInt(Integer.reverseBytes((int) (file.length() - 8)));

            file.seek(40); // data chunk size
            file.writeInt(Integer.reverseBytes((int) (file.length() - 44)));

            Log.d(TAG, "wav size: " + file.length());

        } finally {
            file.close();
        }
    }
}