package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.widget.Toast;

import com.xs.stocksettings.utils.DeviceInfo;
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
    private static final String NowDensity = SystemProperties.get("persist.xsdensity");
    //获取persist.xsdensity值并转换为int类型
    private static final int IntNowDensity = Integer.parseInt(NowDensity);

    private CheckBoxPreference mDoubleTapHomeToSleep;
    private PreferenceScreen mCMSettings;
    private PreferenceScreen mAppScreenMask;
    private ListPreference mCameraSwitch;
    private ListPreference mHomeLayoutSwitch;
    private EditTextPreference mDensity;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings);

        mDoubleTapHomeToSleep = (CheckBoxPreference) findPreference(DoubleTapHomeToSleep);
        mCMSettings = (PreferenceScreen) findPreference(CMSettings);
        mAppScreenMask = (PreferenceScreen) findPreference(AppScreenMask);
        mDensity = (EditTextPreference) findPreference(Density);

        mCameraSwitch = (ListPreference) findPreference(CameraSwitch);
        mCameraSwitch.setOnPreferenceChangeListener(this);

        mHomeLayoutSwitch = (ListPreference) findPreference(HomeLayoutSwitch);
        mHomeLayoutSwitch.setOnPreferenceChangeListener(this);

        //默认布局切换
        mHomeLayoutSwitch.setEntries(new String[]{"4x5", "4x6", "5x5"});
        mHomeLayoutSwitch.setEntryValues(new String[]{"0", "1", "2"});

        //Device
        if (DeviceInfo.IsBacon()) { /* Oneplus A0001 */

            //添加DPI判断，如果DPI≤440，则移除4x5布局切换
            if (IntNowDensity <= 440) {
                //mHomeLayoutSwitch.setEntries(new String[]{"4x5", "4x6", "5x5"});
                //mHomeLayoutSwitch.setEntryValues(new String[]{"0", "1", "2"});
                mHomeLayoutSwitch.setEntries(new String[]{"4x6", "5x5"});
                mHomeLayoutSwitch.setEntryValues(new String[]{"1", "2"});
                Toast.makeText(this, R.string.home_layout_switch_summary_not_support_4x5, Toast.LENGTH_SHORT).show();
            }

            getPreferenceScreen().removePreference(mAppScreenMask);
            mCameraSwitch.setEntries(R.array.camera_switch_entries_bacon);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_bacon);
            //DPI
            String density_edit_message = getResources().getString(R.string.density_edit_message);
            String density_edit_message_format = String.format(density_edit_message, "300-600");
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
                        if (IntNowDensity < 300 || IntNowDensity > 600) {
                            Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                        String density_sumarry = getResources().getString(R.string.density_summary);
                        String density_summary_format = String.format(density_sumarry, NewDensity, 480);
                        mDensity.setSummary(density_summary_format);
                        DialogReboot();
                        Tools.Shell("setprop persist.xsdensity " + NewDensity + "");
                        return true;
                    }
                }
            });
        } else if (DeviceInfo.Is8297()) { /* Coolpad 8297 */
            mCameraSwitch.setEntries(R.array.camera_switch_entries_8297);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_8297);
            getPreferenceScreen().removePreference(mDoubleTapHomeToSleep);
            getPreferenceScreen().removePreference(mCMSettings);
            //DPI
            String density_edit_message = getResources().getString(R.string.density_edit_message);
            String density_edit_message_format = String.format(density_edit_message, "280-320");
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
                        if (IntNowDensity < 280 || IntNowDensity > 320) {
                            Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                    String density_summary = getResources().getString(R.string.density_summary);
                    String density_summar_format = String.format(density_summary, NewDensity, 320);
                    mDensity.setSummary(density_summar_format);
                    DialogReboot();
                    Tools.Shell("setprop persist.xsdensity " + NewDensity + "");
                    return true;
                }
            });
        } else { /* Null Device*/
            getPreferenceScreen().removeAll();
        }
    }

    public void onStart() {
        super.onStart();
        setListPreferenceSummary(mCameraSwitch);
        setListPreferenceSummary(mHomeLayoutSwitch);
        setEditTextPreferenceSummary(mDensity);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen, Preference preference) {
        if (preference == mDoubleTapHomeToSleep) {
            if (mDoubleTapHomeToSleep.isChecked()) {
                Settings.System.putInt(getContentResolver(), "key_home_double_tap_action", 8);
            } else {
                Settings.System.putInt(getContentResolver(), "key_home_double_tap_action", 0);
            }
        }
        return false;
    }

    private void setEditTextPreferenceSummary(EditTextPreference mEditTextPreference) {
        if (mEditTextPreference == mDensity) {
            if (DeviceInfo.IsBacon()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary, NowDensity, "480");
                mDensity.setSummary(density_summary_format);
            } else if (DeviceInfo.Is8297()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary, NowDensity, "320");
                mDensity.setSummary(density_summary_format);
            }
        }
    }

    private void setListPreferenceSummary(ListPreference mListPreference) {
        if (mListPreference == mCameraSwitch) {
            if (DeviceInfo.IsBacon()) {
                if (0 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_oppo_summary);
                } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_miui_summary);
                } else if (2 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.camera_switch_cm_summary);
                }
            } else if (DeviceInfo.Is8297()) {
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
                //如果检测到是一加一，并且DPI≤440，则设置4x5布局的summary为不支持4x5布局
                if (DeviceInfo.IsBacon() && IntNowDensity <= 440) {
                    mListPreference.setSummary(R.string.home_layout_switch_summary_not_support_4x5);
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
            if (DeviceInfo.IsBacon()) {
                String ValueCameraSwitch = (String) newValue;
                mCameraSwitch.setValue(ValueCameraSwitch);
                int mode = Integer.parseInt(ValueCameraSwitch);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.camera_switch_oppo_summary);
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.Shell("cp -f /system/stocksettings/OppoCamera.apk /system/priv-app/Camera.apk");
                        Tools.Shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_miui_summary);
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.Shell("cp -f /system/stocksettings/MiuiCamera.apk /system/priv-app/Camera.apk");
                        Tools.Shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 2:
                        preference.setSummary(R.string.camera_switch_cm_summary);
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.Shell("cp -f /system/stocksettings/CyanogenModCamera.apk /system/priv-app/Camera.apk");
                        Tools.Shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    default:
                        break;
                }
            } else if (DeviceInfo.Is8297()) {
                String ValueCameraSwitch = (String) newValue;
                mCameraSwitch.setValue(ValueCameraSwitch);
                int mode = Integer.parseInt(ValueCameraSwitch);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.camera_switch_miui_summary);
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.Shell("cp -f /system/stocksettings/MiuiCamera.apk /system/priv-app/Camera.apk");
                        Tools.Shell("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_cp_summary);
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/priv-app/Camera.apk");
                        Tools.Shell("cp -f /system/stocksettings/CoolpadCamera.apk /system/priv-app/Camera.apk");
                        Tools.Shell("chmod 0644 /system/priv-app/Camera.apk");
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
                    Tools.Shell("mount -o remount,rw /system");
                    Tools.Shell("rm -rf /system/media/theme/default/com.miui.home");
                    Tools.Shell("busybox killall com.miui.home");
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent);
                    break;
                case 1:
                    preference.setSummary(R.string.home_layout_switch_summary_4x6);
                    if (DeviceInfo.IsBacon() && IntNowDensity <= 440) {
                        //如果检测到是一加一，并且DPI≤440，则直接删除默认主题路径的桌面布局文件
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/media/theme/default/com.miui.home");
                    } else {
                        //否则就使用内置的桌面布局文件
                        Tools.Shell("mount -o remount,rw /system");
                        Tools.Shell("rm -rf /system/media/theme/default/com.miui.home");
                        Tools.Shell("cp -f /system/stocksettings/com.miui.home46 /system/media/theme/default/com.miui.home");
                    }
                    Tools.Shell("busybox killall com.miui.home");
                    Intent intent1 = new Intent(Intent.ACTION_MAIN);
                    intent1.addCategory(Intent.CATEGORY_HOME);
                    startActivity(intent1);
                    break;
                case 2:
                    preference.setSummary(R.string.home_layout_switch_summary_5x5);
                    Tools.Shell("mount -o remount,rw /system");
                    Tools.Shell("rm -rf /system/media/theme/default/com.miui.home");
                    Tools.Shell("cp -f /system/stocksettings/com.miui.home55 /system/media/theme/default/com.miui.home");
                    Tools.Shell("busybox killall com.miui.home");
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

    public void DialogReboot() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.dialog_ok)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Tools.Shell("reboot");
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
