package com.evan.androiddemos.radiogroup;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.evan.androiddemos.R;

/**
 * RadioGroupd的坑：mRadioGroup.check会引起onCheckedChanged多次回调。
 *
 * 改进：
 * 用RadioButton setChecked
 * Created by evan on 18/1/19.
 */

public class RadioGroupActivity extends Activity {

    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_radiogroup);

        initUI();
    }

    private void initUI() {
        mRadioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        mRadioGroup.setOnCheckedChangeListener(mRadioGroupCheckedListener);
//        mRadioGroup.check(R.id.rb_latest);

        findViewById(R.id.btn_invoke_tab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mRadioGroup.check(R.id.rb_latest); //坑
                //改进
                ((RadioButton) findViewById(R.id.rb_latest)).setChecked(true);
            }
        });
    }

    private RadioGroup.OnCheckedChangeListener mRadioGroupCheckedListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_home:
                    Log.e("evan", "onCheckedChanged rb_home");
                    break;
                case R.id.rb_latest:
                    Log.e("evan", "onCheckedChanged rb_latest");
                    break;
                case R.id.rb_me:
                    Log.e("evan", "onCheckedChanged rb_me");
                    break;

                default:
                    break;
            }
        }
    };


}
