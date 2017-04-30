package org.vopen.dashboard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.emoiluj.doubleviewpager.VerticalViewPager;

import java.util.ArrayList;
import java.util.List;

public class MyDoubleViewPagerAdapter extends PagerAdapter implements VerticalViewPager.OnPageChangeListener
{
    private Context mContext;
    private ArrayList<PagerAdapter> mAdapters;
    private List<OnDoublePageChangeListener> pageChangeListeners = new ArrayList<>();
    private PageHolder pageHolder;
    private VerticalViewPager verticalViewPagers[];

    public MyDoubleViewPagerAdapter(Context context, ArrayList<PagerAdapter> verticalAdapters, PageHolder pageHolder)
    {
        mContext = context;
        mAdapters = verticalAdapters;
        this.pageHolder = pageHolder;
    }

    @Override
    public int getCount()
    {
        return mAdapters.size();
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position)
    {
        VerticalViewPager childVP = verticalViewPagers[position];//new VerticalViewPager(mContext);
        childVP.setOffscreenPageLimit(3);
        childVP.setOnPageChangeListener(this);
        childVP.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        childVP.setAdapter(mAdapters.get(position));

        container.addView(childVP);
        return childVP;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view)
    {
        ((VerticalViewPager)view).setOnPageChangeListener(null);
        container.removeView((View)view);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }

    @Override
    public void onPageSelected(int position)
    {
        for (OnDoublePageChangeListener listener : pageChangeListeners)
        {
            listener.onVerticalPageSelected(position);
        }
        //Log.v("oooooooo","verticalViewPagers["+position+"].setCurrentItem(" +pageHolder.currentVerticalPage+");" );
        //verticalViewPagers[position].setCurrentItem(pageHolder.currentVerticalPage);
    }

    @Override
    public void onPageScrollStateChanged(int state)
    {
        for (OnDoublePageChangeListener listener : pageChangeListeners)
        {
            listener.onVerticalPageScrolled();
        }
    }

    public void addOnDoublePageChangeListener(OnDoublePageChangeListener listener)
    {
        pageChangeListeners.add(listener);
    }

    public void setVerticalViewPagers(VerticalViewPager[] verticalViewPagers)
    {
        this.verticalViewPagers = verticalViewPagers;
    }
}