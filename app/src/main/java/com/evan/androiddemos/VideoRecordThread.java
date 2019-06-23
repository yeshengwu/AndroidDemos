package com.evan.androiddemos;

import android.media.projection.MediaProjection;

import com.evan.androiddemos.screenrecoder.AudioEncodeConfig;
import com.evan.androiddemos.screenrecoder.ScreenRecorder;
import com.evan.androiddemos.screenrecoder.VideoEncodeConfig;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by shidu on 18/6/14.
 */

public class VideoRecordThread implements Runnable {
    private static final String TAG = VideoRecordThread.class.getSimpleName();

    private Thread mThread;
    private boolean mStopped;
    private final LinkedBlockingQueue<AVPacket> mPacketQueue = new LinkedBlockingQueue<AVPacket>();

    private ScreenRecorder mRecorder;

    public VideoRecordThread(MediaProjection mediaProjection, VideoEncodeConfig video, AudioEncodeConfig audio,File output) {
        mThread = new Thread(this);
        mStopped = true;
        mRecorder = new ScreenRecorder(video, audio, 1, mediaProjection, output.getAbsolutePath());
//        mRecorder.setCallback(new ScreenRecorder.Callback() {
//            @Override
//            public void onStop(Throwable error) {
//
//            }
//
//            @Override
//            public void onStart() {
//
//            }
//
//            @Override
//            public void onRecording(long presentationTimeUs) {
//
//            }
//
//            @Override
//            public void onRecording(long presentationTimeUs, ByteBuffer byteBuffer) {
//
//            }
//        });
    }

    @Override
    public void run() {
        while (!mStopped) {

        }
    }

    public void start() {
        mStopped = false;
//        onStart();
        mThread.start();

        mRecorder.start();
    }

    public synchronized void stop() {
        if (!mStopped) {
            mStopped = true;
            mThread.interrupt();
            try {
                mThread.join();
            } catch (InterruptedException e) {
            }
            if (mRecorder != null) {
                mRecorder.quit();
            }
            mRecorder = null;

//            mPacketQueue.clear();
//            releaseDecoder();
//
//            onStop();
        }
    }

    private void initRecorder(){

    }
}
