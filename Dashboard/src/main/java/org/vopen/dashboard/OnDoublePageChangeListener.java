package org.vopen.dashboard;

/**
 * Created by GVERGINE on 1/5/2016.
 */
public interface OnDoublePageChangeListener
{
    void onHorizontalPageSelected(int column);

    void onVerticalPageSelected(int row);

    void onVerticalPageScrolled();
}
