package com.wimetro.qrcode.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.utils.BASE64Encoder;
import com.wimetro.qrcode.common.utils.DeviceUtil;
import com.wimetro.qrcode.common.utils.Utils;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
import com.wimetro.qrcode.greendao.entity.Card;
import com.wimetro.qrcode.greendao.entity.Moblie;
import com.wimetro.qrcode.greendao.entity.StationCache;
import com.wimetro.qrcode.http.bean.Message;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.bean.Report;
import com.wimetro.qrcode.http.bean.Token;
import com.wimetro.qrcode.http.bean.Version;

import cn.com.infosec.mobile.android.cert.InfosecCert;
import cn.com.infosec.mobile.android.net.InfosecSSL;
import cn.com.infosec.mobile.android.result.Result;
import cn.com.infosec.mobile.android.sign.InfosecSign;



/**
 * API Client
 */
public class ApiClient implements Api {

	public static final String TAG = ApiClient.class.getSimpleName();
	public static final boolean DEBUG = AppConfig.DEBUG;

	//private RequestQueue mQueue;
	//private RequestQueue mBackgroundQueue;

	private static ApiClient sInstance;

	private static ExecutorService pool = null;

	public synchronized static ApiClient getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new ApiClient(context);
		}
		return sInstance;
	}

	private ApiClient(Context context) {
		//mQueue = VolleyManager.getRequestQueue(context);
		//mBackgroundQueue = VolleyManager.getRequestQueue(context);
	}

	public static ExecutorService createPool()
	{
		if(pool == null) {
			pool = Executors.newCachedThreadPool();
		}

		return pool;
	}

	public static void deletePool()
	{
		if(pool != null) {
			pool.shutdown();
			pool = null;
		}
	}


	private <T> ApiResponse<T> parseOldApi(Context context,String json, Class<T> clazz)
			throws IOException {
		if (DEBUG) {
			WLog.e("HTTP", "response: " + json);
		}
		ApiResponse<T> response = new ApiResponse<T>();
		try {
			if(json == null || json.trim().equals("")) {
				WLog.e("HTTP", "response:empty");
				return null;
			}

			if (!json.startsWith("{")) {
				int index = json.indexOf("{");
				if (index != -1)
				    json = json.substring(index);
			}

			JSONObject jo = JSON.parseObject(json);
			if (jo != null) {
				if (jo.containsKey("rtCode")) {
					Integer ret = jo.getInteger("rtCode");
					response.setCode(ret);
					response.setStatus(ret == 1);
				}
				if (jo.containsKey("rtMessage")) {
					response.setMsg(jo.getString("rtMessage"));
				}
				if (jo.containsKey("rtData") || jo.containsKey("rtListData")) {
					String jsonStr = jo.getString("rtData");
					if(jsonStr == null) jsonStr = jo.getString("rtListData");
					if (jsonStr != null) {
						if (jsonStr.trim().startsWith("[")) {
							response.setList(JSON.parseArray(jsonStr, clazz));
						} else if (jsonStr.trim().startsWith("{")) {
							response.setObject(JSON.parseObject(jsonStr, clazz));
						}
					}
				}

				return response;
			} else {
				WLog.e("HTTP", "response:json is illegal");
				//1. 无网 2. 服务器无法响应 3. 服务器响应，但响应值错误
				response.setMsg(context.getString(R.string.msg_error_response));
			}
		}catch(JSONException e){
			e.printStackTrace();
			//1. 无网 2. 服务器无法响应 3. 服务器响应，但响应值错误
			//    Toast.makeText(context.getApplicationContext(),
			//		R.string.msg_error_network, Toast.LENGTH_SHORT).show();
			response.setMsg(context.getString(R.string.msg_error_response));
		}

		return response;
	}

