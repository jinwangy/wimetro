package com.wimetro.qrcode.common.utils;

import com.wimetro.qrcode.R;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 校验器
 * 
 * @author lds
 * 
 */
public class Validator {

 
    
    public static boolean judgeNotEmpty(Context context, String text, int label) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(context.getApplicationContext(), context.getString(label),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

   

    public static boolean isPhoneNumberFull(Context context, String phoneNumber) {
        if (phoneNumber != null && TextUtils.isDigitsOnly(phoneNumber)
                && phoneNumber.length() == 11) {
            return true;
        }
        return false;
    }
    
    
    public static void onError(Context context, String text) {
        Toast.makeText(context.getApplicationContext(), text,
                Toast.LENGTH_SHORT).show();
    }

    /*
     * public static boolean checkNickname(Context context, String name) { if
     * (name == null) { return false; // 可以为空 } if (name.length() > 12) {
     * Toast.makeText(context.getApplicationContext(),
     * R.string.msg_error_nickname_length, Toast.LENGTH_SHORT) .show(); return
     * false; } if (! isLetterNumberChinese(name)) {
     * Toast.makeText(context.getApplicationContext(),
     * R.string.msg_error_nickname_format, Toast.LENGTH_SHORT) .show(); return
     * false; }
     * 
     * return true; }
     */

    public static boolean checkPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 16;
    }

    public static boolean checkUser(String user) {
        return user != null && user.length() >= 6 && user.length() <= 16;
    }

    /**
     * 是否为字母，数字，中文
     * 
     * @param text
     * @return
     */
    public static boolean isLetterNumberChinese(String text) {
        if (text == null) {
            return false;
        }
        return text.matches("[a-zA-Z0-9\u4e00-\u9fa5]+");
    }

    /**
     * 是否为字母，数字
     * 
     * 
     * @param text
     * @return
     */
    public static boolean isLetterNumber(String text) {
        if (text == null) {
            return false;
        }
        return text.matches("[a-zA-Z0-9]+");
    }

    /**
     * 是否为数字
     * 
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        if (number == null)
            return false;
        return number.matches("[+-]?[1-9]+[0-9]*(\\.[0-9]+)?");
    }

    /**
     * 是否为字母
     * 
     * @param alpha
     * @return
     */
    public boolean isAlpha(String alpha) {
        if (alpha == null)
            return false;
        return alpha.matches("[a-zA-Z]+");
    }

    public static boolean isAscii(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (isAscii(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAscii(char ch) {
        return ch < 128;
    }

    /**
     * 是否为中文
     * 
     * @param chineseContent
     * @return
     */
    public static boolean isChinese(String chineseContent) {
        if (chineseContent == null)
            return false;
        return chineseContent.matches("[\u4e00-\u9fa5]+");
    }

    public static boolean judgePassword(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
       
            return false;
        }
        if (text.length() > 16 || text.length() < 6 || !isAscii(text)) {
           
            return false;
        }
        return true;
    }
    
    public static boolean checkNotEmpty(Context context, String text, int label) {
        if (TextUtils.isEmpty(text)) {
            onError(context,
                    context.getString(label)
                            + context.getString(R.string.error_not_empty));
            return false;
        }
        return true;
    }
    
    public static boolean checkPassword(Context context, String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(context.getApplicationContext(), R.string.reg_pwd,
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        if (text.length() > 16 || text.length() < 6 || !isAscii(text)) {
            Toast.makeText(context.getApplicationContext(),
                    R.string.reg_pwd_limit1, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** 手机号校验 */
    public static boolean isPhoneNumberValid(String phoneNumber) {
        boolean isValid = false;

        if(phoneNumber.length() != 11) {
            return isValid;
        }

        String expression = "((^(13|14|15|17|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
        CharSequence inputStr = phoneNumber;

        Pattern pattern = Pattern.compile(expression);

        Matcher matcher = pattern.matcher(inputStr);

        if (matcher.matches()) {
            isValid = true;
        }

        return isValid;

    }
    
}
