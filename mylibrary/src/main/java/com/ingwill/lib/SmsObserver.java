package com.ingwill.lib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by netcorner on 15/10/30.
 */
public class SmsObserver extends ContentObserver {
    public interface SmsReadCallback {
        void readAfter(String msg);
    }


    private Context ctx;
    private Handler handler;
    private SmsReadCallback smsReadCallback;
    public SmsObserver(Context context, Handler handler, SmsReadCallback smsReadCallback) {
        super(handler);
        this.handler=handler;
        ctx=context;
        this.smsReadCallback=smsReadCallback;
    }



    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        // 每当有新短信到来时，使用我们获取短消息的方法
        getSmsFromPhone();
    }
    public static Uri SMS_INBOX = Uri.parse("content://sms/");

    public void getSmsFromPhone() {
        ContentResolver cr = ctx.getContentResolver();
        String[] projection = new String[] { "body","address","person"};// "_id", "address",
        // "person",, "date",
        // "type
        String where = " date >  "
                + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String number = cur.getString(cur.getColumnIndex("address"));// 手机号
            String name = cur.getString(cur.getColumnIndex("person"));// 联系人姓名列表
            String body = cur.getString(cur.getColumnIndex("body"));


            // 这里我是要获取自己短信服务号码中的验证码~~
            Pattern pattern = Pattern.compile("[0-9]{4}");
            Matcher matcher = pattern.matcher(body);//String body="测试验证码2346ds";
            if (matcher.find()) {
                String res = matcher.group().substring(0, 4);// 获取短信的内容
                smsReadCallback.readAfter(res);
            }
        }
    }


}