package org.vopen.dashboard;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class RemoveRequestListener implements View.OnClickListener
{
    private FlippableView flippableView;
    private WidgetFactory widgetFactory;

    public RemoveRequestListener(FlippableView flippableView, WidgetFactory widgetFactory)
    {
        this.flippableView = flippableView;
        this.widgetFactory = widgetFactory;
    }

    @Override
    public void onClick(View v)
    {
        /*

        // Use this code instead of the non-commented code if you want
        // to replace with a placeHolder instead of deleting te whole flipView

        PlaceHolderFactory pf = new PlaceHolderFactory(v.getContext());
        Button placeHolder = (Button) pf.createPlaceHolder();

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        placeHolder.setLayoutParams(lp);

        placeHolder.setOnClickListener(new AddRequestListener(flippableView, widgetFactory));

        flippableView.removeView(flippableView.frontView);
        flippableView.setViews(placeHolder,flippableView.backView);

        */

        ViewGroup g = (ViewGroup)flippableView.getParent();
        g.removeView(flippableView);
    }
}
