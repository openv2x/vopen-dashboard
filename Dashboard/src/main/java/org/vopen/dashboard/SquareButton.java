package org.vopen.dashboard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by giovanni on 05/01/16.
 */
public class SquareButton extends LinearLayout
{
    public SquareButton(Context context)
    {
        super(context);
    }

    public SquareButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    // This is used to make square buttons.
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int min = parentWidth < parentHeight ? parentWidth : parentHeight;
        int size = min > 120 ? min : 120;
        this.setMeasuredDimension(size, size);
        //this.setLayoutParams(new FlowLayout.LayoutParams(parentWidth, parentHeight));
    }
}