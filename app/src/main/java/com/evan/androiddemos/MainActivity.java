package com.evan.androiddemos;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.media.MediaCodecInfo;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.evan.androiddemos.screenrecoder.AudioEncodeConfig;
import com.evan.androiddemos.screenrecoder.Utils;
import com.evan.androiddemos.screenrecoder.VideoEncodeConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static com.evan.androiddemos.screenrecoder.ScreenRecorder.AUDIO_AAC;
import static com.evan.androiddemos.screenrecoder.ScreenRecorder.VIDEO_AVC;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_MEDIA_PROJECTION = 1;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;

    private MediaCodecInfo[] mAvcCodecInfos; // avc codecs
    private MediaCodecInfo[] mAacCodecInfos; // aac codecs

    private MyService mBluetoothLeService;
    private SocketService mDataSocket;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaProjectionManager = (MediaProjectionManager) getApplicationContext().getSystemService(MEDIA_PROJECTION_SERVICE);
        Utils.findEncodersByTypeAsync(VIDEO_AVC, new Utils.Callback() {
            @Override
            public void onResult(MediaCodecInfo[] infos) {
                logCodecInfos(infos, VIDEO_AVC);
                mAvcCodecInfos = infos;
            }
        });
        Utils.findEncodersByTypeAsync(AUDIO_AAC, new Utils.Callback() {
            @Override
            public void onResult(MediaCodecInfo[] infos) {
                logCodecInfos(infos, AUDIO_AAC);
                mAacCodecInfos = infos;
            }
        });

        mDataSocket = new SocketService(new IProtocolListener() {
            @Override
            public void onConnected() {

            }

            @Override
            public void onError(int code, String msg) {
                // TODO stopRecord
            }

            @Override
            public int onClose(boolean hasError) {
                // TODO stopRecord
                return 0;
            }

            @Override
            public int onResponse(int code, String errorMsg, long serviceId, int seq, String data) {
                MainActivity.this.startCaptureIntent();
                return 0;
            }

            @Override
            public int onMessage(long serviceId, String msg) {
                return 0;
            }
        });

        mDataSocket.connected("192.168.0.164", 9000, false);

//        MainActivity.this.startCaptureIntent(); //Test

        Canvas canvas = new Canvas();
//        canvas.drawCircle
//        canvas.drawRoun

        TextView helloTv = (TextView) findViewById(R.id.tv_hello);
        helloTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("evan","click1");
            }
        });
        helloTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("evan","click2");
            }
        });

//        BuildConfig.VERSION_CODE;

//        String a = "";
//        String a = null;
//        int width =  Integer.parseInt(a);
//        Log.e("evan","width = "+width);
//        RecyclerView

        TestMain testMain = new TestMain(new IADSizeSub() {
            @Override
            public int getSub() {
                return 0;
            }

            @Override
            public int getWidth() {
                return 0;
            }

            @Override
            public int getHeight() {
                return 0;
            }

            @Override
            public int getMode() {
                return 0;
            }
        });
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((MyService.LocalBinder) service).getService();

            // Automatically connects to the device upon successful start-up initialization.

            VideoEncodeConfig video = createVideoConfig();
            AudioEncodeConfig audio = createAudioConfig(); // audio can be null
            if (video == null) {
                Toast.makeText(MainActivity.this, "Create ScreenRecorder failure", Toast.LENGTH_SHORT).show();
                mMediaProjection.stop();
                return;
            }

            File dir = getSavingDir();
            if (!dir.exists() && !dir.mkdirs()) {
//                cancelRecorder();
                return;
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US);
            final File file = new File(dir, "Screen-" + format.format(new Date())
                    + "-" + video.width + "x" + video.height + ".mp4");
            Log.d("@@", "Create recorder with :" + video + " \n " + audio + "\n " + file);

            mBluetoothLeService.setDataHandler(mDataSocket);
            mBluetoothLeService.connect(mMediaProjection, video, audio, file);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        MainActivity.this.startCaptureIntent();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
//            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mMediaProjection == null) {
                Log.e("@@", "media projection is null");
                return;
            }


