package com.evan.androiddemos.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.evan.androiddemos.R;

public class NewsBaseInfoView extends RelativeLayout {

    private static final int[] VISIBILITY_FLAGS = {VISIBLE, INVISIBLE, GONE};

    private String mNewsSource;
    private String mNewsPopularity;
    private String mNewsTime;
    private String mNewsLabel;
    private TextView mTvNewsSource;
    private TextView mTvNewsPopularity;
    private TextView mTvNewsTime;
    private TextView mTvNewsLabel;
    private View mDeleteIcon;
    private int mDeleteIconVisibility;

    public NewsBaseInfoView(Context context) {
        this(context, null);
    }

    public NewsBaseInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewsBaseInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // clone the inflater using the ContextThemeWrapper
//        return localInflater.inflate(R.layout.fragment_featured, container, false);
//        localInflater.inflate(R.layout.newsfeed_article_item_info, this);

        LayoutInflater.from(context).inflate(R.layout.newsfeed_article_item_info, this);

//        View test = LayoutInflater.from(context).inflate(R.layout.newsfeed_article_item_info,this,false);
//        addView(test);

        initView();
        initAttr(context, attrs);
        bindData();
    }


    private void initView() {
        mTvNewsSource = (TextView) findViewById(R.id.newsfeed_item_source);
        mTvNewsPopularity = (TextView) findViewById(R.id.tv_news_popularity);
        mTvNewsTime = (TextView) findViewById(R.id.tv_news_time);
        mTvNewsLabel = (TextView) findViewById(R.id.tv_news_label);
//        mDeleteIcon = findViewById(R.id.iv_news_delete);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NewsBaseInfoView);
        mNewsSource = typedArray.getString(R.styleable.NewsBaseInfoView_news_source);
        mNewsPopularity = typedArray.getString(R.styleable.NewsBaseInfoView_news_popularity);
        mNewsTime = typedArray.getString(R.styleable.NewsBaseInfoView_news_time);
        mNewsLabel = typedArray.getString(R.styleable.NewsBaseInfoView_news_lable);
        final int visibility = typedArray.getInt(R.styleable.NewsBaseInfoView_news_dislike_visibility, 0);
        if (visibility != 0) {
            mDeleteIconVisibility = VISIBILITY_FLAGS[visibility];
        }
        typedArray.recycle();
    }

    private void bindData() {
        bindText(mTvNewsSource, mNewsSource);
        bindText(mTvNewsPopularity, mNewsPopularity);
        bindText(mTvNewsTime, mNewsTime);
        bindText(mTvNewsLabel, mNewsLabel);
//        mDeleteIcon.setVisibility(mDeleteIconVisibility);
    }

    private void bindText(TextView textView, String text) {
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(GONE);
        } else {
            textView.setVisibility(VISIBLE);
            textView.setText(text);
        }
    }

    public void setNewsSource(String newsSource) {
        bindText(mTvNewsSource, newsSource);
    }

    public void setNewsSourceMaxEms(int maxEms) {
        mTvNewsSource.setMaxEms(maxEms);
    }

    public void setNewsPopularity(String newsPopularity) {
        bindText(mTvNewsPopularity, newsPopularity);
    }

    public void setNewsTime(String newsTime) {
        bindText(mTvNewsTime, newsTime);
    }

    public void setNewsLabel(String newsLabel) {
        bindText(mTvNewsLabel, newsLabel);
    }


    public View getDeleteIcon() {
        return mDeleteIcon;
    }

    public void setDeleteIconClickLister(OnClickListener l) {
        mDeleteIcon.setOnClickListener(l);
    }
}