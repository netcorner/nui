package com.ingwill.utils;

import android.util.Log;

import com.ingwill.lib.Lunar;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class DateUtils {
	public static LocalDate date2LocalDate(Date date) {
		if(null == date) {
			return null;
		}
		return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
	/**
	 * 计算两个日期之间相差的天数
	 * @param smdate 较小的时间
	 * @param bdate  较大的时间
	 * @return 相差天数
	 * @throws ParseException
	 */
	public static int daysBetween(Date smdate, Date bdate)
	{
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		try {
			smdate=sdf.parse(sdf.format(smdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		try {
			bdate=sdf.parse(sdf.format(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(smdate);
		long time1 = cal.getTimeInMillis();
		cal.setTime(bdate);
		long time2 = cal.getTimeInMillis();
		long between_days=(time2-time1)/(1000*3600*24);

		return Integer.parseInt(String.valueOf(between_days));
	}
	/**
	 *  获取两个日期相差的月数
	 * @param date1    较大的日期
	 * @param date2    较小的日期
	 * @return  如果d1>d2返回 月数差 否则返回0
	 */
	public static int getMonthDiff(Date date1, Date date2) {
		int result = 0;

		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();

		c1.setTime(date1);
		c2.setTime(date2);

		result = (c2.get(Calendar.YEAR)-c1.get(Calendar.YEAR))*12+c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		return Math.abs(result);
	}
	/**
	 *字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(sdf.parse(smdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time1 = cal.getTimeInMillis();
		try {
			cal.setTime(sdf.parse(bdate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long time2 = cal.getTimeInMillis();
		long between_days=(time2-time1)/(1000*3600*24);

		return Integer.parseInt(String.valueOf(between_days));
	}
	/**
	 * 字符转日期
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date string2Date(String dateStr, String pattern){
		DateFormat dateFormat=new SimpleDateFormat(pattern);
		Date date=null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}
	/**
	 * 字符转日期
	 * @param dateStr
	 * @return
	 */
	public static Date string2Date(String dateStr){
		return string2Date(dateStr,"yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * 字符转日期并格式化
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static String string2DateByFormat(String dateStr, String pattern){
		DateFormat dateFormat=new SimpleDateFormat(pattern);
		Date date=null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateFormat.format(date);
	}
	/**
	 * 字符转日期并格式化
	 * @param dateStr
	 * @return
	 */
	public static String string2DateByFormat(String dateStr){
		return string2DateByFormat(dateStr,"yyyy-MM-dd HH:mm:ss");
	}

    /**
     * 两个日期相减，得到两个之前相差的秒数
     * @param d1
     * @param d2
     * @return
     */
    public static long differDate(Date d1, Date d2){
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 格式化时间
        String ntime = d.format(d1);// 按以上格式 将当前时间转换成字符串
        String btime = d.format(d2);

        try {
            long result = (d.parse(ntime).getTime() - d.parse(btime)
                    .getTime()) / 1000;// 当前时间减去测试时间
            // 这个的除以1000得到秒，相应的60000得到分，3600000得到小时
            return result;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }
	/**
	 * date转Calendar
	 * @param date
	 * @return
	 */
	public static Calendar date2Calendar(Date date){
		Calendar calendar= Calendar.getInstance();
		calendar.setTime(date); 
		return calendar;
	}
	/**
	 * Calendar转date
	 * @param calendar
	 * @return
	 */
	public static Date calendar2Date(Calendar calendar){
		Calendar ca= Calendar.getInstance();
		Date d =(Date) ca.getTime();
		return d;
	}
	/**
	 * 字符转Calendar
	 * @param str
	 * @return
	 */
	public static Calendar string2Calendar(String str){
		return date2Calendar(string2Date(str));
	}
	
	/**
	 * 
	 * @category 获取格式化后的当前日期
	 * @return
	 * @CreateDate 2010-8-30 下午03:48:53
	 */
	public static String getNowDate(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(System.currentTimeMillis());
	}
	/**
	 * 得到当前时间
	 * @return
	 */
	public static String getNowDate() {
		return getNowDate("yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 时间加（周）
	 * @param d
	 * @param week
	 * @return
	 */
	 public static Date addWeek(Date d, long week){
		  long time = d.getTime(); 
		  time+= week*7*24*60*60*1000; 
		  return new Date(time);
	 }
	/**
	 * 时间加（天）
	 * @param d
	 * @param day
	 * @return
	 */
	 public static Date addDay(Date d, long day){
		  long time = d.getTime(); 
		  time+= day*24*60*60*1000; 
		  return new Date(time);
	 }
	/**
	 * 时间加（小时）
	 * @param d
	 * @param hour
	 * @return
	 */
	 public static Date addHour(Date d, long hour){
		  long time = d.getTime(); 
		  time+= hour*60*60*1000; 
		  return new Date(time);
	 }
	 /**
	  * 时间加（分钟）
	  * @param d
	  * @param minute
	  * @return
	  */
	 public static Date addMinute(Date d, long minute){
		  long time = d.getTime(); 
		  time+= minute*60*1000; 
		  return new Date(time);
	 }
	 /**
	  * 时间加（秒）
	  * @param d
	  * @param second
	  * @return
	  */
	 public static Date addSecond(Date d, long second){
		  long time = d.getTime(); 
		  time+= second*1000; 
		  return new Date(time);
	 }
	 /**
	  * 得到农历 
	  * @param cal
	  * @return
	  */
	 public static Lunar getLunar(Calendar cal) {
	        return Lunar.getLunar(cal);
	 }
	 /**
	  * 得到农历 
	  * @param date
	  * @return
	  */
	 public static Lunar getLunar(Date date) {
	        return Lunar.getLunar(date2Calendar(date));
	 }

	/**
	 * 得到星期
	 * @param date
	 * @return
	 */
    public static String getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w= cal.get(Calendar.DAY_OF_WEEK);
        switch (w){
            case 1:
                return "星期天";
            case 2:
                return "星期一";
            case 3:
                return "星期二";
            case 4:
                return "星期三";
            case 5:
                return "星期四";
            case 6:
                return "星期五";
            case 7:
                return "星期六";
        }
        return "";
    }

	/**
	 * 得到格式化日期
	 * @param date
	 * @param format
	 * @return
	 */
    public static String getDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

	/**
	 * 得到常用到的口头日期
	 * @param date
	 * @param format
	 * @return
	 */
    public static String getUsuallyDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        long d= Long.parseLong(sdf.format(date));
        Date cd=new Date();
        long currentdate= Long.parseLong(sdf.format(cd));
        if(currentdate==d){
            return "今天";
        }else if(currentdate==(d+1)){
            return "昨天";
        }else if(currentdate==(d+2)) {
            return "前天";
        }else if(currentdate==(d-1)){
            return "明天";
        }else if(currentdate==(d-2)){
            return "后天";
        }
        return getDate(date,format);
    }

	/**
	 * 得到常用到的口头日期
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getUsuallyDate(long date, String format) {
		return getUsuallyDate(new Date(date),format);
	}

	/**
	 * 得到北京时间
	 * @param date
	 * @return
	 */
	public static Date getBeijinTime(Date date) {
		if(getTimeZone().equals("GMT+08:00")){
			return date;
		}else {
			return getFormatedDateString(date, 8);
		}
	}


	public static Date getFormatedDateString(Date date,float timeZoneOffset){
		if (timeZoneOffset > 13 || timeZoneOffset < -12) {
			timeZoneOffset = 0;
		}
		int newTime=(int)(timeZoneOffset * 60 * 60 * 1000);
		TimeZone timeZone;
		String[] ids = TimeZone.getAvailableIDs(newTime);
		if (ids.length == 0) {
			timeZone = TimeZone.getDefault();
		} else {
			timeZone = new SimpleTimeZone(newTime, ids[0]);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(timeZone);
		return DateUtils.string2Date(sdf.format(date));

	}

	/**
	 * 获取当前时区
	 * @return
	 */
	public static String getTimeZone() {
		TimeZone tz = TimeZone.getDefault();
		String strTz = tz.getDisplayName(false, TimeZone.SHORT);
		return strTz;
	}




	public static String second2HMS(Double date) {
		long currentdate= date.intValue();
		String d=(date+"").replace(currentdate+"","").substring(1);
		if(d.length()==2) d+="0";
		else if(d.length()==1) d+="00";
		else if(d.length()==0) d+="000";
		String s= second2HMS1(currentdate).replace(":","'")+"''"+d;
		if(s.substring(0,1).equals("0")){
			return s.substring(1);
		}else{
			return s;
		}
	}

	/**
	 * 得到常用到的口头时间
	 * @param date
	 * @return
	 */
	public static String getUsuallyTime(Date date) {
		long d= date.getTime();

		//Date cd=new Date();
		Date cd= DateUtils.getBeijinTime(new Date());

		long currentdate= (cd.getTime()-d)/60000;
		if(currentdate==0){
			return "刚刚";
		}else if(currentdate<60){
			return currentdate+"分钟前";
		}else if(currentdate<1440) {
			return (currentdate/60)+"小时前";
		}else{
			long day=currentdate/1440;
			if(day>3){
				return formatDate(date,"yyyy.MM.dd");
//				int month=getMonthDiff(cd,date);
//				if(month>0){
//					int y=month/12;
//					if(y>0){
//						return month + "年前";
//					}else {
//						return month + "个月前";
//					}
//				}else{
//					return day+"天前";
//				}


			}else{
				return day+"天前";
			}

		}
	}

	public static String formatDate(Date date,String formatStr){
		SimpleDateFormat format = new SimpleDateFormat(formatStr);// 格式化时间
		return format.format(date);
	}

    /**
     * @param sourceTime 待转化的时间
     * @param dataFormat 日期的组织形式
     * @return long 当前时间的长整型格式,如 1247771400000
     */
    private static long string2long(String sourceTime, String dataFormat){
        long longTime = 0L;
        DateFormat f = new SimpleDateFormat(dataFormat);
        Date d = null;
        try {
            d = f.parse(sourceTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        longTime = d.getTime();
        return longTime;
    }

	/**
	 * 长整型转换为日期类型
	 * @param longTime
	 * @param dataFormat
	 * @return
     */
    public static String long2String(long longTime, String dataFormat)
    {
        Date d = new Date(longTime);
        SimpleDateFormat s = new SimpleDateFormat(dataFormat);
        String str = s.format(d);
        return str;

    }

	/**
	 * 秒转成小时：分钟：秒格式
	 * @param second
	 * @return
	 */
	public static String second2HMS(long second){
		String HMStime;
		long hour=second/3600;
		long mint=(second%3600)/60;
		long sed=second%60;
		String hourStr=String.valueOf(hour);
		String mintStr=String.valueOf(mint);
		if(mint<10){
			mintStr="0"+mintStr;
		}
		String sedStr=String.valueOf(sed);
		if(sed<10){
			sedStr="0"+sedStr;
		}
		HMStime=hourStr+":"+mintStr+":"+sedStr;
		return HMStime;
	}

	/**
	 * 秒转成小时：分钟：秒格式
	 * @param second
	 * @return
	 */
	public static String second2HMS1(long second){
		String HMStime;
		long hour=second/3600;
		long mint=(second%3600)/60;
		long sed=second%60;
		String hourStr=String.valueOf(hour);
		String mintStr=String.valueOf(mint);
		if(mint<10){
			mintStr="0"+mintStr;
		}
		String sedStr=String.valueOf(sed);
		if(sed<10){
			sedStr="0"+sedStr;
		}
		if(hour==0){
			if(mint==0) {
				HMStime=sedStr;
			}else{
				HMStime = mintStr + ":" + sedStr;
			}
		}else {
			HMStime = hourStr + ":" + mintStr + ":" + sedStr;
		}
		return HMStime;
	}
	public static String second2HMS2(long second){
		String HMStime;
		long hour=second/3600;
		long mint=(second%3600)/60;
		long sed=second%60;
		String hourStr=String.valueOf(hour);
		String mintStr=String.valueOf(mint);
		if(mint<10&&hour>0){
			mintStr="0"+mintStr;
		}
		String sedStr=String.valueOf(sed);
		if(sed<10){
			sedStr="0"+sedStr;
		}
		if(hour==0){
			HMStime = mintStr + ":" + sedStr;
		}else {
			HMStime = hourStr + ":" + mintStr + ":" + sedStr;
		}
		return HMStime;
	}
	public static String second2CHN(long second){
		String HMStime="";
		long hour=second/3600;
		long mint=(second%3600)/60;
		long sed=second%60;

		if(hour>0){
			HMStime+=hour+"小时";
		}
		if(mint>0){
			HMStime+=mint+"分";
		}
		if(sed>0){
			HMStime+=sed+"秒";
		}else{
			if(mint>0){
				HMStime+="钟";
			}
		}
		return HMStime;
	}
}
