package org.vopen.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by GVERGINE on 9/14/2015.
 */
public class Resizer extends View implements View.OnTouchListener
{
    Drawable handle;
    int touchBorderThickness = 30; // so it's 60 really
    int prev_x, prev_y;
    //    private RelativeLayout parent;
    private onResizeListener mResizeListener = null;
    private int w = 0;
    private int h = 0;
    private int x = 0;
    private int y = 0;
    private Paint paintGood;
    private Paint paintBad;
    private Paint currentPaint;
    private Rect rect;
    private boolean draw = false;
    private EditState currentState = EditState.NOTHING;

    public Resizer(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        handle = ContextCompat.getDrawable(context, R.drawable.switch_thumb);

        rect = new Rect();
        paintGood = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGood.setColor(Color.GREEN);
        paintGood.setStyle(Paint.Style.STROKE);
        paintGood.setStrokeWidth(5);
        paintBad = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBad.setColor(Color.RED);
        paintBad.setStyle(Paint.Style.STROKE);
        paintBad.setStrokeWidth(5);
        currentPaint = paintGood;
        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        if (draw)
        {
            canvas.drawRect(rect, currentPaint);
            handle.setBounds(rect.left + w / 2 - 10, rect.top - 10, rect.left + w / 2 + 10, rect.top + 10);
            handle.draw(canvas);
            handle.setBounds(rect.left + w / 2 - 10, rect.bottom - 10, rect.left + w / 2 + 10, rect.bottom + 10);
            handle.draw(canvas);
            handle.setBounds(rect.left - 10, rect.top + h / 2 - 10, rect.left + 10, rect.top + h / 2 + 10);
            handle.draw(canvas);
            handle.setBounds(rect.right - 10, rect.top + h / 2 - 10, rect.right + 10, rect.top + h / 2 + 10);
            handle.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        int touch_x = (int)motionEvent.getX();
        int touch_y = (int)motionEvent.getY();

        switch (motionEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                onActionDown(touch_x, touch_y);
                break;
            case MotionEvent.ACTION_MOVE:
                onActionMove(touch_x, touch_y);
                break;
            case MotionEvent.ACTION_UP:
                onActionUp(touch_x, touch_y);
                break;
            default:
                break;
        }

        return true;
    }

    public void setPaint(boolean good)
    {
        if (good)
        {
            currentPaint = paintGood;
        }
        else
        {
            currentPaint = paintBad;
        }
        invalidate();
    }

    public void setResizeListener(onResizeListener listener)
    {
        mResizeListener = listener;
    }

    public void show()
    {
        draw = true;
        invalidate();
    }

    public void hide()
    {
        draw = false;
        invalidate();
    }

    public void setPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
        rect.left = x;
        rect.top = y;
        rect.right = x + w;
        rect.bottom = y + h;
        invalidate();
    }

    public void setSize(int w, int h)
    {
        this.w = w;
        this.h = h;
        rect.right = x + w;
        rect.bottom = y + h;
        invalidate();
    }

    private void onActionDown(int touch_x, int touch_y)
    {
        if (mResizeListener != null)
        {
            mResizeListener.onSelecting(touch_x, touch_y);
        }

        int rel_x = touch_x - x;
        int rel_y = touch_y - y;

        if ((rel_x > touchBorderThickness) && (rel_x < w - touchBorderThickness) && (rel_y < touchBorderThickness && rel_y > -touchBorderThickness))
        {
            currentState = EditState.RESIZING_NORTH;
        }
        else if ((rel_x > touchBorderThickness) && (rel_x < w - touchBorderThickness) && (rel_y < touchBorderThickness + h && rel_y > -touchBorderThickness + h))
        {
            currentState = EditState.RESIZING_SOUTH;
        }
        else if ((rel_y > touchBorderThickness) && (rel_y < h - touchBorderThickness) && (rel_x < touchBorderThickness && rel_x > -touchBorderThickness))
        {
            currentState = EditState.RESIZING_WEST;
        }
        else if ((rel_y > touchBorderThickness) && (rel_y < h - touchBorderThickness) && (rel_x < touchBorderThickness + w && rel_x > -touchBorderThickness + w))
        {
            currentState = EditState.RESIZING_EAST;
        }
        else if ((rel_y > touchBorderThickness) && (rel_y < h - touchBorderThickness) && (rel_x > touchBorderThickness && rel_x < touchBorderThickness + w))
        {
            currentState = EditState.MOVING;
            prev_x = touch_x;
            prev_y = touch_y;
        }
        else
        {
            currentState = EditState.NOTHING;
        }

        Log.v("CurrentState", "rel_x: " + rel_x + " rel_y:" + rel_y + " --- " + currentState.toString());
    }

    private void onActionMove(int touch_x, int touch_y)
    {
        int rel_x = touch_x - x;
        int rel_y = touch_y - y;

        switch (currentState)
        {
            case MOVING:
                rel_x = touch_x - prev_x;
                rel_y = touch_y - prev_y;
                setPosition(x + rel_x, y + rel_y);
                if (mResizeListener != null)
                {
                    mResizeListener.onMoving(rel_x, rel_y);
                }
                prev_x = touch_x;
                prev_y = touch_y;
                break;

            case RESIZING_NORTH:
                setPosition(x, y + rel_y);
                setSize(w, h - rel_y);
                if (mResizeListener != null)
                {
                    mResizeListener.onResizing(x, y, w, h);
                }
                break;

            case RESIZING_SOUTH:
                setSize(w, rel_y);
                if (mResizeListener != null)
                {
                    mResizeListener.onResizing(x, y, w, h);
                }
                break;

            case RESIZING_EAST:
                setSize(rel_x, h);
                if (mResizeListener != null)
                {
                    mResizeListener.onResizing(x, y, w, h);
                }
                break;

            case RESIZING_WEST:
                setPosition(x + rel_x, y);
                setSize(w - rel_x, h);
                if (mResizeListener != null)
                {
                    mResizeListener.onResizing(x, y, w, h);
                }
                break;

            case NOTHING:
                break;
        }
    }

    private void onActionUp(int touch_x, int touch_y)
    {
        if (currentState == EditState.NOTHING)
        {
            if (mResizeListener != null)
            {
                mResizeListener.onSelecting(touch_x, touch_y);
            }
        }

        if (currentState == EditState.RESIZING_NORTH ||
                currentState == EditState.RESIZING_EAST ||
                currentState == EditState.RESIZING_SOUTH ||
                currentState == EditState.RESIZING_WEST)
        {
            if (mResizeListener != null)
            {
                mResizeListener.onFinishedResizing(x, y, w, h);
            }
        }

        if (currentState == EditState.MOVING)
        {
            if (mResizeListener != null)
            {
                mResizeListener.onFinishedMoving();
            }
        }

        currentState = EditState.NOTHING;
    }

    private enum EditState
    {
        MOVING,
        RESIZING_NORTH,
        RESIZING_SOUTH,
        RESIZING_EAST,
        RESIZING_WEST,
        NOTHING
    }

    public interface onResizeListener
    {
        void onResizing(int left, int top, int width, int height);

        void onFinishedResizing(int left, int top, int width, int height);

        void onSelecting(int touch_x, int touch_y);

        void onMoving(int rel_x, int rel_y);

        void onFinishedMoving();
    }
}
