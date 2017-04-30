package org.vopen.dashboard;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.io.Serializable;

/**
 * Created by dpandeli on 9/2/2015.
 */
public class FlippableView extends FrameLayout implements Serializable
{
    final Handler handler = new Handler();
    protected View frontView;
    protected View backView;
    protected boolean isFrontViewVisible = true;
    boolean longPressDetected = false;
    private float lastX, lastY;
    private OnFlipRequestListener listener;
    Runnable mLongPressed = new Runnable()
    {
        public void run()
        {
            longPressDetected = true;

            if (listener != null)
            {
                listener.OnFlipRequest(frontView);
            }
            //    handler.removeCallbacks(mLongPressed);
        }
    };

    public FlippableView(Context context)
    {
        super(context);
    }

    public FlippableView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FlippableView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.widget_container);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                Log.i("dispatchTouchEvent", "ACTION_DOWN");
                lastX = ev.getRawX();
                lastY = ev.getRawY();
                handler.postDelayed(mLongPressed, 600);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i("dispatchTouchEvent", "ACTION_MOVE");
                float x = ev.getRawX();
                float y = ev.getRawY();
                if ((x - lastX) * (x - lastX) + (y - lastY) * (y - lastY) > 100)
                {
                    handler.removeCallbacks(mLongPressed);
                }
                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
                Log.i("dispatchTouchEvent", "ACTION_UP");
                handler.removeCallbacks(mLongPressed);
                longPressDetected = false;
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (longPressDetected)
        {
            longPressDetected = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        return true;
    }

    /**
     * This sets the two views that will be used for front and back of the flippable view
     *
     * @param frontView
     * @param backView
     */
    public void setViews(View frontView, View backView)
    {
        removeAllViews();
        this.frontView = frontView;
        if (frontView != null)
        {
            addView(frontView);
        }

        this.backView = backView;
        if (backView != null)
        {
            addView(backView);
        }

        showFrontView();
    }

    public void swapViewsAnimated()
    {
        if (isFrontViewVisible)
        {
            isFrontViewVisible = false;
            AnimationCollection.flipViewHorizontally(frontView, backView);
        }
        else
        {
            isFrontViewVisible = true;
            AnimationCollection.flipViewHorizontally(backView, frontView);
        }
    }

    public void swapViews()
    {
        if (isFrontViewVisible)
        {
            showBackView();
        }
        else
        {
            showFrontView();
        }
    }

    public void setOnFlipRequestListener(OnFlipRequestListener listener)
    {
        this.listener = listener;
    }

    /////////////////

    /**
     * Shows the front view AND hides the back view
     */
    private void showFrontView()
    {
        frontView.setVisibility(VISIBLE);
        backView.setVisibility(INVISIBLE);
        isFrontViewVisible = true;
    }

    /**
     * Shows the back view AND hides the front view
     */
    private void showBackView()
    {
        frontView.setVisibility(INVISIBLE);
        backView.setVisibility(VISIBLE);
        isFrontViewVisible = false;
    }

    public interface OnFlipRequestListener
    {
        boolean OnFlipRequest(View v);
    }
}
