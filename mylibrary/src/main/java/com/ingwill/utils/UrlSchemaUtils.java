package com.ingwill.utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.ingwill.lib.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlSchemaUtils {
    public class UrlSchema {
        private int type;
        private String decsription;
        private String rule;
        private String regUrl;
        private String[] reg;
        private List<String> params;
        private List<String> regList;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getDecsription() {
            return decsription;
        }

        public void setDecsription(String decsription) {
            this.decsription = decsription;
        }

        public String getRule() {
            return rule;
        }

        public void setRule(String rule) {
            this.rule = rule;
        }

        public String[] getReg() {
            return reg;
        }

        public void setReg(String[] reg) {
            this.reg = reg;
        }

        public List<String> getParams() {
            return params;
        }

        public void setParams(List<String> params) {
            this.params = params;
        }

        public String getRegUrl() {
            return regUrl;
        }

        public void setRegUrl(String regUrl) {
            this.regUrl = regUrl;
        }

        public List<String> getRegList() {
            return regList;
        }

        public void setRegList(List<String> regList) {
            this.regList = regList;
        }
    }


    /**
     * url Schema的参数
     * @param context
     * @param assetFilePath
     * @param currentUrl
     * @return
     */
    public static Bundle getUrlSchemaParams(Context context, String assetFilePath, String currentUrl){
        //String currentUrl="ingwill://208/share/ac-ac-bd-dd-bb-dd";
        //UrlSchema[] list=JSONTools.getAssetFile2Json(context,"config/url_scheme_config.json", UrlSchema[].class);
        Bundle result=null;
        UrlSchema[] list= JSONUtils.getAssetFile2Json(context,assetFilePath, UrlSchema[].class);
        for(UrlSchema urlSchema:list){
            int i = 0;
            Pattern oPattern=Pattern.compile("\\{\\w+\\}");
            Matcher matcher = oPattern.matcher(urlSchema.getRule());
            urlSchema.setParams(new ArrayList<String>());
            urlSchema.setRegList(new ArrayList<String>());
            String  rule=urlSchema.getRule();
            while(matcher.find()) {
                String param=matcher.group();
                String s=param.replace("{","").replace("}","");
                String[] arr=rule.split("\\{"+s+"\\}");
                urlSchema.getRegList().add(arr[0]+urlSchema.getReg()[i]);
                urlSchema.getParams().add(s);
                rule=rule.replace(param, urlSchema.getReg()[i]);
                i++;
            }
            urlSchema.setRegUrl(rule);
            if(Validator.isMatch(currentUrl,rule)){
                i=0;
                result=new Bundle();
                for(String reg:urlSchema.getRegList()) {
                    String val=currentUrl.replace(currentUrl.replaceFirst(reg,""),"");
                    val=val.replace(val.replaceFirst(urlSchema.getReg()[i]+"$",""),"");
                    Log.d("UrlSchemaUtils params",urlSchema.getParams().get(i)+":"+val);
                    if(urlSchema.getReg()[i].equals("\\d+")){
                        result.putInt(urlSchema.getParams().get(i), Integer.parseInt(val));
                    }else {
                        if(urlSchema.getReg()[i].equals("[\\d.]+")){
                            result.putDouble(urlSchema.getParams().get(i), Double.parseDouble(val));
                        }else {
                            result.putString(urlSchema.getParams().get(i), val);
                        }
                    }
                    result.putInt("type",urlSchema.getType());
                    i++;
                }
                break;
            }
        }
        return result;
    }
}
