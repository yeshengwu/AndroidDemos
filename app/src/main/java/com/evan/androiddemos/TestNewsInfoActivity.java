package com.evan.androiddemos;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.evan.androiddemos.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestNewsInfoActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.news_small);

        TextView textView = new TextView(this);
        textView.setText("我们是迷你特攻队，用测试代码位测试广告后续的转换动作流程原生资讯落地页里的相关推荐列表和文章结束广告的字体全面高仿百度H5落地页里的相关推荐列表及");
        textView.setTextSize(18);
        textView.setLineSpacing(9, 1);
        textView.setTextColor(0xFF00ff00);
        setContentView(textView);

//        BaseAdapter
        DisplayUtil.getDensityDpi(this);
//        WebView webView = new WebView(this);
//        webView.loadUrl();
        show("abcd");
        show("123456"); // 5+ 1
        show("987654321"); // 5+4
        show("abcde12345"); // 5+5
        show("qwertabcde1234500"); // 5+5+2
        show("qwertabcde1234500000"); // 5+5+5
//        String a = "123456";
        String a = "12345";
//        if (a.length()<= 5){
//            Log.e("eee",a);
//        } else {
//            show(a);
//        }
//        show(a);


    }

    public static void show(String str) {
        str = str.trim();
        int index = 0;
        int maxLength = 5;
        String sub;
        while (index < str.length()) {  // 9 10
            if (str.length() <= index + maxLength) {
                sub = str.substring(index);
            } else {
                sub = str.substring(index, index + maxLength);
            }

            index += maxLength;
            Log.i("qidizi_debug", sub.trim());
        }
    }

    @Override
    public void onClick(View v) {

    }
}
