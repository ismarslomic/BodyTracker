// Restrukturert: OK

package no.slomic.body.measurements.tabs;

import android.os.Bundle;

/**
 * Maintains extrinsic info of a tab's construct
 */
public class TabInfo {
    public final String tag;
    public final Class clss;
    public final Bundle args;

    TabInfo(String _tag, Class _class, Bundle _args) {
        tag = _tag;
        clss = _class;
        args = _args;
    }
}
