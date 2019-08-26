#include <jni.h>
#include <string>
#include "HCEPrepay.h"
#include <android/log.h>

#define LOG_TAG "log_from_jni"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#ifdef __cplusplus
extern "C"
{
#endif


	JNIEXPORT jstring JNICALL Java_com_wimetro_qrcode_jni_NativeLib_stringFromJNI(JNIEnv *env, jobject obj)
	{
		std::string hello = "Hello from C++";
		return env->NewStringUTF(hello.c_str());
	}

	JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_processApdu(JNIEnv *env, jobject obj,
		jbyteArray p_apdu_in,
		jshort len_in,
		jbyteArray p_apdu_out, 
		jshortArray len_out,
		jbyteArray trade_type,
		jbyteArray p_trade_data, jshortArray p_token_status)
	{
		LOGE("do ProcessApdu()");
		jshort cv = -1;

		jbyte* _p_apdu_in = (*env).GetByteArrayElements(p_apdu_in,NULL);
		jbyte* _p_apdu_out = (*env).GetByteArrayElements(p_apdu_out,NULL);
		jshort* _len_out = (*env).GetShortArrayElements(len_out,NULL);
		jbyte* _trade_type = (*env).GetByteArrayElements(trade_type,NULL);
		jbyte* _p_trade_data = (*env).GetByteArrayElements(p_trade_data,NULL);
		jshort* _p_token_status = (*env).GetShortArrayElements(p_token_status,NULL);

		if(_p_apdu_in == NULL || _p_apdu_out == NULL || _p_trade_data == NULL || _trade_type == NULL || _len_out == NULL || _p_token_status == NULL) {
			return -1;
		}

		cv = ProcessApdu((char *)_p_apdu_in, len_in, (char *)_p_apdu_out, _len_out, (char *)_trade_type, (char*)_p_trade_data, (short *)_p_token_status);

		(*env).ReleaseByteArrayElements(p_apdu_in,_p_apdu_in,0);
		(*env).ReleaseByteArrayElements(p_apdu_out,_p_apdu_out,0);
		(*env).ReleaseShortArrayElements(len_out,_len_out,0);
		(*env).ReleaseByteArrayElements(trade_type,_trade_type,0);
		(*env).ReleaseByteArrayElements(p_trade_data,_p_trade_data,0);
		(*env).ReleaseShortArrayElements(p_token_status,_p_token_status,0);

		return cv;
	}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_setLocalParam(JNIEnv *env, jobject obj, jstring pdir, jstring p_sys_cache, jstring pime,jshort verLowest,jstring pAccount,jbyteArray p_data_CRC) {
	LOGE("do SetLocalParam()");
	jshort cv = -1;

    const char* _pdir = env->GetStringUTFChars(pdir, 0);
	const char* _p_sys_cache = env->GetStringUTFChars(p_sys_cache, 0);
    const char* _pime = env->GetStringUTFChars(pime, 0);
	const char* _pAccount = env->GetStringUTFChars(pAccount, 0);
	jbyte* _p_data_CRC = (*env).GetByteArrayElements(p_data_CRC,NULL);

	if(_pdir == NULL || _pime == NULL || _p_sys_cache == NULL || _pAccount == NULL || _p_data_CRC == NULL) {
		return -1;
	}

	cv = SetLocalParam(_pdir,_p_sys_cache,_pime,verLowest,_pAccount,(char *)_p_data_CRC);

    (*env).ReleaseStringUTFChars(pdir,_pdir);
	(*env).ReleaseStringUTFChars(p_sys_cache,_p_sys_cache);
    (*env).ReleaseStringUTFChars(pime,_pime);
	(*env).ReleaseStringUTFChars(pAccount,_pAccount);
	(*env).ReleaseByteArrayElements(p_data_CRC,_p_data_CRC,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_createDefaultCard(JNIEnv *env, jobject obj) {
	LOGE("do CreateDefaultCard()");
	jshort cv = CreateDefaultCard();
	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardMF05(JNIEnv *env, jobject obj,jbyteArray p_file_data,jshort len_data) {
	LOGE("do UpdateCard_MF_05()");
	jshort cv = -1;

	jbyte* _p_file_data = (*env).GetByteArrayElements(p_file_data,NULL);
	if(_p_file_data == NULL) {
		return -1;
	}

	cv = UpdateCard_MF_05((char *)_p_file_data,len_data);

	(*env).ReleaseByteArrayElements(p_file_data,_p_file_data,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardADF01key(JNIEnv *env, jobject obj,jbyte type,jbyte index,jbyte ver,jbyte algo,jbyte err_cnt,jbyteArray p_key,jbyte len_key) {
	LOGE("do UpdateCard_ADF01_key()");
	jshort cv = -1;

	jbyte* _p_key = (*env).GetByteArrayElements(p_key,NULL);
	if(_p_key == NULL) {
		return -1;
	}

	cv = UpdateCard_ADF01_key(type,index,ver,algo,err_cnt,(char *)_p_key,len_key);

	(*env).ReleaseByteArrayElements(p_key,_p_key,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardADF01wallet(JNIEnv *env, jobject obj,jint balance,jshort off_cnt,jshort on_cnt, jbyte forceUpdate)
{
	LOGE("do UpdateCard_ADF01_wallet()");

	jshort cv = UpdateCard_ADF01_wallet(balance,off_cnt,on_cnt,forceUpdate);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardADF0115(JNIEnv *env, jobject obj,jbyteArray p_file_data,jshort len_data) {
	LOGE("do UpdateCard_ADF01_15()");
	jshort cv = -1;

	jbyte* _p_file_data = (*env).GetByteArrayElements(p_file_data,NULL);
	if(_p_file_data == NULL) {
		return -1;
	}

	cv = UpdateCard_ADF01_15((char *)_p_file_data,len_data);

	(*env).ReleaseByteArrayElements(p_file_data,_p_file_data,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardADF0117(JNIEnv *env, jobject obj,jbyte id,jbyteArray p_file_data,jshort len_data) {
	LOGE("do UpdateCard_ADF01_17()");
	jshort cv = -1;

	jbyte* _p_file_data = (*env).GetByteArrayElements(p_file_data,NULL);
	if(_p_file_data == NULL) {
		return -1;
	}

	cv = UpdateCard_ADF01_17(id,(char *)_p_file_data,len_data);

	(*env).ReleaseByteArrayElements(p_file_data,_p_file_data,0);

	return cv;
}

JNIEXPORT void JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateCardEnd(JNIEnv *env, jobject obj,jshort verCurrent) {
	LOGE("do UpdateCard_end()");
	UpdateCard_end(verCurrent);
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getWalletInfo(JNIEnv *env, jobject obj, jshort adf_sfi, jintArray p_balance, jshortArray p_offline_cnt, jshortArray p_online_cnt) {
	LOGE("do GetWalletInfo()");
	jshort cv = -1;

	jint* _p_balance = (*env).GetIntArrayElements(p_balance,NULL);
	jshort* _p_offline_cnt = (*env).GetShortArrayElements(p_offline_cnt,NULL);
	jshort* _p_online_cnt = (*env).GetShortArrayElements(p_online_cnt,NULL);

	if(_p_balance == NULL || _p_offline_cnt == NULL || _p_online_cnt == NULL) {
		return -1;
	}

	cv = GetWalletInfo(adf_sfi,_p_balance,_p_offline_cnt,_p_online_cnt);

	(*env).ReleaseIntArrayElements(p_balance,_p_balance,0);
	(*env).ReleaseShortArrayElements(p_offline_cnt,_p_offline_cnt,0);
	(*env).ReleaseShortArrayElements(p_online_cnt,_p_online_cnt,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getRecordMetro(JNIEnv *env, jobject obj, jshort adf_sfi, jbyteArray p_metro, jshort len_metro) {
	LOGE("do GetRecordMetro()");
	jshort cv = -1;

	jbyte* _p_metro = (*env).GetByteArrayElements(p_metro,NULL);
	if(_p_metro == NULL) {
		return -1;
	}

	cv = GetRecordMetro(adf_sfi,(char *)_p_metro,len_metro);

	(*env).ReleaseByteArrayElements(p_metro,_p_metro,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getUploadInfo(JNIEnv *env, jobject obj, jbyteArray p_logicID,jintArray p_balance, jshortArray p_offline_cnt, jshortArray p_online_cnt, jbyteArray p_metro, jshort len_metro,jbyteArray p_data_CRC) {
	LOGE("do GetUploadInfo()");
	jshort cv = -1;

	jbyte* _p_logicID = (*env).GetByteArrayElements(p_logicID,NULL);
	jint* _p_balance = (*env).GetIntArrayElements(p_balance,NULL);
	jshort* _p_offline_cnt = (*env).GetShortArrayElements(p_offline_cnt,NULL);
	jshort* _p_online_cnt = (*env).GetShortArrayElements(p_online_cnt,NULL);
	jbyte* _p_metro = (*env).GetByteArrayElements(p_metro,NULL);
	jbyte* _p_data_CRC = (*env).GetByteArrayElements(p_data_CRC,NULL);

	if(_p_logicID == NULL || _p_balance == NULL || _p_offline_cnt == NULL || _p_online_cnt == NULL || _p_metro == NULL || _p_data_CRC == NULL) {
		return -1;
	}

	cv = GetUploadInfo((char *)_p_logicID,_p_balance,_p_offline_cnt,_p_online_cnt,(char *)_p_metro,len_metro,(char *)_p_data_CRC);

	(*env).ReleaseByteArrayElements(p_logicID,_p_logicID,0);
	(*env).ReleaseIntArrayElements(p_balance,_p_balance,0);
	(*env).ReleaseShortArrayElements(p_offline_cnt,_p_offline_cnt,0);
	(*env).ReleaseShortArrayElements(p_online_cnt,_p_online_cnt,0);
	(*env).ReleaseByteArrayElements(p_metro,_p_metro,0);
	(*env).ReleaseByteArrayElements(p_data_CRC,_p_data_CRC,0);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_setCardStatus(JNIEnv *env, jobject obj,jbyte newStatus) {
	LOGE("do SetCardStatus()");
	jshort cv = SetCardStatus(newStatus);
	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_cardExsit(JNIEnv *env, jobject obj) {
	LOGE("do CardExsit()");
	jshort cv = CardExsit();
	return cv;
}

JNIEXPORT short JNICALL Java_com_wimetro_qrcode_jni_NativeLib_updateLocalToken(JNIEnv *env, jobject obj,jstring p_NewToken,jshort lenToken) {
	LOGE("do UpdateLocalToken()");
	jshort cv = -1;

	const char* _p_NewToken = env->GetStringUTFChars(p_NewToken, 0);
	if(_p_NewToken == NULL) {
		return -1;
	}

	cv = UpdateLocalToken(_p_NewToken,lenToken);
	(*env).ReleaseStringUTFChars(p_NewToken,_p_NewToken);

	return cv;
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_checkToken(JNIEnv *env, jobject obj,jstring p_time_now) {
	LOGE("do CheckToken()");
	jshort cv = -1;

	const char* _p_time_now = env->GetStringUTFChars(p_time_now, 0);
	if(_p_time_now == NULL) {
		return -1;
	}

	cv = CheckToken(_p_time_now);
	(*env).ReleaseStringUTFChars(p_time_now,_p_time_now);

	return cv;
}

JNIEXPORT void JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getTokenOddInfo(JNIEnv *env, jobject obj,jstring p_time_now,jshortArray oddmins,jshortArray oddamount,jshortArray minamount,jbyteArray oddcnt) {
	LOGE("do GetTokenOddInfo()");

	const char* _p_time_now = env->GetStringUTFChars(p_time_now, 0);
	jshort* _oddmins = (*env).GetShortArrayElements(oddmins,NULL);
	jshort* _oddamount = (*env).GetShortArrayElements(oddamount,NULL);
	jshort* _minamount = (*env).GetShortArrayElements(minamount,NULL);
	jbyte* _oddcnt = (*env).GetByteArrayElements(oddcnt,NULL);

	if(_p_time_now == NULL || _oddmins == NULL || _oddamount == NULL || _oddcnt == NULL || _minamount == NULL) {
		return;
	}

	GetTokenOddInfo(_p_time_now,_oddmins,_oddamount,_minamount,(char *)_oddcnt);

	(*env).ReleaseStringUTFChars(p_time_now,_p_time_now);
	(*env).ReleaseShortArrayElements(oddmins,_oddmins,0);
	(*env).ReleaseShortArrayElements(oddamount,_oddamount,0);
	(*env).ReleaseShortArrayElements(minamount,_minamount,0);
	(*env).ReleaseByteArrayElements(oddcnt,_oddcnt,0);
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getTokenForServerVerify(JNIEnv *env, jobject obj,jbyteArray pToken,jshortArray len_token) {
	LOGE("do GetTokenForServerVerify()");
	jshort cv = -1;

	jbyte* _pToken = (*env).GetByteArrayElements(pToken,NULL);
	jshort* _len_token = (*env).GetShortArrayElements(len_token,NULL);

	if(_pToken == NULL || _len_token == NULL) {
		return -1;
	}

	cv = GetTokenForServerVerify((char *)_pToken,_len_token);

	(*env).ReleaseByteArrayElements(pToken,_pToken,0);
	(*env).ReleaseShortArrayElements(len_token,_len_token,0);

	return cv;
}

JNIEXPORT void JNICALL Java_com_wimetro_qrcode_jni_NativeLib_tmpTokenApply(JNIEnv *env, jobject obj,jbyteArray pToken,jshortArray len_token) {
	LOGE("do tmpTokenApply()");

	jbyte* _pToken = (*env).GetByteArrayElements(pToken,NULL);
	jshort* _len_token = (*env).GetShortArrayElements(len_token,NULL);

	if(_pToken == NULL || _len_token == NULL) {
		return;
	}

	//tmpTokenApply((char *)_pToken,_len_token);

	(*env).ReleaseByteArrayElements(pToken,_pToken,0);
	(*env).ReleaseShortArrayElements(len_token,_len_token,0);
}

JNIEXPORT jshort JNICALL Java_com_wimetro_qrcode_jni_NativeLib_getInternelErrorCode(JNIEnv *env, jobject obj) {
	LOGE("do getInternelErrorCode()");
	jshort cv = getInternelErrorCode();
	return cv;
}

#ifdef __cplusplus
};
#endif
