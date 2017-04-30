package org.vopen.dashboard;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class ConfigureRequestListener implements View.OnClickListener, Widget.OnWidgetReady
{
    private FlippableView flippableView;
    private WidgetFactory widgetFactory;
    private Widget widget;

    public ConfigureRequestListener(FlippableView flippableView, WidgetFactory widgetFactory, Widget widget)
    {
        this.flippableView = flippableView;
        this.widgetFactory = widgetFactory;
        this.widget = widget;
    }

    @Override
    public void onClick(View v)
    {
        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widget.getId());
        widget.mListener = this;
        widgetFactory.configureWidget(intent);
    }

    @Override
    public void OnWidgetReady(Widget widget)
    {
        widget.initView(flippableView.getContext());

        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        widget.getHostView().setLayoutParams(flp);

        flippableView.setViews(widget.getHostView(), flippableView.backView);

        if (!flippableView.isFrontViewVisible)
        {
            flippableView.swapViewsAnimated();
        }
    }
}
