package no.slomic.body.measurements.tabs;

import android.content.Context;
import android.view.View;
import android.widget.TabHost.TabContentFactory;

/**
 * A simple factory that returns dummy views to the Tabhost
 * 
 */
public class TabFactory implements TabContentFactory {

	private final Context mContext;

	/**
	 * @param context
	 */
	public TabFactory(Context context) {
		mContext = context;
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
	 */
	public View createTabContent(String tag) {
		View v = new View(mContext);
		v.setMinimumWidth(0);
		v.setMinimumHeight(0);
		return v;
	}

}