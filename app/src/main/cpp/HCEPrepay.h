#ifdef _WIN32
#ifdef HCEPREPAY_EXPORTS
#define HCEPREPAY_API __declspec(dllexport)
#else
#define HCEPREPAY_API __declspec(dllimport)
#endif
#else
#define HCEPREPAY_API
#endif

#include "Types.h"

#ifdef __cplusplus
extern "C"
{
#endif

	HCEPREPAY_API short ProcessApdu(char * p_apdu_in, short len_in, char * p_apdu_out, short * len_out, char * trade_type, char * p_trade_data, short * p_token_status);

	// 设置本地参数，同时检查卡文件是否存在，不存在就创建
	HCEPREPAY_API short SetLocalParam(const char * pdir, const char * p_sys_cache, const char * pime, short verLowest, const char * pAccount, char * p_data_CRC);

	// 本地文件不存在就先调用本接口，然后调用UpdateCardFile
	HCEPREPAY_API short CreateDefaultCard();

	HCEPREPAY_API short UpdateCard_MF_05(char * p_file_data, short len_data);
	HCEPREPAY_API short UpdateCard_ADF01_key(char type, char index, char ver, char algo, char err_cnt, char * p_key, char len_key);
	//forceUpdate: 0-非强制更新（本地交易计数比较大的时候不更新），1-强制更新（不管本地交易计数，强制更新卡数据）
	HCEPREPAY_API short UpdateCard_ADF01_wallet(int balance, short off_cnt, short on_cnt, char forceUpdate);
	HCEPREPAY_API short UpdateCard_ADF01_15(char * p_file_data, short len_data);
	HCEPREPAY_API short UpdateCard_ADF01_17(char id, char * p_file_data, short len_data);
	HCEPREPAY_API void UpdateCard_end(short verCurrent);

	// 获取钱包信息
	HCEPREPAY_API short GetWalletInfo(short adf_sfi, int * p_balance, short * p_offline_cnt, short * p_online_cnt);

	// 获取复合文件01记录-地铁交易信息文件
	HCEPREPAY_API short GetRecordMetro(short adf_sfi, char * p_metro, short len_metro);

	// p_logicID, string 16B
	// p_balance, int引用
	// p_offline_cnt, short引用
	// p_online_cnt,short引用
	// p_metro,HEX码序列，长度len_metro
	HCEPREPAY_API short GetUploadInfo(char * p_logicID, int * p_balance, short * p_offline_cnt, short * p_online_cnt, char * p_metro, short len_metro, char * p_data_CRC);

	// 设置卡片脱机状态，在UpdateCard_end之前调用
	// 0x00-初始状态，CreateDefaultCard成功后为该状态，该状态的卡，cos会限制不可使用
	// 0x01-未启用状态，当APP收到服务端相关消息，如有未支付的交易，设置该状态，令牌过期，未登录（已登出）时可以设置该状态
	// 0x02-正常状态，启用，账户正常，卡片数据同步正常设置该状态
	// 0xFF-未知状态，不可使用，票卡未加载，获取不到相关状态
	HCEPREPAY_API short SetCardStatus(char newStatus);

	// 判断卡是否存在
	HCEPREPAY_API short CardExsit();

	// 更新本地token
	HCEPREPAY_API short UpdateLocalToken(const char * p_NewToken, short lenToken);
	// Token有效性判断
	HCEPREPAY_API short CheckToken(const char * p_time_now);
	// 获取Token剩余可用信息，剩余时间，剩余金额，剩余次数
	HCEPREPAY_API void GetTokenOddInfo(const char * p_time_now, short * oddmins, short * oddamount, short * minamount, char * oddcnt);
	// 获取服务器验证Token
	HCEPREPAY_API short GetTokenForServerVerify(char * pToken, short * len_token);
	// 临时接口，模拟申请令牌
	HCEPREPAY_API void tmpTokenApply(char * pToken, short * len_token);

	// 获取内部错误码
	HCEPREPAY_API short getInternelErrorCode();

#ifdef _WIN32
	HCEPREPAY_API void TestMac(_uint8 Alg, _uint8_p pKey, _uint8_p pData, _uint16 len, _uint8_p pMac, _uint8_p pivec);
#endif

#ifdef __cplusplus
};
#endif