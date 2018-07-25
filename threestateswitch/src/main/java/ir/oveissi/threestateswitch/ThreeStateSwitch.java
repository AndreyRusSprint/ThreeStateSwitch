package ir.oveissi.threestateswitch;

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
import android.graphics.Color;
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

/**
 * Created by abbas on 8/6/16.
 */

public class ThreeStateSwitch extends View {
    private Context context;
    private AttributeSet attrs;

    private int state=0;
    private boolean isDrag;
    private float xDrag;
    private float yDrag;
    private float startX;
    private boolean isAnimate;
    private boolean isPressed;
    private Typeface selectedTextTypeface;
    private Typeface normalTextTypeface;

    int text_normal_color ;
    int text_selected_left_color;
    int text_selected_right_color;
    int text_normal_size ;
    int text_selected_size;

    int colorStateUnSelected = Color.parseColor("#bfbfbf");
    int colorStateSelectedLeft = Color.parseColor("#5ab72e");
    int colorStateSelectedRight = Color.parseColor("#cc2900");

    public int backgroundColor;

    Paint ovalPaint, textSelectedLeftPaint, textSelectedRightPaint, textNormalPaint;
    Bitmap thumbIcon;
    RectF ovalRectF;
    Rect orginalBitmapRect,drawnBitmapRect;
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

