
package no.slomic.body.measurements.activities;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;

import no.slomic.body.measurements.R;
import no.slomic.body.measurements.fragments.HeightMeasurementList;
import no.slomic.body.measurements.fragments.WeightMeasurementList;
import no.slomic.body.measurements.listeners.NavigationListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that
 * switches between tabs and also allows the user to perform horizontal flicks
 * to move between the tabs.
 */
public class MainActivity extends FragmentActivity {
    private TabHost mTabHost;
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MainActivity.class.getName(), "OnCreate method called in MainActivity");
        
        setContentView(R.layout.fragment_tabs_pager);
        mTabHost = (TabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup();

        mViewPager = (ViewPager) findViewById(R.id.pager);

        // Create our tab adapter
        mTabsAdapter = new TabsAdapter(this, mTabHost, mViewPager);

        mTabHost.clearAllTabs();
    
        
        // add our tabs to the adapter
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean activateHeightMeasurement = sharedPref.getBoolean(
                SettingsActivity.PREFERENCE_ACTIVATE_HEIGHT_MEASUREMENT, true);
        Boolean activateWeightMeasurement = sharedPref.getBoolean(
                SettingsActivity.PREFERENCE_ACTIVATE_WEIGHT_MEASUREMENT, true);
        mTabHost.clearAllTabs();
        if (activateWeightMeasurement)
            mTabsAdapter.addTab(mTabHost.newTabSpec("Weight").setIndicator("Weight"),
                    WeightMeasurementList.class, null);
        if (activateHeightMeasurement)
            mTabsAdapter.addTab(mTabHost.newTabSpec("Height").setIndicator("Height"),
                    HeightMeasurementList.class, null);

        if (savedInstanceState != null) {
            // restore the last selected tab if we can
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
            
            Log.d(MainActivity.class.getName(), "Restoring last selected tab to: " + savedInstanceState.getString("tab"));
        }

        initialiseActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                String currentTabTag = mTabHost.getCurrentTabTag();
                Log.d(MainActivity.class.getName(), "Current tab tag is: " + currentTabTag);
                
                int currentPosition = mTabHost.getCurrentTab();
                Log.d(MainActivity.class.getName(), "Current tab position is: " + currentPosition);
                
                Log.d(MainActivity.class.getName(), "Tab fragments in TabsAdapter: " + mTabsAdapter.fragments.size());

                Fragment currentFragment = mTabsAdapter.getFragment(currentPosition);
                
                //TODO: dette er bare workaround for å slippe å få nullpointer når man endrer fra vertikal til horisontal. Posisjonen til fragment bør lagres før man endrer landscape
                if( currentFragment != null )
                {
                    currentFragment.onOptionsItemSelected(item);
                    Log.d(MainActivity.class.getName(), "onOptionsItemSelected: Current fragment found with tag: " + currentFragment.getTag() + ", id: " + currentFragment.getId() + " and content: " + currentFragment);
                }
                else
                    Log.d(MainActivity.class.getName(), "onOptionsItemSelected: Current fragment is null");
                break;
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(MainActivity.class.getName(), "OnSaveInstanceState method called.");
        outState.putString("tab", mTabHost.getCurrentTabTag());
    }

    public void initialiseActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setDisplayShowTitleEnabled(false);

        String[] viewSwitchList = getResources().getStringArray(R.array.view_switch_list);

        ArrayAdapter<CharSequence> spinnerAdapter = new ArrayAdapter<CharSequence>(this,
                R.layout.layout_subtitled_spinner_item, android.R.id.text1, viewSwitchList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        actionBar.setListNavigationCallbacks(spinnerAdapter, new NavigationListener(
                getFragmentManager()));
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes IN tabs, and takes care of
     * switch to the correct paged IN the ViewPager whenever the selected tab
     * changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements
            TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final TabHost mTabHost;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
        private List<Fragment> fragments;
        
        static final class TabInfo {
            private final String tag;
            private final Class clss;
            private final Bundle args;

            TabInfo(String _tag, Class _class, Bundle _args) {
                tag = _tag;
                clss = _class;
                args = _args;
            }
        }

        
        static class DummyTabFactory implements TabHost.TabContentFactory {
            private final Context mContext;

            public DummyTabFactory(Context context) {
                mContext = context;
            }

            @Override
            public View createTabContent(String tag) {
                View v = new View(mContext);
                v.setMinimumWidth(0);
                v.setMinimumHeight(0);
                return v;
            }
        }

        public TabsAdapter(FragmentActivity activity, TabHost tabHost, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            Log.d(MainActivity.class.getName(), "TabsAdapter constructor is called");
            mContext = activity;
            mTabHost = tabHost;
            mViewPager = pager;
            mTabHost.setOnTabChangedListener(this);
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
            fragments = new ArrayList<Fragment>();
        }

        public void addTab(TabHost.TabSpec tabSpec, Class clss, Bundle args) {
            tabSpec.setContent(new DummyTabFactory(mContext));
            String tag = tabSpec.getTag();

            TabInfo info = new TabInfo(tag, clss, args);
            mTabs.add(info);
            mTabHost.addTab(tabSpec);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            // Create a new fragment if necessary.
            Fragment f = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            fragments.add(position, f);
            
            Log.d(MainActivity.class.getName(), "Creating fragment at position: " + position + ", tag: " + f.getTag() + ", id: " + f.getId() + ", and content: " + f);
            return f;
        }
        
        public Fragment getFragment(int position)
        {
            // TODO: dette er bare workaround for å ikke få nullpointer når landscape endres fra vertical til horisontal.
            if(fragments != null && !fragments.isEmpty())
                return fragments.get(position);
            return null;
        }

        @Override
        public void onTabChanged(String tabId) {
            // called when the user clicks on a tab.
            int position = mTabHost.getCurrentTab();
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            // Unfortunately when TabHost changes the current tab, it kindly
            // also takes care of putting focus on it when not IN touch mode.
            // The jerk.
            // This hack tries to prevent this from pulling focus out of our
            // ViewPager.
            TabWidget widget = mTabHost.getTabWidget();
            int oldFocusability = widget.getDescendantFocusability();
            widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            mTabHost.setCurrentTab(position);
            widget.setDescendantFocusability(oldFocusability);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

    }
}
