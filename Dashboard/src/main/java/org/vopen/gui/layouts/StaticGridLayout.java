package org.vopen.gui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import org.vopen.dashboard.Matrix;
import org.vopen.dashboard.MatrixRect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class StaticGridLayout extends ViewGroup implements ViewTreeObserver.OnGlobalLayoutListener, Serializable
{
    protected int cellWidthDp;
    protected int cellHeightDp;
    protected Matrix matrix;
    protected Map<View, MatrixRect> positionsMap = new HashMap<View, MatrixRect>();
    private int widthDp, heightDp;
    private int rows, columns;
    private List<OnLayoutMeasured> listeners = new ArrayList<OnLayoutMeasured>();

    public StaticGridLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        cellWidthDp = 60;
        cellHeightDp = 60;
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        widthDp = w;
        heightDp = h;

        columns = widthDp / cellWidthDp;
        rows = heightDp / cellHeightDp;

        widthDp = columns * cellWidthDp;
        heightDp = rows * cellHeightDp;

        this.setMeasuredDimension(widthDp, heightDp);

        try
        {
            for (int i = 0; i < getChildCount(); i++)
            {
                // MatrixRect pos = positionsMap.get(getChildAt(i)); TODO to delete this comment
                MatrixRect pos = matrix.getObjectsPositions().get(getChildAt(i));
                //  Log.v("onMeasure MatrixRect" ,"pos.columnStart: " + pos.columnStart + " pos.rowStart: " + pos.rowStart + "pos.columnEnd: " + pos.columnEnd + " pos.rowEnd: " + pos.rowEnd);
                if (pos == null)
                {
                    //workaround - to be investigated
                    continue;
                }
                int wspec = MeasureSpec.makeMeasureSpec((pos.columnEnd - pos.columnStart + 1) * cellWidthDp, MeasureSpec.EXACTLY);
                int hspec = MeasureSpec.makeMeasureSpec((pos.rowEnd - pos.rowStart + 1) * cellHeightDp, MeasureSpec.EXACTLY);

                //  Log.v("onMeasure","wspec: " + rows + " hspec: " + columns + "widthDp: " + (pos.columnEnd - pos.columnStart + 1) * cellWidthDp + " heightDp: " + (pos.rowEnd - pos.rowStart + 1) * cellHeightDp);
                getChildAt(i).measure(wspec, hspec);
            }
        }
        catch (NullPointerException exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);
        try
        {
            matrix.removeObject(child);
        }
        catch (Matrix.MatrixException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        // Log.v("StaticGridLayout", "onLayout");
        for (int i = 0; i < getChildCount(); i++)
        {
            MatrixRect pos = matrix.getObjectsPositions().get(getChildAt(i));
            if (pos == null)
            {
                continue;
            }
            int cl;
            int ct;
            int cr;
            int cb;
            cl = pos.columnStart * cellWidthDp;
            ct = pos.rowStart * cellHeightDp;
            cr = (pos.columnEnd + 1) * cellWidthDp;
            cb = (pos.rowEnd + 1) * cellHeightDp;

            //Log.v("onLayout", "cl: " + cl + " ct: " + ct + " cr: " + cr + " cb: " + cb);

            getChildAt(i).layout(cl, ct, cr, cb);
        }
    }

    @Override
    public void onGlobalLayout()
    {
        //  Log.v("Matrix - onGlobalLayout",this.toString());

        if (matrix == null)
        {
            matrix = new Matrix(columns, rows);
            Log.v("onGlobalLayout", "matrix(" + matrix.toString() + ") = new Matrix(columns, rows)");
        }

        for (OnLayoutMeasured listener : listeners)
        {
            listener.onLayoutMeasured(columns, rows);
        }

        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public void setCellSizeDp(int cellWidthDp, int cellHeightDp)
    {
        this.cellWidthDp = cellWidthDp;
        this.cellHeightDp = cellHeightDp;
    }

    public void addViewAt(View view, int columnStart, int rowStart, int columnEnd, int rowEnd)
    {
        addView(view);

        MatrixRect pos = new MatrixRect(columnStart, columnEnd, rowStart, rowEnd);
        pos.columnStart = columnStart;
        pos.columnEnd = columnEnd;
        pos.rowStart = rowStart;
        pos.rowEnd = rowEnd;

        try
        {
            matrix.addObject(view, pos);
        }
        catch (Matrix.MatrixException e)
        {
            e.printStackTrace();
        }
    }

    public View getViewAt(int columnStart, int rowStart)
    {
        View viewAtPosition = null;

        try
        {
            viewAtPosition = (View)matrix.getObjectAt(columnStart, rowStart);
        }
        catch (Matrix.MatrixException e)
        {
            e.printStackTrace();
        }

        return viewAtPosition;
    }

    public void addOnLayoutMeasuredListener(OnLayoutMeasured listener)
    {
        this.listeners.add(listener);
    }

    public void removeOnLayoutMeasuredListener(OnLayoutMeasured listener)
    {
        this.listeners.remove(listener);
    }

    public boolean isCoordFree(int x, int y)
    {
        int column = x / cellWidthDp;
        int row = y / cellHeightDp;
        try
        {
            return matrix.isRectFree(column, row, column, row);
        }
        catch (Matrix.MatrixException e)
        {
            return false;
        }
    }

    public Pair<Integer, Integer> getMatrixCoordsForPixelCoord(int x, int y)
    {
        int column = x / cellWidthDp;
        int row = y / cellHeightDp;
        // Log.v("calculation","column: " + column + " row:" + row);
        return new Pair<Integer, Integer>(column, row);
    }

    public Matrix getStaticGridLayoutMatrix()
    {
        return matrix;
    }

    public interface OnLayoutMeasured
    {
        void onLayoutMeasured(int columns, int rows);
    }
}
