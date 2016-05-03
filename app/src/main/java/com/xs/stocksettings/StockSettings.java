package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;

import com.xs.stocksettings.utils.DeviceInfo;
import com.xs.stocksettings.utils.PrefUtils;
import com.xs.stocksettings.utils.Tools;

import miui.os.SystemProperties;

/**
 * Created by xs on 15-7-25.
 */
public class StockSettings extends miui.preference.PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String DoubleTapHomeToSleep = "double_tap_home_to_sleep_key";
    private static final String CMSettings = "cm_settings_key";
    private static final String CameraSwitch = "camera_switch_key";
    private static final String HomeLayoutSwitch = "home_layout_switch_key";
    private static final String Density = "density_key";
    private static final String AppScreenMask = "app_screen_mask_key";
    private static final String About = "about_key";
    private static final String Donate = "donate_key";
    private String NowDensity = SystemProperties.get("persist.sys.density");

    //将String NowDensity转化为int intNowDensity
    private int IntNowDensity = Integer.parseInt(NowDensity);

    private CheckBoxPreference mDoubleTapHomeToSleep;
    private PreferenceScreen mCMSettings;
    private PreferenceScreen mAppScreenMask;
    private PreferenceScreen mDonate;
    private PreferenceScreen mAbout;
    private ListPreference mCameraSwitch;
    private ListPreference mHomeLayoutSwitch;
    private EditTextPreference mDensity;

    //for kernel config (oepn kernel activity)
    long[] mHits = new long[3];

    private void initFind() {
        mDoubleTapHomeToSleep = (CheckBoxPreference) findPreference(DoubleTapHomeToSleep);
        mCMSettings = (PreferenceScreen) findPreference(CMSettings);
        mAppScreenMask = (PreferenceScreen) findPreference(AppScreenMask);
        mDensity = (EditTextPreference) findPreference(Density);
        mAbout = (PreferenceScreen) findPreference(About);
        mDonate = (PreferenceScreen) findPreference(Donate);

        mCameraSwitch = (ListPreference) findPreference(CameraSwitch);
        mCameraSwitch.setOnPreferenceChangeListener(this);

        mHomeLayoutSwitch = (ListPreference) findPreference(HomeLayoutSwitch);
        mHomeLayoutSwitch.setOnPreferenceChangeListener(this);

        //默认布局切换
        mHomeLayoutSwitch.setEntries(new String[]{"4x5", "4x6", "5x5"});
        mHomeLayoutSwitch.setEntryValues(new String[]{"0", "1", "2"});
    }

    private void setDpi(final int range1, final int range2, final int defaultRange) {
        String density_edit_message = getResources().getString(R.string.density_edit_message);

        if (DeviceInfo.isBacon()) {
            String density_edit_message_restore_homelayout = getResources().getString(R.string.density_edit_message_restore_homelayout);
            String density_edit_message_format = String.format(density_edit_message, "300-600", density_edit_message_restore_homelayout);
            mDensity.setDialogMessage(density_edit_message_format);
        } else if (DeviceInfo.is8297()) {
            String density_edit_message_format = String.format(density_edit_message, "280-320", "");
            mDensity.setDialogMessage(density_edit_message_format);
        }

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
                    SystemProperties.set("persist.sys.density", +i + "");

                    if (DeviceInfo.isBacon()) {
                        //如果新DPI≤440，那么将showDpiWarning窗口重置为true状态
                        if (i <= 440) {
                            PrefUtils.saveBoolean(getApplicationContext(), "show_dpi_warning", true);
                        }
                        //重置桌面布局
                        PrefUtils.saveString(getApplicationContext(), "home_layout_switch_key", "0");
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/media/theme/default/com.miui.home");
                    }

                    dialogReboot();
                    return true;
                }
            }
        });
    }

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings);
        initFind();

        //Device
        if (DeviceInfo.isBacon()) { /* Oneplus A0001 */

            //添加DPI判断，如果DPI≤440，则移除4x5布局切换
            if (IntNowDensity <= 440) {
                //mHomeLayoutSwitch.setEntries(new String[]{"4x5", "4x6", "5x5"});
                //mHomeLayoutSwitch.setEntryValues(new String[]{"0", "1", "2"});
                mHomeLayoutSwitch.setEntries(new String[]{"4x6", "5x5"});
                mHomeLayoutSwitch.setEntryValues(new String[]{"1", "2"});

                //showDpiWarning， 提醒4x5布局移除窗口
                Boolean b = PrefUtils.getBoolean(getApplicationContext(), "show_dpi_warning", false);
                if (b.equals(true)) {
                    showDpiWarning();
                }

            }

            getPreferenceScreen().removePreference(mAppScreenMask);
            mCameraSwitch.setEntries(R.array.camera_switch_entries_bacon);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_bacon);

            setDpi(300, 600, 480);

        } else if (DeviceInfo.is8297()) { /* Coolpad 8297 */
            mCameraSwitch.setEntries(R.array.camera_switch_entries_8297);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_8297);
            getPreferenceScreen().removePreference(mDoubleTapHomeToSleep);
            getPreferenceScreen().removePreference(mCMSettings);

            setDpi(280, 320, 320);

        } else { /* Null Device*/
            getPreferenceScreen().removeAll();
        }

    }

    public void onStart() {
        super.onStart();
        setListPreferenceSummary(mCameraSwitch);
        setListPreferenceSummary(mHomeLayoutSwitch);
        setEditTextPreferenceSummary(mDensity);

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
        if (preference == mDoubleTapHomeToSleep) {
            if (mDoubleTapHomeToSleep.isChecked()) {
                Settings.System.putInt(getContentResolver(), "key_home_double_tap_action", 8);
            } else {
                Settings.System.putInt(getContentResolver(), "key_home_double_tap_action", 0);
            }
        }
        if (preference == mAbout) {
            if (DeviceInfo.isBacon()) {
                //连续点击三次，Google API
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    Intent i = new Intent(Intent.ACTION_MAIN);
                    i.setClassName("de.andip71.boeffla_config_v2", "de.andip71.boeffla_config_v2.MainActivity");
                    startActivity(i);
                    Toast.makeText(this, R.string.kernel_config_open, Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (preference == mDonate) {
            Intent intent = new Intent();
            intent.setClassName("com.xs.stocksettings", "com.xs.stocksettings.DonatePreference");
            startActivity(intent);
        }
        return false;
    }

    private void setEditTextPreferenceSummary(EditTextPreference mEditTextPreference) {
        if (mEditTextPreference == mDensity) {
            if (DeviceInfo.isBacon()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary, NowDensity, "480");
                mDensity.setSummary(density_summary_format);
            } else if (DeviceInfo.is8297()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary, NowDensity, "320");
                mDensity.setSummary(density_summary_format);
            }
        }
    }

    private void setListPreferenceSummary(ListPreference mListPreference) {
        if (mListPreference == mCameraSwitch) {
            if (DeviceInfo.isBacon()) {
                if (0 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_oppo_summary);
                } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_miui_summary);
                } else if (2 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_cm_summary);
                }
            } else if (DeviceInfo.is8297()) {
                if (0 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_miui_summary);
                } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_cp_summary);
                }
            }
        }

        if (mListPreference == mHomeLayoutSwitch) {
            if (0 == Integer.parseInt(mListPreference.getValue())) {
                //默认设置，summary为4x5
                mListPreference.setSummary(R.string.home_layout_switch_summary_4x5);
                //如果检测到是一加一，并且DPI≤440，则设置4x5布局的summary为4x6
                if (DeviceInfo.isBacon() && IntNowDensity <= 440) {
                    mListPreference.setSummary(R.string.home_layout_switch_summary_4x6);
                }
            } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                mListPreference.setSummary(R.string.home_layout_switch_summary_4x6);
            } else if (2 == Integer.parseInt(mListPreference.getValue())) {
                mListPreference.setSummary(R.string.home_layout_switch_summary_5x5);
            }
        }
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (mCameraSwitch == preference) {
            if (DeviceInfo.isBacon()) {
                String ValueCameraSwitch = (String) newValue;
                mCameraSwitch.setValue(ValueCameraSwitch);
                int mode = Integer.parseInt(ValueCameraSwitch);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.camera_switch_oppo_summary);
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.shell("cp -f /system/stocksettings/OppoCamera.apk /system/priv-app/Camera.apk");
                        Tools.shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_miui_summary);
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.shell("cp -f /system/stocksettings/MiuiCamera.apk /system/priv-app/Camera.apk");
                        Tools.shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 2:
                        preference.setSummary(R.string.camera_switch_cm_summary);
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.shell("cp -f /system/stocksettings/CyanogenModCamera.apk /system/priv-app/Camera.apk");
                        Tools.shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    default:
                        break;
                }
            } else if (DeviceInfo.is8297()) {
                String ValueCameraSwitch = (String) newValue;
                mCameraSwitch.setValue(ValueCameraSwitch);
                int mode = Integer.parseInt(ValueCameraSwitch);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.camera_switch_miui_summary);
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.shell("cp -f /system/stocksettings/MiuiCamera.apk /system/priv-app/Camera.apk");
                        Tools.shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_cp_summary);
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.shell("cp -f /system/stocksettings/CoolpadCamera.apk /system/priv-app/Camera.apk");
                        Tools.shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    default:
                        break;
                }
            }
        }

        if (mHomeLayoutSwitch == preference) {
            String ValueHomeLayoutSwitch = (String) newValue;
            mHomeLayoutSwitch.setValue(ValueHomeLayoutSwitch);
            int mode = Integer.parseInt(ValueHomeLayoutSwitch);
            switch (mode) {
                case 0:
                    preference.setSummary(R.string.home_layout_switch_summary_4x5);
                    Tools.shell("mount -o remount,rw /system");
                    Tools.shell("rm -rf /system/media/theme/default/com.miui.home");
                    Tools.shell("busybox killall com.miui.home");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    break;
                case 1:
                    preference.setSummary(R.string.home_layout_switch_summary_4x6);
                    if (DeviceInfo.isBacon() && IntNowDensity <= 440) {
                        //如果检测到是一加一，并且DPI≤440，则直接删除默认主题路径的桌面布局文件
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/media/theme/default/com.miui.home");
                    } else {
                        //否则就使用内置的桌面布局文件
                        Tools.shell("mount -o remount,rw /system");
                        Tools.shell("rm -rf /system/media/theme/default/com.miui.home");
                        Tools.shell("cp -f /system/stocksettings/com.miui.home46 /system/media/theme/default/com.miui.home");
                    }
                    Tools.shell("busybox killall com.miui.home");
                    Intent intent1 = new Intent(Intent.ACTION_MAIN);
                    intent1.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent1);
                    break;
                case 2:
                    preference.setSummary(R.string.home_layout_switch_summary_5x5);
                    Tools.shell("mount -o remount,rw /system");
                    Tools.shell("rm -rf /system/media/theme/default/com.miui.home");
                    Tools.shell("cp -f /system/stocksettings/com.miui.home55 /system/media/theme/default/com.miui.home");
                    Tools.shell("busybox killall com.miui.home");
                    Intent intent2 = new Intent(Intent.ACTION_MAIN);
                    intent2.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent2);
                    break;
                default:
                    break;
            }
        }
        return false;
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

    private void showDpiWarning() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.home_layout_switch_summary_not_support_4x5)
                .setPositiveButton(R.string.dialog_is_agree, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PrefUtils.saveBoolean(getApplicationContext(), "show_dpi_warning", false);
                    }
                })
                .show();
    }

}
