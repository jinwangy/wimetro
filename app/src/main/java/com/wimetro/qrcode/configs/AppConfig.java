package com.wimetro.qrcode.configs;

import com.wimetro.qrcode.common.utils.WLog;

/**
 * jwyuan on 2017/8/30 10:08.
 */

public class AppConfig {

    public static String HTTPS_CERT;
    public static String API_URL_PHONE_HCE_VERSION_APK;

    public static String HOST;
    public static String SLL_HOST;
    public static int SLL_SIGLE_PORT;
    public static int SLL_HCE_DOUBLE_PORT;
    public static int SLL_HCE_DOUBLE_PORT_TRADE;
    public static String WEIXIN_APP_ID = "wx19c034b90bba176f";

    public static short card_no_version;

    public static void init() {
        if(WLog.isDebug) {
            WLog.e("AppConfig","测试环境");
            HTTPS_CERT = "MIIDPTCCAiWgAwIBAgIFBhYY90swDQYJKoZIhvcNAQELBQAwLzELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBUhCSENFMRAwDgYDVQQDDAdIQkhDRUNBMB4XDTE3MTAwOTA5MzkxOFoXDTI3MTAwOTA5MzkxOFowLzELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBUhCSENFMRAwDgYDVQQDDAdIQkhDRUNBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjIZJfHOTEYh1MsEw6Oq2EzbSpPTIqsT1Ev3w0CNF9bzJa3pP5fw0QoNtp3mCd+BG4RfQz8pYa4xEzU+hpVypDOUSqRAp6iq8PN2HMWF1jA2M8Z7KZsX4W8E02wg7dBVb3B5N38HOwAZxy7b8sNxXDts4uVpquHY/Rc/CGJn90kTj7VRyWWBEvhis9EBPeRXwM8icJeM9AT5JE2WqsDTEELy8+3SKWvFgS8/kvLOaRlt7S6c8QuwRhbz6rE4g0bTPixdxyRDk5t1Z4+koKwDb88B5NJBCSevBu0SfHSMdf29WROOKBgGNIav8Z+bDSMerdNuOgRlh76TfYN8+vsmFfQIDAQABo2AwXjAfBgNVHSMEGDAWgBRvtaWEjp7jliZNAlboqyRM3ph09zAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAdBgNVHQ4EFgQUb7WlhI6e45YmTQJW6KskTN6YdPcwDQYJKoZIhvcNAQELBQADggEBACiUdb71zUJH+2uyStatIczsUKTXKIJAm+OVpgbOkqFBNovyf5DHdPncNFUqnnZh/K/zu9jyCQlcmgTJDv/Wy5FiZWF0jXeDbLvLxmrYDxeRtxaemuQe/j2B0YrQ2bLAQrmBNZwHH99+zG/i6y+F5jyI1Aan4WE7DeWgAm+TuRkab3X66nZrtU0doDM2tfmifO2rN2jlOaoezUPwMpQfnwYe+ZVAFYcf4edJUgH4CPPUIG7PEiiyxmQeXk8JiAYjkjK3mHr7tByWgBzRn8qUqLZHnUHdXpkdKpJgz399lbwBwrmBgORJDuR93Cpny4s5dOCaIvNs30AaaVgTJGs78Vs=";

            API_URL_PHONE_HCE_VERSION_APK = "http://59.175.176.236:8239/IAFCSystem/";

            card_no_version = 3;

            //测试环境
            HOST = "59.175.176.236:8228";
            SLL_HOST = "59.175.176.236";
            SLL_SIGLE_PORT = 8233;
            SLL_HCE_DOUBLE_PORT = 8229;
            SLL_HCE_DOUBLE_PORT_TRADE = 8234;
        } else {
            WLog.e("AppConfig","正式环境");
            HTTPS_CERT = "MIIDQzCCAiugAwIBAgIFCBeOVvUwDQYJKoZIhvcNAQELBQAwMTELMAkGA1UEBhMCQ04xDzANBgNVBAoMBldIWkhEVDERMA8GA1UEAwwIV0haSERUQ0EwIBcNMTcxMjA1MTAyMzA3WhgPMjA2NzEyMDUxMDIzMDdaMDExCzAJBgNVBAYTAkNOMQ8wDQYDVQQKDAZXSFpIRFQxETAPBgNVBAMMCFdIWkhEVENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgxinObZofiPB6WBhSAOnNndv20LoJM4tKPZCh2XYdXKZuO1DV+Bau7036PAGTnPGEHAoTHID+jFBWjfwgiOCevBApHILi6N2EbiZLTuTzh/Q20U2mlwCtRwIR235VqQi7toRg63wRv5fs7iyS77rPp5aPKgCSWUqQwnPXSa5tgWlBFp14E6dBnTjMlxfeqQ17uxhXAVlxjkm3xoLIFMGvfThLvYehys1JSJHmm9dmQzrCyFxNxetVOtVWyXXGkBDAaUo+YSmmiK8L2oFM3vbQcP8xL6wrLEBvLF4CPstPlHJpTImAoqIOTiS7bsZ5F1ZlQmZnr59Lu5O65OhaMavLwIDAQABo2AwXjAfBgNVHSMEGDAWgBRZt07u3I4ZqtKQaEuAjh5WpWVq2zAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAdBgNVHQ4EFgQUWbdO7tyOGarSkGhLgI4eVqVlatswDQYJKoZIhvcNAQELBQADggEBACuarsqs8bQie62yTzpwAhQPEPGSGDjOFoBptnM/23W6cO05x4Lpi655Z/2FInRilDdg9b0nBN9W3MiIwZFawylYgopkfgze39zLAIS7CRBOl7DgjbNe5HL9VwF9h9OxemvYVJVwfdSQZRTdcgAt3KC9+nLEr8WuIrHbqgaUgRQKTRLV+93GokVE56JGGTz7NhZX4Syik3DRtgJBTlxSP6XR03BPW9Rtt7nQfJo8PwSiQZpeToL6IzPQPqUluQEoBbcf8FkFBvubMpZm67LkTtFd2Ifp+lb6rwJG1QimW35p1PJvbWWKH0tsQWzZi1y55AbPBkrmBO6UQupd0k9TDi8=";

            API_URL_PHONE_HCE_VERSION_APK = "http://mpay.wimetro.com:5090/IAFC_HCE_Version/";

            card_no_version = 3;

            //正式环境
            HOST = "61.183.232.58:10443";
            SLL_HOST = "61.183.232.58";
            SLL_SIGLE_PORT = 20443;
            SLL_HCE_DOUBLE_PORT = 30443;
            SLL_HCE_DOUBLE_PORT_TRADE = 18234;
        }
    }

