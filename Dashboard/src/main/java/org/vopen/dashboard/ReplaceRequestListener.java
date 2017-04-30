package org.vopen.dashboard;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

/**
 * Created by GVERGINE on 9/22/2015 .
 */
public class ReplaceRequestListener implements View.OnClickListener, Widget.OnWidgetReady
{
    private FlippableView flippableView;
    private WidgetFactory widgetFactory;

    public ReplaceRequestListener(FlippableView flippableView, WidgetFactory widgetFactory)
    {
        this.flippableView = flippableView;
        this.widgetFactory = widgetFactory;
    }

    @Override
    public void onClick(View v)
    {
        widgetFactory.selectWidget(new Widget(this));
    }

    @Override
    public void OnWidgetReady(Widget widget)
    {
        widget.initView(flippableView.getContext());

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        widget.getHostView().setLayoutParams(flp);

        //flippableView.setViews(widget.getHostView(), flippableView.backView);
        // flippableView.setViews( flippableView.backView,widget.getHostView());

        if (flippableView.isFrontViewVisible)
        {
            flippableView.swapViewsAnimated();
        }

        flippableView.setViews(widget.getHostView(), flippableView.backView);

        ViewGroup buttons = (ViewGroup)flippableView.backView;
        ImageButton configureButton = (ImageButton)buttons.findViewById(R.id.configureButton);


        configureButton.setOnClickListener(new ConfigureRequestListener(flippableView, widgetFactory, widget));
    }
}
