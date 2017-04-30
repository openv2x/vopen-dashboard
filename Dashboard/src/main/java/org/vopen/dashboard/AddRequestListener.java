package org.vopen.dashboard;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;


/**
 * Created by GVERGINE on 9/18/2015.
 */
public class AddRequestListener implements View.OnClickListener, Widget.OnWidgetReady
{
    private FlippableView flippableView = null;
    private WidgetFactory mWidgetFactory = null;

    public AddRequestListener(FlippableView flippableView, WidgetFactory widgetFactory)
    {
        this.flippableView = flippableView;
        this.mWidgetFactory = widgetFactory;
    }

    @Override
    public void onClick(View v)
    {
        Log.v("AddRequestListener", "onClick");

        mWidgetFactory.selectWidget(new Widget(this));
    }

    private void setBackViewListeners(Widget widget)
    {
        ViewGroup buttons = (ViewGroup)flippableView.backView;
        ImageButton removeButton = (ImageButton)buttons.findViewById(R.id.removeButton);
        ImageButton configureButton = (ImageButton)buttons.findViewById(R.id.configureButton);
        ImageButton replaceButton = (ImageButton)buttons.findViewById(R.id.replaceButton);
        ImageButton flipButton = (ImageButton)buttons.findViewById(R.id.flipButton);

//        ImageButton flipButton = (ImageButton) flippableView.backView.findViewById(R.id.flipButton);
        flipButton.setOnClickListener(new FlipRequestListener(flippableView));
//
//        ImageButton removeButton = (ImageButton) flippableView.backView.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new RemoveRequestListener(flippableView, mWidgetFactory));
//
//        ImageButton replaceButton = (ImageButton) flippableView.backView.findViewById(R.id.replaceButton);
        replaceButton.setOnClickListener(new ReplaceRequestListener(flippableView, mWidgetFactory));
//
//        ImageButton configureButton = (ImageButton) flippableView.backView.findViewById(R.id.configureButton);
        configureButton.setOnClickListener(new ConfigureRequestListener(flippableView, mWidgetFactory, widget));
    }

    @Override
    public void OnWidgetReady(Widget widget)
    {
        Log.v("AddRequestListener", "OnWidgetReady");

        widget.initView(flippableView.getContext());
        View newFrontView = widget.getHostView();

        FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams)flippableView.frontView.getLayoutParams();
        newFrontView.setLayoutParams(flp);

        setBackViewListeners(widget);
        flippableView.setViews(newFrontView, flippableView.backView);

        if (!flippableView.isFrontViewVisible)
        {
            flippableView.swapViewsAnimated();
        }
    }
}
