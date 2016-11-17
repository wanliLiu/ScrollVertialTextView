package soli.scrollvertialtextview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Provides a simple marquee effect for a single {@link android.widget.TextView}.
 *
 * @author Sebastian Roth <sebastian.roth@gmail.com>
 */
public class MarqueeView extends LinearLayout {
    private static  int TEXTVIEW_VIRTUAL_WIDTH = 2000;
    /**
     * Control the speed. The lower this value, the faster it will scroll.
     */
    private static final int DEFAULT_SPEED = 60;
    /**
     * Control the pause between the animations. Also, after starting this activity.
     */
    private static final int DEFAULT_ANIMATION_PAUSE = 2000;
    private TextView mTextField;
    private Animation mMoveTextOut = null;
    private Animation mMoveTextIn = null;
    private Paint mPaint;
    private boolean mMarqueeNeeded = false;
    private float mTextDifference;
    private int mSpeed = DEFAULT_SPEED;

    private int mAnimationPause = DEFAULT_ANIMATION_PAUSE;

    private boolean mAutoStart = false;

    private Interpolator mInterpolator = new LinearInterpolator();

    private boolean mCancelled = false;
    private Runnable mAnimationStartRunnable;

    private int viewWidth = 0;

    private boolean isInit = false;

    @SuppressWarnings({"UnusedDeclaration"})
    public MarqueeView(Context context) {
        this(context, null);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MarqueeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init(attrs);
    }


    /**
     * @param attrs
     */
    private void init(AttributeSet attrs) {

        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // init helper
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mInterpolator = new LinearInterpolator();

        extractAttributes(attrs);
    }

    /**
     * @param attrs
     */
    private void extractAttributes(AttributeSet attrs) {
        if (getContext() == null) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.asia_ivity_android_marqueeview_MarqueeView);

        if (a == null) {
            return;
        }

        mSpeed = a.getInteger(R.styleable.asia_ivity_android_marqueeview_MarqueeView_speed, DEFAULT_SPEED);
        mAnimationPause = a.getInteger(R.styleable.asia_ivity_android_marqueeview_MarqueeView_pause, DEFAULT_ANIMATION_PAUSE);
        mAutoStart = a.getBoolean(R.styleable.asia_ivity_android_marqueeview_MarqueeView_autoStart, false);

        a.recycle();
    }

    /**
     * Sets the animation speed.
     * The lower the value, the faster the animation will be displayed.
     *
     * @param speed Milliseconds per PX.
     */
    public void setSpeed(int speed) {
        this.mSpeed = speed;
    }

    /**
     * Sets the pause between animations
     *
     * @param pause In milliseconds.
     */
    public void setPauseBetweenAnimations(int pause) {
        this.mAnimationPause = pause;
    }

    /**
     * Sets a custom interpolator for the animation.
     *
     * @param interpolator Animation interpolator.
     */
    public void setInterpolator(Interpolator interpolator) {
        this.mInterpolator = interpolator;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (!isInit) {
            isInit = true;
            prepareAnimation();
        }
    }

    /**
     * Starts the configured marquee effect.
     */
    public void startMarquee() {
        if (mMarqueeNeeded) {
            startTextFieldAnimation();
        }

        mCancelled = false;
    }

    /**
     *
     */
    private void startTextFieldAnimation() {
        mAnimationStartRunnable = new Runnable() {
            public void run() {
                mTextField.startAnimation(mMoveTextOut);
            }
        };
        postDelayed(mAnimationStartRunnable, mAnimationPause);
    }

    /**
     * Disables the animations.
     */
    public void reset() {
        try {
            mCancelled = true;

            if (mAnimationStartRunnable != null) {
                removeCallbacks(mAnimationStartRunnable);
            }

            mTextField.clearAnimation();

            mMoveTextOut.reset();
            mMoveTextIn.reset();

            invalidate();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     *
     */
    private void prepareAnimation() {

        if (viewWidth == 0) return;

        if (TextUtils.isEmpty(mTextField.getText().toString()))
            return;

        // Measure
        mPaint.setTextSize(mTextField.getTextSize());
        mPaint.setTypeface(mTextField.getTypeface());
        TEXTVIEW_VIRTUAL_WIDTH = (int)mPaint.measureText(mTextField.getText().toString());

        // See how much functions are needed at all
        mMarqueeNeeded = TEXTVIEW_VIRTUAL_WIDTH > viewWidth;

        mTextDifference = Math.abs((TEXTVIEW_VIRTUAL_WIDTH - viewWidth)) + 10;

        final int duration = (int) (mTextDifference * mSpeed);

        mMoveTextOut = new TranslateAnimation(0, -mTextDifference, 0, 0);
        mMoveTextOut.setDuration(duration);
        mMoveTextOut.setInterpolator(mInterpolator);
        mMoveTextOut.setFillAfter(true);

        mMoveTextIn = new TranslateAnimation(-mTextDifference, 0, 0, 0);
        mMoveTextIn.setDuration(duration);
        mMoveTextIn.setStartOffset(mAnimationPause);
        mMoveTextIn.setInterpolator(mInterpolator);
        mMoveTextIn.setFillAfter(true);

        mMoveTextOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                expandTextView();
            }

            public void onAnimationEnd(Animation animation) {
                if (mCancelled) {
                    return;
                }

                mTextField.startAnimation(mMoveTextIn);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        mMoveTextIn.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {

//                cutTextView();

                if (mCancelled) {
                    return;
                }
                startTextFieldAnimation();
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });

        if (mAutoStart) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    startMarquee();
                }
            }, 500);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (getChildAt(0) instanceof TextView) {
            mTextField = (TextView) getChildAt(0);
            removeView(mTextField);

            LayoutParams sv1lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            sv1lp.gravity = Gravity.CENTER_HORIZONTAL;
            ScrollView mScrollView = new ScrollView(getContext());

            mScrollView.addView(mTextField, new ScrollView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            addView(mScrollView, sv1lp);

            mTextField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!TextUtils.isEmpty(editable.toString())) {
                        reset();

                        isInit = false;

//                        cutTextView();

                        requestLayout();
                    }
                }
            });
        }
    }

    /**
     *
     */
    private void expandTextView() {
        ViewGroup.LayoutParams lp = mTextField.getLayoutParams();
        lp.width = TEXTVIEW_VIRTUAL_WIDTH;
        mTextField.setLayoutParams(lp);
    }

    /**
     *
     */
    private void cutTextView() {
        if (mTextField.getWidth() != viewWidth) {
            ViewGroup.LayoutParams lp = mTextField.getLayoutParams();
            lp.width = viewWidth;
            mTextField.setLayoutParams(lp);
        }
    }
}