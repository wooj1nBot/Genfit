package com.realese.genfit.items;

import android.content.Context;
import android.provider.Settings;

public class GetDeviceId {
    public static String getDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return deviceId;
    }
}
