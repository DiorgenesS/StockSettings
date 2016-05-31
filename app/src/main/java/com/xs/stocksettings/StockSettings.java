package com.xs.stocksettings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.xs.stocksettings.utils.PrefUtils;

/**
 * Created by xs on 16-5-31.
 */
public class StockSettings extends PreferenceActivity {
    private static final String WEIBO = "weibo_key";
    private static final String Donate = "donate_key";
    private static final String ONEPLUS_GESTURE = "oneplus_gesture";
    private static final String ONEPLUS_KEY_CUSTOMIZE = "oneplus_key_customize";

    private PreferenceScreen mWeiBo;
    private PreferenceScreen mDonate;
    private PreferenceScreen mOnePlusGesture;
    private PreferenceScreen mOnePlusKeyCustomize;

    private void initPreference() {
        mWeiBo = (PreferenceScreen) findPreference(WEIBO);
        mDonate = (PreferenceScreen) findPreference(Donate);
        mOnePlusGesture = (PreferenceScreen) findPreference(ONEPLUS_GESTURE);
        mOnePlusKeyCustomize = (PreferenceScreen) findPreference(ONEPLUS_KEY_CUSTOMIZE);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings_preference);
        initPreference();
    }

    public void onStart() {
        super.onStart();

        Boolean b = PrefUtils.getBoolean(getApplicationContext(), "showDonations", true);
        if (b.equals(true)) {
            //Activity启动时 调用捐赠页面
            Intent intent = new Intent();
            intent.setClassName("com.xs.stocksettings", "com.xs.stocksettings.DonatePreference");
            startActivity(intent);
            PrefUtils.saveBoolean(getApplicationContext(), "showDonations", false);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen, Preference preference) {
        if (preference == mWeiBo) {
            Uri uri = Uri.parse("http://weibo.com/acexs");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        if (preference == mDonate) {
            Intent intent = new Intent();
            intent.setClassName("com.xs.stocksettings", "com.xs.stocksettings.DonatePreference");
            startActivity(intent);
        }
        if (preference == mOnePlusGesture) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings_ex",
                    "com.android.settings_ex.accessibility.BlackScreenPreferenceActivity");
            startActivity(intent);
        }
        if (preference == mOnePlusKeyCustomize) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings_ex",
                    "com.android.settings_ex.accessibility.KeyCustomizeActivity");
            startActivity(intent);
        }
        return true;
    }

}
