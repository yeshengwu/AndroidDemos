package com.evan.androiddemos.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.evan.androiddemos.R;

/**
 * 文本展开收起组件
 * @author zhangmeng25
 */
public class CollapsibleTextView extends android.support.v7.widget.AppCompatTextView {
    /** 动画执行的时间 */
    public static final long ANIM_DURATION = 200L;
    /** 文本收起后缀 */
    public static final String SUFFIX_PREFIX = "...";
    /** 文本后缀的文案颜色 */
    private int mSuffixColor = 0xff0000ff;
    /** 文本收起的行数 */
    private int mCollapsedLines = 1;
    /** 文本收起的最小行数 */
    private int mMinCollapseLines;
    /** 当前控件的文本 */
    private String mText;
    /** 是否需要初始化layout */
    private boolean mShouldInitLayout = true;
    /** 当前控件是否是展开态 */
    private boolean mExpanded = false;
    /** 控件的后缀展开文案 */
    private String mCollapsedText = " Show All";
    /** 控件的后缀收起文案 */
    private String mExpandedText = " Hide ";
    private String mExpandedChText = " 展开 ";
    /** 用于换行的后知文案 */
    private String mCollapsedTag = "\n     ";
    /** 行数变化的动画 */
    private ValueAnimator mChangeHeightAnimator;
    /** 接口 */
    private ICollapsibleTextViewParent mParent;
    /** 收起时的高度 */
    private int mCollapsedHeight;
    /** 后缀文案的尺寸 */
    private float mRelativeSize;
    /** 后缀文案是否加粗 */
    private boolean isSuffixBold;
    /** 是否应该显示后缀 */
    private boolean showHideSuffix;
    /** 是否需要执行动画 */
    private boolean doAnimator;
    /** 控件的收起显示icon */
    private Drawable mTagDrawableClose;
    private Bitmap mTagDrawableCloseBitmap;
    /** 控件的展开显示icon */
    private Drawable mTagDrawableOpen;
    private Bitmap mTagDrawableOpenBitmap;
    /** 文本展开收起组件点击事件监听 */
    private OnCollapsibleClickListener mListener;

