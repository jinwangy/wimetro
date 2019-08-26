package com.wimetro.qrcode.http;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.configs.AppConfig;
//import com.wimetro.qrcode.model.bean.TradeDetailQuery;

import com.otech.yoda.utils.DialogUtils;
import com.wimetro.qrcode.ui.activity.LoginActivity;


public class ApiRequest<T> {
	private static final String PROTOCOL_CHARSET = "utf-8";
	private static final String Tag = "ApiRequest<T>";
	private static final boolean DEBUG = AppConfig.DEBUG;

	//private final Listener<ApiResponse<T>> mListener;
	//private final Map<String, String> mParams;

	private Class<T> mClazz;

	public ApiRequest(String url, Map<String, String> params,
			Class<T> clazz) {
//		super(params != null ? Method.POST : Method.GET, url, errorlistener);
//		mListener = listener;
//		mParams = params;
//		mClazz = clazz;
//
//		setRetryPolicy(new DefaultRetryPolicy(AppConfig.HTTP_TIMEOUT_SO,
//				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//		String jsonString=JSON.toJSONString(params);
//		if (DEBUG) {
//			Log.e("HTTP", "request: " + jsonString);
//		}
	}

//	@Override
//	protected Map<String, String> getParams() throws AuthFailureError {
//		if (DEBUG) {
//			Log.e("HTTP", "url: " + getUrl());
//			Log.e("HTTP", "params: " + mParams);
//		}
//		return mParams;
//	}
//
//	@Override
//	protected void deliverResponse(ApiResponse<T> response) {
//		mListener.onResponse(response);
//	}
//
//	@Override
//	protected Response<ApiResponse<T>> parseNetworkResponse(
//			NetworkResponse response) {
//		try {
//
//			final String data = new String(response.data, PROTOCOL_CHARSET);
//			return Response.success(parseResponse(data),
//					HttpHeaderParser.parseCacheHeaders(response));
//		} catch (UnsupportedEncodingException e) {
//			return Response.error(new ParseError(e));
//		} catch (JSONException e) {
//			return Response.error(new ParseError(e));
//		} catch (Throwable e) {
//			e.printStackTrace();
//			return Response.error(new ParseError(e));
//		}
//	};

//	/**
//	 * 网络出错
//	 *
//	 * 1. 无网 2. 服务器无法响应 3. 服务器响应，但响应值错误
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static ErrorListener getErrorListener(final Context context) {
//		return new ErrorListener() {
//			public void onErrorResponse(VolleyError error) {
//				error.printStackTrace();
//				Toast.makeText(context.getApplicationContext(),
//						R.string.msg_error_network, Toast.LENGTH_SHORT).show();
//			}
//		};
//	}
//
//	/**
//	 * 网络出错
//	 *
//	 * 1. 无网 2. 服务器无法响应 3. 服务器响应，但响应值错误
//	 *
//	 * @param context
//	 * @return
//	 */
//	public static ErrorListener getErrorListener(final Context context,
//			final Dialog dialog) {
//		return new ErrorListener() {
//			public void onErrorResponse(VolleyError error) {
//				error.printStackTrace();
//				if(dialog != null) {
//					DialogUtils.dismissDialog(dialog);
//				}
//				Toast.makeText(context.getApplicationContext(),
//						R.string.msg_error_network, Toast.LENGTH_SHORT).show();
//			}
//		};
//	}
//
//	public static ErrorListener getErrorListener(final Context context,
//			final View view) {
//		return new ErrorListener() {
//			public void onErrorResponse(VolleyError error) {
//				error.printStackTrace();
//				if (view != null)
//					view.setVisibility(View.GONE);
//				Toast.makeText(context.getApplicationContext(),
//						R.string.msg_error_network, Toast.LENGTH_SHORT).show();
//			}
//		};
//	}

	private ApiResponse<T> parseResponse(String jsonStr) throws JSONException {
		return parseResponse(jsonStr, mClazz);
	}

	@SuppressWarnings("unchecked")
	public static <T> ApiResponse<T> parseResponse(String jsonStr,
			Class<T> clazz) throws JSONException {
		if (DEBUG) {
			WLog.e("HTTP", "response: " + jsonStr);
		}
		JSONObject jo = JSON.parseObject(jsonStr);
		if (jo != null) {
			ApiResponse<T> res = new ApiResponse<T>();
			if (jo.containsKey("rtData")) {
				String data = jo.getString("rtData");
//				if (clazz==GmainTips.class) {
//					res= (ApiResponse<T>) parseMainTips(data);
//				}else {
				if (data != null && !data.equals("")) {
					if (data != null && data.trim().startsWith("[")) {
						res.setList(JSON.parseArray(data, clazz));
					} else {
						// data = parseTips(data);
						res.setObject(JSON.parseObject(data, clazz));
					}
				}
				//}
			}

			if (jo.containsKey("rtCode")) {
				Integer code = null;
				try {
					code = Integer.parseInt(jo.getString("rtCode"));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if (code != null) {
					res.setCode(code);
					res.setStatus(code == CODE_SUCCESS);
				}
			}

			if (jo.containsKey("rtMessage")) {
				res.setMsg(jo.getString("rtMessage"));
			}



			return res;
		}

		return null;
	}
	
	public static void showEmptyResponseError(Context context) {
		Toast.makeText(context.getApplicationContext(),
				R.string.msg_error_no_data, Toast.LENGTH_SHORT).show();
	}

	public static boolean handleResponse(Context context, ApiResponse<?> res) {
		return handleResponse(context, res, true);
	}

	public static boolean handleResponse(Context context, ApiResponse<?> res, boolean isShowError) {
		if (res != null) {
			// String msg = getErrorByCode(res.getCode());
			if(res.getCode() == null) {
				String msg = res.getMsg();
				if (msg != null) {
					if (isShowError && !msg.equals("")) {
						Toast.makeText(context.getApplicationContext(), msg,
								Toast.LENGTH_SHORT).show();
					} else {
						if (DEBUG)
							WLog.e(Tag, msg);
					}
				}
				return false;
			}
			else if (res.isStatus()) {
				return true;
			} else if (res.getCode() == CODE_EMPTY_DATA) {
//				res.setList(Collections.EMPTY_LIST);
				return true;
			} else if (res.getCode() == CODE_SAME_DATA) {
				return true;
			}
			//else if (res.getCode() == CODE_EXPIRED_SESSIONKEY) {
//				LoginActivity.startThisAct(context,true);
//				if (isShowError) {
//					Toast.makeText(context.getApplicationContext(),
//							R.string.expired_sessionkey, Toast.LENGTH_SHORT).show();
//				} else {
//					if (DEBUG)
//						Log.d(Tag, context.getResources().getString(R.string.expired_sessionkey));
//				}
//				return false;
//			}
			else if (res.getCode() == CODE_SUCCESS){
				return true;
			}

			String msg2 = res.getMsg();
			if (isShowError && !msg2.equals("")) {
				Toast.makeText(context.getApplicationContext(), msg2,
						Toast.LENGTH_SHORT).show();
			} else {
				if (DEBUG)
					WLog.e(Tag, msg2);
			}
		} else {
			if (isShowError) {
				Toast.makeText(context.getApplicationContext(),
						R.string.msg_error_no_data, Toast.LENGTH_SHORT).show();
			} else {
				if (DEBUG)
					WLog.e(Tag, context.getResources().getString(R.string.msg_error_no_data));
			}
		}
		return false;
	}

	public static final int CODE_EMPTY_DATA = 99;
	public static final int CODE_SAME_DATA = 20002;
	public static final int CODE_EXPIRED_SESSIONKEY = 00003;
    public static final int CODE_SUCCESS = 00001;
}
