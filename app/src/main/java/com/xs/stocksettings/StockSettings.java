package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;

import com.xs.stocksettings.utils.PrefUtils;

/**
 * Created by xs on 16-5-31.
 */
public class StockSettings extends PreferenceActivity {
    private static final String WEIBO = "weibo_key";
    private static final String DONATE = "donate_key";
    private static final String DENSITY = "density_key";
    private static final String ONEPLUS_OTG = "oneplus_otg_key";
    private static final String ONEPLUS_GESTURE = "oneplus_gesture_key";
    private static final String ONEPLUS_BUTTONS_LED = "oneplus_buttons_led_key";
    private static final String ONEPLUS_BUTTONS_CUSTOMIZE = "oneplus_buttons_customize_key";

    private String NowDensity = SystemProperties.get("persist.xsdensity");

    //将String NowDensity转化为int intNowDensity
    private int IntNowDensity = Integer.parseInt(NowDensity);

    private EditTextPreference mDensity;

    private PreferenceScreen mWeiBo;
    private PreferenceScreen mDonate;
    private PreferenceScreen mOnePlusGesture;
    private PreferenceScreen mOnePlusButtonsCustomize;

    private CheckBoxPreference mOnePlusOTG;
    private CheckBoxPreference mOnePlusButtonsLed;

    private void initPreference() {
        mWeiBo = (PreferenceScreen) findPreference(WEIBO);
        mDonate = (PreferenceScreen) findPreference(DONATE);
        mOnePlusGesture = (PreferenceScreen) findPreference(ONEPLUS_GESTURE);
        mOnePlusButtonsCustomize = (PreferenceScreen) findPreference(ONEPLUS_BUTTONS_CUSTOMIZE);

        mDensity = (EditTextPreference) findPreference(DENSITY);

        mOnePlusOTG = (CheckBoxPreference) findPreference(ONEPLUS_OTG);
        mOnePlusButtonsLed = (CheckBoxPreference) findPreference(ONEPLUS_BUTTONS_LED);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings_preference);
        initPreference();
        setDpi(360, 480, 480);
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

        setEditTextPreferenceSummary(mDensity);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen, Preference preference) {
        if (preference == mWeiBo) {
            Uri uri = Uri.parse("http://weibo.com/acexs");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
        if (preference == mDonate) {
            Intent intent = new Intent();
            intent.setClassName("com.xs.stocksettings",
                    "com.xs.stocksettings.DonatePreference");
            startActivity(intent);
        }
        if (preference == mOnePlusGesture) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings_ex",
                    "com.android.settings_ex.accessibility.BlackScreenPreferenceActivity");
            startActivity(intent);
        }
        if (preference == mOnePlusButtonsCustomize) {
            Intent intent = new Intent();
            intent.setClassName("com.android.settings_ex",
                    "com.android.settings_ex.accessibility.KeyCustomizeActivity");
            startActivity(intent);
        }
        if (preference == mOnePlusButtonsLed) {
            if (mOnePlusButtonsLed.isChecked()) {
                Settings.System.putInt(getContentResolver(), "buttons_brightness", 1);
            } else {
                Settings.System.putInt(getContentResolver(), "buttons_brightness", 0);
            }
        }
        if (preference == mOnePlusOTG) {
            if (mOnePlusOTG.isChecked()) {
                SystemProperties.set("persist.sys.oem.otg_support", "true");
            } else {
                SystemProperties.set("persist.sys.oem.otg_support", "false");
            }
        }
        return true;
    }

    private void setDpi(final int range1, final int range2, final int defaultRange) {
        String density_edit_message = getResources().getString(R.string.density_edit_message);
        String density_edit_message_format = String.format(density_edit_message, "360-480", "");
        mDensity.setDialogMessage(density_edit_message_format);

        mDensity.setDialogTitle(getResources().getString(R.string.density_edit_title));
        mDensity.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String NewDensity = (String) newValue;
                if (NewDensity.equals("")) {
                    Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    int i = Integer.parseInt(NewDensity);
                    if (i < range1 || i > range2) {
                        Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String density_summary = getResources().getString(R.string.density_summary);
                    String density_summary_format = String.format(density_summary, NewDensity, defaultRange);
                    mDensity.setSummary(density_summary_format);
                    SystemProperties.set("persist.xsdensity", +i + "");
                    dialogReboot();
                    return true;
                }
            }
        });
    }

    private void setEditTextPreferenceSummary(EditTextPreference mEditTextPreference) {
        if (mEditTextPreference == mDensity) {
            String density_summary = getResources().getString(R.string.density_summary);
            String density_summary_format = String.format(density_summary, NowDensity, "480");
            mDensity.setSummary(density_summary_format);
        }
    }

    public void dialogReboot() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dialog_ok)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                        pm.reboot("");
                    }
                })
                .setNeutralButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), R.string.dialog_reboot, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
