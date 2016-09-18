package com.xs.stocksettings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import com.xs.stocksettings.utils.DeviceInfo;
import com.xs.stocksettings.utils.Tools;

/**
 * Created by xs on 15-8-25.
 */
public class Sounds extends miui.preference.PreferenceActivity {

    private static final String CameraSound = "camera_sound_key";
    private static final String SoundPatch = "sound_patch_key";


    private CheckBoxPreference mCameraSound;
    private CheckBoxPreference mSoundPatch;

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.sounds);

        mCameraSound = (CheckBoxPreference) findPreference(CameraSound);
        mSoundPatch = (CheckBoxPreference) findPreference(SoundPatch);

        if (DeviceInfo.isBacon()) {
            getPreferenceScreen().removePreference(mSoundPatch);
        }
    }

    public void onStart() {
        super.onStart();
        setCheckBoxPreferenceSummary(mCameraSound);
    }

    private void setCheckBoxPreferenceSummary(CheckBoxPreference mCheckBoxPreference) {
        if (mCheckBoxPreference == mCameraSound) {
            if (mCameraSound.isChecked()) {
                mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_on));
            } else {
                mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_off));
            }
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferencescreen, Preference preference) {
        if (preference == mSoundPatch) {
            Tools.shell("mount -o remount,rw /data");
            if (mSoundPatch.isChecked()) {
                Tools.shell("cp -r /system/stocksettings/Audio_ver1_Vol_custom /data/nvram/APCFG/APRDCL/Audio_ver1_Vol_custom");
                dialogReboot();
            } else {
                Tools.shell("rm -rf /data/nvram/APCFG/APRDCL/Audio_ver1_Vol_custom");
                dialogReboot();
            }
        }
        if (preference == mCameraSound) {
            if (DeviceInfo.is8297()) {
                Tools.shell("mount -o remount,rw /system");
                if (mCameraSound.isChecked()) {
                    new Thread() {
                        public void run() {
                            Tools.shell("sed -i 's/bak/ogg/g' /system/lib/libcameraservice.so");
                        }
                    }.start();
                    mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_on));
                    dialogReboot();
                } else {
                    new Thread() {
                        public void run() {
                            Tools.shell("sed -i 's/ogg/bak/g' /system/lib/libcameraservice.so");
                        }
                    }.start();
                    mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_off));
                    if (Tools.isInstall(this, "com.oppo.camera")) {
                        Toast.makeText(this, getResources().getString(R.string.find_oppo_camera), Toast.LENGTH_LONG).show();
                    }
                    dialogReboot();
                }
            } else if (DeviceInfo.isBacon()) {
                if (mCameraSound.isChecked()) {
                    Tools.shell("setprop persist.camera.shutter.disable 0");
                    mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_on));
                } else {
                    Tools.shell("setprop persist.camera.shutter.disable 1");
                    mCameraSound.setSummary(getResources().getString(R.string.camera_sound_summary_off));
                    if (Tools.isInstall(this, "com.oppo.camera")) {
                        Toast.makeText(this, getResources().getString(R.string.find_oppo_camera), Toast.LENGTH_LONG).show();
                    }
                }
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
}
