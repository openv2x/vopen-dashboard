package org.vopen.dashboard;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by giovanni on 08/09/15.
 */
public class WidgetFactory
{
    static final public int APPWIDGET_HOST_ID = 2048;
    static final public int REQUEST_PICK_APPWIDGET = 0;
    static final public int REQUEST_CREATE_APPWIDGET = 5;
    static final public int RESULT_OK = -1;
    static final public int RESULT_CANCELED = 0;
    static private AppWidgetManager mAppWidgetManager;
    static private AppWidgetHost mAppWidgetHost;
    static public Map<Integer, Widget> widgetMap;
    private Context context;
    private ProgressDialog waitDialog = null;

    public WidgetFactory(Context context)
    {
        this.context = context;
        widgetMap = new android.support.v4.util.ArrayMap<>();

        mAppWidgetManager = AppWidgetManager.getInstance(context);
        mAppWidgetHost = new AppWidgetHost(context, APPWIDGET_HOST_ID);
        mAppWidgetHost.startListening();
    }

    public Context getContext()
    {
        return context;
    }

    public void dispose()
    {
        mAppWidgetHost.stopListening();
    }

    public void selectWidget(Widget w)
    {
        waitDialog = new ProgressDialog(context);
        waitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        waitDialog.setMessage("Loading. Please wait...");
        waitDialog.setIndeterminate(true);
        waitDialog.setCanceledOnTouchOutside(false);
        waitDialog.show();

        int appWidgetId = mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        widgetMap.put(appWidgetId, w);
        addEmptyData(pickIntent);
        ((Activity)context).startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }

    public void onWidgetSelected(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            if (requestCode == REQUEST_PICK_APPWIDGET)
            {
                configureWidget(data);
            }
            else if (requestCode == REQUEST_CREATE_APPWIDGET)
            {
                createWidget(data);
            }
        }
        else if (resultCode == RESULT_CANCELED && data != null)
        {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1)
            {
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
            waitDialog.dismiss();
        }
    }

    public void configureWidget(Intent data)
    {
        Log.v("configureWidget", "configureWidget");

        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null)
        {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            ((Activity)context).startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        }
        else
        {
            createWidget(data);
        }
    }

    private void addEmptyData(Intent pickIntent)
    {

        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    public void createWidget(Intent data)
    {
        Log.v("createWidget", "createWidget");

        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        widgetMap.get(appWidgetId).setId(appWidgetId);
        widgetMap.get(appWidgetId).setProviderInfo(appWidgetInfo);
        widgetMap.get(appWidgetId).setHost(mAppWidgetHost);
        widgetMap.get(appWidgetId).onWidgetReady();

        if (waitDialog != null)
        {
            waitDialog.dismiss();
        }
    }
}

