package org.vopen.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by GVERGINE on 1/5/2016.
 */
public class PageIndicator extends View
{
    Paint backgroundPainter;
    Paint emptySquarePainter;
    Paint fullSquarePainter;
    private int currentPage;
    private int width, height;

    public PageIndicator(Context context)
    {
        super(context);
        init();
    }

    public PageIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        int min = width < height ? width : height;
        min = min / 2;
        int smallsquaresize = min / 7;

        int x_offset = (width - min) / 2;
        int y_offset = (height - min) / 2;
        RectF rf = new RectF(x_offset, y_offset, x_offset + min, y_offset + min);
        canvas.drawRoundRect(rf, 50, 50,
                backgroundPainter
        );

        for (int i = 0; i < 9; ++i)
        {
            RectF rfsmall = new RectF(x_offset + ((i % 3) * 2 + 1) * smallsquaresize, y_offset + ((i / 3) * 2 + 1) * smallsquaresize, x_offset + ((i % 3) * 2 + 1) * smallsquaresize + smallsquaresize, y_offset + ((i / 3) * 2 + 1) * smallsquaresize + smallsquaresize);

            if (currentPage == i)
            {
                canvas.drawRoundRect(rfsmall, 50, 50, fullSquarePainter);
            }
            else
            {
                canvas.drawRoundRect(rfsmall, 50, 50, emptySquarePainter);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        width = parentWidth;
        height = parentHeight;
        this.setMeasuredDimension(parentWidth, parentHeight);
        this.setLayoutParams(new FrameLayout.LayoutParams(parentWidth, parentHeight));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(int page)
    {
        currentPage = page;
        invalidate();
        requestLayout();
    }

    private void init()
    {
        backgroundPainter = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptySquarePainter = new Paint(Paint.ANTI_ALIAS_FLAG);
        fullSquarePainter = new Paint(Paint.ANTI_ALIAS_FLAG);

        backgroundPainter.setColor(Color.LTGRAY);
        emptySquarePainter.setColor(Color.DKGRAY);
        fullSquarePainter.setColor(Color.WHITE);

        backgroundPainter.setStyle(Paint.Style.FILL);
        backgroundPainter.setAlpha(128);
        emptySquarePainter.setStyle(Paint.Style.FILL);
        emptySquarePainter.setAlpha(230);
        fullSquarePainter.setStyle(Paint.Style.FILL);

    }
}
