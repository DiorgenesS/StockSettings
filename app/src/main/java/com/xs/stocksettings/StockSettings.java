package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.*;
import android.provider.Settings;
import android.widget.Toast;
import com.xs.stocksettings.utils.*;

import miui.os.SystemProperties;

/**
 * Created by xs on 15-7-25.
 */
public class StockSettings extends miui.preference.PreferenceActivity implements Preference.OnPreferenceChangeListener {

    private static final String DoubleTapHomeToSleep = "key_home_double_tap_action";
    private static final String CMSettings = "cm_settings_key";
    private static final String CameraSwitch = "camera_switch_key";
    private static final String HomeLayoutSwitch = "home_layout_switch_key";
    private static final String Density = "density_key";
    private static final String SoundPatch = "sound_patch_key";
    private static final String AppScreenMask = "app_screen_mask_key";
    private static final String StorageSwitch = "storage_switch_key";
    private static final String SystemuiStyle = "systemui_style_key";
    private static final String NowDensity = SystemProperties.get("persist.xsdensity");

    private CheckBoxPreference mDoubleTapHomeToSleep;
    private CheckBoxPreference mSoundPatch;
    private PreferenceScreen mCMSettings;
    private PreferenceScreen mAppScreenMask;
    private ListPreference mCameraSwitch;
    private ListPreference mHomeLayoutSwitch;
    private ListPreference mStorageSwitch;
    private ListPreference mSystemuiStyle;
    private EditTextPreference mDensity;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.stocksettings);

        mDoubleTapHomeToSleep = (CheckBoxPreference) findPreference(DoubleTapHomeToSleep);
        mSoundPatch = (CheckBoxPreference) findPreference(SoundPatch);
        mCMSettings = (PreferenceScreen) findPreference(CMSettings);
        mAppScreenMask = (PreferenceScreen) findPreference(AppScreenMask);
        mDensity = (EditTextPreference) findPreference(Density);

        mCameraSwitch = (ListPreference) findPreference(CameraSwitch);
        mCameraSwitch.setOnPreferenceChangeListener(this);

        mHomeLayoutSwitch = (ListPreference) findPreference(HomeLayoutSwitch);
        mHomeLayoutSwitch.setOnPreferenceChangeListener(this);

        mStorageSwitch = (ListPreference) findPreference(StorageSwitch);
        mStorageSwitch.setOnPreferenceChangeListener(this);

        mSystemuiStyle = (ListPreference) findPreference(SystemuiStyle);
        mSystemuiStyle.setOnPreferenceChangeListener(this);

        //Device
        if (DeviceInfo.IsBacon()) { /* Oneplus A0001 */
            getPreferenceScreen().removePreference(mSoundPatch);
            getPreferenceScreen().removePreference(mAppScreenMask);
            getPreferenceScreen().removePreference(mStorageSwitch);
            getPreferenceScreen().removePreference(mSystemuiStyle);
            mCameraSwitch.setEntries(R.array.camera_switch_entries_bacon);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_bacon);
            mHomeLayoutSwitch.setEntries(R.array.home_layout_switch_entries);
            mHomeLayoutSwitch.setEntryValues(R.array.home_layout_switch_values);
            //DPI
            String density_edit_message = getResources().getString(R.string.density_edit_message);
            String density_edit_message_format = String.format(density_edit_message,"300-600");
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
                        if (i < 300 || i > 600) {
                            Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                        String density_sumarry = getResources().getString(R.string.density_summary);
                        String density_summary_format = String.format(density_sumarry,NewDensity,480);
                        mDensity.setSummary(density_summary_format);
                        DialogReboot();
                        RootCmd.RunRootCmd("setprop persist.xsdensity " + NewDensity + "");
                        return true;
                    }
                }
            });
        } else if (DeviceInfo.Is8297()) { /* Coolpad 8297 */
            mCameraSwitch.setEntries(R.array.camera_switch_entries_8297);
            mCameraSwitch.setEntryValues(R.array.camera_switch_values_8297);
            mStorageSwitch.setEntries(R.array.storage_switch_entries);
            mStorageSwitch.setEntryValues(R.array.storage_switch_values);
            mSystemuiStyle.setEntries(R.array.systemui_style_entries);
            mSystemuiStyle.setEntryValues(R.array.systemui_style_values);
            mHomeLayoutSwitch.setEntries(R.array.home_layout_switch_entries);
            mHomeLayoutSwitch.setEntryValues(R.array.home_layout_switch_values);
            getPreferenceScreen().removePreference(mDoubleTapHomeToSleep);
            getPreferenceScreen().removePreference(mCMSettings);
            //DPI
            String density_edit_message = getResources().getString(R.string.density_edit_message);
            String density_edit_message_format = String.format(density_edit_message,"280-320");
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
                        if (i < 280 || i > 320) {
                            Toast.makeText(getBaseContext(), R.string.density_error, Toast.LENGTH_LONG).show();
                            return false;
                        }
                    }
                    String density_summary = getResources().getString(R.string.density_summary);
                    String density_summar_format = String.format(density_summary,NewDensity,320);
                    mDensity.setSummary(density_summar_format);
                    DialogReboot();
                    RootCmd.RunRootCmd("setprop persist.xsdensity " + NewDensity + "");
                    return true;
                }
            });
        } else { /* Null Device*/
            getPreferenceScreen().removeAll();
        }

        setListPreferenceSummary(mCameraSwitch);
        setListPreferenceSummary(mHomeLayoutSwitch);
        setListPreferenceSummary(mStorageSwitch);
        setListPreferenceSummary(mSystemuiStyle);
        setEditTextPreferenceSummary(mDensity);
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen , Preference preference) {
        if (preference == mDoubleTapHomeToSleep) {
            if (mDoubleTapHomeToSleep.isChecked()) {
                Settings.System.putInt(getContentResolver(), DoubleTapHomeToSleep, 8);
            } else {
                Settings.System.putInt(getContentResolver(),DoubleTapHomeToSleep,0);
            }
        }
        if (preference == mSoundPatch) {
            if (mSoundPatch.isChecked()){
                RootCmd.RunRootCmd("mount -o remount,rw /data");
                RootCmd.RunRootCmd("cp -r /system/stocksettings/Audio_ver1_Vol_custom /data/nvram/APCFG/APRDCL/Audio_ver1_Vol_custom");
                DialogReboot();
            } else {
                RootCmd.RunRootCmd("mount -o remount,rw /data");
                RootCmd.RunRootCmd("rm -rf /data/nvram/APCFG/APRDCL/Audio_ver1_Vol_custom");
                DialogReboot();
            }
        }
        return false;
    }

    private void setEditTextPreferenceSummary(EditTextPreference mEditTextPreference) {
        if (mEditTextPreference == mDensity) {
            if (DeviceInfo.IsBacon()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary,NowDensity,"480");
                mDensity.setSummary(density_summary_format);
            } else if (DeviceInfo.Is8297()) {
                String density_summary = getResources().getString(R.string.density_summary);
                String density_summary_format = String.format(density_summary,NowDensity,"320");
                mDensity.setSummary(density_summary_format);
            }
        }
    }

    private void setListPreferenceSummary(ListPreference mListPreference) {
            if (mListPreference == mCameraSwitch) {
                if (DeviceInfo.IsBacon()) {
                    if (0 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.camera_switch_color_summary);
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
                if (DeviceInfo.IsBacon()) {
                    if (0 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_4x5);
                    } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_4x6);
                    } else if (2 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_5x5);
                    }
                } else if (DeviceInfo.Is8297()) {
                    if (0 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_4x5);
                    } else if (1 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_4x6);
                    } else if (2 == Integer.parseInt(mListPreference.getValue())) {
                        mListPreference.setSummary(R.string.home_layout_switch_summary_5x5);
                    }
                }
            }

        if (mListPreference == mStorageSwitch) {
            if (DeviceInfo.IsBacon()) {

            } else if (DeviceInfo.Is8297()) {
                if (0 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.storage_switch_internal);
                } else {
                    mListPreference.setSummary(R.string.storage_switch_external);
                }
            }
        }

        if (mListPreference == mSystemuiStyle) {
            if (DeviceInfo.IsBacon()) {

            } else if (DeviceInfo.Is8297()) {
                if (0 == Integer.parseInt(mListPreference.getValue())) {
                    mListPreference.setSummary(R.string.systemui_style_default);
                } else {
                    mListPreference.setSummary(R.string.systemui_style_diy);
                }
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
                        preference.setSummary(R.string.camera_switch_color_summary);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/app/Camera.apk");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/ColorCameraMod.apk /system/app/Camera.apk");
                        RootCmd.RunRootCmd("chmod 0644 /system/app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_miui_summary);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/app/Camera.apk");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/MiuiCamera.apk /system/app/Camera.apk");
                        RootCmd.RunRootCmd("chmod 0644 /system/app/Camera.apk");
                        break;
                    case 2:
                        preference.setSummary(R.string.camera_switch_cm_summary);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/app/Camera.apk");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/CyanogenModCamera.apk /system/app/Camera.apk");
                        RootCmd.RunRootCmd("chmod 0644 /system/app/Camera.apk");
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
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/priv-app/Camera.apk");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/MiuiCamera.apk /system/priv-app/Camera.apk");
                        RootCmd.RunRootCmd("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    case 1:
                        preference.setSummary(R.string.camera_switch_cp_summary);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/priv-app/Camera.apk");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/CoolpadCamera.apk /system/priv-app/Camera.apk");
                        RootCmd.RunRootCmd("chmod 0644 /system/priv-app/Camera.apk");
                        break;
                    default:
                        break;
                }
            }
        }

        if (mHomeLayoutSwitch == preference) {
            if (DeviceInfo.IsBacon()) {
                String ValueHomeLayoutSwitch = (String) newValue;
                mHomeLayoutSwitch.setValue(ValueHomeLayoutSwitch);
                int mode = Integer.parseInt(ValueHomeLayoutSwitch);
                switch  (mode) {
                    case 0:
                        preference.setSummary(R.string.home_layout_switch_summary_4x5);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        break;
                    case 1:
                        preference.setSummary(R.string.home_layout_switch_summary_4x6);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/com.miui.home46 /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                        intent1.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent1);
                        break;
                    case 2:
                        preference.setSummary(R.string.home_layout_switch_summary_5x5);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/com.miui.home55 /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent2 = new Intent(Intent.ACTION_MAIN);
                        intent2.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
            } else if (DeviceInfo.Is8297()) {
                String ValueHomeLayoutSwitch = (String) newValue;
                mHomeLayoutSwitch.setValue(ValueHomeLayoutSwitch);
                int mode = Integer.parseInt(ValueHomeLayoutSwitch);
                switch  (mode) {
                    case 0:
                        preference.setSummary(R.string.home_layout_switch_summary_4x5);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent);
                        break;
                    case 1:
                        preference.setSummary(R.string.home_layout_switch_summary_4x6);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/com.miui.home46 /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent1 = new Intent(Intent.ACTION_MAIN);
                        intent1.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent1);
                        break;
                    case 2:
                        preference.setSummary(R.string.home_layout_switch_summary_5x5);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("cp -f /system/stocksettings/com.miui.home55 /system/media/theme/default/com.miui.home");
                        RootCmd.RunRootCmd("busybox killall com.miui.home");
                        Intent intent2 = new Intent(Intent.ACTION_MAIN);
                        intent2.addCategory(Intent.CATEGORY_HOME);
                        startActivity(intent2);
                        break;
                    default:
                        break;
                }
            }
        }

        if (mStorageSwitch == preference) {
            if (DeviceInfo.IsBacon()) {

            } else if (DeviceInfo.Is8297()) {
                String ValueStorageSwitch = (String) newValue;
                mStorageSwitch.setValue(ValueStorageSwitch);
                int mode = Integer.parseInt(ValueStorageSwitch);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.storage_switch_internal);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/bin/sdcard");
                        RootCmd.RunRootCmd("rm -rf /system/bin/vold");
                        RootCmd.RunRootCmd("rm -rf /system/etc/vold.fstab");
                        RootCmd.RunRootCmd("rm -rf /system/etc/vold.fstab.nand");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/internal/sdcard /system/bin/sdcard");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/internal/vold /system/bin/vold");
                        RootCmd.RunRootCmd("chmod 0755 /system/bin/sdcard");
                        RootCmd.RunRootCmd("chmod 0755 /system/bin/vold");
                        RootCmd.RunRootCmd("dd if=/system/stocksettings/internal/boot.img of=/dev/bootimg");
                        DialogReboot();
                        break;
                    case 1:
                        preference.setSummary(R.string.storage_switch_external);
                        RootCmd.RunRootCmd("mount -o remount,rw /system");
                        RootCmd.RunRootCmd("rm -rf /system/bin/sdcard");
                        RootCmd.RunRootCmd("rm -rf /system/bin/vold");
                        RootCmd.RunRootCmd("rm -rf /system/etc/vold.fstab");
                        RootCmd.RunRootCmd("rm -rf /system/etc/vold.fstab.nand");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/external/sdcard /system/bin/sdcard");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/external/vold /system/bin/vold");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/external/vold.fstab /system/etc/vold.fstab");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/external/vold.fstab.nand /system/etc/vold.fstab.nand");
                        RootCmd.RunRootCmd("chmod 0755 /system/bin/sdcard");
                        RootCmd.RunRootCmd("chmod 0755 /system/bin/vold");
                        RootCmd.RunRootCmd("chmod 0755 /system/etc/vold.fstab");
                        RootCmd.RunRootCmd("chmod 0755 /system/etc/vold.fstab.nand");
                        RootCmd.RunRootCmd("dd if=/system/stocksettings/external/boot.img of=/dev/bootimg");
                        DialogReboot();
                        break;
                    default:
                        break;
                }
            }
        }

        if (mSystemuiStyle == preference) {
            if (DeviceInfo.IsBacon()) {

            } else if (DeviceInfo.Is8297()) {
                String ValueSystemuiStyle = (String) newValue;
                mSystemuiStyle.setValue(ValueSystemuiStyle);
                int mode = Integer.parseInt(ValueSystemuiStyle);
                switch (mode) {
                    case 0:
                        preference.setSummary(R.string.systemui_style_default);
                        RootCmd.RunRootCmd("mount -o remount,rw /data");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/systemui/default/statusbar_clock.maml /data/data/com.android.systemui/files/statusbar_clock.maml");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/systemui/default/statusbar_music.maml /data/data/com.android.systemui/files/statusbar_music.maml");
                        DialogReboot();
                        break;
                    case 1:
                        preference.setSummary(R.string.systemui_style_diy);
                        RootCmd.RunRootCmd("mount -o remount,rw /data");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/systemui/diy/statusbar_clock.maml /data/data/com.android.systemui/files/statusbar_clock.maml");
                        RootCmd.RunRootCmd("cp -r /system/stocksettings/systemui/diy/statusbar_music.maml /data/data/com.android.systemui/files/statusbar_music.maml");
                        DialogReboot();
                        break;
                    default:
                        break;
                }
            }
        }

        return false;
    }

    public void DialogReboot() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_ok)
                .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RootCmd.RunRootCmd("busybox killall system_server");
                    }
                })
                .setNeutralButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),R.string.dialog_reboot,Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
