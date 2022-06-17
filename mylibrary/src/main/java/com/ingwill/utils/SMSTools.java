package com.ingwill.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

/**
 * Created by netcorner on 15/11/16.
 */
public class SMSTools {
    /**
     * 调起系统发短信功能
     * @param context
     * @param phoneNumber
     * @param message
     */
    public static void doSendSMSTo(Context context, String phoneNumber, String message){
        if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)){
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
            intent.putExtra("sms_body", message);
            context.startActivity(intent);
        }
    }
}
