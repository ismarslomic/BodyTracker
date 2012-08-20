package no.slomic.body.measurements.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 
 * Maintains extrinsic info of a tab's construct
 */
public class TabInfo {
	public String tag;
	private Class<?> clss;
	private Bundle args;
	private Fragment fragment;

	public TabInfo(String tag, Class<?> clazz, Bundle args) {
		this.tag = tag;
		this.clss = clazz;
		this.args = args;
	}

}
