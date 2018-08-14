package andreyrussprint.threestateswitch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import andreyrussprint.threestateswitch.states.SwitchStates;
import andreyrussprint.threestateswitch.R;

/**
 * Modified by andreyrussprint on 25/7/18.
 */

public class ThreeStateSwitch extends View {
    private SwitchStates state = SwitchStates.MIDDLE_STATE;
    private boolean isDrag;
    private float startX;
    private boolean isAnimate;
    private boolean isPressed;

    int textNormalColor;
    int textSelectedLeftColor;
    int textSelectedRightColor;
    int textNormalSize;
    int textSelectedSize;

    int colorMiddleState = R.color.background_middle_state_color;
    int colorLeftState = R.color.background_left_state_color;
    int colorRightState = R.color.background_right_state_color;

    public int backgroundColor;

    Paint ovalPaint, textLeftPaint, textRightPaint, textNormalPaint;
    Bitmap thumbIcon;
    RectF ovalRectF;
    Rect originalBitmapRect, drawnBitmapRect;
    Rect textBounds;

    private float viewWidth;
    private float viewHeight;

    private int paddingRight;
    private int paddingLeft;
    private int paddingBottom;
    private int paddingTop;

    public int xCircle = 0;
    public int yCircle = 0;
    public int diameterSize = 0;

    String leftStateText;
    String rightStateText;

    float textLeftSelectedWidth, textRightSelectedWidth, textLeftNormalWidth, textRightNormalWidth, maxNeededTextWidth;

    public OnStateChangeListener onStateChangeListener;

    public ThreeStateSwitch(Context context) {
        super(context);
        init(context,null);
    }

