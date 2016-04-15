package com.xs.stocksettings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

/**
 * Created by xs on 16-4-14.
 */
public class DonatePreference extends miui.preference.PreferenceActivity {

    public void onCreate(Bundle savedInstanceState) {
        setTheme(miui.R.style.Theme_Light_Settings);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.donate_preference);
    }

    //捐赠按钮 触发事件
    public void donate_button(View view) {
        //支付宝QR Link
        String uri = "https://qr.alipay.com/apqb2gl09e91ifsq17";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            intent.setData(Uri.parse("alipays://platformapi/startApp?appId=10000007&sourceId=xiaomiScan&actionType=route&qrcode=" + uri));
            startActivity(intent);
        } catch (Exception e) {
            intent.setData(Uri.parse("http://d.alipay.com"));
            startActivity(intent);
        }
    }

}