    public CollapsibleTextView(Context context) {
        this(context, null);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.getTheme()
                .obtainStyledAttributes(attrs, R.styleable.CollapsibleTextView, defStyleAttr, 0);

        mSuffixColor = attributes.getColor(R.styleable.CollapsibleTextView_suffixColor, 0xff0000ff);
        mCollapsedLines = attributes.getInt(R.styleable.CollapsibleTextView_collapsedLines, 2);
        mCollapsedText = attributes.getString(R.styleable.CollapsibleTextView_collapsedText);
        mMinCollapseLines = attributes.getInteger(R.styleable.CollapsibleTextView_min_collapse_lines, 2);
        mRelativeSize = attributes.getFraction(R.styleable.CollapsibleTextView_suffixRelativeSize, 1, 1, 1.f);
        isSuffixBold = attributes.getBoolean(R.styleable.CollapsibleTextView_suffixBoldText, false);
        showHideSuffix = attributes.getBoolean(R.styleable.CollapsibleTextView_showHideSuffix, true);
        doAnimator = attributes.getBoolean(R.styleable.CollapsibleTextView_doAnimator, true);
        mTagDrawableClose = attributes.getDrawable(R.styleable.CollapsibleTextView_suffixDrawableClose);
        mTagDrawableOpen = attributes.getDrawable(R.styleable.CollapsibleTextView_suffixDrawableOpen);

        initTagBitmap(); // 初始化时将标志图片给完成加载构建

        if (TextUtils.isEmpty(mCollapsedText)) {
            mCollapsedText = " Show All";
        }

        mExpandedText = attributes.getString(R.styleable.CollapsibleTextView_expandedText);
        if (TextUtils.isEmpty(mExpandedText)) {
            mExpandedText = " Hide";
        }

        this.mText = getText() == null ? "" : getText().toString();

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CollapsibleTextView.this.pendingFullString(!isExpanded(),
                        CollapsibleTextView.this.getText().toString());
                // 通知点击展开事件
                if (mListener != null) {
                    mListener.onExpandClick();
                }
            }
        });
    }

    /**
     * 构建需要作为标志的图片  Drawable转化为Bitmap
     */
    private void initTagBitmap() {
        if (mTagDrawableOpen != null) {
            int width = mTagDrawableOpen.getIntrinsicWidth();
            int height = mTagDrawableOpen.getIntrinsicHeight();
            mTagDrawableOpenBitmap = Bitmap.createBitmap(width, height, mTagDrawableOpen.getOpacity()
                    != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mTagDrawableOpenBitmap);
            mTagDrawableOpen.setBounds(0, 0, width, height);
            mTagDrawableOpen.draw(canvas);
        }
        if (mTagDrawableClose != null) {
            int width = mTagDrawableClose.getIntrinsicWidth();
            int height = mTagDrawableClose.getIntrinsicHeight();
            mTagDrawableCloseBitmap = Bitmap.createBitmap(width, height, mTagDrawableClose.getOpacity()
                    != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(mTagDrawableCloseBitmap);
            mTagDrawableClose.setBounds(0, 0, width, height);
            mTagDrawableClose.draw(canvas);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewParent parent = getParent();
        while (parent != null && !(parent instanceof ICollapsibleTextViewParent)) {
            parent = parent.getParent();
        }
        if (parent != null && parent instanceof ICollapsibleTextViewParent) {
            mParent = (ICollapsibleTextViewParent) parent;
        }
    }

    /**
     * 设置文本展开收起组件点击事件监听
     *
     */
    public void setOnCollapsibleClickListener(OnCollapsibleClickListener listener) {
        this.mListener = listener;
    }

    /**
     * 切换当前控件的展开收起状态
     * @param expanded 是否展开
     */
    private void applyState(boolean expanded) {
        if (TextUtils.isEmpty(mText)) {
            return;
        }
        if (expanded) {
            if (shouldShowCollapsedTag()) {
                withExpendedTextTag();
            } else {
                setText(mText);
            }
            return;
        } else {
            if (mTagDrawableOpen == null) {
                withTextTag();
            } else {
                withDrawableTag();
            }
        }
    }

    /**
     * 收起态时使用图片做后缀tag
     */
    private void withDrawableTag() {
        String note = mText;
        try {
            if (mCollapsedLines - 1 < 0) {
                throw new RuntimeException("CollapsedLines must equal or greater than 1");
            }

            String suffix;

            int lineEnd = getLayout().getLineEnd(mCollapsedLines - 1);
            suffix = mCollapsedText;

            int end = lineEnd - 1;

            TextPaint paint = getPaint();
            int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int maxWidth = mCollapsedLines * lineWidth;

            while (paint.measureText(note.substring(0, end) + SUFFIX_PREFIX)
                    + mTagDrawableOpen.getIntrinsicWidth() > maxWidth) {
                end--;
            }

            note = note.substring(0, end);
            SpannableString tempString = buildSpannableTag(note + SUFFIX_PREFIX, suffix);

            int lines = new StaticLayout(tempString, getPaint(), lineWidth,
                    Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true)
                    .getLineCount();

            while (lines > mCollapsedLines) {
                note = note.substring(0, note.length() - 1);
                tempString = buildSpannableTag(note + SUFFIX_PREFIX, suffix);

                lines = new StaticLayout(tempString, getPaint(), maxWidth,
                        Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true)
                        .getLineCount();
            }

            final String ss = note + SUFFIX_PREFIX;

            mCollapsedHeight = getTextHeight(ss);
            setText(ss);
        } catch (Exception e) {
            mExpanded = !mExpanded;
        }
    }

    /**
     * 展开状态判断是否后缀需要换行显示
     */
    private void withExpendedTextTag() {
        String note = mText;

        String suffix = mExpandedText;

        TextPaint paint = getPaint();
        int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int maxWidth = getTextLines() * lineWidth;

        float textWidth;
        if (mTagDrawableClose == null) {
            textWidth = paint.measureText(note + suffix + suffix);
        } else {
            textWidth = paint.measureText(note + mExpandedChText);
        }

        if (textWidth >= maxWidth) {
            note = note + mCollapsedTag;
        }
        final String text = note;
        mCollapsedHeight = getTextHeight(text);
        setText(text);
    }

    /**
     * 收起态使用文本做后主tag
     */
    private void withTextTag() {
        String note = mText;

        try {
            if (mCollapsedLines - 1 < 0) {
                throw new RuntimeException("CollapsedLines must equal or greater than 1");
            }

            String suffix;

            int lineEnd = getLayout().getLineEnd(mCollapsedLines - 1);
            suffix = mCollapsedText;
            int newEnd = lineEnd - suffix.length() - 1;
            int end = newEnd > 0 ? newEnd : lineEnd;

            TextPaint paint = getPaint();
            int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int maxWidth = mCollapsedLines * lineWidth;

            while (paint.measureText(note.substring(0, end) + SUFFIX_PREFIX + suffix) > maxWidth) {
                end--;
            }

            note = note.substring(0, end);
            SpannableString ss = buildSpannableString(note, SUFFIX_PREFIX + suffix);

            int lines = new StaticLayout(ss, getPaint(), lineWidth,
                    Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true)
                    .getLineCount();

            while (lines > mCollapsedLines) {
                note = note.substring(0, note.length() - 1);
                ss = buildSpannableString(note, SUFFIX_PREFIX + suffix);

                lines = new StaticLayout(ss, getPaint(), maxWidth,
                        Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true)
                        .getLineCount();
            }

            note = note.substring(0, note.length() - 1);
            final String str = note + SUFFIX_PREFIX;

            mCollapsedHeight = getTextHeight(str);

            setText(str);
        } catch (Exception e) {
            mExpanded = !mExpanded;
        }
    }

    /**
     * 构建文本SpannableString 用图片做后缀
     * @param text 要展示的文本
     * @param suffix 后缀
     * @return SpannableString 富文本内容
     */
    private SpannableString buildSpannableTag(String text, String suffix) {
        SpannableString ss = new SpannableString(text + suffix);

        ss.setSpan(new DrawableTag(isExpanded() ? mTagDrawableClose : mTagDrawableOpen),
                text.length(), text.length() + suffix.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }
    /**
     * 构建文本SpannableString 用文本做后缀
     * @param text 要展示的文本
     * @param suffix 后缀
     * @return SpannableString 富文本内容
     */
    private SpannableString buildSpannableString(String text, String suffix) {
        SpannableString ss = new SpannableString(text + suffix);

        ss.setSpan(new ForegroundColorSpan(mSuffixColor),
                text.length(),
                text.length() + suffix.length(),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (isSuffixBold) {
            ss.setSpan(new StyleSpan(Typeface.BOLD),
                    text.length(), text.length() + suffix.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        ss.setSpan(new RelativeSizeSpan(mRelativeSize),
                text.length(), text.length() + suffix.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mChangeHeightAnimator != null && mChangeHeightAnimator.isRunning()) {
            return;
        }
        setMaxLines(Integer.MAX_VALUE);
        if (mShouldInitLayout && getLineCount() > mCollapsedLines) {
            mShouldInitLayout = false;
            applyState(mExpanded);

            if (mParent != null) {
                mParent.setViewHeight(getCollapsedHeight());
            }
        }
    }

    /**
     * 状态的显示tag在onDraw后根据是否需要显示标志给绘制在右下角
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextPaint paint = getPaint();
        int originTextColor = paint.getColor();
        Typeface originTypeface = paint.getTypeface();
        if (isExpanded() && shouldShowCollapsedTag()) {
            String expandedText = mExpandedText;
            float textWidth = paint.measureText(expandedText);
            if (mTagDrawableClose == null || mTagDrawableCloseBitmap == null) {
                paint.setColor(mSuffixColor);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(expandedText, getMeasuredWidth() - textWidth,
                        getTextHeight(mText) - paint.getFontMetrics().ascent + getLineSpacingExtra(), paint);
                paint.setColor(originTextColor);
                paint.setTypeface(originTypeface);
            } else {
                canvas.drawBitmap(mTagDrawableCloseBitmap,
                        getMeasuredWidth() - mTagDrawableClose.getIntrinsicWidth() * 3 / 2,
                        getMeasuredHeight() - mTagDrawableClose.getIntrinsicHeight() * 3 / 2, paint);
            }
        } else if (!isExpanded() && shouldShowExpandedTag()) {
            String collapsedText = mCollapsedText;
            float textWidth = paint.measureText(collapsedText);
            if (mTagDrawableOpen == null || mTagDrawableOpenBitmap == null) {
                paint.setColor(mSuffixColor);
                paint.setTypeface(Typeface.DEFAULT_BOLD);
                canvas.drawText(collapsedText, getMeasuredWidth() - textWidth,
                        getTextHeight(mText) - paint.getFontMetrics().ascent + getLineSpacingExtra(), paint);
                paint.setColor(originTextColor);
                paint.setTypeface(originTypeface);
            } else {
                canvas.drawBitmap(mTagDrawableOpenBitmap,
                        getMeasuredWidth() - mTagDrawableOpen.getIntrinsicWidth() * 3 / 2,
                        getMeasuredHeight() - mTagDrawableOpen.getIntrinsicHeight(), paint);
            }
        }
    }

    /**
     * 执行文本高度变化的动画
     * @param startHeight 起始高度
     * @param endHeight 结束高度
     */
    private void doAnimator(int startHeight, final int endHeight) {
        if (mChangeHeightAnimator != null && mChangeHeightAnimator.isRunning()) {
            mChangeHeightAnimator.cancel();
        }

        mChangeHeightAnimator = ValueAnimator.ofInt(startHeight, endHeight);
        mChangeHeightAnimator.setDuration(ANIM_DURATION);
        mChangeHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (Integer) animation.getAnimatedValue();
                if (mParent != null) {
                    mParent.setViewHeight(height);
                }
            }
        });

        mChangeHeightAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                onAnimationEnd(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mChangeHeightAnimator = null;
                if (mParent != null) {
                    mParent.setViewHeight(endHeight);
                }

                applyState(mExpanded);
            }
        });

        mChangeHeightAnimator.start();
    }

    /**
     * 设置要显示的文本
     * @param str 文本内容
     */
    public void setFullString(String str) {
        this.mText = str;

        if (TextUtils.isEmpty(str)) {
            if (mParent != null) {
                mParent.setViewHeight(0);
            }
            return;
        }

        if (!shouldShowExpandedTag()) {
            setText(str);
            if (mParent != null) {
                mParent.setViewHeight(getTextHeight(str));
            }
            return;
        }

        if (isExpanded()) {
            setMaxLines(Integer.MAX_VALUE);
            applyState(true);

            if (mParent != null) {
                mParent.setViewHeight(getExpandedHeight());
            }
        } else {
            setMaxLines(mCollapsedLines);
            mShouldInitLayout = true;
            setText(mText);
        }
    }

    /**
     * 获取当前控件的状态是否展开
     * @return boolean 是否是展开态
     */
    public boolean isExpanded() {
        return mExpanded;
    }

    /**
     * 设置控件的状态
     * @param expanded 是否展开
     */
    public void pendingExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    /**
     * 设置控件的状态和内容，如果当前控件宽度为0，就在计算高度后在设置
     * @param expanded 是否展开
     * @param text 要显示的文案
     */
    public void pendingFullString(boolean expanded, final String text) {
        pendingExpanded(expanded);
        if (getMeasuredWidth() == 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setFullString(text);
                }
            });
        } else {
            setFullString(text);
        }
    }
    /**
     * 设置控件的状态
     * @param expanded 是否展开
     */
    public void setExpanded(boolean expanded) {
        if (mExpanded != expanded) {
            mExpanded = expanded;

            if (!shouldShowExpandedTag()) {
                return;
            }

            int collapsedHeight = getCollapsedHeight();
            int expandedHeight = getExpandedHeight();

            if (expanded) {
                if (shouldShowCollapsedTag()) {
                    withExpendedTextTag();
                } else {
                    setText(mText);
                }
                if (doAnimator) {
                    doAnimator(collapsedHeight, expandedHeight);
                }
            } else {
                if (doAnimator) {
                    doAnimator(expandedHeight, collapsedHeight);
                }
            }

            if (!doAnimator) {
                applyState(mExpanded);
            }
        }
    }
    /**
     * 获取后缀颜色
     * @return  int 后缀颜色
     */
    public int getSuffixColor() {
        return mSuffixColor;
    }

    /**
     * 设置后缀颜色
     * @param   suffixColor 后缀颜色
     */
    public void setSuffixColor(int suffixColor) {
        mSuffixColor = suffixColor;
        applyState(mExpanded);
    }

    /**
     * 获取收起行数
     * @return  int 行数
     */
    public int getCollapsedLines() {
        return mCollapsedLines;
    }
    /**
     * 设置收起行数
     * @param   collapsedLines 行数
     */
    public void setCollapsedLines(int collapsedLines) {
        mCollapsedLines = collapsedLines;
        mShouldInitLayout = true;
        setText(mText);
    }
    /**
     * 获取收起后的文本
     * @return  String 文本
     */
    public String getCollapsedText() {
        return mCollapsedText;
    }
    /**
     * 设置收起文本
     * @param   collapsedText 文本
     */
    public void setCollapsedText(String collapsedText) {
        mCollapsedText = collapsedText;
        applyState(mExpanded);
    }
    /**
     * 获取展开后的文本
     * @return  String 文本
     */
    public String getExpandedText() {
        return mExpandedText;
    }
    /**
     * 设置展开文本
     * @param   expandedText 文本
     */
    public void setExpandedText(String expandedText) {
        mExpandedText = expandedText;
        applyState(mExpanded);
    }

    @Override
    public CharSequence getText() {
        return mText;
    }

    /**
     * 计算当前行数是否需要显示展开标志， 如果超过收起行数就显示
     * @return boolean 是否需要显示
     */
    public boolean shouldShowExpandedTag() {
        return getTextLines() > mCollapsedLines;
    }

    /**
     * 是否需要显示收起标志， 如果超过最小收起行数就显示
     *
     * @return boolean 是否需要显示
     */
    public boolean shouldShowCollapsedTag() {
        return showHideSuffix && getTextLines() > mMinCollapseLines;
    }

    /**
     *  计算当前行数
     * @return int 行数
     */
    public int getTextLines() {
        int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        return new StaticLayout(getText(), getPaint(), lineWidth,
                Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true).getLineCount();
    }

    /**
     * 获取收起后文本的高度
     *
     * @return int 高度
     */
    public int getCollapsedHeight() {
        if (mCollapsedHeight != 0) {
            return mCollapsedHeight;
        }

        Paint.FontMetrics fm = getPaint().getFontMetrics();
        float fontHeight = fm.bottom - fm.top;

        return (int) ((fontHeight + getLineSpacingExtra()) * (mCollapsedLines - 1) + fontHeight);
    }

    /**
     * 获取展开后文本的高度
     *
     * @return int 高度
     */
    public int getExpandedHeight() {
        String expandedFullText = mText;
        if (shouldShowCollapsedTag()) {
            expandedFullText += mCollapsedTag;
        }

        return getTextHeight(expandedFullText);
    }

    /**
     * 获取文本的高度
     *
     * @return int 高度
     */
    public int getTextHeight(CharSequence text) {
        int lineWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

        return new StaticLayout(text, getPaint(), lineWidth,
                Layout.Alignment.ALIGN_NORMAL, 1.f, getLineSpacingExtra(), true).getHeight();
    }

    private static class DrawableTag extends DynamicDrawableSpan {
        private Drawable mDrawable;

        DrawableTag(Drawable drawable) {
            super(ALIGN_BOTTOM);

            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            mDrawable = drawable;
        }

        @Override
        public Drawable getDrawable() {
            return mDrawable;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end,
                           Paint.FontMetricsInt fontMetricsInt) {
            Drawable drawable = getDrawable();
            Rect rect = drawable.getBounds();
            if (fontMetricsInt != null) {
                Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.descent - fmPaint.ascent;
                int drHeight = rect.bottom - rect.top;
                int centerY = fmPaint.ascent + fontHeight / 2;

                fontMetricsInt.ascent = centerY - drHeight / 2;
                fontMetricsInt.top = fontMetricsInt.ascent;
                fontMetricsInt.bottom = centerY + drHeight / 2;
                fontMetricsInt.descent = fontMetricsInt.bottom;
            }
            return rect.right;
        }

        @Override
        public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                         int bottom, Paint paint) {
            Drawable drawable = getDrawable();
            canvas.save();
            Paint.FontMetricsInt fmPaint = paint.getFontMetricsInt();
            int fontHeight = fmPaint.descent - fmPaint.ascent;
            int centerY = y + fmPaint.descent - fontHeight / 2;
            int transY = centerY - (drawable.getBounds().bottom - drawable.getBounds().top) / 2;
            canvas.translate(x, transY);
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * 文本展开收起组件点击事件监听
     *
     */
    public interface OnCollapsibleClickListener {
        /**
         * 文本展开
         *
         */
        void onExpandClick();
    }
}
