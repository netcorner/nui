package com.ingwill.utils;

import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;


public final class StringUtils {

    private static final AtomicInteger requestCodeSeed = new AtomicInteger(1);

    public static int generateRequestCode() {
        return requestCodeSeed.getAndIncrement();
    }

    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.trim().length() > 0));
    }




    /**
     * 取小数点后两位或限定位数
     * @param num
     * @param numStr
     * @return
     */
    public static String getFormatNum(double num,String numStr){
        DecimalFormat df = new DecimalFormat(numStr);//格式化小数   "0.00"
        return df.format(num);//返回的是String类型
    }

    /**
     * double格式化保留若干个小数
     * @param d
     * @param smallNum
     * @return
     */
    public static double formatDouble(double d,int smallNum) {
        double l=Math.pow(10, smallNum);
        return Math.round(d*l)/l;
    }
    /**
     * 产生随机数
     * @param numberFlag
     * @param length
     * @return
     */
    public static String generateNumString(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890"
                : "1234567890abcdefghijklmnopqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

    /**
     * 通过点的方式取hash数据，如Application.FileType.Value ，就会取得Application下面FileType下面属性为Value的值
     * @param map
     * @param path
     * @return
     */
    public static Object getMapByKey(Map<String,Object> map, String path){
        String[] arr=path.split("\\.");
        Map<String,Object> hash=map;
        for(String key:arr){
            //System.out.println("key:"+key);
            if(hash.containsKey(key)){
                if(hash.get(key) instanceof Map){
                    hash=(Map<String,Object>)hash.get(key);
                    //System.out.println("hash:"+hash);
                }else{
                    //System.out.println("hash1:"+hash);
                    return hash.get(key);
                }
            }else{
                return null;
            }
        }
        return hash;
    }


    public static final String empty="";

    public static List<String> split(String inputValue, String regex){
        return split(inputValue,regex,true);
    }
    public static List<String> split(String inputValue, String regex, boolean isIgnoreCase){
        Pattern oPattern;
        if (isIgnoreCase)
        {
            oPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        }
        else
        {
            oPattern = Pattern.compile(regex);
        }
        StringBuffer sb = new StringBuffer();
        Matcher matcher = oPattern.matcher(inputValue);
        List<String> list=new ArrayList<String>();
        while(matcher.find()) {
            list.add(matcher.group());
        }
        if(list.size()==0) list.add(inputValue);
        return list;
    }


    /**
     * 字符串是空还是null
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str){
        if(str==null) return true;
        if(str.equals(""))return true;
        return false;
    }
    /**
     * 字符串是空还是null
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(Object str){
        if(str==null) return true;
        if((str+"").equals(""))return true;
        return false;
    }
    /**
     * md加密
     * @param message
     * @return
     */
    public static String getMD5(String message) {
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(message.getBytes("utf-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; i++) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100)
                        .toUpperCase().substring(1, 3));
            }
            md5 = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }
    /**
     * md5加密
     */
    public static String md5(Object object) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(toByteArray(object));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
    public static byte[] toByteArray (Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray ();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return bytes;
    }

    public static String getStringLimit(String input, int length) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        int len = input.length();
        if (len <= length) {
            return input;
        } else {
            input = input.substring(0, length);
            input += "...";
        }
        return input;
    }

    /**
     * 删除字符串中的html格式
     *
     * @param input
     * @param length
     * @return
     */
    public static String clearHtml(String input, int length) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素,
        String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "");
        int len = str.length();
        if (len <= length) {
            return str;
        } else {
            str = str.substring(0, length);
            str += "...";
        }
        return str;
    }
    public static String clearHtml(String input) {
        if (input == null || input.trim().equals("")) {
            return "";
        }
        // 去掉所有html元素,
        String str = input.replaceAll("\\&[a-zA-Z]{1,10};", "").replaceAll(
                "<[^>]*>", "");
        str = str.replaceAll("[(/>)<]", "");
        return str;
    }
    /**
     * HTML标签转义方法 —— java代码库
     * @param content
     * @return
     */
    public static String htmlEncode(String content) {
        if(content==null) return "";
        String html = content;
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        return html;
    }


    /**
     *
     * @Title: 保留位数自动补零
     * @category 左补0
     * @param str
     * @param strLength
     * @return
     * @throws
     */
    public static String addZeroForNum(String str, int strLength) {
        int strLen = str.length();
        while (strLen < strLength) {
            StringBuffer sb = new StringBuffer();
            sb.append("0").append(str);// 左补0
            str = sb.toString();
            strLen = str.length();
        }
        return str;
    }
    /**
     *
     * @category: 返回一个定长的随机字符串(只包含大小写字母、数字)
     * @param length
     *            随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {
        final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return new SimpleDateFormat("HHmmss").format(new Date())
                + sb.toString();
    }
    /**
     *
     * @Title: generateNumString
     * @category 返回指定长度的数字字符串
     * @param length
     * @return
     * @throws
     */
    public static String generateNumString(int length){
        final String allChar = "0123456789";
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChar.charAt(random.nextInt(allChar.length())));
        }
        return sb.toString();
    }


    /**
     * 将某种编码(如GB2312)转换成另外一种编码(如UTF-8)
     * @param str
     * @param convertedCodeName
     * @param convertCodeName
     * @return
     */
    public static String codeConvert(String str, String convertedCodeName, String convertCodeName) {
        try {
            if (str != null)
                str = new String(str.getBytes(convertedCodeName), convertCodeName);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }
    /**
     * 将某种编码转成utf-8
     * @param str
     * @param code
     * @return
     */
    public static String convertUTF8(String str, String code) {
        return codeConvert(str,code,"UTF-8");
    }
    /**
     * url编码
     * @param source
     * @param code
     * @return
     */
    public static String urlEncode(String source, String code) {
        try {
            return URLEncoder.encode(source, code);
        } catch (UnsupportedEncodingException e) {
            return source;
        }
    }
    // 根据Unicode编码完美的判断中文汉字和符号
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            return true;
        }
        return false;
    }

    // 完整的判断中文汉字和符号
    public static boolean isChinese(String strName) {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (isChinese(c)) {
                return true;
            }
        }
        return false;
    }


    public static String formatFriendTotal(int total){
        if(total<1000){
            return total+"";
        }else if(total<1000000){
            return total/1000+"k+";
        }else{
            return total/1000000+"m+";
        }
    }


    private final static String DES = "DES";
    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    public static String encrypt(String data, String key) {
        byte[] bt=null;
        try {
            bt = encrypt(data.getBytes(), key.getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String strs = Base64.encodeToString(bt, Base64.NO_WRAP);
        return strs;
    }

    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws IOException
     * @throws Exception
     */
    public static String decrypt(String data, String key){
        if (data == null)
            return null;
        byte[] buf = Base64.decode(data, Base64.NO_WRAP);
        byte[] bt=null;
        try {
            bt = decrypt(buf,key.getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String(bt);
    }

    /**
     * Description 根据键值进行加密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }


    /**
     * Description 根据键值进行解密
     * @param data
     * @param key  加密键byte数组
     * @return
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        // 生成一个可信任的随机数源
        SecureRandom sr = new SecureRandom();

        // 从原始密钥数据创建DESKeySpec对象
        DESKeySpec dks = new DESKeySpec(key);

        // 创建一个密钥工厂，然后用它把DESKeySpec转换成SecretKey对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        SecretKey securekey = keyFactory.generateSecret(dks);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(DES);

        // 用密钥初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);

        return cipher.doFinal(data);
    }

    public static String getRate(int left,int total){
        return ((int)Math.ceil((left+0.0)/total*100))+"%";//小数点进位
    }
    public static String getRateByRint(int left,int total){
        return ((int)Math.rint((left+0.0)/total*100))+"%";//四舍五入
    }

    /**
     * 得到ascii的长度
     * @param str
     * @return
     */
    public static int getAscIILength(String str){
        String regEx = "[\\u4e00-\\u9fa5]";
        String term = str.replaceAll(regEx, "aa");
        return term.length();
    }

    /**
     * Decodes the passed UTF-8 String using an algorithm that's compatible with
     * JavaScript's <code>decodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The UTF-8 encoded String to be decoded
     * @return the decoded String
     */
    public static String decodeURIComponent(String s) {
        if (s == null) {
            return null;
        }

        String result = null;

        try {
            result = URLDecoder.decode(s, "UTF-8");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }

    /**
     * Encodes the passed String as UTF-8 using an algorithm that's compatible
     * with JavaScript's <code>encodeURIComponent</code> function. Returns
     * <code>null</code> if the String is <code>null</code>.
     *
     * @param s The String to be encoded
     * @return the encoded String
     */
    public static String encodeURIComponent(String s) {
        String result = null;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\%7E", "~");
        }

        // This exception should never occur.
        catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }


    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     * @author lzf
     */
    public static String truncateUrlPage(String strURL){
        strURL= StringUtils.decodeURIComponent(strURL);
        String[] arrSplit=null;
        arrSplit=strURL.split("[?]");
        if(strURL.length()>1){
            if(arrSplit.length>1){
                return strURL.replace(arrSplit[0]+"?","");
            }else{
                return strURL;
            }
        }else{
            return strURL;
        }
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL  url地址
     * @return  url请求参数部分
     * @author lzf
     */
    public static Map<String, String> urlSplit(String URL){
        Map<String, String> mapRequest = new HashMap<String, String>();
        String[] arrSplit=null;
        String strUrlParam=truncateUrlPage(URL);
        if(strUrlParam==null){
            return mapRequest;
        }
        arrSplit=strUrlParam.split("[&]");
        for(String strSplit:arrSplit){
            String[] arrSplitEqual=null;
            arrSplitEqual= strSplit.split("[=]");
            //解析出键值
            if(arrSplitEqual.length>1){
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }else{
                if(arrSplitEqual[0]!=""){
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }
}
