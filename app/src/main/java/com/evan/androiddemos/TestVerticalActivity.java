package com.evan.androiddemos;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.evan.androiddemos.widget.VerticalTextView;

import java.io.File;

public class TestVerticalActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertial_test);

        Log.e("evan","TestVerticalActivity setContentView");
//        VerticalTextView verticalTextView = findViewById(R.id.verticaltextview);
//        verticalTextView.setText("我是页头：我是的");
        Log.e("evan","File.pathSeparator = "+File.pathSeparator);
        Log.e("evan","File.separator = "+File.separator);
    }
}
