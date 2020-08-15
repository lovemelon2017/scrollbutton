package com.hanchao.scrollbutton.btn;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hanchao.scrollbutton.R;


/**
 * @author :小豆豆打飞机
 * @date: 2020/8/15
 * @motto: A good beginning is half the battle
 * 右边横滑接单
 */
public class ScrollHorizontalRightButton extends RelativeLayout {

    private String center_text;
    private int center_text_color;
    private int center_text_size;
    private Drawable background;//底部默认资源引用
    private int scrollButtonWith;
    private Drawable scrollButtonBackground;//左边滚动按钮的背景图片
    private Drawable rightImageViewSrc;

    private TextView textView;//中间文字效果
    private ImageView rightImageView;//右边箭头图片
    private Button scrollButton;//滑动Button

    private GestureDetector mGestureDetector;
    private boolean isScroll = false;//是否滚动了
    private boolean isSuccess = false;//viewGroup滑动是否成功
    public OnScrollRightListener onScrollListener;

    public void setOnScrollListener(OnScrollRightListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public ScrollHorizontalRightButton(Context context) {
        super(context);
    }

    public ScrollHorizontalRightButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ScrollHorizontalButton);
        center_text_size = typedArray.getDimensionPixelSize(R.styleable.ScrollHorizontalButton_sh_center_textSize, dip2px(getContext(), 14));
        center_text_color = typedArray.getColor(R.styleable.ScrollHorizontalButton_sh_center_textColor, Color.WHITE);
        center_text = typedArray.getString(R.styleable.ScrollHorizontalButton_sh_center_text);
        background = typedArray.getDrawable(R.styleable.ScrollHorizontalButton_sh_center_background);

        scrollButtonWith = typedArray.getLayoutDimension(R.styleable.ScrollHorizontalButton_sh_scroll_button_with, dip2px(getContext(), 50));
        scrollButtonBackground = typedArray.getDrawable(R.styleable.ScrollHorizontalButton_sh_scroll_button_background);

