package com.xs.stocksettings.utils;

import android.util.Log;

import java.io.DataOutputStream;

//RootCmd Shell
//RootCmd.RunRootCmd("shell");

public class RootCmd {
    private static final String LOG_TAG = "StockSettings";

    public static int RunRootCmd(String paramString) {
        try {
            Log.i(LOG_TAG, paramString);
            Process localProcess = Runtime.getRuntime().exec("su");
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    localProcess.getOutputStream());
            String str = paramString + "\n";
            localDataOutputStream.writeBytes(str);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            return localProcess.exitValue();

        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return 1;

    }
}