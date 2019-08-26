package com.wimetro.qrcode.http;

import android.content.Context;

import com.wimetro.qrcode.greendao.entity.Card;
import com.wimetro.qrcode.greendao.entity.Moblie;
import com.wimetro.qrcode.greendao.entity.StationCache;
import com.wimetro.qrcode.greendao.entity.User;
import com.wimetro.qrcode.http.bean.Report;
import com.wimetro.qrcode.http.bean.Token;
import com.wimetro.qrcode.http.bean.Version;

import java.io.IOException;

/**
 * API interface
 */
public interface Api {

	/**
	 * 用户注册接口
	 *
	 * @param context
	 * @param app_user
	 * @param pass_word
	 * @param device_imei
	 * @param device_type
	 * @param email
	 * @param validateCode
	 * @param channel_type
     * @return
     */
	public ApiResponse<User> register(Context context, String app_user,String tele_no,
									  String pass_word,String device_imei,String device_type,String email,
								  String validateCode,String channel_type) throws IOException;

	/**
	 *  用户登录接口
	 *
	 * @param context
	 * @param app_user
	 * @param pass_word
	 * @param device_imei
     * @return
     */
	public ApiResponse<User> login(Context context, String app_user,String tele_no,
								  String pass_word,String device_imei,String app_sys_time,String device_detail) throws IOException;

	/**
	 * 获取”唤起钱包签约页面”的报文接口
	 *
	 * @param context
	 * @param hce_id
     * @return
     */
	public ApiResponse<User> gainAlipyAgreementPageMessage(Context context, String hce_id) throws IOException;

	/**
	 * 确认激活结果接口
	 *
	 * @param context
	 * @param hce_id
	 *
     * @return
     */
	public ApiResponse<User> activateAlipyUserAction_gainActivateResult(Context context, String hce_id) throws IOException;



	/**
	 * 获取”唤起钱包签约页面”的报文接口
	 *
	 * @param context
	 * @param hce_id
	 * @return
	 */
	public ApiResponse<User> gainWeiXinAgreementPageMessage(Context context, String hce_id) throws IOException;

	/**
	 * 确认激活结果接口
	 *
	 * @param context
	 * @param hce_id
	 *
	 * @return
	 */
	public ApiResponse<User> activateWeiXinUserAction_gainActivateResult(Context context, String hce_id) throws IOException;





	/**
	 * 发送短信验证码接口
	 *
	 * @param context
	 * @param tele_no

     * @return
     */
	public ApiResponse<Void> sendRandomCode(Context context, String tele_no) throws IOException;

	/**
	 * 修改密码接口
	 *
	 * @param context
	 * @param app_user
	 * @param validateCode
	 * @param pass_word
	 * @param device_type
	 * @param device_imei

     * @return
     */

	public ApiResponse<?> modifyPassWord(Context context, String app_user,
								  String validateCode,String pass_word,String device_type,String device_imei) throws IOException;

	/**
	 * 验证短信验证码接口
	 *
	 * @param context
	 * @param tele_no
	 * @param validateCode
	 * @return
	 * @throws IOException
     */
	public ApiResponse<Void> verifySMSCode(Context context, String tele_no,String validateCode )throws IOException;


	/**
	 *设置新密码接口
	 *
	 * @param context
	 * @param tele_no
	 * @param pass_word
	 * @param validateCode
	 * @return
	 * @throws IOException
     */
	public ApiResponse<Void> findPassWord(Context context, String tele_no,String validateCode ,String pass_word)throws IOException;

	/**
	 * 邮件推送密码接口
	 *
	 * @param context
	 * @param app_user

     * @return
     */

	public ApiResponse<User> sendEmailPassWord(Context context, String app_user) throws IOException;

	/**
	 * 卡数据下载
	 *
	 * @param context
	 * @param hce_id
	 * @param file_type

     * @return
     */
	public ApiResponse<Card> downloadCardInfo(Context context, String hce_id,String file_type)throws IOException;

