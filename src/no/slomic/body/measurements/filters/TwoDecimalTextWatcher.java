// Restrukturert: ok

package no.slomic.body.measurements.filters;

import android.text.Editable;
import android.text.TextWatcher;

public class TwoDecimalTextWatcher implements TextWatcher {
    public void afterTextChanged(Editable edittext) {
        String str = edittext.toString();
        int posDot = str.indexOf(".");
        if (posDot <= 0)
            return;
        if (str.length() - posDot - 1 > 2) {
            edittext.delete(posDot + 3, posDot + 4);
        }
    }

    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}