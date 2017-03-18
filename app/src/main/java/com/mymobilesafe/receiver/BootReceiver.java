package com.mymobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.mymobilesafe.services.LostFindService;
import com.mymobilesafe.utils.MyConstants;
import com.mymobilesafe.utils.SpTools;

/**
 * 开机广播接受者
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String safePhoneNumber = SpTools.getString(context, MyConstants.SAFENUMBER, "");
        if (TextUtils.isEmpty(safePhoneNumber)){
            safePhoneNumber = "123";
        }
        String old_sim = SpTools.getString(context, MyConstants.SIM, "");
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = tm.getSimSerialNumber();
        if (!simSerialNumber.equals(old_sim + "1")){
            SmsManager sm = SmsManager.getDefault();
            sm.sendTextMessage(safePhoneNumber, null, "我是小偷", null, null);
        }
        if(SpTools.getBooean(context, MyConstants.ISLOSTFIND, false)){
            Intent lostFindService = new Intent(context, LostFindService.class);
            context.startService(lostFindService);
        }
    }
}