//	public void execute(Context context, Request<?> request) {
//		request.setTag(context.getClass().getSimpleName());
//		mQueue.add(request);
//	}
//
//	public void executeInBackground(Context context, Request<?> request) {
//		request.setTag(context.getClass().getSimpleName());
//		mBackgroundQueue.add(request);
//	}
//
//	public RequestQueue getQueue() {
//		return mQueue;
//	}
//
//	public void cancel(ApiRequest<?> request) {
//		cancelRequest(mQueue, request);
//	}
//
//	public void cancelAll(Context context) {
//		String tag = context.getClass().getSimpleName();
//		mQueue.cancelAll(tag);
//	}
//
//	public static void cancelRequest(final RequestQueue queue,
//			final Request<?> request) {
//		if (queue != null && request != null) {
//			queue.cancelAll(new RequestFilter() {
//				public boolean apply(Request<?> other) {
//					return other.getUrl().equals(request.getUrl());
//				}
//			});
//		}
//	}

	/* ------------------ API Methods ------------------- */

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

	private void copyFromAssert(String assertPath, String dataPath,Context context) {
		try {
			BufferedInputStream bufferedInputStream = new BufferedInputStream(context.getAssets().open(assertPath));
			BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(context.openFileOutput(dataPath, context.MODE_PRIVATE));
			int len;
			byte[] buf = new byte[1024];
			while ((len = bufferedInputStream.read(buf)) > 0) {
				bufferedOutputStream.write(buf, 0, len);
			}
			bufferedInputStream.close();
			bufferedOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String signAttach(String attachContext,String pid,String imei ) {
		InfosecSign infosecSign = new InfosecSign();
		String attachPlainText = infosecSign.attachSign(attachContext.getBytes(), pid, imei);
        return  attachPlainText;
	}

	private String signDeatch(String deatchContext,String pid,String imei ) {
		InfosecSign infosecSign = new InfosecSign();
		String deatchPlainText = infosecSign.detachSign(deatchContext.getBytes(),pid, imei);
		return  deatchPlainText;
	}

	private InfosecSSL initSSL(Context context) {
		copyFromAssert("LOCAL_CA.cer", AppConfig.LOCAL_CA_PEM,context);
		copyFromAssert("LOCAL_CA.cer", AppConfig.LOCAL_INTER_CA_PEM,context);

		// 初始化SSL对象
		InfosecSSL infosecSSL = new InfosecSSL();
		// 设置信任证书
		infosecSSL.setCACert(new String[]{new File(context.getFilesDir(), AppConfig.LOCAL_CA_PEM).getPath(), new File(context.getFilesDir(), AppConfig.LOCAL_INTER_CA_PEM).getPath()});

		infosecSSL.setTimeOut(AppConfig.HTTP_TIMEOUT_SO);

		return infosecSSL;
	}



	@Override
	public ApiResponse<User> register(Context context, String app_user, String tele_no, String pass_word, String device_imei, String device_type, String email, String validateCode, String channel_type) throws IOException {

		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);


		// 设置参数并进行连接
		Map<String, String> params = new HashMap<String, String>();

		params.put("app_user", app_user);
		params.put("tele_no", tele_no);
		params.put("pass_word", pass_word);
		params.put("device_imei", device_imei);
		params.put("device_type", device_type);
		params.put("email", email);
		params.put("validateCode", validateCode);
		params.put("channel_type", channel_type);
		String md5Params = app_user + tele_no + pass_word + device_imei + device_type + email + validateCode + channel_type;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_USER_REGISTER, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;
	}

	@Override
	public ApiResponse<User> login(Context context, String app_user, String tele_no, String pass_word, String device_imei,String app_sys_time,String device_detail) throws IOException {

		InfosecSSL infosecSSL = initSSL(context);
		WLog.e(TAG,"sigle,IP = " + AppConfig.SLL_HOST + ",PORT = " + AppConfig.SLL_SIGLE_PORT);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();

		params.put("app_user", app_user);
		params.put("tele_no", tele_no);
		params.put("pass_word", pass_word);
		params.put("device_imei", device_imei);
		params.put("app_sys_time", app_sys_time);
		params.put("device_detail", device_detail);

		String md5Params = app_user + tele_no + pass_word + device_imei;
		String md5Str = Utils.GetMD5Code(md5Params);
		//params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_USER_LOGIN, params);
		//Log.i("Log","RSA返回结果:"+response);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;
	}

	@Override
	public ApiResponse<User> gainAlipyAgreementPageMessage(Context context, String hce_id) throws IOException{
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		String md5Params = hce_id;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_GNAIN_AGREEMENTPAGEMESSAGE, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}

	@Override
	public ApiResponse<User> activateAlipyUserAction_gainActivateResult(Context context, String hce_id) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);

		String md5Params = hce_id;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);
		WLog.i("Log","activateUserAction_gainActivateResult");
		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_ACTIVATEUSERACTION_GAINACTIVATERESULT, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}

	@Override
	public ApiResponse<User> gainWeiXinAgreementPageMessage(Context context, String hce_id) throws IOException{
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		String md5Params = hce_id;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_GNAIN_WEIXIN_AGREEMENTPAGEMESSAGE, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}

	@Override
	public ApiResponse<User> activateWeiXinUserAction_gainActivateResult(Context context, String hce_id) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);

		String md5Params = hce_id;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);
		WLog.i("Log","activateUserAction_gainActivateResult");
		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_ACTIVATEUSERACTION_GAINACTIVATERESULT, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}
















	@Override
	public ApiResponse<User> gainActivateResultByAppUser(Context context, String tele_no) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("tele_no", tele_no);

		String md5Params = tele_no;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);
		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_GAINACTIVATERESULT_BYUSER, params);

		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, User.class);
		}
		return null;
	}

	@Override
	public ApiResponse<User> cancelAlipyWeiXinAgreementStatus(Context context, String hce_id, String pass_word, String channal_type) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
        // 设置双向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_HCE_DOUBLE_PORT);

		String CN = DeviceUtil.getCN(context);
		String imei = DeviceUtil.getImei(context);

		// 设置SDK内部的证书作为客户端证书
		boolean setLocalClientCert = infosecSSL.setLocalClientCert(CN, imei);//双向 注释掉单向

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("pass_word", pass_word);

		String url ="";
		if(channal_type.equals("APMP")) {
			url = AppConfig.API_URL_PHONE_CANCEL_ALIPY_AGREEMENT;
		} else if(channal_type.equals("WX")) {
			url = AppConfig.API_URL_PHONE_CANCEL_WEIXIN_AGREEMENT;
		}
		String response = infosecSSL.execute(url, params);

		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, User.class);
		}

		return null;
	}

	@Override
	public ApiResponse<Moblie> gainHCEMobileModelList(Context context) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		// 设置参数并进行连接
		Map<String, String> params = new HashMap<String, String>();
		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_GAIN_HCE_MOBIL_MODEL_LIST, params);
		WLog.i("Log","RSA返回结果:"+response);
		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, Moblie.class);
		}

		return null;
	}

	@Override
	public ApiResponse<Void> sendRandomCode(Context context, String tele_no) throws IOException {

		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		// 设置参数并进行连接
		Map<String, String> params = new HashMap<String, String>();
		params.put("tele_no", tele_no);
		String md5Params = tele_no ;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_SENDRANDCODE, params);
		WLog.i("Log","RSA返回结果:"+response);
		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, Void.class);
		}

		return null;
	}


	@Override
	public ApiResponse<?> modifyPassWord(Context context, String app_user, String validateCode, String pass_word, String device_type, String device_imei) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("app_user", app_user);
		params.put("validateCode", validateCode);
		params.put("pass_word", pass_word);
		params.put("device_type", device_type);
		params.put("device_imei", device_imei);

		String md5Params = app_user + validateCode + pass_word + device_type + device_imei;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_MODIFY_PASSWORD, params);
		WLog.e("Log","RSA返回结果:"+response);
		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}

	@Override
	public ApiResponse<Void> verifySMSCode(Context context, String tele_no, String validateCode) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("tele_no", tele_no);
		params.put("validateCode", validateCode);

		WLog.e("Log","tele_no:"+tele_no+",validateCode:"+validateCode);

		String md5Params = tele_no + validateCode;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_VERIFYSMSCODE, params);
		WLog.e("Log","RSA返回结果:"+response);
		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, Void.class);
		}
		return null;
	}

	@Override
	public ApiResponse<Void> findPassWord(Context context, String tele_no, String validateCode, String pass_word) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("tele_no", tele_no);
		params.put("validateCode", validateCode);
		params.put("pass_word", pass_word);

		String md5Params = tele_no + validateCode + pass_word;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_FINDPASSWORD_, params);
		WLog.e("Log","RSA返回结果:"+response);
		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, Void.class);
		}
		return null;
	}

	@Override
	public ApiResponse<User> sendEmailPassWord(Context context, String app_user) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("app_user", app_user);

		String md5Params = app_user;
		String md5Str = Utils.GetMD5Code(md5Params);
		params.put("md5", md5Str);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_SEND_EMAIL_PASSWORD, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, User.class);
		}
		return null;

	}

	@Override
	public ApiResponse<Card> downloadCardInfo(Context context, String hce_id,String file_type) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		WLog.e(TAG,"double,IP = " + AppConfig.SLL_HOST + ",PORT = " + AppConfig.SLL_HCE_DOUBLE_PORT);
		// 设置双向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_HCE_DOUBLE_PORT);

		String CN = DeviceUtil.getCN(context);
		String imei = DeviceUtil.getImei(context);

		// 设置SDK内部的证书作为客户端证书
		boolean setLocalClientCert = infosecSSL.setLocalClientCert(CN,imei);//双向 注释掉单向

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("file_type", file_type);

		String paramsStr = hce_id + file_type;
		String md5Str = Utils.GetMD5Code(paramsStr);
		params.put("md5", md5Str);

		String sign = signAttach(paramsStr,CN,imei);
		sign = new BASE64Encoder().encode(sign.getBytes("UTF-8"));
		params.put("sign_code", sign);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_DOWN_LODE_CARD_INFO, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {
			return parseOldApi(context,response, Card.class);
		}
		return null;
	}

	@Override
	public ApiResponse<Void> uploadCardInfo(Context context, String hce_id, String cardNo,String ADF1_0017_01, String fee_total, String offlineval, String onlineval) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);

		// 设置双向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_HCE_DOUBLE_PORT);

		String CN = DeviceUtil.getCN(context);
		String imei = DeviceUtil.getImei(context);

		// 设置SDK内部的证书作为客户端证书
		boolean setLocalClientCert = infosecSSL.setLocalClientCert(CN, imei);//双向 注释掉单向

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("cardNo", cardNo);
		params.put("ADF1_0017_01", ADF1_0017_01);
		params.put("fee_total", fee_total);
		params.put("onlineval", onlineval);
		params.put("offlineval", offlineval);

		String paramsStr = hce_id + cardNo + ADF1_0017_01  + fee_total + onlineval + offlineval;
		String md5Str = Utils.GetMD5Code(paramsStr);
		params.put("md5", md5Str);

		String sign = signAttach(paramsStr,CN,imei);
		sign = new BASE64Encoder().encode(sign.getBytes("UTF-8"));
		params.put("sign_code", sign);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_UPLOAD_CARD_INFO, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {
			return parseOldApi(context,response, Void.class);
		}
		return null;
	}

	@Override
	public ApiResponse<Report> upStationState(Context context, String hce_id, String info_type, String deal_device_code,
											  String deal_seq_group_no, String deal_seq_no, String deal_station, String deal_type,
											  String main_type, String sub_type, String area_code, String sam_code, String logical_code,
											  String read_count, String deal_amount, String balance, String deal_time, String last_deal_dev_code,
											  String last_deal_sq_no, String last_deal_amount, String last_deal_time, String tac, String degrade_mode,
											  String in_gate_station, String in_gate_dev, String in_gate_time, String pay_type, String pay_card_no,
											  String destination_station, String deal_cause, String deal_total_amount, String deposit, String deal_fee,
											  String expiry_date, String last_expiry_date, String oper_id, String work_sq_no, String info_id) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		// 设置双向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_HCE_DOUBLE_PORT_TRADE);

		String CN = DeviceUtil.getCN(context);
		String imei = DeviceUtil.getImei(context);

		// 设置SDK内部的证书作为客户端证书
		boolean setLocalClientCert = infosecSSL.setLocalClientCert(CN, imei);//双向 注释掉单向

		Map<String, String> params = new HashMap<String, String>();

		params.put("hce_id", hce_id);
		params.put("info_type", info_type);
		params.put("deal_device_code", deal_device_code);
		params.put("deal_seq_group_no", deal_seq_group_no);
		params.put("deal_seq_no", deal_seq_no);
		params.put("deal_station", deal_station);

		params.put("deal_type", deal_type);
		params.put("main_type", main_type);
		params.put("sub_type", sub_type);
		params.put("area_code", area_code);
		params.put("sam_code", sam_code);
		params.put("logical_code", logical_code);

		params.put("read_count", read_count);
		params.put("deal_amount", deal_amount);
		params.put("balance", balance);
		params.put("deal_time", deal_time);
		params.put("last_deal_dev_code", last_deal_dev_code);
		params.put("last_deal_sq_no", last_deal_sq_no);

		params.put("last_deal_amount", last_deal_amount);
		params.put("last_deal_time", last_deal_time);
		params.put("tac", tac);
		params.put("degrade_mode", degrade_mode);
		params.put("in_gate_station", in_gate_station);
		params.put("in_gate_dev", in_gate_dev);

		params.put("in_gate_time", in_gate_time);
		params.put("pay_type", pay_type);
		params.put("pay_card_no", pay_card_no);
		params.put("destination_station", destination_station);
		params.put("deal_cause", deal_cause);
		params.put("deal_total_amount", deal_total_amount);

		params.put("deposit", deposit);
		params.put("deal_fee", deal_fee);
		params.put("expiry_date", expiry_date);
		params.put("last_expiry_date", last_expiry_date);
		params.put("oper_id", oper_id);
		params.put("work_sq_no", work_sq_no);

		String paramsStr = hce_id + info_type + deal_station + deal_type + deal_time;
		String md5Str = Utils.GetMD5Code(paramsStr);
		params.put("md5", md5Str);

		String sign = signAttach(paramsStr,CN,imei);
		sign = new BASE64Encoder().encode(sign.getBytes("UTF-8"));
		params.put("sign_code", sign);

		params.put("info_id", info_id);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_UPLOAD_STATION_STATE, params);
        //Log.i("SSL 双向","response="+response);
		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, Report.class);
		}
		return null;
	}

	@Override
	public ApiResponse<StationCache> queryStationStateByPage(Context context, String hce_id, String deal_type, String info_type,String start_time, String end_time, String page, String rows) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		WLog.e(TAG,"double(trade),IP = " + AppConfig.SLL_HOST + ",PORT = " + AppConfig.SLL_HCE_DOUBLE_PORT_TRADE);
		// 设置双向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_HCE_DOUBLE_PORT_TRADE);

		String CN = DeviceUtil.getCN(context);
		String imei = DeviceUtil.getImei(context);

		// 设置SDK内部的证书作为客户端证书
		boolean setLocalClientCert = infosecSSL.setLocalClientCert(CN, imei);//双向 注释掉单向

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("deal_type", deal_type);
		params.put("info_type", info_type);
		params.put("start_time", start_time);
		params.put("end_time", end_time);
		params.put("page", page);
		params.put("rows", rows);

		String paramsStr = hce_id + page + rows;
		String md5Str = Utils.GetMD5Code(paramsStr);
		params.put("md5", md5Str);

		String sign = signAttach(paramsStr,CN,imei);
		sign = new BASE64Encoder().encode(sign.getBytes("UTF-8"));
		params.put("sign_code", sign);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_QUERY_STATION_STATE, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {
			return parseOldApi(context,response, StationCache.class);
		}
		return null;
	}

	@Override
	public ApiResponse<Token> gainToken(Context context, String hce_id,String voucher_id,String device_imei,String app_sys_time) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("voucher_id", voucher_id);
		params.put("device_imei", device_imei);
		params.put("app_sys_time", app_sys_time);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_GAIN_TOKEN, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {
			return parseOldApi(context,response, Token.class);
		}

		return null;
	}

	@Override
	public ApiResponse<Token> verifyToken(Context context, String hce_id,String voucher_id,String app_sys_time,String token_id) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("voucher_id", voucher_id);
		params.put("app_sys_time", app_sys_time);
		params.put("token_id", token_id);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_VERIFY_TOKEN, params);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {
			return parseOldApi(context,response, Token.class);
		}

		return null;
	}

	@Override
	public ApiResponse<User> silentLogIn(Context context, String hce_id,String voucher_id,String device_imei) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("hce_id", hce_id);
		params.put("voucher_id", voucher_id);
		params.put("device_imei", device_imei);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_SILENT_LOGIN, params);

		// 释放连接对象
		infosecSSL.releaseConnection();
		if (response != null) {
			return parseOldApi(context,response, User.class);
		}

		return null;
	}

	@Override
	public ApiResponse<Void> logout(Context context, String app_user) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();
		params.put("app_user", app_user);

		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_LOGOUT, params);
		//Log.i("Log","RSA返回结果:"+response);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, Void.class);
		}

		return null;
	}

	@Override
	public ApiResponse<Version> gainHCEVersion(Context context) throws IOException {
		InfosecSSL infosecSSL = initSSL(context);
		//单向主机和端口
		infosecSSL.setHost(AppConfig.SLL_HOST, AppConfig.SLL_SIGLE_PORT);

		Map<String, String> params = new HashMap<String, String>();


		String response = infosecSSL.execute(AppConfig.API_URL_PHONE_HCE_VERSION, params);
		//Log.i("Log","RSA返回结果:"+response);

		// 释放连接对象
		infosecSSL.releaseConnection();

		if (response != null) {

			return parseOldApi(context,response, Version.class);
		}

		return null;
	}
}
