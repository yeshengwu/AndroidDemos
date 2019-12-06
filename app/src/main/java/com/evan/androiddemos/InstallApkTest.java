package com.evan.androiddemos;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdk;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

public class InstallApkTest extends Activity {
    private static final String TAG = "InstallApkTest";

    private String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private final int PERMS_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_install_apk);

        findViewById(R.id.tv_browser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser(InstallApkTest.this, "https://alissl.ucdl.pp.uc.cn/fs08/2019/04/12/3/2_4ccf97bac1682ced4b753c49f6d76870.apk?yingid=wdj_web&fname=TXT%E5%85%8D%E8%B4%B9%E5%85%A8%E6%9C%AC%E5%B0%8F%E8%AF%B4&pos=wdj_web%2Fdetail_normal_dl%2F0&appid=7848612&packageid=500506460&apprd=7848612&iconUrl=http%3A%2F%2Fandroid-artworks.25pp.com%2Ffs08%2F2019%2F04%2F12%2F2%2F2_5a4cbe858f200dcdd8f664e640c4c0bc_con.png&pkg=com.mfqbxs.reader&did=74072dfa74c7c9247a1d76fb0e5ed995&vcode=105&md5=b015d02e4e8859942fc173bdfb76034c");
            }
        });

        findViewById(R.id.tv_system).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1 &&
                        PackageManager.PERMISSION_GRANTED != checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {//Android 6.0以上版本需要获取临时权限
                    requestPermissions(perms, PERMS_REQUEST_CODE);
                } else {
                    String filePath = copyFile();//首先把assets下的apk文件复制到sdcard上
                    installApk(filePath);
                }

            }
        });

        Log.e("evan","Build.MANUFACTURER = " + Build.MANUFACTURER); // Build.MANUFACTURER = HUAWEI

        String memo = "Created-Time: 2019-05-09 18:27:41.957\n" +
                "Created-By: YaFix(1.1)\n" +
                "YaPatchType: 2\n" +
                "VersionName: 2.1\n" +
                "VersionCode: 4\n" +
                "From: 2.0.4-base\n" +
                "To: 2.0.4-patch";
        BufferedReader br = new BufferedReader(new StringReader(memo));
        String line = null;
        try {
            while ((line = br.readLine()) != null){
                Log.e("evan","line = "+line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String a = "{\n" +
                "    \"forceUpdate\": [\n" +
                "        \"test1\",\n" +
                "        \"test2\"\n" +
                "    ]\n" +
                "}";
        try {
            JSONObject expandObject = new JSONObject(a);
            Log.e("evan","expandObject = "+expandObject);
            Log.e("evan","expandObject = "+expandObject.optJSONArray("forceUpdate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String b = "{\"forceUpdate\":[\"test1\",\"test2\"]}";
        try {
            JSONObject compressObject = new JSONObject(b);
            Log.e("evan","compressObject = "+compressObject);
            Log.e("evan","compressObject = "+compressObject.optJSONArray("forceUpdate"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String d = "";

        try {
            JSONObject json = new JSONObject("{\"code\":0,\"msg\":\"success\",\"data\":{\"order_no\":\"201905211009274682115\",\"order_status\":1,\"chapter_ids\":[\"476796\"]}}");
            String dataStr = json.getString("data");
            Log.e("evan","dataStr:"+dataStr);

            JSONObject dataJsonObj = json.getJSONObject("data");
            Log.e("evan","dataJsonObj:"+dataJsonObj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        getDeviceIds(InstallApkTest.this);

        startActivity(new Intent(this, TestActivity.class));

    }

    /**
     * 安装apk
     *
     * @param fileSavePath
     */
    private void installApk(String fileSavePath) {
        File file = new File(fileSavePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判断版本大于等于7.0
            // "sven.com.fileprovider.fileprovider"即是在清单文件中配置的authorities
            // 通过FileProvider创建一个content类型的Uri
            data = FileProvider.getUriForFile(this, getPackageName()+".fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);// 给目标应用一个临时授权
        } else {
            data = Uri.fromFile(file);
        }
        Log.i("installApp", "_uri = " + data.toString());
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    /**
     * 如果sdcard没有文件就复制过去
     */
    private String copyFile() {
        AssetManager assetManager = this.getAssets();
//        String newFilePath = Environment.getExternalStorageDirectory() + "/mwh/app-release.apk";
        String newFilePath = Environment.getExternalStorageDirectory() + "/mwh/app-release.apk";
        String Path = Environment.getExternalStorageDirectory() + "/mwh";
        try {
            File file1 = new File(Path);

            if (!file1.exists()) {
                file1.mkdir();
            }

            File file = new File(newFilePath);
            if (!file.exists()) {//文件不存在才复制
                InputStream in = assetManager.open("app-release.apk");
                OutputStream out = new FileOutputStream(newFilePath);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.flush();
                out.close();
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return newFilePath;
    }


    /**
     * 调用第三方浏览器打开
     *
     * @param context
     * @param url     要浏览的资源地址
     */
    public static void openBrowser(Context context, String url) {
        final Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
// 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
// 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(context.getPackageManager()); // 打印Log   ComponentName到底是什么 L.d("componentName = " + componentName.getClassName());
            context.startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(context.getApplicationContext(), "请下载浏览器", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case PERMS_REQUEST_CODE:
                boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (storageAccepted) {
                    String filePath = copyFile();//首先把assets下的apk文件复制到sdcard上
                    installApk(filePath);
                }
                break;
        }
    }

    public void getDeviceIds(Context cxt) {
        long timeb = System.currentTimeMillis();
        Log.d("ylyread", "before init sdk");
        int nres = CallFromReflect(cxt);
//        int nres=DirectCall(cxt);
        long timee = System.currentTimeMillis();
        long offset = timee - timeb;
        Log.d("ylyread", "cost timeMs: " + offset);
        Log.d("ylyread", "return value: " + String.valueOf(nres));

        if (nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT) {//不支持的设备
            Log.e("ylyread", "INIT_ERROR_DEVICE_NOSUPPORT");
        } else if (nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE) {//加载配置文件出错
            Log.e("ylyread", "INIT_ERROR_LOAD_CONFIGFILE");
        } else if (nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT) {//不支持的设备厂商
            Log.e("ylyread", "INIT_ERROR_MANUFACTURER_NOSUPPORT");
        } else if (nres == ErrorCode.INIT_ERROR_RESULT_DELAY) {//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
            Log.e("ylyread", "INIT_ERROR_RESULT_DELAY");
        } else if (nres == ErrorCode.INIT_HELPER_CALL_ERROR) {//反射调用出错
            Log.e("ylyread", "INIT_HELPER_CALL_ERROR");
        }

    }

    /*
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     *
     * */
    private int CallFromReflect(Context cxt) {
        return MdidSdkHelper.InitSdk(cxt, true, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean isSupport, IdSupplier _supplier) {
                Log.d("ylyread", "OnSupport thread MAIN ? : " + (Looper.myLooper() == Looper.getMainLooper()));
                if (_supplier == null) {
                    Log.d("ylyread", "OnSupport _supplier : null");
                    return;
                }
                long t1 = System.currentTimeMillis();
                String oaid = _supplier.getOAID();
                Log.d("ylyread", "OnSupport oaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String vaid = _supplier.getVAID();
                Log.d("ylyread", "OnSupport vaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String aaid = _supplier.getAAID();
                Log.d("ylyread", "OnSupport aaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String udid = _supplier.getUDID();
                Log.d("ylyread", "OnSupport udid cost = "+(System.currentTimeMillis() - t1));
                StringBuilder builder = new StringBuilder();
                builder.append("support: ").append(isSupport ? "true" : "false").append("\n");
                builder.append("UDID: ").append(udid).append("\n");
                builder.append("OAID: ").append(oaid).append("\n");
                builder.append("VAID: ").append(vaid).append("\n");
                builder.append("AAID: ").append(aaid).append("\n");
                String idstext = builder.toString();
                _supplier.shutDown();
                Log.d("ylyread", "OnSupport id values : " + idstext);
            }
        });
    }

    /*
     * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
     * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
     * A和B不能存在直接和间接依赖关系，否则也会报错
     *
     * */
    private int DirectCall(Context cxt){
        MdidSdk sdk = new MdidSdk();
        return sdk.InitSdk(cxt, new IIdentifierListener() {
            @Override
            public void OnSupport(boolean isSupport, IdSupplier _supplier) {
                Log.d("ylyread", "OnSupport thread MAIN ? : " + (Looper.myLooper() == Looper.getMainLooper()));
                if (_supplier == null) {
                    Log.d("ylyread", "OnSupport _supplier : null");
                    return;
                }
                long t1 = System.currentTimeMillis();
                String oaid = _supplier.getOAID();
                Log.d("ylyread", "OnSupport oaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String vaid = _supplier.getVAID();
                Log.d("ylyread", "OnSupport vaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String aaid = _supplier.getAAID();
                Log.d("ylyread", "OnSupport aaid cost = "+(System.currentTimeMillis() - t1));
                t1 = System.currentTimeMillis();
                String udid = _supplier.getUDID();
                Log.d("ylyread", "OnSupport udid cost = "+(System.currentTimeMillis() - t1));
                StringBuilder builder = new StringBuilder();
                builder.append("support: ").append(isSupport ? "true" : "false").append("\n");
                builder.append("UDID: ").append(udid).append("\n");
                builder.append("OAID: ").append(oaid).append("\n");
                builder.append("VAID: ").append(vaid).append("\n");
                builder.append("AAID: ").append(aaid).append("\n");
                String idstext = builder.toString();
                _supplier.shutDown();
                Log.d("ylyread", "OnSupport id values : " + idstext);
            }
        });
    }

}