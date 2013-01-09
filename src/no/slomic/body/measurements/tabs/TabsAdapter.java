// Restrukturert: OK

package no.slomic.body.measurements.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;

import java.util.ArrayList;

/**
 * This is a helper class that implements the management of tabs and all details
 * of connecting a ViewPager with associated TabHost. It relies on a trick.
 * Normally a tab host has a simple API for supplying a View or Intent that each
 * tab will show. This is not sufficient for switching between pages. So instead
 * we make the content part of the tab host 0dp high (it is not shown) and the
 * TabsAdapter supplies its own dummy view to show as the tab content. It
 * listens to changes in tabs, and takes care of switch to the correct paged in
 * the ViewPager whenever the selected tab changes.
 */
public class TabsAdapter extends FragmentPagerAdapter implements TabHost.OnTabChangeListener,
        ViewPager.OnPageChangeListener {
    private final Context mContext;
    private final TabHost mTabHost;
    private final ViewPager mViewPager;
    private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

    public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
        super(activity.getSupportFragmentManager());
        this.mContext = activity;
        this.mTabHost = tabHost;
        this.mViewPager = pager;
        this.mTabHost.setOnTabChangedListener(this);
        this.mViewPager.setAdapter(this);
        this.mViewPager.setOnPageChangeListener(this);
    }

    public void clearAll() {
        this.mTabHost.clearAllTabs();
        this.mTabs.clear();
    }

    public void addTab(TabHost.TabSpec tabSpec, Class clss, Bundle args) {
        tabSpec.setContent(new TabFactory(this.mContext));
        String tag = tabSpec.getTag();

        TabInfo info = new TabInfo(tag, clss, args);
        this.mTabs.add(info);
        this.mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.mTabs.size();
    }

    /**
     * ATTENTION! The name of this method is rather misleading. It is not
     * returning existing fragments but creates new ones. In so meaning, it
     * should be renamed to createItem(int position) (non-Javadoc)
     * 
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        TabInfo info = this.mTabs.get(position);
        Fragment f = Fragment.instantiate(this.mContext, info.clss.getName(), info.args);
        return f;
    }

    /**
     * Returning the fragment tag based on the pattern Android SDK uses to
     * automatically generate it. Be aware of that this method does not check if
     * the fragment already exists
     * 
     * @param position - the position of the page
     * @return tag of the fragment
     */
    public String getFragmentTag(int position) {
        return "android:switcher:" + this.mViewPager.getId() + ":" + position;
    }

    @Override
    public void onTabChanged(String tabId) {
        // called when the user clicks on a tab.
        int position = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        // Unfortunately when TabHost changes the current tab, it kindly
        // also takes care of putting focus on it when not in touch mode.
        // The jerk.
        // This hack tries to prevent this from pulling focus out of our
        // ViewPager.
        TabWidget widget = this.mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        this.mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
