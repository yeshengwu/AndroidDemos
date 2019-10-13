package com.evan.androiddemos;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Button;

import com.evan.androiddemos.http.HttpServer;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.android.AndroidUpnpServiceImpl;
import org.fourthline.cling.android.FixedAndroidLogHandler;
import org.fourthline.cling.registry.RegistryListener;
import org.fourthline.cling.support.model.PortMapping;

import java.net.UnknownHostException;

public class UPNPActivity extends Activity {
    private AndroidUpnpService upnpService;
    private Button mUpnpText;

    private HttpServer mHttpServer;

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.e("evan", "onServiceConnected.");
            upnpService = (AndroidUpnpService) service;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        doPortForwarding();
                    } catch (UnknownHostException e) {
                        Log.e("evan", "doPortForwarding fail");
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };

    private void doPortForwarding() throws UnknownHostException {

        PortMapping[] desiredMapping = new PortMapping[2];
        desiredMapping[0] = new PortMapping(9000, DeviceUtil.getIPAddress(true),
                PortMapping.Protocol.TCP, " DATA_SERVER_PORT POT Forwarding");

        desiredMapping[1] = new PortMapping(9001, DeviceUtil.getIPAddress(true),
                PortMapping.Protocol.TCP, " HTTP_SERVER_PORT POT Forwarding");


        RegistryListener registryListener = new PortEvanListener(desiredMapping,mHandler);
        upnpService.getRegistry().addListener(registryListener);

        upnpService.getControlPoint().search();

        Log.e("evan", "doPortForwarding.");
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    try {
                        DefaultRPCDispatcher dispatcher = new DefaultRPCDispatcher(null);
                        mHttpServer = new HttpServer(9001, dispatcher);
                        mHttpServer.start();

//                        Message message = new Message();
//                        message.what = 1;
//                        mHandler.sendMessage(message);
                        mUpnpText.setText("开启服务成功");

                    } catch (Exception e) {
                        e.printStackTrace();
//                        Message message = new Message();
//                        message.what = 2;
//                        mHandler.sendMessage(message);
                        mUpnpText.setText("开启服务失败");
                    }
                    break;

                case 2:
                    mUpnpText.setText("开启服务失败");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upnp);

        mUpnpText = (Button)findViewById(R.id.tv_upnp);
        mUpnpText.setEnabled(false);

        // Fix the logging integration between java.util.logging and Android internal logging
        org.seamless.util.logging.LoggingUtil.resetRootHandler(
                new FixedAndroidLogHandler()
        );

//        // This will start the UPnP service if it wasn't already started
        bindService(
                new Intent(this, AndroidUpnpServiceImpl.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        upnpService.get().shutdown();
        // This will stop the UPnP service if nobody else is bound to it
        unbindService(serviceConnection);
//        getActivity().getApplication().onTerminate();
    }

}