    String lessText = "";
    String moreText = "";

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
        this.context = context;
        this.attrs = attrs;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ThreeStateSwitch);

        colorStateSelectedLeft = ta.getColor(R.styleable.ThreeStateSwitch_background_selected_left_color, getResources().getColor(R.color.background_selected_left_color));
        colorStateSelectedRight = ta.getColor(R.styleable.ThreeStateSwitch_background_selected_right_color, getResources().getColor(R.color.background_selected_right_color));
        colorStateUnSelected = ta.getColor(R.styleable.ThreeStateSwitch_background_normal_color, getResources().getColor(R.color.background_normal_color));

        backgroundColor = colorStateUnSelected;

        text_normal_color = ta.getColor(R.styleable.ThreeStateSwitch_text_normal_color, getResources().getColor(R.color.text_normal_color));
        text_selected_left_color = ta.getColor(R.styleable.ThreeStateSwitch_text_selected_left_color, getResources().getColor(R.color.text_selected_left_color));
        text_selected_right_color = ta.getColor(R.styleable.ThreeStateSwitch_background_selected_right_color, getResources().getColor(R.color.text_selected_right_color));

        text_normal_size = ta.getDimensionPixelSize(R.styleable.ThreeStateSwitch_text_normal_size, (int)getResources().getDimension(R.dimen.text_normal_size));
        text_selected_size = ta.getDimensionPixelSize(R.styleable.ThreeStateSwitch_text_selected_size, (int)getResources().getDimension(R.dimen.text_selected_size));

        lessText = ta.getString(R.styleable.ThreeStateSwitch_text_left);
        if (lessText == null) {
            lessText=getResources().getString(R.string.text_left);
        }

        moreText = ta.getString(R.styleable.ThreeStateSwitch_text_right);
        if (moreText == null) {
            moreText=getResources().getString(R.string.text_right);
        }

        ta.recycle();

        ovalPaint = new Paint();
        ovalPaint.setColor(backgroundColor);
        ovalPaint.setAntiAlias(true);

        thumbIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.switch_circle);

        orginalBitmapRect = new Rect();
        drawnBitmapRect = new Rect();
        ovalRectF = new RectF();
        textBounds = new Rect();

        textSelectedLeftPaint = new Paint();
        textSelectedLeftPaint.setColor(text_selected_left_color);
        textSelectedLeftPaint.setTextSize(text_selected_size);
        textSelectedLeftPaint.setAntiAlias(true);

        textSelectedRightPaint = new Paint();
        textSelectedRightPaint.setColor(text_selected_right_color);
        textSelectedRightPaint.setTextSize(text_selected_size);
        textSelectedRightPaint.setAntiAlias(true);

        textNormalPaint = new Paint();
        textNormalPaint.setColor(text_normal_color);
        textNormalPaint.setTextSize(text_normal_size);
        textNormalPaint.setAntiAlias(true);
    }

    public interface OnStateChangeListener {
        void onStateChangeListener(int currentState);
    }

    public OnStateChangeListener onStateChangeListener;

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

    public int getState() { return this.state; }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {

        paddingRight = getPaddingRight();
        paddingLeft = getPaddingLeft();
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();

        textLeftNormalWidth = textNormalPaint.measureText(lessText);
        textRightNormalWidth = textNormalPaint.measureText(moreText);

        textLeftSelectedWidth = textSelectedLeftPaint.measureText(lessText);
        textRightSelectedWidth = textSelectedRightPaint.measureText(moreText);

        maxNeededTextWidth = Math.max(textLeftNormalWidth, textRightNormalWidth);
        maxNeededTextWidth = Math.max(maxNeededTextWidth, textLeftSelectedWidth);
        maxNeededTextWidth = Math.max(maxNeededTextWidth, textRightSelectedWidth);
        maxNeededTextWidth += dpToPx(5);

        float minWidth = (2*maxNeededTextWidth) + dpToPx(130) + paddingLeft + paddingRight;
        float minHeight = dpToPx(35) + paddingBottom + paddingTop;

        int pWidth= resolveSize((int) minWidth, widthSpec);
        int pHeight= resolveSize((int) minHeight, heightSpec);

        float leftRect = maxNeededTextWidth + paddingLeft;
        float topRect = paddingTop;
        float rightRect = pWidth - maxNeededTextWidth - paddingRight;
        float bottomRect = pHeight - paddingBottom;

        ovalRectF.set(leftRect, topRect, rightRect, bottomRect);

        diameterSize = Math.min(pWidth - paddingRight - paddingLeft - (int)(2*maxNeededTextWidth)
                , pHeight - paddingTop - paddingBottom);

        switch (state) {
            case -1:
                xCircle = (int)(ovalRectF.left + (diameterSize / 2.0));
            case 0:
                xCircle = (int)(ovalRectF.centerX());
                break;
            case 1:
                xCircle = (int)(ovalRectF.right - (diameterSize / 2.0));
                break;
        }
        yCircle = (int) ovalRectF.centerY();

        viewWidth = pWidth;
        viewHeight = pHeight;
        setMeasuredDimension(pWidth, pHeight);
    }

    float textLeftSelectedWidth, textRightSelectedWidth, textLeftNormalWidth, textRightNormalWidth, maxNeededTextWidth;

    int delta = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                int tempState = whichStateClick(event.getX(), event.getY());
                isDrag = false;
                isPressed = false;
                changeState(tempState);
                break;
            }
        }
        xDrag=event.getX();
        yDrag=event.getY();
        invalidate();
        return true;
    }

    public void setNormalTextTypeface(Typeface typeface) {
        this.normalTextTypeface = typeface;
        textNormalPaint.setTypeface(typeface);
    }

    public void setSelectedLeftTextTypeface(Typeface typeface) {
        this.selectedTextTypeface = typeface;
        textSelectedLeftPaint.setTypeface(typeface);
    }

    public void setSelectedRightTextTypeface(Typeface typeface) {
        this.selectedTextTypeface = typeface;
        textSelectedRightPaint.setTypeface(typeface);
    }

    private int whichStateClick(float x ,float y) {
        int yekSevom = (int)((viewWidth - (2 * maxNeededTextWidth) - paddingLeft - paddingRight) / 3.0);
        if (x < yekSevom + maxNeededTextWidth + paddingLeft)
            return -1;
        else if (x > (2 * yekSevom) + maxNeededTextWidth + paddingLeft)
            return 1;
        return 0;
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
            if (state == -1) {
                xCircle = (int)(diameterSize / 2.0) + (int)ovalRectF.left;
            }
            else if (state == 0) {
                xCircle = (int)(ovalRectF.centerX());
            }
            else if (state == 1) {
                xCircle = (int)(ovalRectF.right - (diameterSize / 2.0));
            }
            yCircle = (int)(ovalRectF.centerY());
        }

        if (isPressed)
            diameterSize += dpToPx(4);

        yCircle = (int)(ovalRectF.centerY());

        xCircle = setBoundForXCircle(xCircle);
        orginalBitmapRect.set(0,0, thumbIcon.getWidth(), thumbIcon.getHeight());
        drawnBitmapRect.set(xCircle - (int)(diameterSize / 2.0),yCircle - (int)(diameterSize / 2.0)
                ,xCircle + (int)(diameterSize / 2.0), yCircle + (int)(diameterSize / 2.0));

        canvas.drawBitmap(thumbIcon, orginalBitmapRect, drawnBitmapRect,null);
        if (isPressed)
            diameterSize -= dpToPx(4);

        if (state == -1)
            drawCenter(canvas,(int)(maxNeededTextWidth / 2.0) + paddingLeft, (int)(ovalRectF.centerY()), textSelectedLeftPaint, lessText);
        else
            drawCenter(canvas,(int)(maxNeededTextWidth / 2.0) + paddingLeft, (int)(ovalRectF.centerY()), textNormalPaint, lessText);

        if (state == 1)
            drawCenter(canvas, (int)(viewWidth - (maxNeededTextWidth / 2.0) - paddingRight), (int)(ovalRectF.centerY()), textSelectedRightPaint, moreText);
        else
            drawCenter(canvas, (int)(viewWidth - (maxNeededTextWidth / 2.0) - paddingRight), (int)(ovalRectF.centerY()), textNormalPaint, moreText);

    }

    private Rect r = new Rect();
    private void drawCenter(Canvas canvas, int drawX, int drawY, Paint paint, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        paint.getTextBounds(text, 0, text.length(), r);
        float x = drawX - r.width() / 2f - r.left;
        float y = drawY + r.height() / 2f - r.bottom;
        canvas.drawText(text, x, y, paint);
    }

    public void changeState(int mState) {
        this.changeState(mState,true);
    }

    public void changeState(int mState,boolean animate)
    {
        if(!animate) {
            switch (mState) {
                case -1:
                    setBackColor(colorStateSelectedLeft);
                    break;
                case 0:
                    setBackColor(colorStateUnSelected);
                    break;
                case 1:
                    setBackColor(colorStateSelectedRight);
                    break;
            }
            this.state=mState;
            return;
        }

        if (onStateChangeListener != null)
            onStateChangeListener.onStateChangeListener(mState);

        AnimatorSet animSet = new AnimatorSet();
        ArrayList<Animator> viewAnimList = new ArrayList<Animator>();
        ObjectAnimator anim, anim1;

        switch (mState) {
            case -1:
                anim = ObjectAnimator.ofInt(this, "BackColor", colorStateSelectedLeft)
                        .setDuration(400);
                anim.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(anim);

                anim1 = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle((float)(diameterSize / 2.0)))
                        .setDuration(400);
                viewAnimList.add(anim1);
                break;
            case 0:
                anim = ObjectAnimator.ofInt(this, "BackColor", colorStateUnSelected)
                        .setDuration(400);
                anim.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(anim);

                anim1 = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle((float)(ovalRectF.centerX())))
                        .setDuration(400);
                viewAnimList.add(anim1);
                break;
            case 1:
                anim = ObjectAnimator.ofInt(this, "BackColor", colorStateSelectedRight)
                        .setDuration(400);
                anim.setEvaluator(new ArgbEvaluator());
                viewAnimList.add(anim);

                anim1 = ObjectAnimator.ofInt(this, "xCircle", setBoundForXCircle((float)(viewWidth- (diameterSize / 2.0))))
                        .setDuration(400);
                viewAnimList.add(anim1);
                break;
        }

        animSet.playTogether(viewAnimList);
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { isAnimate=true; }

            @Override
            public void onAnimationEnd(Animator animation) { isAnimate=false; }

            @Override
            public void onAnimationCancel(Animator animation) { isAnimate=false; }

            @Override
            public void onAnimationRepeat(Animator animation) { isAnimate=false; }
        });

        animSet.start();
        this.state = mState;
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

        public final Integer state;

        private SavedState(Parcelable superState, Integer state) {
            super(superState);
            this.state = state;
        }

        private SavedState(Parcel in) {
            super(in);
            state = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel destination, int flags) {
            super.writeToParcel(destination, flags);
            destination.writeInt(state);
        }

    }
}