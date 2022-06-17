package com.ingwill.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.ingwill.entity.PhoneContact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by netcorner on 15/11/15.
 */
public class ContactUtils {
    /**
     * 根据来电号码获取联系人名字,全字匹配
     * */
    public static String getContactByNumber(Context context, String number){
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        if(cursor.moveToFirst()){
            return cursor.getString(0);
        }
        return null;
    }

    /**
     * 根据来电号码获取联系人,模糊匹配
     * @param context
     * @param number
     * @return
     */
    public static List<PhoneContact> getContactsByNumber(Context context, String number){
        Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 访问所有联系人
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        List<PhoneContact> list=new ArrayList<PhoneContact>();
        List<String> listStr=new ArrayList<>();
        while(cursor.moveToNext()){
            int contactsId = cursor.getInt(0);
            uri = Uri.parse("content://com.android.contacts/contacts/" + contactsId + "/data"); //某个联系人下面的所有数据
            Cursor dataCursor = resolver.query(uri, new String[]{"mimetype", "data1", "data2"}, null, null, null);
            PhoneContact phoneContact =new PhoneContact();
            phoneContact.setContactID(contactsId);
            while(dataCursor.moveToNext()){
                String data = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                String type = dataCursor.getString(dataCursor.getColumnIndex("mimetype"));

                if("vnd.android.cursor.item/name".equals(type)){    // 如果他的mimetype类型是name
                    phoneContact.setName(data);
                } else if("vnd.android.cursor.item/phone_v2".equals(type)){ // 如果他的mimetype类型是phone
                    if(data.contains(number)||number.equals("*")) {
                        if(!listStr.contains(data)) {
                            int index=list.size()-1;
                            if(index>0&&phoneContact.getContactID()==list.get(index).getContactID()){
                                PhoneContact phoneContact1 =new PhoneContact();
                                phoneContact1.setContactID(phoneContact.getContactID());
                                phoneContact1.setName(phoneContact.getName());
                                phoneContact1.setNumber(data.replaceAll("[+]{1}\\d{2}[ ]*", "").replaceAll(" ", ""));
                                list.add(phoneContact1);
                            }else {
                                phoneContact.setNumber(data.replaceAll("[+]{1}\\d{2}[ ]*", "").replaceAll(" ", ""));
                                list.add(phoneContact);
                            }
                            listStr.add(data);
                        }
                    }
                }
            }
            dataCursor.close();
        }
        cursor.close();
        return list;
    }

    /**
     * 根据来电号码获取联系人
     * @param context
     * @return
     */
    public static List<PhoneContact> getContacts(Context context){
        return getContactsByNumber(context,"*");
    }
}
