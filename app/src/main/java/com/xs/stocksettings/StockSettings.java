package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.xs.stocksettings.utils.DeviceInfo;
import com.xs.stocksettings.utils.PrefUtils;

import miui.os.SystemProperties;

/**
 * Created by xs on 16-10-9.
 */
public class StockSettings extends miui.preference.PreferenceActivity {

    private static final String TAG = "StockSettings";

    private static final String WEIBO = "weibo_key";
    private static final String ABOUT = "about_key";
    private static final String DONATE = "donate_key";
    private static final String DENSITY = "density_key";
    private static final String ONEPLUS_OTG = "oneplus_otg_key";
    private static final String ORIGINAL_SETTINGS = "original_settings_key";
    private static final String ONEPLUS_BUTTONS_LED = "oneplus_buttons_led_key";
    private static final String ONEPLUS_NOTIFY_LIGHT = "oneplus_notify_light_key";

    //获取当前系统设定的density值
    private String StringNowDensity = SystemProperties.get("persist.sys.density");

    //将StringNowDensity转化为IntNowDensity
    private int IntNowDensity = Integer.parseInt(StringNowDensity);

    private PreferenceScreen mWeiBo;
    private PreferenceScreen mAbout;
    private PreferenceScreen mDonate;
    private PreferenceScreen mOriginalSettings;

    private EditTextPreference mDensity;

    private CheckBoxPreference mOnePlusOTG;
    private CheckBoxPreference mOnePlusButtonsLed;
    private CheckBoxPreference mOnePlusNotifyLight;

    private void initPreference() {
        mWeiBo = (PreferenceScreen) findPreference(WEIBO);
        mAbout = (PreferenceScreen) findPreference(ABOUT);
        mDonate = (PreferenceScreen) findPreference(DONATE);
        mOriginalSettings = (PreferenceScreen) findPreference(ORIGINAL_SETTINGS);

        mDensity = (EditTextPreference) findPreference(DENSITY);

        mOnePlusOTG = (CheckBoxPreference) findPreference(ONEPLUS_OTG);
        mOnePlusButtonsLed = (CheckBoxPreference) findPreference(ONEPLUS_BUTTONS_LED);
        mOnePlusNotifyLight = (CheckBoxPreference) findPreference(ONEPLUS_NOTIFY_LIGHT);

        if (DeviceInfo.isBacon()) {

        } else if (DeviceInfo.isOnePlus2()) {

        } else if (DeviceInfo.isOnePlus3()) {

        } else {
            getPreferenceScreen().removeAll();
            getPreferenceScreen().addPreference(mAbout);
            getPreferenceScreen().addPreference(mWeiBo);
            getPreferenceScreen().addPreference(mDonate);
        }
    }

    private void setDpi(final int range1, final int range2, final int defaultRange) {
        String density_edit_message = getResources().getString(R.string.density_edit_message);
        String density_edit_message_format = String.format(density_edit_message, range1 + "-" + range2, "");
        mDensity.setDialogMessage(density_edit_message_format);
        mDensity.setDialogTitle(getResources().getString(R.string.density_edit_title));
        mDensity.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String NewDensity = (String) newValue;
                if (NewDensity.equals("")) {
                    Toast.makeText(getApplicationContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    int i = Integer.parseInt(NewDensity);
                    if (i < range1 || i > range2) {
                        Toast.makeText(getApplicationContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                        return false;
                    }
                    String density_summary = getResources().getString(R.string.density_summary);
                    String density_summary_format = String.format(density_summary, NewDensity, defaultRange);
                    mDensity.setSummary(density_summary_format);
                    SystemProperties.set("persist.sys.density", +i + "");
                    dialogReboot();
                    return true;
                }
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings_preference);
        initPreference();

        if (DeviceInfo.isBacon() || DeviceInfo.isOnePlus2() || DeviceInfo.isOnePlus3()) {
            //设置默认density
            setDpi(300, 600, 480);
        }
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

        //每次启动设置当前density值至summary
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
        if (preference == mOnePlusNotifyLight) {
            if (mOnePlusNotifyLight.isChecked()) {
                updateNotifyLightStatus(1);
            } else {
                updateNotifyLightStatus(0);
            }
        }
        if (preference == mOriginalSettings) {
            try {
                Intent intent = new Intent().setClassName("com.android.settings_ex",
                        "com.android.settings_ex.Settings");
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Can't found com.android.settings_ex");
            }
        }
        return true;
    }

    private void updateNotifyLightStatus(int value) {
        Settings.System.putInt(getContentResolver(), "oem_acc_breath_light", value);
        Settings.System.putInt(getContentResolver(), "notification_light_pulse", value);
        Settings.System.putInt(getContentResolver(), "battery_led_low_power", value);
        Settings.System.putInt(getContentResolver(), "battery_led_charging", value);
    }

    private void setEditTextPreferenceSummary(EditTextPreference mEditTextPreference) {
        if (mEditTextPreference == mDensity) {
            if (DeviceInfo.isBacon() || DeviceInfo.isOnePlus2() || DeviceInfo.isOnePlus3()) {
                String density_summary = getResources().getString(R.string.density_summary);
                //StringNowDensity为当前系统density，480为默认
                String density_summary_format = String.format(density_summary, StringNowDensity, "480");
                mDensity.setSummary(density_summary_format);
            }
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
