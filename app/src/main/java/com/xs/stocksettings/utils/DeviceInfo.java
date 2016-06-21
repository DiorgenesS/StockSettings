package com.xs.stocksettings.utils;

import android.os.SystemProperties;

/**
 * Created by xs on 15-7-26.
 */
public class DeviceInfo {

    private static final String MOD_DEVICE = SystemProperties.get("ro.product.model_romer");
    private static final String IsA2001 = "A2001_xs";
    private static final String IsNote3 = "Note3_wonitor";

    public static boolean isA2001() {
        return MOD_DEVICE.equals(IsA2001);
    }

    public static boolean isNote3() {
        return MOD_DEVICE.equals(IsNote3);
    }
}
