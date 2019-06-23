package com.evan.androiddemos;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.LinkedBlockingQueue;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by shidu on 18/1/10.
 */

public class SocketService {
    private static final String TAG = "SocketService";
    private static final boolean D = true;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    // sendThread request queue.
    private final LinkedBlockingQueue<AVPacket> mPacketQueue = new LinkedBlockingQueue<AVPacket>();
    private static int REQUEST = 1;
    private static int RESPONSE = 2;
    private static int MESSAGE = 3;

    private static final int HEART_BEAT_DELAY = 30 * 1000; //30s
    private static final byte[] BYTE_ZERO = {0};

    private HandlerThread mHandlerThread;
    private Handler mNonUIHandler;

    private ConnectedThread mConnectedThread;
    private SendThread mSendThread;
    private int mState;

    private final IProtocolListener mListener;

    public SocketService(IProtocolListener listener) {
        mState = STATE_NONE;
        mListener = listener;
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        Log.d(TAG, "setState() " + mState + " -> " + state);
        mState = state;
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    /**
     * Start the ConnectedThread to begin managing a connection
     */
    public synchronized void connected(String ip, int port, final boolean secure) {
        if (D) Log.d(TAG, "connected, Socket Type:" + secure);

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSendThread != null) {
            mSendThread.cancel();
            mSendThread.interrupt();
            mSendThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(ip, port, secure);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
    }

    public synchronized void sendThreadStart(DataOutputStream outputStream) {
        if (D) Log.d(TAG, "sendThreadStart outputStream = " + outputStream);

        // Start the thread to manage the connection and perform transmissions
        mSendThread = new SendThread(outputStream);
        mSendThread.start();

        setState(STATE_CONNECTED);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D) Log.d(TAG, "stop");

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread.interrupt();
            mConnectedThread = null;
        }

        if (mSendThread != null) {
            mSendThread.cancel();
            mSendThread.interrupt();
            mSendThread = null;
        }