	/**10.1.卡数据上传
	 *
	 * @param context
	 * @param hce_id
	 * @param cardNo
	 * @param ADF1_0017_01
	 * @param fee_total
	 * @param onlineval
	 * @param offlineval

     * @return
     */
	public ApiResponse<Void> uploadCardInfo(Context context, String hce_id, String cardNo, String ADF1_0017_01, String fee_total, String offlineval, String onlineval) throws IOException;

	/**
	 *  上报出入站信息
	 * @param context
	 * @param hce_id
	 * @param info_type
	 * @param deal_device_code
	 * @param deal_seq_group_no
	 * @param deal_seq_no
	 * @param deal_station
	 * @param deal_type
	 * @param main_type
	 * @param sub_type
	 * @param area_code
	 * @param sam_code
	 * @param logical_code
	 * @param read_count
	 * @param deal_amount
	 * @param balance
	 * @param deal_time
	 * @param last_deal_dev_code
	 * @param last_deal_sq_no
	 * @param last_deal_amount
	 * @param last_deal_time
	 * @param tac
	 * @param degrade_mode
	 * @param in_gate_station
	 * @param in_gate_dev
	 * @param in_gate_time
	 * @param pay_type
	 * @param pay_card_no
	 * @param destination_station
	 * @param deal_cause
	 * @param deal_total_amount
     * @param deposit
     * @param deal_fee
     * @param expiry_date
     * @param last_expiry_date
     * @param oper_id
     * @param work_sq_no
     * @return
     * @throws IOException
     */
	public ApiResponse<Report> upStationState(Context context, String hce_id, String info_type, String deal_device_code,
											  String deal_seq_group_no, String deal_seq_no, String deal_station, String deal_type,
											  String main_type, String sub_type, String area_code, String sam_code, String logical_code,
											  String read_count, String deal_amount, String balance, String deal_time, String last_deal_dev_code,
											  String last_deal_sq_no, String last_deal_amount, String last_deal_time, String tac, String degrade_mode,
											  String in_gate_station, String in_gate_dev, String in_gate_time, String pay_type, String pay_card_no,
											  String destination_station, String deal_cause, String deal_total_amount, String deposit, String deal_fee,
											  String expiry_date, String last_expiry_date, String oper_id, String work_sq_no, String info_id) throws IOException;


	/**
	 * 11.4. 查询出入站信息
	 *
	 * @param context
	 * @param hce_id
	 * @param deal_type
	 * @param start_time
	 * @param end_time
	 * @param page
	 * @param rows

     * @return
     */
	public ApiResponse<StationCache> queryStationStateByPage(Context context, String hce_id, String deal_type, String info_type, String start_time, String end_time, String page, String rows) throws IOException;


	public ApiResponse<Token> gainToken(Context context, String hce_id, String voucher_id, String device_imei, String app_sys_time) throws IOException;

	public ApiResponse<Token> verifyToken(Context context, String hce_id,String voucher_id,String app_sys_time,String token_id) throws IOException;

	public ApiResponse<User> silentLogIn(Context context, String hce_id,String voucher_id,String device_imei) throws IOException;

	/**
	 *
	 * 登出
	 *
	 * @param context
	 * @param app_user
	 * @return
	 * @throws IOException
     */
	public ApiResponse<Void> logout(Context context, String app_user) throws IOException;

	/**
	 * 获取版本信息
	 *
	 * @param context
	 * @return
	 * @throws IOException
     */
	public ApiResponse<Version> gainHCEVersion(Context context) throws IOException;

	/**
	 *
	 * @param context
	 * @param tele_no
	 * @return
	 * @throws IOException
     */
	public ApiResponse<User> gainActivateResultByAppUser(Context context, String tele_no) throws IOException;


	/**
	 * 解约
	 *
	 * @param context
	 * @param hce_id
	 * @param pass_word
	 * @return
	 * @throws IOException
     */
	public ApiResponse<User> cancelAlipyWeiXinAgreementStatus(Context context, String hce_id,String pass_word,String channal_type) throws IOException;


    /**
	 *
	 * @return
	 * @throws IOException
     */
	public ApiResponse<Moblie> gainHCEMobileModelList(Context context) throws IOException;
}
