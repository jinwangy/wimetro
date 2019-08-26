#pragma once

#include "Types.h"
#include <ctime>

typedef struct {
	_uint8		ver;					// 令牌版本
	_uint8		userID[16];				// uuid每次登录由系统分配
	_uint8		domain_type;			// 类型（0x01-服务器下发， 0x02-服务器验签）
	_uint8		purpose[2];				// 作用类型，类似于授权登录
	_uint8		tm_expire[7];			// 有效期截至时间
	_uint8		credit_cnt;				// 脱机次数信用
	_uint16		credit_wallet;			// 脱机信用钱包
	_uint16		wallet_min;				// 信用钱包下限
	char		ime[20];				// ime码
	_uint8		rfu[10];				// 预留
	_uint8		mac[4];					// MAC码

} TOKEN_FAC, *PTOKEN_FAC;

typedef struct tm TMLOCAL, * PTMLOCAL;

class TokenLize
{
public:
	TokenLize(void);
	~TokenLize(void);

	// APP申请到令牌后设置到本地
	_uint16 setLocalToken(const char * p_toekn_base64, _uint32 len_token_base64);

	// 获取本地需要上传到服务器验证的token
	_uint16 getToken4Verify(char * p_toekn_base64, short * p_len_token_base64);

	// 判断本地token是否有效
	_uint16 TokenValid(const char * pTimeNow);
	_uint16 TokenValid_internel(_uint8_p p_timetrade);

	// 获取token剩余信用信息
	void getTokenInf(const char * pTimeNow, short * oddmins, short *  oddamount, short * minamount, char * oddcnt);

	// 更新token
	_uint16 updateToken(short amount, _uint8_p p_time_trade);

	// 重置token
	void reset_token();

	// 模拟申请令牌
	_uint16 simulate_tokenApply(char * pToken);

private:
	bool m_tokenInit;

	// 未加密的token
	TOKEN_FAC m_tokenDecode;

	// token中的ime是否当前手机的ime
	bool isCurrentPhoneToken(char * p_ime);

	// 本地时间与bcd时间互转
	void time_local2bcd(PTMLOCAL ptmsrc, _uint8_p p_tmbcd);
	void time_bcd2local(_uint8_p p_tmbcd, PTMLOCAL ptmsrc);
	time_t time_bcd2time_t(_uint8_p p_tmbcd);
	void time_string2bcd(const char * p_string_time, _uint8_p p_bcd_time);

	// 当前时间加上一定时间后转换成BCD码
	void time_localadd2bcd(short mins_add, _uint8_p p_tmbcd);

	// 获取两个时间的差值（分钟）
	_uint32 times_deffer_in_min(PTMLOCAL ptmnew, PTMLOCAL ptmold);

	void getImeCodeRightPaddingSpace(char * pPaddingIme);
};

extern TokenLize g_tokenMgr;