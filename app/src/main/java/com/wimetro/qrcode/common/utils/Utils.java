package com.wimetro.qrcode.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.otech.yoda.utils.TextUtil;
import com.otech.yoda.utils.Util;
import com.wimetro.qrcode.service.TokenService;
import com.wimetro.qrcode.ui.activity.LoginActivity;

/**
 * 通版工具包
 * 
 * @author fwp
 * 
 */
public class Utils {
	

   
   


	public static String Encrypt(String strSrc) {
		MessageDigest md = null;
		String strDes = null;
		try {
			byte[] bt = strSrc.getBytes("UTF-8");
			md = MessageDigest.getInstance("SHA-1");
			md.update(bt);
			strDes = bytes2Hex(md.digest()); // to HexString
		} catch (UnsupportedEncodingException e) {
			System.out.println("UnsupportedEncodingException.");
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Invalid algorithm.");
			return null;
		}
		return strDes;
	}

	public static String bytes2Hex(byte[] bts) {
		String des = "";
		String tmp = null;
		for (int i = 0; i < bts.length; i++) {
			tmp = (Integer.toHexString(bts[i] & 0xFF));
			if (tmp.length() == 1) {
				des += "0";
			}
			des += tmp;
		}
		return des;
	}
	
	public static String GetMD5Code(String strObj) {
        String resultString = null;
        try {
            resultString = new String(strObj);
            MessageDigest md = MessageDigest.getInstance("MD5");
            // md.digest() 该函数返回值为存放哈希值结果的byte数组
            resultString = byteToString(md.digest(strObj.getBytes()));
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return resultString;
    }
	
	// 转换字节数组为16进制字串
    private static String byteToString(byte[] bByte) {
        StringBuffer sBuffer = new StringBuffer();
        for (int i = 0; i < bByte.length; i++) {
            sBuffer.append(byteToArrayString(bByte[i]));
        }
        return sBuffer.toString();
    }
	 
    // 返回形式为数字跟字符串
    private static String byteToArrayString(byte bByte) {
        int iRet = bByte;
        // System.out.println("iRet="+iRet);
        if (iRet < 0) {
            iRet += 256;
        }
        int iD1 = iRet / 16;
        int iD2 = iRet % 16;
        return strDigits[iD1] + strDigits[iD2];
    }

    // 全局数组
    private final static String[] strDigits = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	
	
	
	/**
	 * 下划线文字
	 * 
	 * @param tv
	 */
	public static void underlineText(TextView tv) {
		if (tv != null) {
			tv.setPaintFlags(tv.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		}
	}

	/**
	 * 启动地图
	 * 
	 * @param context
	 * @param latitude
	 *            维度
	 * @param longitude
	 *            经度
	 */
	public static void startMapActivity(Context context, Double latitude,
			Double longitude) {
		if (latitude != null && longitude != null && context != null
				&& latitude > 0 && longitude > 0) {
			Toast.makeText(context.getApplicationContext(),
					"打开默认地图:" + latitude + "," + longitude, Toast.LENGTH_SHORT)
					.show();
			Uri uri = Uri.parse("geo:" + latitude + "," + longitude);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(context.getApplicationContext(), "暂无详细地址经纬度坐标",
					Toast.LENGTH_SHORT).show();
		}
	}

	public static void startMapActivityByAddress(Context context,
			Address address) {
		if (address != null) {
			startMapActivity(context, address.getLatitude(),
					address.getLongitude());
		}
	}

	
	/**
	 * 传入地址，浏览器打开百度地图
	 * 
	 * @param context
	 * @param address
	 */
	public static void startMapByBrowser(Context context, String address) {
		try {
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri
					.parse("http://api.map.baidu.com/geocoder?address="
							+ address + "&output=html&src=otech|general");
			intent.setData(content_url);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 传入地址，浏览器打开
	 * 
	 * @param context
	 * @param address
	 */
	public static void startBrowser(Context context, String address) {
		if (address != null && !address.equals("")) {
			try {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				Uri content_url = Uri.parse(address);
				intent.setData(content_url);
				context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 拨打电话
	 * 
	 * @param context
	 * @param phoneNumber
	 *            电话号码
	 */
	public static void makePhoneCall(Context context, String phoneNumber) {
		if (!TextUtils.isEmpty(phoneNumber)) {
			phoneNumber = phoneNumber.replace("-", "").trim();
			// ACTION_CALL need <uses-permission
			// android:name="android.permission.CALL_PHONE" />
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ phoneNumber));
			try {
				context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 打开QQ
	 * 
	 * @param context
	 * @param qq
	 *            QQ号码（预留)
	 * 
	 */
	public static void launchMobileQQ(Context context, String qq) {
		if (context != null && !TextUtils.isEmpty(qq)) {
			// String packagename = "com.qq.android";
			String packagename = "com.tencent.mobileqq";
			launchApplication(context, packagename);
		}
	}

	/**
	 * 打开软件（如果有安装)
	 * 
	 * @param context
	 * @param packagename
	 *            要打开App的包名
	 */
	public static void launchApplication(Context context, String packagename) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(packagename, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String packageName = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				ComponentName cn = new ComponentName(packageName, className);
				intent.setComponent(cn);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
				context.startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送邮件
	 * 
	 * @param context
	 * @param email
	 *            "test@gmail.com"
	 */
	public static void sendEmail(Context context, String email) {
		Uri uri = Uri.parse("mailto:" + email);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取本机手机号
	 * 
	 * @param context
	 * @return
	 */
	public static String getPhoneNumber(Context context) {
		try {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String number = tm.getLine1Number();
			if (number != null) {
				number = number.replace("-", "");
				number = number.replace("+86", "");
				return number;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	

	/**
	 * 设置文字
	 * 
	 * @param tv
	 * @param text
	 */
	public static void setTextIfNotEmpty(TextView tv, String text) {
		if (!TextUtils.isEmpty(text)) {
			tv.setText(text);
		}
	}

	/**
	 * 设置文字 format: "%s: %s"
	 * 
	 * @param tv
	 * @param label
	 *            标题
	 * @param value
	 *            文字
	 */
	public static void setTextIfNotEmpty(TextView tv, String label, String value) {
		if (TextUtils.isEmpty(value)) {
			value = "未知";
		}
		tv.setText(String.format("%s: %s", label, value));
	}

	/**
	 * 设置文字
	 * 
	 * @param tv
	 * @param text
	 */
	public static void setTextIfNotEmptyAndTrim(TextView tv, String text) {
		if (!TextUtils.isEmpty(text)) {
			tv.setText(text.trim());
		}
	}

	public static String getUrl(String root, String url) {
		if (!TextUtils.isEmpty(url)) {
			return root + url;
		}
		return null;
	}

	public static String getUrlSmall(String root, String url) {
		if (url != null && !"".equals(url) && url.length() > 0
				&& url.contains(".")) {
			int index = url.lastIndexOf(".");
			if (index > 0) {
				return root + url.substring(0, index) + "_small"
						+ url.substring(index);
			}
		}
		return null;
	}

	/**
	 * 获得默认小图片
	 * 
	 * @param url
	 * @return
	 */
	public static String getSmallImagesUrl(String url) {
		if (url != null && !"".equals(url) && url.length() > 0
				&& url.contains(".")) {
			int index = url.lastIndexOf(".");
			if (index > 0) {
				return url.substring(0, index) + "_small"
						+ url.substring(index);
			}
		}
		return "";
	}

	public static String getPraseUrlSmall(String root, String url, int index) {

		if (!TextUtils.isEmpty(url)) {
			String[] mUrl = url.split(",");
			if (mUrl.length > index)
				return getUrlSmall(root, mUrl[index]);
		}
		return null;
	}

	public static String getPraseUrl(String root, String url, int index) {

		if (!TextUtils.isEmpty(url)) {
			String[] mUrl = url.split(",");
			if (mUrl.length > index)
				return root + mUrl[index];
		}
		return null;
	}

	public static String getPraseIdUrlSmall(String root, String url,
			int entityID, int index) {

		if (!TextUtils.isEmpty(url)) {
			String[] mUrl = url.split(",");
			if (mUrl.length > index)
				return getUrlSmall((root + entityID + "/"), mUrl[index]);
		}
		return null;
	}

	public static String getPraseIdUrl(String root, String url, int entityID,
			int index) {

		if (!TextUtils.isEmpty(url)) {
			String[] mUrl = url.split(",");
			if (mUrl.length > index)
				return ((root + entityID + "/") + mUrl[index]);
		}
		return null;
	}

	

	/**
	 * onFocus的时候自动把光标移动尾部
	 * 
	 * @param et
	 */
	public static void fixRightAutoFocus(EditText et) {
		et.setOnFocusChangeListener(getEditTextOnFocusChangeListener());
	}

	public static OnFocusChangeListener getEditTextOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			@Override
			public void onFocusChange(final View v, boolean hasFocus) {
				if (hasFocus) {
					v.postDelayed(new Runnable() {

						@Override
						public void run() {
							// 光标移到尾部
							try {
								Editable etext = ((EditText) v).getText();
								Selection.setSelection(etext, etext.length());
							} catch (IndexOutOfBoundsException e) {
								e.printStackTrace();
							}
						}
					}, 100);
				}
			}
		};
	}

	/**
	 * 将逗号分隔的字符串切成数组
	 * 
	 * @param text
	 * @return
	 */
	public static List<String> convertStringToStringList(String text) {
		if (!TextUtils.isEmpty(text)) {
			String[] tmp = text.split(",");
			if (tmp != null && tmp.length > 0) {
				List<String> list = new ArrayList<String>();
				for (String name : tmp) {
					list.add(name);
				}
				return list;
			}
		}
		return Collections.emptyList();
	}

	private static final String T_AGO_FULL_DATE_FORMATTER = "yyyy-MM-dd HH:mm:ss";
	private static ThreadLocal<DateFormat> TL_AGO_FULL_DATE_FORMATTER = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_FULL_DATE_FORMATTER() {
		return getDataFormatThreadSafe(TL_AGO_FULL_DATE_FORMATTER,
				T_AGO_FULL_DATE_FORMATTER);
	}

	private static final String T_DATE_FORMATTER_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	private static ThreadLocal<DateFormat> TL_DATE_FORMATTER_YYYY_MM_DD_HH_MM = new ThreadLocal<DateFormat>();

	public static DateFormat DATE_FORMATTER_YYYY_MM_DD_HH_MM() {
		return getDataFormatThreadSafe(TL_DATE_FORMATTER_YYYY_MM_DD_HH_MM,
				T_DATE_FORMATTER_YYYY_MM_DD_HH_MM);
	}

	private static final String T_AGO_DATE_FORMATTER = "yyyy年MM月dd日";
	private static ThreadLocal<DateFormat> TL_AGO_DATE_FORMATTER = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_DATE_FORMATTER() {
		return getDataFormatThreadSafe(TL_AGO_DATE_FORMATTER,
				T_AGO_DATE_FORMATTER);
	}

	private static final String T_AGO_FULL_DATE_FORMATTER2 = "yyyy/MM/dd HH:mm:ss";
	private static ThreadLocal<DateFormat> TL_AGO_FULL_DATE_FORMATTER2 = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_FULL_DATE_FORMATTER2() {
		return getDataFormatThreadSafe(TL_AGO_FULL_DATE_FORMATTER2,
				T_AGO_FULL_DATE_FORMATTER2);
	}

	private static final String T_AGO_FULL_DATE_FORMATTER3 = "yyyy-MM-dd";
	private static ThreadLocal<DateFormat> TL_AGO_FULL_DATE_FORMATTER3 = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_FULL_DATE_FORMATTER3() {
		return getDataFormatThreadSafe(TL_AGO_FULL_DATE_FORMATTER3,
				T_AGO_FULL_DATE_FORMATTER3);
	}

	private static final String T_AGO_FULL_DATE_FORMATTER4 = "yyyyMMdd";
	private static ThreadLocal<DateFormat> TL_AGO_FULL_DATE_FORMATTER4 = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_FULL_DATE_FORMATTER4() {
		return getDataFormatThreadSafe(TL_AGO_FULL_DATE_FORMATTER4,
				T_AGO_FULL_DATE_FORMATTER4);
	}
	
	private static final String T_AGO_FULL_DATE_FORMATTER5 = "yyyy-MM";
	private static ThreadLocal<DateFormat> TL_AGO_FULL_DATE_FORMATTER5 = new ThreadLocal<DateFormat>();

	public static DateFormat AGO_FULL_DATE_FORMATTER5() {
		return getDataFormatThreadSafe(TL_AGO_FULL_DATE_FORMATTER5,
				T_AGO_FULL_DATE_FORMATTER5);
	}
	
	private static DateFormat getDataFormatThreadSafe(
			ThreadLocal<DateFormat> threadLocal, String format) {
		if (threadLocal == null) {
			threadLocal = new ThreadLocal<DateFormat>();
		}
		DateFormat df = threadLocal.get();
		if (df == null) {
			df = new SimpleDateFormat(format, Locale.US);
			threadLocal.set(df);
		}
		return df;
	}

	/**
	 * 获取相对时间(如: 几分钟前)
	 * 
	 * @param now
	 *            当前时间
	 * @param date
	 * @return
	 */
	public static String getRelativeDate(Date now, String date) {
		if (date != null) {
			try {
				Date d = AGO_FULL_DATE_FORMATTER2().parse(date);
				return getRelativeDate(AGO_FULL_DATE_FORMATTER2(), now, d);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 获取相对时间
	 * 
	 * @param
	 *            //基准时间（相对于这个时间而言），一般是现在时间(new Date())
	 * @param date
	 *            绝对时间
	 * @return
	 */
	public static String getRelativeDate(DateFormat formator, Date now,
			Date date) {
		String prefix = "";
		String sec = "秒";
		String min = "分钟";
		String hour = "小时";
		String day = "天";
		String week = "周";
		String month = "月";
		String suffix = "前";

		// Seconds.
		long diff = (now.getTime() - date.getTime()) / 1000;
		if (diff < 0) {
			diff = 0;
		}
		if (diff < 60) {
			return diff + sec + suffix;
		}

		// Minutes.
		diff /= 60;
		if (diff < 60) {
			return prefix + diff + min + suffix;
		}

		// Hours.
		diff /= 60;
		if (diff < 24) {
			return prefix + diff + hour + suffix;
		}

		// Days
		diff /= 24;
		if (diff < 7) {
			return prefix + diff + day + suffix;
		}

		// Week
		diff /= 7;
		if (diff < 4) {
			return prefix + diff + week + suffix;
		}

		// Month
		diff /= 4;
		if (diff < 12) {
			return prefix + diff + month + suffix;
		}

		return formator.format(date);
	}

	

	/**
	 * 格式化服务器传回的时间戳字符串
	 * 
	 * @param timestamp
	 *            eg: /Date(1417511327000+0800)/
	 * @return
	 */
	public static String formatServerDate(String timestamp) {
		try {
			String strs[] = timestamp.replace("/Date(", "").split("\\+");
			long l = Long.parseLong(strs[0]);
			return AGO_FULL_DATE_FORMATTER().format(new Date(l));
		} catch (Exception e) {
			e.printStackTrace();
			return timestamp;
		}
	}

	/**
	 * 计算长宽比
	 * 
	 * @param width
	 * @param height
	 * @return
	 */
	public static double calHeightRatio(int width, int height) {
		if (width > 0 && height > 0) {
			return height / (float) width;
		}
		return 1; // 默认矩形
	}

	/**
	 * 格式化日期
	 * 
	 * @param //addTime
	 * @return
	 */
	public static String formatDate(String date) {
		if (date != null) {
			try {
				Date d = AGO_FULL_DATE_FORMATTER2().parse(date);
				return AGO_FULL_DATE_FORMATTER().format(d);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 格式化日期
	 * 
	 * @param //addTime
	 * @return
	 */
	public static String formatDate2(String date) {
		if (date != null) {
			try {
				Date d = AGO_FULL_DATE_FORMATTER2().parse(date);
				return AGO_DATE_FORMATTER().format(d);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 格式化日期
	 * 
	 * @param //addTime
	 * @return
	 */
	public static String formatDate3(String date) {
		if (date != null) {
			try {
				Date d = AGO_FULL_DATE_FORMATTER2().parse(date);
				return AGO_FULL_DATE_FORMATTER3().format(d);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	public static String getCurrent() {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	
	public static String getCurrentTestTime() {
		SimpleDateFormat formatter = new SimpleDateFormat (T_AGO_FULL_DATE_FORMATTER);
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String str = formatter.format(curDate);
		return str;
	}
	
	public static String getCurrentMonth() {
		SimpleDateFormat formatter = new SimpleDateFormat (T_AGO_FULL_DATE_FORMATTER5);
		Date curDate = new Date(System.currentTimeMillis());
		String str = formatter.format(curDate);
		return str;
	} 
	/**
	 * 活动屏幕高度
	 * 
	 * @param context
	 * @return
	 */
	public static int[] getWindowSize(Context context) {
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		return new int[] { dm.widthPixels, dm.heightPixels };
	}

	

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/***
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFile
	 * @throws Exception
	 */
	public static void copyFile(String sourceFile, String targetFile)
			throws Exception {
		OutputStream out = null;
		InputStream in = null;
		try {
			in = new FileInputStream(sourceFile); // 读入原文件
			out = new FileOutputStream(targetFile);
			byte[] buffer = new byte[1024];
			while (true) {
				int ins = in.read(buffer);
				if (ins == -1) {
					break;
				}
				out.write(buffer, 0, ins);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	/**
	 * change string to the red color View for Html.fromHtml
	 * 
	 * @param str
	 * @return
	 */
	public static String changeGreen(String str) {
		return "<font color='#3076c1'>" + str + "</font>";
	}

	/**
	 * 把鼠标焦点发到最后一个字符
	 * 
	 * @param editText
	 */
	public static void editTextCursor(TextView editText) {
		CharSequence text = editText.getText();
		if (text instanceof Spannable) {
			Spannable spanText = (Spannable) text;
			Selection.setSelection(spanText, text.length());
		}
	}

	
	/**
	 * TextView 上数字加减法
	 * 
	 * @param tw
	 * @param num
	 */
	public static void textviewChangeNum(View tw, int num) {
		try {
			if (tw instanceof TextView) {
				String old = ((TextView) tw).getText().toString().trim();
				long oldValue = Long.parseLong(old);
				long newValue = oldValue + num;
				if (newValue < 0)
					newValue = 0;
				((TextView) tw).setText(String.valueOf(newValue));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 格式化标题，显示6个字，超过6个显示5个字节...
	 * 
	 * @param str
	 * @return
	 */
	public static String titleFormat(String str) {
		return TextUtil.truncate2(str, 6, "...");
	}

	
	/**
	 * 修改View top的图标
	 * 
	 * @param context
	 * @param resId
	 * @param tv
	 */
	public static void setViewDrawTop(Context context, int resId, TextView tv) {
		Drawable img = context.getResources().getDrawable(resId);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		tv.setCompoundDrawables(null, img, null, null); // 设置左图标
	}

	/**
	 * 修改View left的图标
	 * 
	 * @param context
	 * @param resId
	 * @param tv
	 */
	public static void setViewDrawLeft(Context context, int resId, TextView tv) {
		Drawable img = context.getResources().getDrawable(resId);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
		tv.setCompoundDrawables(img, null, null, null); // 设置左图标
	}

	/**
	 * 修改View right的图标
	 * 
	 * @param context
	 * @param resId
	 * @param tv
	 */
	public static void setViewDrawRight(Context context, int resId, TextView tv) {
		if (resId != -1) {
			Drawable img = context.getResources().getDrawable(resId);
			// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
			img.setBounds(0, 0, img.getMinimumWidth(), img.getMinimumHeight());
			tv.setCompoundDrawables(null, null, img, null); // 设置左图标
		} else {
			tv.setCompoundDrawables(null, null, null, null);
		}
	}

	/**
	 * 评论回复
	 * 
	 * @param name
	 * @return
	 */

	public static String toReplyText(String name) {
		return "回复" + name + ":";
	}

	public static String linkText(String link) {
		return "<font color='#0fc7d3'>" + "<u>" + link + "</u>" + "</font>";
	}

	/**
	 * 首页显示 注册人数，在线人数。
	 * 
	 * @param context
	 * @param onlineCount
	 * @param totalCount
	 * @return
	 */
	public static String topUserCount(Context context, int onlineCount,
			int totalCount) {
		return "<font color='#6a6a6a'>" + "当前注册人数 " + "</font>"
				+ "<font color='#0fc7d3'>" + totalCount + "</font>"
				+ "<font color='#6a6a6a'>" + " 人，在线人数 " + "</font>"
				+ "<font color='#0fc7d3'>" + onlineCount + "</font>"
				+ "<font color='#6a6a6a'>" + " 人" + "</font>";
	}



	/**
	 * 获取图片名称
	 * 
	 * @param //context
	 */
	public static String getimagename(String url) {
		if (url != null) {
			String temp[] = null;
			temp = url.split("/");
			return temp[temp.length - 1];
		}
		return null;
	}

	
	/**
	 * 生产随机数
	 * 
	 * @return
	 */
	public static String getRandom() {
		String str = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA)
		.format(new Date());
		str = str + Math.round(Math.random() * 100000000);
		return str;
	}

	/**
	 * 判断3G网络状态
	 * 
	 * @param context
	 */
	public static boolean is3GActive(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);
		if (connectivityManager.getActiveNetworkInfo() == null
				|| !connectivityManager.getActiveNetworkInfo().isAvailable()) {
			// 注意一：
			// NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
			// 但是有些电信机器，仍可以正常联网，
			// 所以当成net网络处理依然尝试连接网络。
			// （然后在socket中捕捉异常，进行二次判断与用户提示）。
			return false;
		} else {
			int type = connectivityManager.getActiveNetworkInfo().getType();
			if (type == ConnectivityManager.TYPE_MOBILE) {
				return true;
			}
		}
		return false;
	}
 

//	/**
//	 * 判断网络状态
//	 * 
//	 * @param context
//	 */
//	public static boolean isNetworkAvaiable(Context context) {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context
//				.getSystemService(context.CONNECTIVITY_SERVICE);
//		NetworkInfo info = connectivityManager.getActiveNetworkInfo(); // 获取代表联网状态的NetWorkInfo对象
//		return (info != null && info.isAvailable() && info.isConnected());
//	}
 
    /**
     * 验证网址Url
     * 
     * @param //待验证的字符串
     * @return 如果是符合格式的字符串,返回 <b>true </b>,否则为 <b>false </b>
     */
     public static boolean IsUrl(String str) {
         String regex = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";
         return match(regex, str);
     }
     
     /**
      * @param regex
      * 正则表达式字符串
      * @param str
      * 要匹配的字符串
      * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
      */
      private static boolean match(String regex, String str) {
          Pattern pattern = Pattern.compile(regex);
          Matcher matcher = pattern.matcher(str);
          return matcher.matches();
      }
    /**
     * 判断网络状态
     * 
     * @param context
     */
    public static boolean isNetworkAvaiable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo(); // 获取代表联网状态的NetWorkInfo对象
        return (info != null && info.isAvailable() && info.isConnected());
    }
 

	/**
	 * 判断wifi网络状态
	 * 
	 * @param context
	 */
	public static boolean isWifiActive(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(context.CONNECTIVITY_SERVICE);

		if (connectivityManager.getActiveNetworkInfo() == null
				|| !connectivityManager.getActiveNetworkInfo().isAvailable()) {
			// 注意一：
			// NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
			// 但是有些电信机器，仍可以正常联网，
			// 所以当成net网络处理依然尝试连接网络。
			// （然后在socket中捕捉异常，进行二次判断与用户提示）。
			return false;
		} else {
			int type = connectivityManager.getActiveNetworkInfo().getType();
			if (type == ConnectivityManager.TYPE_WIFI) {
				return true;
			}

		}
		return false;
	}



	/**
	 * 检查当前网络是否可用
	 *
	 * @param
	 * @return
	 */

	public static boolean isNetworkAvailable(Context context)
	{
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null)
		{
			return false;
		}
		else
		{
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0)
			{
				for (int i = 0; i < networkInfo.length; i++)
				{
					System.out.println(i + "===状态===" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	 

	public static String ConvertTimeTo_yyyy_MM_dd_HH_mm_ssByLongTime(String timeString){
		String result="";
		try {
			if (timeString.startsWith("/Date(")) {
				timeString=timeString.substring(6,timeString.length()-7);
			}       
			long datelong=Long.parseLong(timeString);
			final Date date=new Date(datelong);
			final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			result=format.format(date);	
		} catch (Exception e) {
			//
		}		
		return result;
	}
	
	
	public static boolean isIdentity(String number) {
		Pattern p = Pattern.compile("^\\d{15}|\\d{18}$");
		CharSequence inputStr = number;

		Matcher matcher = p.matcher(inputStr);
		//Matcher matcher18 = p18.matcher(inputStr);

		if (matcher.matches()) {
			return true;
		}
		
		return false;
	}

	/**
	 * 判断邮箱是否合法
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){  
		if (null==email || "".equals(email)) return false;	
		    Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配  
		//Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配  
		Matcher m = p.matcher(email);  
		return m.matches();  
	}

	public static boolean isMobileNO(String mobiles){  

		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  

		Matcher m = p.matcher(mobiles);  

		return m.matches();  

	}
	
    
	/** 
     * 判断应用是否已安装 
     *  
     * @param context 
     * @param packageName 
     * @return 
     */  
    public static boolean isInstalled(Context context, String packageName) {  
        boolean hasInstalled = false;  
        PackageManager pm = context.getPackageManager();  
        List<PackageInfo> list = pm  
                .getInstalledPackages(PackageManager.PERMISSION_GRANTED);  
        for (PackageInfo p : list) {  
            if (packageName != null && packageName.equals(p.packageName)) {  
                hasInstalled = true;  
                break;  
            }  
        }  
        return hasInstalled;  
    }  
    
    public static boolean copyApkFromAssets(Context context, String fileName, String path) {  
        boolean copyIsFinish = false;  
        try {  
            InputStream is = context.getAssets().open(fileName);  
            File file = new File(path);  
            file.createNewFile();  
            FileOutputStream fos = new FileOutputStream(file);  
            byte[] temp = new byte[1024];  
            int i = 0;  
            while ((i = is.read(temp)) > 0) {  
                fos.write(temp, 0, i);  
            }  
            fos.close();  
            is.close();  
            copyIsFinish = true;  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        return copyIsFinish;  
    }  
    
    public static String ensure_path(Context context,String path,String asst_apk) {
		if (path == null || path.equals("")) {
			File file = new File(Util.getSDPath() + File.separator
					+ context.getPackageName());
			if (!file.exists()) {
				file.mkdirs();
			}
			path = file.getAbsolutePath() + File.separator + asst_apk;
			
		}
		return path;
	}
    
    

	 
    /**
	 * 判断手机是否支持NFC功能和OpenMobileAPI
	 * */
	public static boolean isSupportMocam(Context mContext) {

		boolean isSupportMocam = false;

		// 判断手机是否有NFC功能
		PackageManager pm = mContext.getPackageManager();
		isSupportMocam = pm.hasSystemFeature(PackageManager.FEATURE_NFC);
		
		return isSupportMocam;
	}

	public static int getVersionCode(Context context)	{
		int code= 0;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			code = info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return code;
	}

	public static String getVersionName(Context context)	{
		String name = "";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			name = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	public static void showTokenDialog(final Context mContext) {
		if (!LoginActivity.isLoginTopActivity) {
			//LoginActivity.startThisAct(mContext, true);
		}

	}

	public static void showTokenDialog(final Context mContext,boolean isExpired) {

		Dialog alertDialog=new AlertDialog.Builder(mContext)
				.setTitle("地铁信用VIP令牌已过期，请重新登录!")
				.setPositiveButton("确定",new  DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();


					}

				}).create();


		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				return false;
			}




		});

		//alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG);
		//alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();
	}


	public static void wirteTokenLog(String content) {
		try {
			String tokenFilePath = FileUtil.SDPATH + "tokenLog.txt";
			FileUtil.writeFile(tokenFilePath, content + "Token:" + Utils.getCurrentTestTime()+"\n", true);
		} catch (IOException e) {
			Log.e("Log","e="+e.toString());
		}
	}

	public static String getPhoneModel() {
		return  android.os.Build.MODEL;
	}

	public static boolean matchPhoneModel(String[] devicesArray,String device) {
		if (devicesArray != null && devicesArray.length > 0 && device != null) {
			for (int i = 0; i < devicesArray.length; i++) {
				if (device.equals(devicesArray[i]))
					return true;
			}
		}
		return false;
	}


	public static void showPhoneModelDialog(final Activity mContext) {

		Dialog alertDialog=new AlertDialog.Builder(mContext)
				.setTitle("暂时不支持该您的手机型号！系统将持续升级，支持更多手机型号，敬请关注!")
				.setPositiveButton("确定",new  DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						mContext.finish();
					}

				}).create();


		alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener()
		{
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

				return false;
			}




		});


		alertDialog.show();
	}
}
