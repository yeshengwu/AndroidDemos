package com.evan.androiddemos;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseBooleanArray;

public class TestActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }
}
