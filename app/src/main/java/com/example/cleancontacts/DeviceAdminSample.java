package com.example.cleancontacts;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdminSample extends DeviceAdminReceiver {

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Device admin enabled");
        // Вызывается, когда пользователь разрешил использовать
        // этот приложение как администратор устройства.
        // Здесь можно использовать DevicePolicyManager
        // для установки политик администрирования.
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, "Device admin disabled");
    }
}