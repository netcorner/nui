package com.ingwill.entity;

/**
 * Created by netcorner on 15/11/16.
 */
public class PhoneContact {
    private String Number;
    private String Name;
    private int ContactID;
    private String Photo;
    private int FriendID;
    private String ModifyName;
    private String SortLetters;	// 显示数据拼音的首字母

    public String getSortLetters() {
        return SortLetters;
    }

    public void setSortLetters(String sortLetters) {
        SortLetters = sortLetters;
    }
    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getContactID() {
        return ContactID;
    }

    public void setContactID(int contactID) {
        ContactID = contactID;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public int getFriendID() {
        return FriendID;
    }

    public void setFriendID(int friendID) {
        FriendID = friendID;
    }

    public String getModifyName() {
        return ModifyName;
    }

    public void setModifyName(String modifyName) {
        ModifyName = modifyName;
    }
}
