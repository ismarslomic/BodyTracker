
package no.slomic.body.measurements.listeners;

import android.app.ActionBar;
import android.app.FragmentManager;

public class NavigationListener implements ActionBar.OnNavigationListener {
    private FragmentManager fragMgr;

    public NavigationListener(FragmentManager fragMgr) {
        this.fragMgr = fragMgr;
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        /*
         * FragmentTransaction transaction = fragMgr.beginTransaction();
         * transaction.commit(); return true;
         */
        return true;
    }
}
