package org.vopen.gui.layouts;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import org.vopen.dashboard.Matrix;
import org.vopen.dashboard.MatrixRect;
import org.vopen.dashboard.Resizer;

/**
 * Created by GVERGINE on 9/23/2015.
 */
public class EditableStaticGridLayout extends StaticGridLayout implements Resizer.onResizeListener
{


    int cur_x, cur_y;
    private Boolean editModeEnabled = false;
    private Switch editSwitch;
    private View currentSelectedView = null;
    private Resizer resizer;

    public EditableStaticGridLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public EditableStaticGridLayout(Context context, AttributeSet attrs, Matrix m)
    {
        super(context, attrs);
        matrix = m;
    }

    public Matrix getGridMatrix()
    {
        return matrix;
    }

    @Override
    public void onResizing(int left, int top, int width, int height)
    {

        // Log.v("onMoving","left: " + left + " top:" + top);

        if (currentSelectedView != null)
        {
            MatrixRect gr = new MatrixRect(matrix.getObjectsPositions().get(currentSelectedView));
            gr.columnStart = (left + cellWidthDp / 2) / cellWidthDp;
            gr.rowStart = (top + cellHeightDp / 2) / cellHeightDp;
            gr.columnEnd = ((left + width) - cellWidthDp / 2) / cellWidthDp;
            gr.rowEnd = ((top + height) - cellHeightDp / 2) / cellHeightDp;

            try
            {
                matrix.moveObject(currentSelectedView, gr);
                resizer.setPaint(true);
                int cl;
                int ct;
                int cr;
                int cb;
                cl = gr.columnStart * cellWidthDp;
                ct = gr.rowStart * cellHeightDp;
                cr = (gr.columnEnd + 1) * cellWidthDp;
                cb = (gr.rowEnd + 1) * cellHeightDp;

                int wspec = MeasureSpec.makeMeasureSpec((gr.columnEnd - gr.columnStart + 1) * cellWidthDp, MeasureSpec.EXACTLY);
                int hspec = MeasureSpec.makeMeasureSpec((gr.rowEnd - gr.rowStart + 1) * cellHeightDp, MeasureSpec.EXACTLY);
                currentSelectedView.measure(wspec, hspec);
                currentSelectedView.layout(cl, ct, cr, cb);
            }
            catch (Matrix.MatrixException e)
            {
                resizer.setPaint(false);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFinishedResizing(int left, int top, int width, int height)
    {
        if (currentSelectedView != null)
        {
            MatrixRect gr = matrix.getObjectsPositions().get(currentSelectedView);
            int cl;
            int ct;
            int cr;
            int cb;
            cl = gr.columnStart * cellWidthDp;
            ct = gr.rowStart * cellHeightDp;
            cr = (gr.columnEnd + 1) * cellWidthDp;
            cb = (gr.rowEnd + 1) * cellHeightDp;

            currentSelectedView.layout(cl, ct, cr, cb);
            resizer.setPosition(cl, ct);
            resizer.setSize(cr - cl, cb - ct);
            resizer.setPaint(true);
            currentSelectedView.invalidate();
        }

    }

    @Override
    public void onSelecting(int touch_x, int touch_y)
    {
        currentSelectedView = findViewThatContains(touch_x, touch_y);
        if (currentSelectedView != null)
        {
            bringChildToFront(currentSelectedView);
            requestLayout();
            MatrixRect gr = matrix.getObjectsPositions().get(currentSelectedView);
            cur_x = gr.columnStart * cellWidthDp;
            cur_y = gr.rowStart * cellHeightDp;
            resizer.setPosition(gr.columnStart * cellWidthDp, gr.rowStart * cellHeightDp);
            resizer.setSize((gr.columnEnd - gr.columnStart + 1) * cellWidthDp, (gr.rowEnd - gr.rowStart + 1) * cellHeightDp);
            resizer.show();
        }
        else
        {
            resizer.hide();
            editSwitch.setChecked(false);
        }

    }

    @Override
    public void onMoving(int rel_x, int rel_y)
    {
        Log.v("onMoving", "rel_x: " + rel_x + " rel_y:" + rel_y);
        if (currentSelectedView != null)
        {
            cur_x += rel_x;
            cur_y += rel_y;


            MatrixRect gr = new MatrixRect(matrix.getObjectsPositions().get(currentSelectedView));

            int w = gr.columnEnd - gr.columnStart;
            int h = gr.rowEnd - gr.rowStart;
            gr.columnStart = (cur_x) / cellWidthDp;
            gr.rowStart = (cur_y) / cellHeightDp;
            gr.columnEnd = gr.columnStart + w;
            gr.rowEnd = gr.rowStart + h;
            int cl;
            int ct;
            int cr;
            int cb;
            cl = gr.columnStart * cellWidthDp;
            ct = gr.rowStart * cellHeightDp;
            cr = (gr.columnEnd + 1) * cellWidthDp;
            cb = (gr.rowEnd + 1) * cellHeightDp;


            try
            {
                matrix.moveObject(currentSelectedView, gr);
                resizer.setPaint(true);
                currentSelectedView.layout(cl, ct, cr, cb);

            }
            catch (Matrix.MatrixException e)
            {
                resizer.setPaint(false);
                e.printStackTrace();
            }
            resizer.setPosition(cl, ct);
            resizer.setSize(cr - cl, cb - ct);
            currentSelectedView.invalidate();
        }
    }

    @Override
    public void onFinishedMoving()
    {
        if (currentSelectedView != null)
        {
            MatrixRect gr = new MatrixRect(matrix.getObjectsPositions().get(currentSelectedView));

            int cl;
            int ct;
            int cr;
            int cb;
            cl = gr.columnStart * cellWidthDp;
            ct = gr.rowStart * cellHeightDp;
            cr = (gr.columnEnd + 1) * cellWidthDp;
            cb = (gr.rowEnd + 1) * cellHeightDp;

            resizer.setPosition(cl, ct);
            resizer.setSize(cr - cl, cb - ct);
            resizer.setPaint(true);

        }
    }

    public void setEditSwitch(Switch editSwitch)
    {
        this.editSwitch = editSwitch;
    }

    public void setResizer(Resizer resizer)
    {
        this.resizer = resizer;
        resizer.setResizeListener(this);
    }

    public void editModeChanged(boolean editModeEnabled)
    {
        //  Log.v("edit mode","" + editModeEnabled);
        this.editModeEnabled = editModeEnabled;

        resizer.hide();

        if (editModeEnabled)
        {

            resizer.setVisibility(VISIBLE);

            if (currentSelectedView != null)
            {
                MatrixRect gr = matrix.getObjectsPositions().get(currentSelectedView);
                if (gr != null)
                {
                    cur_x = gr.columnStart * cellWidthDp;
                    cur_y = gr.rowStart * cellHeightDp;
                    resizer.setPosition(gr.columnStart * cellWidthDp, gr.rowStart * cellHeightDp);
                    resizer.setSize((gr.columnEnd - gr.columnStart + 1) * cellWidthDp, (gr.rowEnd - gr.rowStart + 1) * cellHeightDp);

                    resizer.show();
                }
                else
                {
                    currentSelectedView = null;
                }
            }
        }
        else
        {
            resizer.setVisibility(GONE);
            resizer.hide();

        }
    }

    public void setCurrentSelectedView(View currentSelectedView)
    {
        this.currentSelectedView = currentSelectedView;
    }

    private View findViewThatContains(int x, int y)
    {
        int childCount = getChildCount();

        if (currentSelectedView != null)
        {

            MatrixRect gr = matrix.getObjectsPositions().get(currentSelectedView);

            if (gr.columnStart * cellWidthDp - 30 < x &&
                    (gr.columnEnd + 1) * cellWidthDp + 30 > x &&
                    gr.rowStart * cellHeightDp - 30 < y &&
                    (gr.rowEnd + 1) * cellHeightDp + 30 > y)
            {
                return currentSelectedView;
            }
        }


        int column = x / cellWidthDp; // take in consideration paddings here?
        int row = y / cellHeightDp; //


        View view = null;
        try
        {
            Log.v("aaa", "Searching in matrix for " + column + " " + row);
            view = (View)matrix.getObjectAt(column, row);
        }
        catch (Matrix.MatrixException e)
        {
            e.printStackTrace();
        }
        return view;

    }


}