        rightImageViewSrc = typedArray.getDrawable(R.styleable.ScrollHorizontalButton_sh_right_image_view_src);
        typedArray.recycle();
        initView();
    }

    public ScrollHorizontalRightButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    private void initView() {
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setBackground(background);
            } else {
                setBackgroundDrawable(background);
            }

        }

        initLeftScrollButton();//添加并初始化左侧滑动块
        initCenterTextView();//添加并初始化中间文本框
        initRightArrowImageView();//添加并初始化右侧箭头图片

        Drawable drawable = getResources().getDrawable(R.drawable.scroll_swich_arrow_right);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        scrollButton.setCompoundDrawables(null, null, drawable, null);
        scrollButton.setBackgroundColor(Color.BLACK);

        mGestureDetector = new GestureDetector(getContext(), new ScrollGestureListener());

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mGestureDetector.onTouchEvent(event);
                if (isScroll && event.getAction() == MotionEvent.ACTION_UP) {
                    if (isSuccess) {
                        startLeftToRightAnimation(scrollButton.getWidth(), getWidth());
                    } else {
                        startLeftToRightAnimation(scrollButton.getWidth(), scrollButtonWith);
                    }

                }

                return true;
            }
        });
    }

    /**
     * 左侧滑动View
     */
    private void initLeftScrollButton() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(scrollButtonWith, LayoutParams.MATCH_PARENT);
        layoutParams.addRule(CENTER_VERTICAL);
        layoutParams.addRule(ALIGN_PARENT_LEFT);

        scrollButton = new Button(getContext());
        scrollButton.setPadding(24, 0, 24, 0);//int left, int top, int right, int bottom
        scrollButton.setGravity(Gravity.CENTER);
        scrollButton.setClickable(false);
        scrollButton.setEnabled(false);

        if (scrollButtonBackground != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                scrollButton.setBackground(scrollButtonBackground);
            } else {
                scrollButton.setBackgroundDrawable(scrollButtonBackground);
            }

        }

        addView(scrollButton, layoutParams);
    }

    /**
     * 添加并初始化中间文本框
     */
    private void initCenterTextView() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_IN_PARENT);
        textView = new TextView(getContext());
        textView.setTextColor(center_text_color);
        textView.setTextSize(center_text_size);
        textView.setText(center_text);
        addView(textView, layoutParams);
    }

    /**
     * 添加并初始化右边箭头图像
     */
    private void initRightArrowImageView() {
        RelativeLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(CENTER_VERTICAL);
        layoutParams.addRule(ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(0, 0, 24, 0);//int left, int top, int right, int bottom
        rightImageView = new ImageView(getContext());
        if (rightImageViewSrc != null) {
            rightImageView.setImageDrawable(rightImageViewSrc);
        }
        addView(rightImageView, layoutParams);
    }

    public interface OnScrollRightListener {
        void onScrollingMoreThanCritical();//当前滑动超过临界值

        void onScrollingLessThanCriticalX();//当前滑动小于临界值X

        void onSlideFinishSuccess();//已经滑动确认成功

        void onSlideFinishCancel();//已经滑动确认取消

    }

    private void startLeftToRightAnimation(int currentheight, int toheight) {
        ValueAnimator animator = null;
        //为了看到动画值的变化，这里添加了动画更新监听事件来打印动画的当前值

        animator = ValueAnimator.ofInt(currentheight, toheight);


        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = scrollButton.getLayoutParams();
                layoutParams.width = value;
                scrollButton.requestLayout();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                setEnabled(false);
                setClickable(false);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                setEnabled(true);
                setClickable(true);
                if (isSuccess) {
                    startShakeAnimator();
                    recoveryScrollButton();
                    if (onScrollListener != null) {

                        onScrollListener.onSlideFinishSuccess();
                    }
                } else {
                    if (onScrollListener != null) {
                        onScrollListener.onSlideFinishCancel();
                    }
                }


            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setDuration(250);//动画时间
        animator.start();//启动动画
    }

    /**
     * 恢复按钮
     */
    private void recoveryScrollButton() {
        ViewGroup.LayoutParams layoutParams = scrollButton.getLayoutParams();
        layoutParams.width = scrollButtonWith;
        scrollButton.requestLayout();
    }

    /**
     * 开始抖动动画
     */
    private void startShakeAnimator() {


        TranslateAnimation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(7));
        translateAnimation.setDuration(200);
        startAnimation(translateAnimation);


    }


    /**
     * 滑动手势
     */
    public class ScrollGestureListener implements GestureDetector.OnGestureListener {
        private float previousX;//down事件按下的X的相对坐标
        private int moriginalScrollSwithViewWidth;//当前控件的原始大小
        private int moriginalWidth;//srcollButton的原始宽度

        private int criticalX;//临界点的X位置

        @Override
        public boolean onDown(MotionEvent e) {
            isScroll = false;
            previousX = e.getX();//在按下的时候设置X的坐标
            moriginalScrollSwithViewWidth = getWidth();
            moriginalWidth = scrollButtonWith;
            criticalX = getWidth() * 3 / 4;
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            int currentX = (int) e2.getX();

            if (currentX >= moriginalWidth && currentX <= moriginalScrollSwithViewWidth) {
                isScroll = true;

                ViewGroup.LayoutParams layoutParams = scrollButton.getLayoutParams();


                layoutParams.width = currentX;

                scrollButton.requestLayout();

                if (currentX >= criticalX) {
                    isSuccess = true;
                    if (onScrollListener != null) {
                        onScrollListener.onScrollingMoreThanCritical();
                    }
                } else {
                    isSuccess = false;
                    if (onScrollListener != null) {
                        onScrollListener.onScrollingLessThanCriticalX();
                    }

                }

            }


            return true;
        }


        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }


    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