    public ThreeStateSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ThreeStateSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ThreeStateSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThreeStateSwitch);

        colorMiddleState = ta.getColor(R.styleable.ThreeStateSwitch_background_middle_color, getResources().getColor(R.color.background_middle_state_color));
        colorLeftState = ta.getColor(R.styleable.ThreeStateSwitch_background_left_color, getResources().getColor(R.color.background_left_state_color));
        colorRightState = ta.getColor(R.styleable.ThreeStateSwitch_background_right_color, getResources().getColor(R.color.background_right_state_color));

        backgroundColor = colorMiddleState;

        textNormalColor = ta.getColor(R.styleable.ThreeStateSwitch_text_normal_color, getResources().getColor(R.color.text_normal_color));
        textSelectedLeftColor = ta.getColor(R.styleable.ThreeStateSwitch_text_selected_left_color, getResources().getColor(R.color.text_selected_left_color));
        textSelectedRightColor = ta.getColor(R.styleable.ThreeStateSwitch_text_selected_right_color, getResources().getColor(R.color.text_selected_right_color));

        textNormalSize = ta.getDimensionPixelSize(R.styleable.ThreeStateSwitch_text_normal_size, (int)getResources().getDimension(R.dimen.text_normal_size));
        textSelectedSize = ta.getDimensionPixelSize(R.styleable.ThreeStateSwitch_text_selected_size, (int)getResources().getDimension(R.dimen.text_selected_size));

        leftStateText = ta.getString(R.styleable.ThreeStateSwitch_text_left);
        if (leftStateText == null) {
            leftStateText = getResources().getString(R.string.text_left);
        }

        rightStateText = ta.getString(R.styleable.ThreeStateSwitch_text_right);
        if (rightStateText == null) {
            rightStateText = getResources().getString(R.string.text_right);
        }

        ta.recycle();

        ovalPaint = new Paint();
        ovalPaint.setColor(backgroundColor);
        ovalPaint.setAntiAlias(true);

        thumbIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.switch_circle);

        originalBitmapRect = new Rect();
        drawnBitmapRect = new Rect();
        ovalRectF = new RectF();
        textBounds = new Rect();

        textLeftPaint = new Paint();
        textLeftPaint.setColor(textSelectedLeftColor);
        textLeftPaint.setTextSize(textSelectedSize);
        textLeftPaint.setAntiAlias(true);

        textRightPaint = new Paint();
        textRightPaint.setColor(textSelectedRightColor);
        textRightPaint.setTextSize(textSelectedSize);
        textRightPaint.setAntiAlias(true);

        textNormalPaint = new Paint();
        textNormalPaint.setColor(textNormalColor);
        textNormalPaint.setTextSize(textNormalSize);
        textNormalPaint.setAntiAlias(true);
    }

    public interface OnStateChangeListener {
        void onStateChangeListener(SwitchStates currentState);
    }

    public void setOnChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }

    public int getBackColor() { return this.backgroundColor; }

    public int getXCircle() { return xCircle; }

    public void setBackColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    public void setXCircle(int xCircle) {
        this.xCircle = xCircle;
        invalidate();
    }

    public SwitchStates getState() { return this.state; }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        paddingRight = getPaddingRight();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();

        textLeftNormalWidth = textNormalPaint.measureText(leftStateText);
        textRightNormalWidth = textNormalPaint.measureText(rightStateText);

        textLeftSelectedWidth = textLeftPaint.measureText(leftStateText);
        textRightSelectedWidth = textRightPaint.measureText(rightStateText);

        maxNeededTextWidth = Math.max(textLeftNormalWidth, textRightNormalWidth);
        maxNeededTextWidth = Math.max(maxNeededTextWidth, textLeftSelectedWidth);
        maxNeededTextWidth = Math.max(maxNeededTextWidth, textRightSelectedWidth);
        maxNeededTextWidth += dpToPx(5);

        float minWidth = (maxNeededTextWidth * 2) + dpToPx(130) + paddingLeft + paddingRight;
        float minHeight = dpToPx(35) + paddingBottom + paddingTop;

        int pWidth = resolveSize((int) minWidth, widthSpec);
        int pHeight = resolveSize((int) minHeight, heightSpec);

        float leftRect = maxNeededTextWidth + paddingLeft;
        float topRect = paddingTop;
        float rightRect = pWidth - maxNeededTextWidth - paddingRight;
        float bottomRect = pHeight - paddingBottom;

        ovalRectF.set(leftRect, topRect, rightRect, bottomRect);

        diameterSize = Math.min(pWidth - paddingRight - paddingLeft - (int)(maxNeededTextWidth * 2),
                pHeight - paddingTop - paddingBottom);

        switch (state) {
            case LEFT_STATE:
                xCircle = (int)(ovalRectF.left + (diameterSize / 2.0));
            case MIDDLE_STATE:
                xCircle = (int)(ovalRectF.centerX());
                break;
            case RIGHT_STATE:
                xCircle = (int)(ovalRectF.right - (diameterSize / 2.0));
                break;
        }
        yCircle = (int) ovalRectF.centerY();

        viewWidth = pWidth;
        viewHeight = pHeight;
        setMeasuredDimension(pWidth, pHeight);
    }

    int delta = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;

        int action = event.getAction() & MotionEvent.ACTION_MASK;

        switch(action) {
            case MotionEvent.ACTION_DOWN: {
                this.getParent().requestDisallowInterceptTouchEvent(true);
                startX = event.getX();
                if (state == whichStateClick(event.getX(), event.getY())) {
                    delta = -1 * ((int)event.getX() - xCircle);
                    isPressed = true;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (!isDrag && Math.abs(startX - event.getX()) > 15) {
                    isDrag = true;
                }
                if (isDrag) {
                    int endBoundry = (int) Math.max((diameterSize / 2) - delta, event.getX());
                    int tempX = (int) Math.min(endBoundry, viewWidth - (diameterSize / 2));
                    xCircle = tempX + delta;
                    xCircle = setBoundForXCircle(xCircle);
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                SwitchStates tempState = whichStateClick(event.getX(), event.getY());
                isDrag = false;
                isPressed = false;
                changeState(tempState);
                break;
            }
        }
        invalidate();
        return true;
    }

    public void setNormalTextTypeface(Typeface typeface) {
        textNormalPaint.setTypeface(typeface);
    }

    public Typeface getNormalTextTypeface() {
        return textNormalPaint.getTypeface();
    }

    public void setSelectedLeftTextTypeface(Typeface typeface) {
        textLeftPaint.setTypeface(typeface);
    }

    public Typeface getSelectedLeftTextTypeface() {
        return textLeftPaint.getTypeface();
    }

    public void setSelectedRightTextTypeface(Typeface typeface) {
        textRightPaint.setTypeface(typeface);
    }

    public Typeface getSelectedRightTextTypeface() {
        return textRightPaint.getTypeface();
    }

    private SwitchStates whichStateClick(float x ,float y) {
        int yekSevom = (int)((viewWidth - (2 * maxNeededTextWidth) - paddingLeft - paddingRight) / 3.0);
        if (x < yekSevom + maxNeededTextWidth + paddingLeft)
            return SwitchStates.LEFT_STATE;
        else if (x > (2 * yekSevom) + maxNeededTextWidth + paddingLeft)
            return SwitchStates.RIGHT_STATE;
        return SwitchStates.MIDDLE_STATE;
    }

    private int setBoundForXCircle(float xCircle) {
        if (xCircle < ovalRectF.left + (diameterSize / 2.0))
            xCircle = (float)(ovalRectF.left + (diameterSize / 2.0));
        else if (xCircle > (float)(ovalRectF.right - (diameterSize / 2.0)))
            xCircle = (float)(ovalRectF.right - (diameterSize / 2.0));
        return (int)xCircle;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ovalPaint.setColor(backgroundColor);
        canvas.drawRoundRect(ovalRectF, diameterSize / 2, diameterSize / 2, ovalPaint);

        if (!isAnimate && !isDrag) {
            if (state == SwitchStates.LEFT_STATE) {
                xCircle = (int)(diameterSize / 2.0) + (int)ovalRectF.left;
            }
            else if (state == SwitchStates.MIDDLE_STATE) {
                xCircle = (int)(ovalRectF.centerX());
            }
            else if (state == SwitchStates.RIGHT_STATE) {
                xCircle = (int)(ovalRectF.right - (diameterSize / 2.0));
            }
            yCircle = (int)(ovalRectF.centerY());
        }

        if (isPressed)
            diameterSize += dpToPx(4);

        yCircle = (int)(ovalRectF.centerY());

        xCircle = setBoundForXCircle(xCircle);
        originalBitmapRect.set(0,0, thumbIcon.getWidth(), thumbIcon.getHeight());
        drawnBitmapRect.set(xCircle - (int)(diameterSize / 2.0),yCircle - (int)(diameterSize / 2.0),
                xCircle + (int)(diameterSize / 2.0), yCircle + (int)(diameterSize / 2.0));

        canvas.drawBitmap(thumbIcon, originalBitmapRect, drawnBitmapRect,null);
        if (isPressed)
            diameterSize -= dpToPx(4);

        if (state == SwitchStates.LEFT_STATE)
            drawCenter(canvas,(int)(maxNeededTextWidth / 2.0) + paddingLeft, (int)(ovalRectF.centerY()), textLeftPaint, leftStateText);
        else
            drawCenter(canvas,(int)(maxNeededTextWidth / 2.0) + paddingLeft, (int)(ovalRectF.centerY()), textNormalPaint, leftStateText);

        if (state == SwitchStates.RIGHT_STATE)
            drawCenter(canvas, (int)(viewWidth - (maxNeededTextWidth / 2.0) - paddingRight), (int)(ovalRectF.centerY()), textRightPaint, rightStateText);
        else
            drawCenter(canvas, (int)(viewWidth - (maxNeededTextWidth / 2.0) - paddingRight), (int)(ovalRectF.centerY()), textNormalPaint, rightStateText);
    }

    private Rect r = new Rect();
    private void drawCenter(Canvas canvas, int drawX, int drawY, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = drawX - r.width() / 2f - r.left;
        float y = drawY + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    public void changeState(SwitchStates newState) {
        this.changeState(newState,true);
    }

    public void changeState(SwitchStates newState, boolean animate)
    {
        if (!animate) {
            switch (newState) {
                case LEFT_STATE:
                    setBackColor(colorLeftState);
                    break;
                case MIDDLE_STATE:
                    setBackColor(colorMiddleState);
                    break;
                case RIGHT_STATE:
                    setBackColor(colorRightState);
                    break;
            }
            this.state = newState;
            return;
        }

        if (onStateChangeListener != null)
            onStateChangeListener.onStateChangeListener(newState);

        AnimatorSet animSet = new AnimatorSet();
        ArrayList<Animator> viewAnimList = new ArrayList<>();
        ObjectAnimator firstAnimation, secondAnimation;

        switch (newState) {
            case LEFT_STATE:
                firstAnimation = ObjectAnimator.ofInt(this, "BackColor", colorLeftState)
                        .setDuration(400);
                firstAnimation.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(firstAnimation);

                secondAnimation = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle((float)(diameterSize / 2.0)))
                        .setDuration(400);
                viewAnimList.add(secondAnimation);
                break;
            case MIDDLE_STATE:
                firstAnimation = ObjectAnimator.ofInt(this, "BackColor", colorMiddleState)
                        .setDuration(400);
                firstAnimation.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(firstAnimation);

                secondAnimation = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle(ovalRectF.centerX()))
                        .setDuration(400);
                viewAnimList.add(secondAnimation);
                break;
            case RIGHT_STATE:
                firstAnimation = ObjectAnimator.ofInt(this, "BackColor", colorRightState)
                        .setDuration(400);
                firstAnimation.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(firstAnimation);

                secondAnimation = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle((float)(viewWidth - (diameterSize / 2.0))))
                        .setDuration(400);
                viewAnimList.add(secondAnimation);
                break;
        }

        animSet.playTogether(viewAnimList);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { isAnimate = true; }

            @Override
            public void onAnimationEnd(Animator animation) { isAnimate = false; }

            @Override
            public void onAnimationCancel(Animator animation) { isAnimate = false; }

            @Override
            public void onAnimationRepeat(Animator animation) { isAnimate = false; }
        });

        animSet.start();
        this.state = newState;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, this.state);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        changeState(savedState.state,false);
    }

    /**
     * Convenience class to save / restore the lock combination picker state. Looks clumsy
     * but once created is easy to maintain and use.
     */
    protected static class SavedState extends BaseSavedState {

        public static final Parcelable.Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }

        };

        public final SwitchStates state;

        private SavedState(Parcelable superState, SwitchStates state) {
            super(superState);
            this.state = state;
        }

        private SavedState(Parcel in) {
            super(in);
            switch (in.readInt()) {
                case -1:
                    state = SwitchStates.LEFT_STATE;
                    break;
                case 0:
                    state = SwitchStates.MIDDLE_STATE;
                    break;
                case 1:
                    state = SwitchStates.RIGHT_STATE;
                    break;
                default:
                    state = SwitchStates.MIDDLE_STATE;
                    break;
            }
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            switch (state) {
                case MIDDLE_STATE:
                    destination.writeInt(0);
                    break;
                case LEFT_STATE:
                    destination.writeInt(-1);
                    break;
                case RIGHT_STATE:
                    destination.writeInt(1);
                    break;
            }
        }

    }
}