package com.evan.androiddemos.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * 文案展开收起组件
 * @author zhangmeng25
 */
public class CollapsibleTextViewParentFrameLayout extends FrameLayout
        implements ICollapsibleTextViewParent {

    public CollapsibleTextViewParentFrameLayout(@NonNull Context context) {
        super(context);
    }

    public CollapsibleTextViewParentFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CollapsibleTextViewParentFrameLayout(@NonNull Context context,
                                                @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setViewHeight(int height) {
        getLayoutParams().height = height + getPaddingTop() + getPaddingBottom();
        requestLayout();
    }
}
