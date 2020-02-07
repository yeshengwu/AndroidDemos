package com.evan.androiddemos;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseBooleanArray;

import com.evan.androiddemos.event.EventToStore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class TestActivity extends Activity{
    private final String TAG = "TestActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

//        CollapsibleTextView collapsibleTextView = findViewById(R.id.collapse_text);
//        collapsibleTextView.setFullString("一场惊心设计，夺走了她的第一次。曾经恩爱甜蜜的恋情被冰冷葬送。“既然你已经怀孕了，那么就结婚吧。正好我不喜欢你，你也不喜欢我。”高冷邪魅的尹司宸潇洒的丢下了一纸契约：“我们只是契约夫妻。”顾兮兮刚要松口气，却不料那个签了契约的男人竟然无视她的抗拒，对全天下宣告他的占有权。尹司宸你到底要做什么？尹司宸邪魅/对全天下宣告他的占有权对全天下宣告");

        //        Executors.newSingleThreadExecutor()
//        LinkedHashMap
//        LruCache
//        LinkedList
//        DexClassLoader
//        PhantomReference
//        Handler
//        SparseArray
        SparseBooleanArray d = new SparseBooleanArray();
        d.put(1,true);
        d.put(2,false);
        d.get(1);
//        PriorityQueue

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void toStore(EventToStore toStore) {
        Log.d(TAG, "toStore.");
    }
}
