package com.example.cleancontacts;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class DeviceAdminSample extends DeviceAdminReceiver {

    void showToast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        super.onEnabled(context, intent);
        showToast(context, "Device admin enabled");

        /*В дальнейшем этот менеджер будет использоваться для установки политик.
        Метод onEnabled(), устанавливающий требуемое качество пароля мог бы выглядеть примерно так:*/
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName cn = new ComponentName(context, getClass());
        dpm.setCameraDisabled(cn, true);

        // Вызывается, когда пользователь разрешил использовать
        // этот приложение как администратор устройства.
        // Здесь можно использовать DevicePolicyManager
        // для установки политик администрирования.
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        super.onDisabled(context, intent);
        showToast(context, "Device admin disabled");
    }
}