    /** 调式开关 */
    public static final boolean DEBUG = true;

    public static int MAIN_ANIMATION_TIME = 2000;//进入应用的背景画面3秒
    public static int GUIDE_ANIMATION_TIME = 2000;//进入应用的过场画面2秒

    /** 短信验证码倒计时(秒) */
    public static final int SMS_CODE_TIME_COUNT = 120;

    /** HTTP超时 */
    public static final int HTTP_TIMEOUT_SO = 20;
    public static final int AD_TIME = 1000;
    public static final int HOME_PART = 1;


    //正式环境证书秘钥
    //public static final String HTTPS_CERT = "MIIDQzCCAiugAwIBAgIFCBeOVvUwDQYJKoZIhvcNAQELBQAwMTELMAkGA1UEBhMCQ04xDzANBgNVBAoMBldIWkhEVDERMA8GA1UEAwwIV0haSERUQ0EwIBcNMTcxMjA1MTAyMzA3WhgPMjA2NzEyMDUxMDIzMDdaMDExCzAJBgNVBAYTAkNOMQ8wDQYDVQQKDAZXSFpIRFQxETAPBgNVBAMMCFdIWkhEVENBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgxinObZofiPB6WBhSAOnNndv20LoJM4tKPZCh2XYdXKZuO1DV+Bau7036PAGTnPGEHAoTHID+jFBWjfwgiOCevBApHILi6N2EbiZLTuTzh/Q20U2mlwCtRwIR235VqQi7toRg63wRv5fs7iyS77rPp5aPKgCSWUqQwnPXSa5tgWlBFp14E6dBnTjMlxfeqQ17uxhXAVlxjkm3xoLIFMGvfThLvYehys1JSJHmm9dmQzrCyFxNxetVOtVWyXXGkBDAaUo+YSmmiK8L2oFM3vbQcP8xL6wrLEBvLF4CPstPlHJpTImAoqIOTiS7bsZ5F1ZlQmZnr59Lu5O65OhaMavLwIDAQABo2AwXjAfBgNVHSMEGDAWgBRZt07u3I4ZqtKQaEuAjh5WpWVq2zAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAdBgNVHQ4EFgQUWbdO7tyOGarSkGhLgI4eVqVlatswDQYJKoZIhvcNAQELBQADggEBACuarsqs8bQie62yTzpwAhQPEPGSGDjOFoBptnM/23W6cO05x4Lpi655Z/2FInRilDdg9b0nBN9W3MiIwZFawylYgopkfgze39zLAIS7CRBOl7DgjbNe5HL9VwF9h9OxemvYVJVwfdSQZRTdcgAt3KC9+nLEr8WuIrHbqgaUgRQKTRLV+93GokVE56JGGTz7NhZX4Syik3DRtgJBTlxSP6XR03BPW9Rtt7nQfJo8PwSiQZpeToL6IzPQPqUluQEoBbcf8FkFBvubMpZm67LkTtFd2Ifp+lb6rwJG1QimW35p1PJvbWWKH0tsQWzZi1y55AbPBkrmBO6UQupd0k9TDi8=";
    //测试环境证书秘钥
    //public static final String HTTPS_CERT = "MIIDPTCCAiWgAwIBAgIFBhYY90swDQYJKoZIhvcNAQELBQAwLzELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBUhCSENFMRAwDgYDVQQDDAdIQkhDRUNBMB4XDTE3MTAwOTA5MzkxOFoXDTI3MTAwOTA5MzkxOFowLzELMAkGA1UEBhMCQ04xDjAMBgNVBAoMBUhCSENFMRAwDgYDVQQDDAdIQkhDRUNBMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjIZJfHOTEYh1MsEw6Oq2EzbSpPTIqsT1Ev3w0CNF9bzJa3pP5fw0QoNtp3mCd+BG4RfQz8pYa4xEzU+hpVypDOUSqRAp6iq8PN2HMWF1jA2M8Z7KZsX4W8E02wg7dBVb3B5N38HOwAZxy7b8sNxXDts4uVpquHY/Rc/CGJn90kTj7VRyWWBEvhis9EBPeRXwM8icJeM9AT5JE2WqsDTEELy8+3SKWvFgS8/kvLOaRlt7S6c8QuwRhbz6rE4g0bTPixdxyRDk5t1Z4+koKwDb88B5NJBCSevBu0SfHSMdf29WROOKBgGNIav8Z+bDSMerdNuOgRlh76TfYN8+vsmFfQIDAQABo2AwXjAfBgNVHSMEGDAWgBRvtaWEjp7jliZNAlboqyRM3ph09zAPBgNVHRMBAf8EBTADAQH/MAsGA1UdDwQEAwIBBjAdBgNVHQ4EFgQUb7WlhI6e45YmTQJW6KskTN6YdPcwDQYJKoZIhvcNAQELBQADggEBACiUdb71zUJH+2uyStatIczsUKTXKIJAm+OVpgbOkqFBNovyf5DHdPncNFUqnnZh/K/zu9jyCQlcmgTJDv/Wy5FiZWF0jXeDbLvLxmrYDxeRtxaemuQe/j2B0YrQ2bLAQrmBNZwHH99+zG/i6y+F5jyI1Aan4WE7DeWgAm+TuRkab3X66nZrtU0doDM2tfmifO2rN2jlOaoezUPwMpQfnwYe+ZVAFYcf4edJUgH4CPPUIG7PEiiyxmQeXk8JiAYjkjK3mHr7tByWgBzRn8qUqLZHnUHdXpkdKpJgz399lbwBwrmBgORJDuR93Cpny4s5dOCaIvNs30AaaVgTJGs78Vs=";

