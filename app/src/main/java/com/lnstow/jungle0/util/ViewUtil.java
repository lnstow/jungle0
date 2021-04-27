package com.lnstow.jungle0.util;

import android.content.res.Resources;

public class ViewUtil {
    public static int dp2px(float dp) {
        //px=dp*dpi/160
        return (int) (dp * Resources.getSystem().getDisplayMetrics().densityDpi / 160 + 0.5f);
    }

    public static int px2dp(float px) {
        return (int) (px * 160 / Resources.getSystem().getDisplayMetrics().densityDpi + 0.5f);
    }

}
