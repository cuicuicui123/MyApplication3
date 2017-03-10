package com.cui.myapplication;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataTransform {
	/**
	 * 只转换日期
	 * @param date
	 * @return
     */
	public static String transform(String date){
		String[] DateAndTime = date.split(" ");
		String[] moreDate;
		if(date.contains("/")){
			moreDate = DateAndTime[0].split("/");
		}else{
			moreDate = DateAndTime[0].split("-");
		}
		String dateAfter = "";
		for (int i = 0;i < moreDate.length;i ++) {
			int before = Integer.parseInt(moreDate[i]);
			if (before < 10) {
				moreDate[i] = "0" + before;
			}
			if (i == moreDate.length - 1) {
				dateAfter = dateAfter + moreDate[i];
			} else {
				dateAfter = dateAfter + moreDate[i] + "-";
			}
		}
		return dateAfter;
	}

	/**
	 * 转换时间和日期小于10加0
	 * @param dateTime
	 * @return
     */
	public static String transformDateAndTime(String dateTime) {
		String[] dateTimes = dateTime.split(" ");
		String date = transform(dateTimes[0]);
		String time = transformTime(dateTimes[1]);
		return date + " " + time;
	}

	/**
	 * @param time
	 * @return 小于10加0
     */
	public static String transformTime(String time) {
		String[] times = time.split(":");
		String timeAfterTransform = "";
		int hour = Integer.parseInt(times[0]);
		int minute = Integer.parseInt(times[1]);
		if (hour < 10) {
			timeAfterTransform = "0" + hour;
		} else {
			timeAfterTransform = hour + "";
		}
		if (minute < 10) {
			timeAfterTransform = timeAfterTransform + ":0" + minute;
		} else {
			timeAfterTransform = timeAfterTransform + ":" + minute;
		}
		return timeAfterTransform;
	}

	/**
	 * 判断某个日期字符串是否介于开始时间和结束时间之间
	 * @param str
	 * @param begin
	 * @param end
     * @return
     */
	public static boolean isDateStrBetween(String str, String begin, String end) {
		if (str.compareTo(begin) >= 0 && str.compareTo(end) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 返回yyyy年mm月dd日 hh:mm 格式字符串，去除秒
	 * @param date
	 * @return
     */
	public static String getDateTimeStr(String date) {
		String split;
		if (date.contains("-")){
			split = "-";
		} else{
			split = "/";
		}
		String[] dateAndTimes = date.split(" ");
		String[] dates = dateAndTimes[0].split(split);
		String[] times = dateAndTimes[1].split(":");
		return dates[0] + "年" + dates[1] + "月" + dates[2] + "日 " + times[0] + ":" + times[1];
	}

	/**
	 * 返回mm月dd日 hh:mm格式字符串
	 * @param date
	 * @return
     */
	public static String getDateTimeStrNoYear(String date){
		String split;
		if (date.contains("-")){
			split = "-";
		} else{
			split = "/";
		}
		String[] dateAndTimes = date.split(" ");
		String[] dates = dateAndTimes[0].split(split);
		String[] times = dateAndTimes[1].split(":");
		return dates[1] + "月" + dates[2] + "日 " + times[0] + ":" + times[1];
	}

	/**
	 * 返回mm月dd日
	 * @param date
	 * @return
     */
	public static String getDateStrNoYear(String date){
		String split = date.contains("-") ? "-" : "/";
		String[] dateAndTimes = date.split(" ");
		String[] dates = dateAndTimes[0].split(split);
		return dates[1] + "月" + dates[2] + "日";
	}

	/**
	 * 消息获取要显示的日期
	 * @param dateAndTime
	 * @return
     */
	public static String compare(String dateAndTime){
		Date now = new Date();
		Date date = null;
		try {
			date = MyDateFormat.getDateTimeFormatNoSecond().parse(dateAndTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (isTheSameDate(date, now)) {
			String time = dateAndTime.split(" ")[1];
			String[] times = time.split(":");
			int timeInt = Integer.parseInt(times[0]);
			if (timeInt < 12) {
				return "上午" + timeInt + ":" + times[1];
			} else {
				if (timeInt > 12) {
					timeInt = timeInt - 12;
				}
				return "下午" + timeInt + ":" + times[1];
			}
		} else {
			Calendar calendar = Calendar.getInstance();
			int yearNow = calendar.get(Calendar.YEAR);
			calendar.setTime(date);
			int yearDate = calendar.get(Calendar.YEAR);
			String monthAndDay = dateAndTime.substring(dateAndTime.indexOf("-") + 1, dateAndTime.indexOf(" "));
			if (yearDate == yearNow) {
				return monthAndDay;
			} else {
				String year = (yearDate + "").substring(2);
				return year + "-" + monthAndDay;
			}
		}
	}
	//判断是否同天
	private static boolean isTheSameDate(Date date, Date now) {
		String dateStr = MyDateFormat.getDateFormat().format(date);
		String dateNow = MyDateFormat.getDateFormat().format(now);
		if (dateStr.equals(dateNow)){
			return true;
		} else {
			return false;
		}
	}
	//转发外部邮件添加contentEditable属性
	public static String outEmailTransformHtmlToEditable(String html){
		Pattern pattern = Pattern.compile("<body ");
		Matcher matcher = pattern.matcher(html);
		String newHtml;
		if (matcher.find()) {
			newHtml = html.replace("<body ", "<body contenteditable=\"true\"");
		} else {
			newHtml = innerEmailTransformHtmlToEditable(html);
		}
		return newHtml;
	}

	public static String outEmailRemoveContentEditable(String html){
		Pattern pattern = Pattern.compile("<body contenteditable=\"true\"");
		Matcher matcher = pattern.matcher(html);
		String newHtml;
		if (matcher.find()) {
			newHtml = html.replace("<body contenteditable=\"true\"", "<body");
		} else {
			newHtml = html;
		}
		return newHtml;
	}

	//转发内部电函时添加contentEditable属性
	public static String innerEmailTransformHtmlToEditable(String html){
		return "<div contenteditable=\"true\">" + html + "</div>";
	}



	/**
	 * @param text
	 * @return 去除秒之后的时间
     */
	public static String transformDateTimeNoSecond(String text){
		if (text.contains(":")) {
			return text.substring(0, text.lastIndexOf(":"));
		} else {
			return "";
		}
	}

}