        setState(STATE_NONE);
    }

    public void request(long presentationTimeUs, byte[] bytes) {
        Log.e(TAG,"request ps = "+presentationTimeUs+" bytes.size = "+bytes.length);
        mPacketQueue.add(new AVPacket(presentationTimeUs, bytes, bytes.length));
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        if (D) Log.e(TAG, "connectionFailed");
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        if (D) Log.e(TAG, "connectionLost");
    }

    private class SendThread extends Thread {
        private DataOutputStream mOutputStream;
        private boolean mStopped;

        public SendThread(DataOutputStream outputStream) {
            mOutputStream = outputStream;
            Log.i(TAG, "SendThread () = mOutputStream" + mOutputStream);
        }

        public void run() {
            Log.i(TAG, "BEGIN SendThread");

            if (mListener != null) {
                mListener.onConnected();
            }

//            startHeartbeatTimer();
            sendHeartbeat();

            while (!mStopped) {
                try {
                    AVPacket packet = mPacketQueue.take();
                    send(packet);
                } catch (InterruptedException e) {
                    if (D) {
                        Log.e(TAG, "SendThread interrupted.");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "SendThread failed. msg: ");
                    e.printStackTrace();
                }
            }
        }

        private void send(AVPacket packet) {
            try {
                if (D) Log.d(TAG, "SendThread outputStream = " + mOutputStream);
                int bufferSize = 4+ 1 + 8+ 8+8+ 4+packet.data.length;

                ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                byteBuffer.putInt(bufferSize); //bufferSize
                byteBuffer.put((byte)1); //video type
                byteBuffer.putLong(System.currentTimeMillis() - ChangxianApplication.sInstance.deltaTimeProtocal);//本地时间ts
                byteBuffer.putLong(0); //placeholder for server
                byteBuffer.putInt(packet.size);
                byteBuffer.putLong(packet.pts);
                byteBuffer.put(packet.data);
                byteBuffer.flip();

//                mOutputStream.writeByte(1); //video type
//                mOutputStream.writeLong(System.currentTimeMillis());//本地时间ts
//                mOutputStream.writeLong(0);//placeholder for server
//
//                mOutputStream.writeInt(packet.size); //4byte 包头
//                mOutputStream.writeLong(packet.pts);
//                mOutputStream.write(packet.data);

                byte[] bytes = new byte[byteBuffer.remaining()]; //ByteBuffer转化为byte[]
                Log.i(TAG, "Sent bytes = " + bytes.length);
                byteBuffer.get(bytes);
                mOutputStream.write(bytes);
                mOutputStream.flush();
            } catch (IOException e) {
                Log.e(TAG, "SendThread send failed. msg: ");
                e.printStackTrace();
            }
        }

        private void sendHeartbeat() {
            if (D) Log.d(TAG, "send BYTE_ZERO.outputStream = " + mOutputStream);
            try {
//                mOutputStream.writeByte(0); //time type
//                if (D) Log.d(TAG, "send System.currentTimeMillis() = " + System.currentTimeMillis());
//                mOutputStream.writeLong(System.currentTimeMillis());//本地时间ts
//                mOutputStream.flush();

                int bufferSize = 4+ 1 + 8+ 8 ;
                ByteBuffer byteBuffer = ByteBuffer.allocate(bufferSize);
                byteBuffer.putInt(bufferSize); //bufferSize
                byteBuffer.put((byte)0); //time type
                byteBuffer.putLong(System.currentTimeMillis());//本地时间ts
                byteBuffer.putLong(0); //placeholder for server
                byteBuffer.flip();

                Log.i(TAG, "sendHeartbeat byteBuffer.remaining() = " + byteBuffer.remaining());
                byte[] bytes = new byte[byteBuffer.remaining()]; //ByteBuffer转化为byte[]
                Log.i(TAG, "sendHeartbeat " + bytes.length);
                byteBuffer.get(bytes);
                mOutputStream.write(bytes);
                mOutputStream.flush();
            } catch (IOException e) {
                //ignore. per 30s send.
                if (D) Log.d(TAG, "send BYTE_ZERO. IOException");
//                e.printStackTrace();
            }
        }

        public void cancel() {
            mStopped = true;
            stopHeartbeat();
            try {
                mOutputStream.close();
                Log.i(TAG, "send cancel = mOutputStream = " + mOutputStream);
            } catch (IOException e) {
                Log.e(TAG, "close() of SendThread socket failed", e);
            }
        }
    }

    private void startHeartbeatTimer() {
        if (D) Log.d(TAG, "startHeartbeatTimer.");
        if (mSendThread == null) return;

        mHandlerThread = new HandlerThread("Heartbeat HandlerThread");
        mHandlerThread.start();
        mNonUIHandler = new Handler(mHandlerThread.getLooper());
        mNonUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mSendThread.sendHeartbeat();
                mNonUIHandler.postDelayed(this, HEART_BEAT_DELAY);
            }
        });
    }

    private void stopHeartbeat() {
        if (mNonUIHandler == null) return;
        mHandlerThread.quit();
        mNonUIHandler.removeCallbacksAndMessages(null);
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private Socket mmSocket;
        private DataInputStream mmInStream;
        private DataOutputStream mmOutStream;

        private String ip;
        private int port;
        private boolean secure;

        private boolean mStopped;

        public ConnectedThread(String ip, int port, boolean secure) {
            Log.d(TAG, "create ConnectedThread: " + secure);
            this.ip = ip;
            this.port = port;
            this.secure = secure;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");

            if (!connect(secure)) {
                if (mListener != null) {
                    mListener.onError(-1, "network error");
                }
                return;
            }

            // Start the sendThread thread.
            sendThreadStart(mmOutStream);

            int BUFFER_SIZE = 8 * 1024;
            byte[] buffer = new byte[BUFFER_SIZE];
            byte[] framebuffer = new byte[0];

            int h264Read = 0;
            int frameOffset = 0;
            int count;

            // Keep listening to the InputStream while connected
            while (!mStopped) {
                try {
                    // Read from the InputStream.
                    int offset = 0;
                    int type = mmInStream.readByte();

                    Log.i(TAG, "type = " + type);
                    if (type == 0) {
                        long customeTime =mmInStream.readLong();
                        long serverTime =mmInStream.readLong();
                        Log.i(TAG, "System.currentTimeMillis() = " + System.currentTimeMillis()+" customeTime "+customeTime+" serverTime = "+serverTime);
                        ChangxianApplication.sInstance.deltaTimeProtocal = (System.currentTimeMillis() + customeTime) / 2L - serverTime;
                        Log.i(TAG, "ChangxianApplication.sInstance.deltaTimeProtocal ="+ChangxianApplication.sInstance.deltaTimeProtocal);
                    } else if(type == 1) {
                        if (mListener != null) {
                            mListener.onResponse(0, "null", 0, 0, "");
                        }
                    }

                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);

                    connectionLost();
                    break;
                }
            }
        }

        private boolean connect(boolean secure) {
            if (secure) {
                mmSocket = startHandShake();
                if (mmSocket == null) return false;
            } else {
                try {
                    mmSocket = new Socket(ip, port);
//                    InetAddress serverAddr = InetAddress.getByName(ip);
//                    mmSocket = new Socket(serverAddr, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            try {
                mmInStream = new DataInputStream(mmSocket.getInputStream());
                mmOutStream = new DataOutputStream(mmSocket.getOutputStream());

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }


        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }

        public void cancel() {
            mStopped = true;
            boolean hasError = false;
            try {
                if (mmInStream != null) {
                    mmInStream.close();
                }
                if (mmOutStream != null) {
                    mmOutStream.close();
                }
                if (mmSocket != null) {
                    mmSocket.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
                hasError = true;
            }
            if (mListener != null) {
                mListener.onClose(hasError);
            }
        }

        private SSLSocket startHandShake() {
            try {
                // 初始化key manager factory
                KeyManagerFactory kmf = null;
                kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                kmf.init(null, null);

                // 初始化ssl context
                SSLContext context = SSLContext.getInstance("TLS");
                context.init(kmf.getKeyManagers(), new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());

                // 监听和接收客户端连接
                SSLSocketFactory factory = context.getSocketFactory();
                SSLSocket socket = null;
//                socket = (SSLSocket) factory.createSocket("paipai.idianyun.cn", 1219);
                socket = (SSLSocket) factory.createSocket(ip, port);
                socket.startHandshake();
                Log.i(TAG, "createSocket = " + socket);

                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            return null;
        }

        class MyHostnameVerifier implements HostnameVerifier {

            @Override
            public boolean verify(String hostname, SSLSession session) {
                session.getPeerHost();
                return false;
            }
        }

        class MyX509TrustManager implements X509TrustManager {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

        }
    }

}


