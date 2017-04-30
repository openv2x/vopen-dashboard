package org.vopen.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import org.apmem.tools.layouts.FlowLayout;

/**
 * Created by GVERGINE on 9/22/2015.
 */
public class EditPanelFactory
{
    private Context context;
    private FlippableView flippableView;
    private WidgetFactory widgetFactory;

    public EditPanelFactory(Context context, FlippableView flippableView, WidgetFactory widgetFactory)
    {
        this.context = context;
        this.flippableView = flippableView;
        this.widgetFactory = widgetFactory;
    }

    public View createEditPanel()
    {

//        ImageButton removeButton = new ImageButton(context);
//        FlowLayout.LayoutParams removeButtonLayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        removeButtonLayoutParams.setWeight(1);
//        removeButton.setBackgroundResource(R.drawable.remove);
//        removeButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        removeButton.setAdjustViewBounds(true);
//        removeButton.setLayoutParams(removeButtonLayoutParams);
//
//
//
//        ImageButton configureButton = new ImageButton(context);
//        FlowLayout.LayoutParams configureButtonLayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        configureButtonLayoutParams.setWeight(1);
//        configureButton.setBackgroundResource(R.drawable.configure);
//        configureButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        configureButton.setAdjustViewBounds(true);
//        configureButton.setLayoutParams(configureButtonLayoutParams);
//
//        FlowLayout subEditPanel1 = new FlowLayout(context);
//        subEditPanel1.setOrientation(FlowLayout.VERTICAL);
//        FlowLayout.LayoutParams subEditPanel1LayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        subEditPanel1LayoutParams.setWeight(1);
//        subEditPanel1LayoutParams.setGravity(Gravity.FILL);
//        subEditPanel1.setLayoutParams(subEditPanel1LayoutParams);
//
//
//
//        ImageButton replaceButton = new ImageButton(context);
//        FlowLayout.LayoutParams replaceButtonLayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        replaceButtonLayoutParams.setWeight(1);
//        replaceButton.setBackgroundResource(R.drawable.replace);
//        replaceButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        replaceButton.setAdjustViewBounds(true);
//        replaceButton.setLayoutParams(replaceButtonLayoutParams);
//
//        ImageButton flipButton = new ImageButton(context);
//        FlowLayout.LayoutParams flipButtonLayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        flipButtonLayoutParams.setWeight(1);
//        flipButton.setBackgroundResource(R.drawable.flip);
//        flipButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        flipButton.setAdjustViewBounds(true);
//        flipButton.setLayoutParams(flipButtonLayoutParams);
//
//        FlowLayout subEditPanel2 = new FlowLayout(context);
//        subEditPanel2.setOrientation(FlowLayout.VERTICAL);
//        FlowLayout.LayoutParams subEditPanel2LayoutParams = new FlowLayout.LayoutParams(0,FlowLayout.LayoutParams.MATCH_PARENT);
//        subEditPanel2LayoutParams.setWeight(1);
//        subEditPanel2LayoutParams.setGravity(Gravity.FILL);
//        subEditPanel2.setLayoutParams(subEditPanel2LayoutParams);
//
//
//        FlowLayout editPanel = new FlowLayout(context);
//        editPanel.setOrientation(FlowLayout.VERTICAL);
//        FrameLayout.LayoutParams editPanelLayoutParams = new FrameLayout.LayoutParams(FlowLayout.LayoutParams.MATCH_PARENT,FlowLayout.LayoutParams.MATCH_PARENT);
//        editPanelLayoutParams.gravity = Gravity.FILL;
//        editPanel.setPadding(10, 10, 10, 10);
//        editPanel.setBackgroundResource(R.drawable.widget_container);
//        editPanel.setLayoutParams(editPanelLayoutParams);
//
//        subEditPanel1.addView(removeButton);
//        subEditPanel1.addView(configureButton);
//        subEditPanel2.addView(replaceButton);
//        subEditPanel2.addView(flipButton);
//        editPanel.addView(subEditPanel1);
//        editPanel.addView(subEditPanel2);
//
//
//        editPanel.invalidate();
//        editPanel.requestLayout();

        // working but ugly

      /*  ImageButton removeButton = new ImageButton(context);
        LinearLayout.LayoutParams removeButtonLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        removeButton.setBackgroundResource(R.drawable.remove);
        removeButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        removeButton.setAdjustViewBounds(true);
        removeButtonLayoutParams.weight = 1;
        removeButton.setLayoutParams(removeButtonLayoutParams);
        removeButton.setOnClickListener(new RemoveRequestListener(flippableView, widgetFactory));

        ImageButton configureButton = new ImageButton(context);
        LinearLayout.LayoutParams configureButtonLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        configureButton.setBackgroundResource(R.drawable.configure);
        configureButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        configureButton.setAdjustViewBounds(true);
        configureButtonLayoutParams.weight = 1;
        configureButton.setLayoutParams(configureButtonLayoutParams);


        ImageButton replaceButton = new ImageButton(context);
        LinearLayout.LayoutParams replaceButtonLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        replaceButton.setBackgroundResource(R.drawable.replace);
        replaceButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        replaceButton.setAdjustViewBounds(true);
        replaceButtonLayoutParams.weight = 1;
        replaceButton.setLayoutParams(replaceButtonLayoutParams);
        replaceButton.setOnClickListener(new ReplaceRequestListener(flippableView, widgetFactory));

        ImageButton flipButton = new ImageButton(context);
        LinearLayout.LayoutParams flipButtonLayoutParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.MATCH_PARENT);
        flipButton.setBackgroundResource(R.drawable.flip);
        flipButton.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        flipButton.setAdjustViewBounds(true);
        flipButtonLayoutParams.weight = 1;
        flipButton.setLayoutParams(flipButtonLayoutParams);
        flipButton.setOnClickListener(new FlipRequestListener(flippableView));

        LinearLayout editPanel = new LinearLayout(context);
        editPanel.setOrientation(LinearLayout.HORIZONTAL);
        FrameLayout.LayoutParams editPanelLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        editPanel.setPadding(10, 10, 10, 10);
        editPanel.setBackgroundResource(R.drawable.placeholder_btn);
        editPanel.setLayoutParams(editPanelLayoutParams);





        editPanel.addView(removeButton);
        editPanel.addView(configureButton);
        editPanel.addView(replaceButton);
        editPanel.addView(flipButton);
*/

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        FlowLayout parent = (FlowLayout)inflater.inflate(R.layout.backview_layout, null);

        ImageButton removeButton = (ImageButton)parent.findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new RemoveRequestListener(flippableView, widgetFactory));
        ImageButton configureButton = (ImageButton)parent.findViewById(R.id.configureButton);
        ImageButton replaceButton = (ImageButton)parent.findViewById(R.id.replaceButton);
        replaceButton.setOnClickListener(new ReplaceRequestListener(flippableView, widgetFactory));
        ImageButton flipButton = (ImageButton)parent.findViewById(R.id.flipButton);
        flipButton.setOnClickListener(new FlipRequestListener(flippableView));

        //for (int i = 0; i < 3; i++) {
        //    View custom = inflater.inflate(R.layout.custom, null);
        //    TextView tv = (TextView) custom.findViewById(R.id.text);
        //    tv.setText("Custom View " + i);
        //    parent.addView(custom);
        // }

        //setContentView(parent);

        return parent;
    }
}