    public static final String PROTOCOL = "https";

    public static final String CERT_Algorithm = "SHA256WithRSA";
    public static final int KEY_LENGTH = 2048;

    //证书有效期
    public static  String VALIDITY = "60";

    public static final String ECC_CA_PEM = "ecc_ca.pem";
    public static final String ECC_INTER_CA_PEM = "ecc_inter_ca.pem";
    public static final String ECC_SIGN_PEM = "ecc_sign.pem";
    public static final String ECC_SIGN_KEY_PEM = "ecc_sign_key.pem";
    public static final String ECC_ENC_PEM = "ecc_enc.pem";
    public static final String ECC_ENC_KEY_PEM = "ecc_enc_key.pem";

    public static final String RSA_CA_PEM = "rsa_ca.pem";
    public static final String RSA_INTER_CA_PEM = "rsa_inter_ca.pem";
    public static final String RSA_CLIENT_KEY = "rsa_key.pem";
    public static final String RSA_CLIENT_CER = "rsa_cer.pem";

    public static final String LOCAL_CA_PEM = "local_ca.pem";
    public static final String LOCAL_INTER_CA_PEM = "local_inter_ca.pem";

    //public static final int SLL_SIGLE_PORT=8247;    //zhou本地机器
    //public static final int SLL_HCE_DOUBLE_PORT=8248;//zhou本地机器
    //public static final int SLL_CA_CERT_PORT=8228;

    //内网10.129.68.219
    //public static final String SLL_HOST="10.129.68.219";
    //public static final int SLL_SIGLE_PORT=8300;
    //public static final int SLL_DOUBLE_PORT=8229;
    //public static final int SLL_DOUBLE_PORT_TRADE=8311;

    /** request code for finish this activity */
    public static final int REQUEST_CODE_FINISH_FLAG = 0x012;

    public static final  String API_URL = "IAFC_HCE_Account_Manager/iafc/";
    public static final  String API_URL_TRADE = "IAFC_HCE_Trade_Manager/trade/";
    public static final  String API_URL_BUSINESS = "IAFC_HCE_Business_Manager/iafc/";
    public static final String ACTION = ".action";

