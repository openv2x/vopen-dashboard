package org.vopen.dashboard;


import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.view.View;


/**
 * Created by giovanni on 08/09/15.
 */
public class Widget
{
    OnWidgetReady mListener;
    AppWidgetHost host;
    private int id;
    private AppWidgetHostView hostView;
    private AppWidgetProviderInfo providerInfo;

    public Widget(OnWidgetReady listener)
    {
        this.mListener = listener;
    }

    public AppWidgetHostView getHostView()
    {
        return hostView;
    }

    public void setHost(AppWidgetHost host)
    {
        this.host = host;
    }

    public AppWidgetProviderInfo getProviderInfo()
    {
        return providerInfo;
    }

    public void setProviderInfo(AppWidgetProviderInfo providerInfo)
    {
        this.providerInfo = providerInfo;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void initView(Context context)
    {
        // { place the object in the matrixposition, go in edit mode }
        hostView = host.createView(context, id, providerInfo);
        hostView.setAppWidget(id, providerInfo);
    }

    public View getObjectView()
    {
        return getHostView();
    }


    public void onWidgetReady()
    {
        mListener.OnWidgetReady(this);
    }


    public interface OnWidgetReady
    {
        void OnWidgetReady(Widget widget);
    }
}
