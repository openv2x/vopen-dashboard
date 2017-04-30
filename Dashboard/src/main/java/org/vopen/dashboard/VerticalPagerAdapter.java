package org.vopen.dashboard;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.vopen.gui.layouts.EditableStaticGridLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GVERGINE on 1/4/2016.
 */
public class VerticalPagerAdapter extends PagerAdapter
{
    private Context mContext;
    private int mParent;
    private int mChilds;
    private PageHolder pageHolder;
    private List<EditableStaticGridLayout> elements = new ArrayList<>();

    public VerticalPagerAdapter(Context c, int parent, int childs, PageHolder pageHolder)
    {
        mContext = c;
        mParent = parent;
        mChilds = childs;
        this.pageHolder = pageHolder;
    }

    @Override
    public int getCount()
    {
        return mChilds;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        Log.v("VerticalPagerAdapter", "instantiateItem " + position);
        if (position >= 0 && position < elements.size())
        {
            View view = elements.get(position);
            ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup)
            {
                ((ViewGroup)parent).removeView(view);
            }
            container.addView(view);
            return view;
        }
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }

    public int getItemPosition(Object object)
    {
        return POSITION_NONE;
    }

    public List<EditableStaticGridLayout> getElementList()
    {
        return elements;
    }
}
