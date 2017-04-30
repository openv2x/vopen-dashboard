package org.vopen.dashboard;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class PlaceHolderFactory {

    private Context context;

    public PlaceHolderFactory(Context context)
    {
        this.context = context;
    }

    public View createPlaceHolder()
    {
        Log.v("PlaceHolderFactory", "createPlaceHolder");
        Button placeHolder = new Button(context);
        placeHolder.setBackgroundResource(R.drawable.placeholder_btn);
        placeHolder.setText("+");
        placeHolder.setTextSize(60);
        return  placeHolder;
    }
}
