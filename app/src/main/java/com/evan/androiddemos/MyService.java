package com.evan.androiddemos;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.evan.androiddemos.screenrecoder.AudioEncodeConfig;
import com.evan.androiddemos.screenrecoder.ScreenRecorder;
import com.evan.androiddemos.screenrecoder.VideoEncodeConfig;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by shidu on 18/6/14.
 */

public class MyService extends Service {
    private static final String TAG = MyService.class.getSimpleName();

    private SocketService mSocketService;

    private final LinkedBlockingQueue<AVPacket> mPacketQueue = new LinkedBlockingQueue<AVPacket>();

    private ScreenRecorder mRecorder;

    @Override
    public void onCreate() {
        super.onCreate();

        createfile();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG,"onStartCommand");
        if (mRecorder == null) return START_STICKY;
        Log.e(TAG,"onStartCommand");
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        stopRecorder();
        return super.onUnbind(intent);
    }

    public void stopRecorder() {
        Log.e(TAG, "stopRecorder");
        if (mRecorder != null) {
            mRecorder.quit();
        }
        mRecorder = null;
    }

    private final IBinder mBinder = new LocalBinder();

    public void setDataHandler(SocketService socketService){
        this.mSocketService = socketService;
    }

    public void connect(MediaProjection mediaProjection, VideoEncodeConfig video, AudioEncodeConfig audio, File output) {
        mRecorder = new ScreenRecorder(video, audio, 1, mediaProjection, output.getAbsolutePath());
        mRecorder.setCallback(new ScreenRecorder.Callback() {
            @Override
            public void onStop(Throwable error) {
                Log.e(TAG,"onStop");
                if (error != null){
                    error.printStackTrace();
                }
            }

            @Override
            public void onStart() {
                Log.e(TAG,"onStart");
            }

            @Override
            public void onRecording(long presentationTimeUs) {
                Log.e(TAG,"onRecording");
            }

            @Override
            public void onRecording(long presentationTimeUs, byte[] bytes) {
                try {
                    outputStream.write(bytes, 0, bytes.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mSocketService.request(presentationTimeUs, bytes);
            }
        });
        mRecorder.start();
    }

    private static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test1.h264";
    private BufferedOutputStream outputStream;
    private FileOutputStream outStream;

    private void createfile(){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + SystemClock.elapsedRealtime()+".h264";
        File file = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
//            File file1 = getApplicationContext().getExternalCacheDir();
            File file1 = new File("sdcard/Android/data/com.hy.changxian/cache");
            if (file1 != null) {
                file1 = FileUtils.getFile(file1, "test");
                file1.mkdirs();
                file = FileUtils.getFile(file1,1+".h264");
                Log.e("evan","filepateh="+file.getAbsolutePath());
            }
        }

        if(file.exists()){
            file.delete();
        }

        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
