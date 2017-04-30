package org.vopen.dashboard;

import android.view.View;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class FlipRequestListener implements View.OnClickListener, FlippableView.OnFlipRequestListener
{
    private FlippableView flipView;

    public FlipRequestListener(FlippableView flipView)
    {
        this.flipView = flipView;
    }

    @Override
    public void onClick(View v)
    {
        flipView.swapViewsAnimated();
    }

    @Override
    public boolean OnFlipRequest(View v)
    {
        flipView.swapViewsAnimated();
        return true;
    }
}
