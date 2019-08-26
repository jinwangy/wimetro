#pragma once

#include "Types.h"
#include <ctime>

typedef struct {
	_uint8		ver;					// ���ư汾
	_uint8		userID[16];				// uuidÿ�ε�¼��ϵͳ����
	_uint8		domain_type;			// ���ͣ�0x01-�������·��� 0x02-��������ǩ��
	_uint8		purpose[2];				// �������ͣ���������Ȩ��¼
	_uint8		tm_expire[7];			// ��Ч�ڽ���ʱ��
	_uint8		credit_cnt;				// �ѻ���������
	_uint16		credit_wallet;			// �ѻ�����Ǯ��
	_uint16		wallet_min;				// ����Ǯ������
	char		ime[20];				// ime��
	_uint8		rfu[10];				// Ԥ��
	_uint8		mac[4];					// MAC��

} TOKEN_FAC, *PTOKEN_FAC;

typedef struct tm TMLOCAL, * PTMLOCAL;

class TokenLize
{
public:
	TokenLize(void);
	~TokenLize(void);

	// APP���뵽���ƺ����õ�����
	_uint16 setLocalToken(const char * p_toekn_base64, _uint32 len_token_base64);

	// ��ȡ������Ҫ�ϴ�����������֤��token
	_uint16 getToken4Verify(char * p_toekn_base64, short * p_len_token_base64);

	// �жϱ���token�Ƿ���Ч
	_uint16 TokenValid(const char * pTimeNow);
	_uint16 TokenValid_internel(_uint8_p p_timetrade);

	// ��ȡtokenʣ��������Ϣ
	void getTokenInf(const char * pTimeNow, short * oddmins, short *  oddamount, short * minamount, char * oddcnt);

	// ����token
	_uint16 updateToken(short amount, _uint8_p p_time_trade);

	// ����token
	void reset_token();

	// ģ����������
	_uint16 simulate_tokenApply(char * pToken);

private:
	bool m_tokenInit;

	// δ���ܵ�token
	TOKEN_FAC m_tokenDecode;

	// token�е�ime�Ƿ�ǰ�ֻ���ime
	bool isCurrentPhoneToken(char * p_ime);

	// ����ʱ����bcdʱ�以ת
	void time_local2bcd(PTMLOCAL ptmsrc, _uint8_p p_tmbcd);
	void time_bcd2local(_uint8_p p_tmbcd, PTMLOCAL ptmsrc);
	time_t time_bcd2time_t(_uint8_p p_tmbcd);
	void time_string2bcd(const char * p_string_time, _uint8_p p_bcd_time);

	// ��ǰʱ�����һ��ʱ���ת����BCD��
	void time_localadd2bcd(short mins_add, _uint8_p p_tmbcd);

	// ��ȡ����ʱ��Ĳ�ֵ�����ӣ�
	_uint32 times_deffer_in_min(PTMLOCAL ptmnew, PTMLOCAL ptmold);

	void getImeCodeRightPaddingSpace(char * pPaddingIme);
};

extern TokenLize g_tokenMgr;