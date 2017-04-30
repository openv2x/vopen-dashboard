package org.vopen.dashboard;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.emoiluj.doubleviewpager.DoubleViewPager;
import com.emoiluj.doubleviewpager.HorizontalViewPager;
import com.emoiluj.doubleviewpager.VerticalViewPager;

import org.vopen.dashboard.events.AllPagesInitialized;
import org.vopen.dashboard.events.GatewayEvent;
import org.vopen.gui.layouts.EditableStaticGridLayout;
import org.vopen.gui.layouts.StaticGridLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MainActivity extends AppCompatActivity implements StaticGridLayout.OnLayoutMeasured, Switch.OnCheckedChangeListener, View.OnLongClickListener, View.OnTouchListener, OnDoublePageChangeListener, HorizontalViewPager.OnPageChangeListener
{
    private final static String SAVED_LAYOUT_FILENAME = "savedLayout.xml";
    final static int PAGES_COUNT = 9;
    final AnimatorSet mAnimationSetFadeIn = new AnimatorSet();
    final AnimatorSet mAnimationSetFadeOut = new AnimatorSet();
    final Handler handler = new Handler();
    int currentGridLayout = 0;
    ObjectAnimator fadeOut;
    ObjectAnimator fadeIn;
    int initializedPages;
    private WidgetFactory widgetFactory;
    private Switch editSwitch;
    private Resizer resizer;
    private int clickX, clickY;
    private volatile EditableStaticGridLayout[] pages;
    private PageHolder pageHolder = new PageHolder();
    private VerticalViewPager[] verticalPagers;
    private TextView obdStatus;
    private ToggleButton obdButton;
    private PageIndicator pageIndicator;
    private ArrayList<PagerAdapter> verticalAdapters = new ArrayList<>();
    private DoubleViewPager viewpager;
    private boolean isMapShown = false;
    final Runnable showingMap = new Runnable()
    {
        @Override
        public void run()
        {
            mAnimationSetFadeOut.start();
            isMapShown = false;
            // pageIndicator.setVisibility(View.GONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i("MainAct", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializedPages = 0;
        currentGridLayout = 0;
        resizer = (Resizer)findViewById(R.id.resizer);
        editSwitch = (Switch)findViewById(R.id.editSwitch);
        obdStatus = (TextView)findViewById(R.id.obdStatus);
        obdButton = (ToggleButton)findViewById(R.id.obdButton);
        obdButton.setTextOff("OBD disabled");
        obdButton.setTextOn("OBD enabled");
        editSwitch.setChecked(false);
        editSwitch.setOnCheckedChangeListener(this);
        widgetFactory = new WidgetFactory(this);
        pages = new EditableStaticGridLayout[PAGES_COUNT];
        verticalPagers = new VerticalViewPager[3];
        pageHolder.currentHorizontalPage = 0;
        pageHolder.currentVerticalPage = 0;
        pageIndicator = (PageIndicator)findViewById(R.id.pageIndicator);
        fadeOut = ObjectAnimator.ofFloat(pageIndicator, "alpha", 1f, 0f);
        fadeOut.setDuration(300);
        fadeIn = ObjectAnimator.ofFloat(pageIndicator, "alpha", 0f, 1f);
        fadeIn.setDuration(100);
        initListeners();
        mAnimationSetFadeIn.play(fadeIn);
        mAnimationSetFadeOut.play(fadeOut);

    }

    @Override
    protected void onStop()
    {
        Log.i("MainAct", "onStop");
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
        saveCompleteLayout();
    }

    @Override
    protected void onDestroy()
    {
        Log.i("MainAct", "onDestroy");
        super.onDestroy();
        widgetFactory.dispose();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == WidgetFactory.REQUEST_PICK_APPWIDGET
                || requestCode == WidgetFactory.REQUEST_CREATE_APPWIDGET)
        {
            widgetFactory.onWidgetSelected(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart()
    {
        Log.i("MainAct", "onStart");
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onLayoutMeasured(int columns, int rows)
    {
        Log.i("MainAct", "onLayoutMeasured");
        //StaticGridLayout gl = (StaticGridLayout) findViewById(R.id.gridLayout);
        //addPlaceHolders(gl, columns, rows);
        pages[currentGridLayout].removeOnLayoutMeasuredListener(this);
        initializedPages++;
        if (initializedPages == PAGES_COUNT)
        {
            EventBus.getDefault().postSticky(new AllPagesInitialized());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        //EditableStaticGridLayout gl = (EditableStaticGridLayout) findViewById(R.id.gridLayout);
        pages[currentGridLayout].editModeChanged(isChecked);
    }

    @Override
    public boolean onLongClick(View view)
    {
        EditableStaticGridLayout gl = pages[currentGridLayout];//(EditableStaticGridLayout) findViewById(R.id.gridLayout);
        if (gl.isCoordFree(clickX, clickY))
        {
            Pair<Integer, Integer> matrixCoords = gl.getMatrixCoordsForPixelCoord(clickX, clickY);
            addPlaceHolderAround(gl, matrixCoords.first, matrixCoords.second);
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                clickX = (int)event.getX();
                clickY = (int)event.getY();
                Log.v("ACTION_DOWN", clickX + " " + clickY);
                break;
            }
        }
        return false;
    }

    @Override
    public void onHorizontalPageSelected(int column)
    {
        pageHolder.currentHorizontalPage = column;
        Log.v("****", pageHolder.currentHorizontalPage + " " + pageHolder.currentVerticalPage);
        currentGridLayout = pageHolder.currentVerticalPage * 3 + pageHolder.currentHorizontalPage;
        pages[currentGridLayout].setResizer(resizer);
        pageIndicator.setCurrentPage(currentGridLayout);
        showMap();

    }

    @Override
    public void onVerticalPageSelected(int row)
    {
        pageHolder.currentVerticalPage = row;
        Log.v("****", pageHolder.currentHorizontalPage + " " + pageHolder.currentVerticalPage);
        currentGridLayout = pageHolder.currentVerticalPage * 3 + pageHolder.currentHorizontalPage;
        pages[currentGridLayout].setResizer(resizer);
        pageIndicator.setCurrentPage(currentGridLayout);
        showMap();
    }

    @Override
    public void onVerticalPageScrolled()
    {

        showMap();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {
        showMap();
    }

    @Override
    public void onPageSelected(int position)
    {
        this.onHorizontalPageSelected(position);
        verticalPagers[position].setCurrentItem(pageHolder.currentVerticalPage);

    }

    @Override
    public void onPageScrollStateChanged(int state)
    {

    }

    public void onEventMainThread(AllPagesInitialized event)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                //restoring saved layout
                LayoutFile savedLayoutFile = XmlReader.readFile(new File(getFilesDir().getAbsolutePath() + "/" + SAVED_LAYOUT_FILENAME));
                if (savedLayoutFile == null)
                {
                    Log.i("Reading saved layout", "layout file cannot be read");
                    return;
                }
                List<LayoutPage> pagesList = savedLayoutFile.getList();
                for (int i = 0; i < pagesList.size(); i++)
                {
                    //cleaning the page
                    pages[i].getStaticGridLayoutMatrix().removeAllObjects();
                    LayoutPage page = pagesList.get(i);
                    if (page != null && page.getList() != null && page.getList().size() > 0)
                    {
                        for (GraphicalWidget graphicalWidget : page.getList())
                        {
                            final FlippableView v = (FlippableView)addPlaceHolder(pages[i], graphicalWidget.getColumnStart(),
                                    graphicalWidget.getRowStart(), graphicalWidget.getColumnEnd(), graphicalWidget.getRowEnd());
                            if (graphicalWidget.getId() != 0)
                            {
                                Intent intent = new Intent();

                                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, graphicalWidget.getId());
                                Widget widget = new Widget(new Widget.OnWidgetReady()
                                {
                                    @Override
                                    public void OnWidgetReady(Widget widget)
                                    {
                                        widget.initView(getApplicationContext());

                                        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                                        widget.getHostView().setLayoutParams(flp);

                                        v.setViews(widget.getHostView(), v.backView);

                                        if (!v.isFrontViewVisible)
                                        {
                                            v.swapViewsAnimated();
                                        }
                                    }
                                });
                                if (!WidgetFactory.widgetMap.containsKey(graphicalWidget.getId()))
                                {
                                    WidgetFactory.widgetMap.put(graphicalWidget.getId(), widget);
                                }
                                widgetFactory.createWidget(intent);
                            }
                        }
                    }
                }
            }
        });
    }

    public void onEventMainThread(GatewayEvent event)
    {
        obdStatus.setText(event.getMessage());
    }

    private void saveCompleteLayout()
    {
        //saving to file
        LayoutFile layoutFile = new LayoutFile();
        List<LayoutPage> aList = layoutFile.getList();

        for (EditableStaticGridLayout page : pages)
        {
            List<GraphicalWidget> allGraphicalWidgets = new ArrayList<>();
            LayoutPage allGraphicalWidgetsListWrapper = new LayoutPage();
            allGraphicalWidgetsListWrapper.setList(allGraphicalWidgets);

            Map<Object, MatrixRect> widgetsPositionMap = page.getStaticGridLayoutMatrix().getObjectsPositions();
            for (Map.Entry entry : widgetsPositionMap.entrySet())
            {
                MatrixRect matrixRect = (MatrixRect)entry.getValue();

                Log.v("Writing XML", "Reading " + matrixRect);
                if (matrixRect != null)
                {
                    GraphicalWidget graphicalWidget = new GraphicalWidget();
                    graphicalWidget.setColumnStart(matrixRect.columnStart);
                    graphicalWidget.setColumnEnd(matrixRect.columnEnd);
                    graphicalWidget.setRowStart(matrixRect.rowStart);
                    graphicalWidget.setRowEnd(matrixRect.rowEnd);

                    FlippableView flippableView = (FlippableView)entry.getKey();
                    for (Map.Entry<Integer, Widget> widgetMapEntry : WidgetFactory.widgetMap.entrySet())
                    {
                        Widget value = widgetMapEntry.getValue();
                        //check equals
                        if (flippableView.frontView.equals(value.getHostView()))
                        {
                            graphicalWidget.setId(widgetMapEntry.getKey());
                            break;
                        }
                    }

                    allGraphicalWidgets.add(graphicalWidget);
                }
            }

            if (widgetsPositionMap.size() > 0)
            {
                aList.add(allGraphicalWidgetsListWrapper);
            }
        }

        try
        {
            XmlWriter.writeToFile(layoutFile, SAVED_LAYOUT_FILENAME, getFilesDir().getAbsolutePath());

        }
        catch (Exception e)
        {
            Log.e("Writing XML", "Error during writing");
            Log.e("Writing XML", e.getMessage());
        }
    }

    private void initListeners()
    {

        viewpager = (DoubleViewPager)findViewById(R.id.pager);
        viewpager.setOffscreenPageLimit(3);
        for (int j = 0; j < 3; j++)
        {
            // 3 = horizontal childs
            VerticalViewPager verticalViewPager = new VerticalViewPager(this);
            verticalPagers[j] = verticalViewPager;

            // 3 = vertical childs
            VerticalPagerAdapter adapter = new VerticalPagerAdapter(this, j, 3, pageHolder);
            verticalAdapters.add(adapter);

            for (int i = 0; i < 3; ++i)
            {
                pages[i * 3 + j] = new EditableStaticGridLayout(this, null);
                pages[i * 3 + j].setLayoutParams(new EditableStaticGridLayout.LayoutParams(EditableStaticGridLayout.LayoutParams.MATCH_PARENT, EditableStaticGridLayout.LayoutParams.MATCH_PARENT));
                pages[i * 3 + j].setResizer(resizer);
                pages[i * 3 + j].setCellSizeDp(60, 60);
                pages[i * 3 + j].addOnLayoutMeasuredListener(this);
                pages[i * 3 + j].setOnLongClickListener(this);
                pages[i * 3 + j].setOnTouchListener(this);
                pages[i * 3 + j].setEditSwitch(editSwitch);

                adapter.getElementList().add(pages[i * 3 + j]);
            }
        }

        MyDoubleViewPagerAdapter doubleAdapter = new MyDoubleViewPagerAdapter(getApplicationContext(), verticalAdapters, pageHolder);
        doubleAdapter.addOnDoublePageChangeListener(this);
        doubleAdapter.setVerticalViewPagers(verticalPagers);
        viewpager.setAdapter(doubleAdapter);
        viewpager.setOnPageChangeListener(this);

        onCheckedChanged(editSwitch, false); // init to gone
        //frameLayout.addView(pages[currentGridLayout], 0);
        pages[currentGridLayout].setResizer(resizer);
    }

    private View addPlaceHolder(StaticGridLayout gl, int columnStart, int rowStart, int columnEnd, int rowEnd)
    {
        FlippableView placeHolder = new FlippableView(this);
        PlaceHolderFactory pf = new PlaceHolderFactory(this);
        EditPanelFactory epf = new EditPanelFactory(this, placeHolder, widgetFactory);

        placeHolder.setLayoutParams(new StaticGridLayout.LayoutParams(StaticGridLayout.LayoutParams.MATCH_PARENT, StaticGridLayout.LayoutParams.MATCH_PARENT));

        Button frontView = (Button)pf.createPlaceHolder();
        View backView = epf.createEditPanel();

        placeHolder.setViews(frontView, backView);
        frontView.setOnClickListener(new AddRequestListener(placeHolder, widgetFactory));
        placeHolder.setOnFlipRequestListener(new FlipRequestListener(placeHolder));

        gl.addViewAt(placeHolder, columnStart, rowStart, columnEnd, rowEnd);

        return placeHolder;
    }

    private void addPlaceHolderAround(StaticGridLayout gl, int column, int row)
    {
        MatrixPlaceFinder finder = new MatrixPlaceFinder(gl.getStaticGridLayoutMatrix());
        finder.setMinCostraints(2, 2, 8);
        finder.setMaxContstraints(6, 6, 36);

        MatrixRect position = finder.findPlaceAround(column, row);

        if (position != null)
        {
            View placeHolder = addPlaceHolder(gl, position.columnStart, position.rowStart, position.columnEnd, position.rowEnd);
            ((EditableStaticGridLayout)gl).setCurrentSelectedView(placeHolder);
            editSwitch.setChecked(true);
        }
    }

    private boolean expandPlaceHolderRecursive(StaticGridLayout gl, int columnStart, int rowStart, int columnEnd, int rowEnd)
    {
        return false;
    }

    private void showMap()
    {
        handler.removeCallbacks(showingMap);
        if (!isMapShown)
        {
            mAnimationSetFadeIn.start();
        }
        else
        {
            pageIndicator.setAlpha(1);
        }
        handler.postDelayed(showingMap, 1000);
        isMapShown = true;
    }


}