    public static final String API_URL_PHONE_USER_REGISTER =API_URL + "hce/account/hceAppUserAction_register" + ACTION;
    public static final String API_URL_PHONE_USER_LOGIN =API_URL + "hce/account/hceAppUserAction_login" + ACTION;
    public static final String API_URL_PHONE_GNAIN_AGREEMENTPAGEMESSAGE =API_URL + "hce/account/hceAppUserAction_gainAgreementPageMessage"+ ACTION;
    public static final String API_URL_PHONE_GNAIN_WEIXIN_AGREEMENTPAGEMESSAGE =API_URL + "hce/account/hceAppUserAction_gainWXAgreementPageMessage"+ ACTION;
    public static final String API_URL_PHONE_RECEIVE_SIGNAGREEMENTRESULT =API_URL + "hce/account/hceAppUserAction_receiveSignAgreementResult"+ ACTION;
    public static final String API_URL_PHONE_RECEIVE_WEIXIN_SIGNAGREEMENTRESULT =API_URL + "hce/account/hceServerNotifyAction_recWXSignAgreementResult"+ ACTION;
    public static final String API_URL_PHONE_RECEIVE_CANCELAGREEMENTRESULT =API_URL + "hce/account/hceAppUserAction_receiveCancelAgreementResult"+ ACTION;
    public static final String API_URL_PHONE_ACTIVATEUSERACTION_GAINACTIVATERESULT =API_URL + "hce/account/hceAppUserAction_gainActivateResult"+ ACTION;
    public static final String API_URL_PHONE_SENDRANDCODE =API_URL + "hce/account/hceAppUserAction_sendRandomCode"+ ACTION;
    public static final String API_URL_PHONE_MODIFY_PASSWORD =API_URL + "hce/account/hceAppUserAction_modifyPassWord"+ ACTION;
    public static final String API_URL_PHONE_VERIFYSMSCODE =API_URL + "hce/account/hceAppUserAction_verifySMSCode"+ ACTION;
    public static final String API_URL_PHONE_SEND_EMAIL_PASSWORD =API_URL + "hce/account/hceAppUserAction_sendEmailPassWord"+ ACTION;
    public static final String API_URL_PHONE_DOWN_LODE_CARD_INFO =API_URL_BUSINESS + "hce/business/hceCardAction_downloadCardInfo"+ ACTION;
    public static final String API_URL_PHONE_UPLOAD_CARD_INFO =API_URL_BUSINESS + "hce/business/hceCardAction_uploadCardInfo"+ ACTION;
    public static final String API_URL_PHONE_UPLOAD_STATION_STATE =API_URL_TRADE + "tradeManagerAction_upStationState"+ ACTION;
    public static final String API_URL_PHONE_QUERY_STATION_STATE =API_URL_TRADE + "tradeManagerAction_queryStationStateByPage"+ ACTION;
    public static final String API_URL_PHONE_FINDPASSWORD_ =API_URL + "hce/account/hceAppUserAction_findPassWord"+ ACTION;
    public static final String API_URL_PHONE_RECEIVE_PAY_RESULT =API_URL + "hce/pay/dealPayAction_receivePayResult"+ ACTION;

    public static final String API_URL_PHONE_GAIN_TOKEN =API_URL + "hce/account/hceAppUserAction_gainToken" + ACTION;
    public static final String API_URL_PHONE_VERIFY_TOKEN =API_URL + "hce/account/hceAppUserAction_verifyToken" + ACTION;

    public static final String API_URL_PHONE_LOGOUT =API_URL + "hce/account/hceAppUserAction_logout" + ACTION;
    public static final String API_URL_PHONE_HCE_VERSION =API_URL + "hce/account/hceAppUserAction_gainHCEVersion" + ACTION;

    public static final String API_URL_PHONE_GAINACTIVATERESULT_BYUSER =API_URL + "hce/account/hceAppUserAction_gainActivateByAppUser"+ ACTION;

    public static final String API_URL_PHONE_SILENT_LOGIN =API_URL + "hce/account/hceAppUserAction_silentLogIn" + ACTION;

    public static final String API_URL_PHONE_CANCEL_ALIPY_AGREEMENT =API_URL + "hce/account/hceAppUserAction_cancelAPMPAgreementStatus" + ACTION;

    public static final String API_URL_PHONE_CANCEL_WEIXIN_AGREEMENT =API_URL + "hce/account/hceAppUserAction_cancelWXAgreementStatus" + ACTION;

    public static final String API_URL_PHONE_GAIN_HCE_MOBIL_MODEL_LIST =API_URL + "hce/account/hceAppUserAction_gainHCEMobileModelList" + ACTION;
}