//            //start record thread.
//            VideoRecordThread recordThread = new VideoRecordThread(mediaProjection, video, audio, file);
//            recordThread.start();
//            moveTaskToBack(true);

            Intent gattServiceIntent = new Intent(this, MyService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
            startService(gattServiceIntent);
        }
    }

    private AudioEncodeConfig createAudioConfig() {
//        if (!mAudioToggle.isChecked()) return null;
//        String codec = getSelectedAudioCodec();
//        if (codec == null) {
//            return null;
//        }
//        int bitrate = getSelectedAudioBitrate();
//        int samplerate = getSelectedAudioSampleRate();
//        int channelCount = getSelectedAudioChannelCount();
//        int profile = getSelectedAudioProfile();
//
//        return new AudioEncodeConfig(codec, AUDIO_AAC, bitrate, samplerate, channelCount, profile);
        return null;
    }

    private VideoEncodeConfig createVideoConfig() {
        final String codec = getSelectedVideoCodec(); //默认选了第0个
        if (codec == null) {
            // no selected codec ??
            return null;
        }
        int width = 640;
        int height = 1136;
        int framerate = 30;
        int iframe = 1;
        int bitrate = 2000*000; //bps
        MediaCodecInfo.CodecProfileLevel profileLevel = Utils.toProfileLevel(evanGetProfile());
        Log.e("evan","createVideoConfig profileLevel = "+profileLevel);
        return new VideoEncodeConfig(width, height, bitrate,
                framerate, iframe, codec, VIDEO_AVC, profileLevel);
    }

    private String getSelectedVideoCodec() {
        return codecInfoNames(mAvcCodecInfos)[0];
    }

    private static File getSavingDir() {
        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
                "ScreenCaptures");
    }

    private String evanGetProfile(){
        MediaCodecInfo codec = getVideoCodecInfo(getSelectedVideoCodec());
        Log.e("evan"," evanGetProfile "+ codec.getName());

        MediaCodecInfo.CodecCapabilities capabilities = codec.getCapabilitiesForType(VIDEO_AVC);

        MediaCodecInfo.CodecProfileLevel[] profiles = capabilities.profileLevels;
        if (profiles == null || profiles.length == 0) {
            return null;
        }

        String[] profileLevels = new String[profiles.length + 1];
        profileLevels[0] = "Default";
        for (int i = 0; i < profiles.length; i++) {
            profileLevels[i + 1] = Utils.avcProfileLevelToString(profiles[i]);
            Log.e("evan"," profileLevels[i + 1] "+ profileLevels[i + 1]);

        }
        return profileLevels[1];
    }

    private MediaCodecInfo getVideoCodecInfo(String codecName) {
        if (codecName == null) return null;
        if (mAvcCodecInfos == null) {
            mAvcCodecInfos = Utils.findEncodersByType(VIDEO_AVC);
        }
        MediaCodecInfo codec = null;
        for (int i = 0; i < mAvcCodecInfos.length; i++) {
            MediaCodecInfo info = mAvcCodecInfos[i];
            if (info.getName().equals(codecName)) {
                codec = info;
                break;
            }
        }
        if (codec == null) return null;
        return codec;
    }

    private void startCaptureIntent() {
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        Intent dd = new Intent();
        dd.setClass(this,int.class);
        startActivityForResult(captureIntent, REQUEST_MEDIA_PROJECTION);
    }

    private static String[] codecInfoNames(MediaCodecInfo[] codecInfos) {
        String[] names = new String[codecInfos.length];
        for (int i = 0; i < codecInfos.length; i++) {
            names[i] = codecInfos[i].getName();
            Log.e("evan","codecInfoNames item = "+ names[i]);
        }
        return names;
    }

    /**
     * Print information of all MediaCodec on this device.
     */
    private static void logCodecInfos(MediaCodecInfo[] codecInfos, String mimeType) {
        for (MediaCodecInfo info : codecInfos) {
            StringBuilder builder = new StringBuilder(512);
            MediaCodecInfo.CodecCapabilities caps = info.getCapabilitiesForType(mimeType);
            builder.append("Encoder '").append(info.getName()).append('\'')
                    .append("\n  supported : ")
                    .append(Arrays.toString(info.getSupportedTypes()));
            MediaCodecInfo.VideoCapabilities videoCaps = caps.getVideoCapabilities();
            if (videoCaps != null) {
                builder.append("\n  Video capabilities:")
                        .append("\n  Widths: ").append(videoCaps.getSupportedWidths())
                        .append("\n  Heights: ").append(videoCaps.getSupportedHeights())
                        .append("\n  Frame Rates: ").append(videoCaps.getSupportedFrameRates())
                        .append("\n  Bitrate: ").append(videoCaps.getBitrateRange());
                if (VIDEO_AVC.equals(mimeType)) {
                    MediaCodecInfo.CodecProfileLevel[] levels = caps.profileLevels;

                    builder.append("\n  Profile-levels: ");
                    for (MediaCodecInfo.CodecProfileLevel level : levels) {
                        builder.append("\n  ").append(Utils.avcProfileLevelToString(level));
                    }
                }
                builder.append("\n  Color-formats: ");
                for (int c : caps.colorFormats) {
                    builder.append("\n  ").append(Utils.toHumanReadable(c));
                }
            }
            MediaCodecInfo.AudioCapabilities audioCaps = caps.getAudioCapabilities();
            if (audioCaps != null) {
                builder.append("\n Audio capabilities:")
                        .append("\n Sample Rates: ").append(Arrays.toString(audioCaps.getSupportedSampleRates()))
                        .append("\n Bit Rates: ").append(audioCaps.getBitrateRange())
                        .append("\n Max channels: ").append(audioCaps.getMaxInputChannelCount());
            }
            Log.i("@@@", builder.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.e("evan","onDestroy");
        if (mBluetoothLeService != null) {
            mBluetoothLeService.stopRecorder();
        }

//        if (mServiceConnection!=null){
//            unbindService(mServiceConnection);
//        }
    }
}
