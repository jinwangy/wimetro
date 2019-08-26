#include "TokenLize.h"
#include "Base64.h"
#include "PenCipher.h"
#include "PenAES.h"
#include "ConstDef.h"
#include "InterfaceMgr.h"
#include "StorageMgr.h"
#include <ctime>
#include <time.h>

TokenLize g_tokenMgr;

TokenLize::TokenLize(void)
{
	m_tokenInit = false;
}

TokenLize::~TokenLize(void)
{
}

_uint16 TokenLize::setLocalToken(const char * p_token_base64, _uint32 len_token_base64)
{
	_uint16 ret				= INTER_SUCCED;
	_uint32 len_token		= 0;
	_uint8 token_rd[64]		= {0};
	_uint8 tac_key[16]		= {0};
	_uint8 mac_now[4]		= {0};

	do 
	{
		Base64::base64_decode(p_token_base64, len_token_base64, token_rd, &len_token);


		// ��ȡTAC��Կ
		ret = InterfaceMgr::getTACKey(tac_key);

		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;
		// ��������
		PenAES::InvCipher(tac_key, token_rd, len_token);

		// ��֤ǩ��
		PenCipher::gmac4(ALG_DES, tac_key, token_rd, (_uint16)(len_token - 4), mac_now);
		if (memcmp(mac_now, token_rd + len_token - 4, 4) != 0)
		{
			ret = INTER_TOKEN_SIGN;
			break;
		}

		// ��token���ݽ���
		memset(&m_tokenDecode, 0, sizeof(m_tokenDecode));
		m_tokenDecode.ver = token_rd[0];
		memcpy(m_tokenDecode.userID, token_rd + 1, 16);
		m_tokenDecode.domain_type = token_rd[17];

		memcpy(m_tokenDecode.purpose, token_rd + 18, 2);

		m_tokenDecode.tm_expire[0] = 0x20;
		memcpy(m_tokenDecode.tm_expire + 1, token_rd + 20, 5);

		m_tokenDecode.credit_cnt = token_rd[25];
		m_tokenDecode.credit_wallet = (_uint16)((token_rd[26] << 8) + token_rd[27]);
		m_tokenDecode.wallet_min = (_uint16)((token_rd[28] << 8) + token_rd[29]);

		memcpy(m_tokenDecode.ime, token_rd + 30, 20);
		if (!isCurrentPhoneToken(m_tokenDecode.ime))
		{
			ret = INTER_TOKEN_IME;
			memset(&m_tokenDecode, 0, sizeof(m_tokenDecode));
			break;
		}
		if (!VAR_EQUAL(m_tokenDecode.domain_type, 0x01))
		{
			ret = INTER_TOKEN_IME;
			memset(&m_tokenDecode, 0, sizeof(m_tokenDecode));
			break;
		}

		m_tokenInit = true;

	} while (0);

	return ret;
}

_uint16 TokenLize::getToken4Verify(char * p_token_base64, short * p_len_token_base64)
{
	_uint16 ret				= INTER_SUCCED;
	_uint32 len_token		= 64;
	_uint8 token_sd[64]		= {0};
	_uint8 tac_key[16]		= {0};
	_uint8 mac_now[4]		= {0};
	char ime[20]			= {0};

	do 
	{
		// ��ȡTAC��Կ
		ret = InterfaceMgr::getTACKey(tac_key);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		// ��֯token����
		token_sd[0] = m_tokenDecode.ver;
		memcpy(token_sd + 1, m_tokenDecode.userID, 16);
		token_sd[17] = 0x02;

		memcpy(token_sd + 18, m_tokenDecode.purpose, 2);

		memcpy(token_sd + 20, m_tokenDecode.tm_expire + 1, 5);

		token_sd[25] = m_tokenDecode.credit_cnt;
		token_sd[26] = (_uint8)((m_tokenDecode.credit_wallet >> 8) & 0xFF);
		token_sd[27] = (_uint8)(m_tokenDecode.credit_wallet & 0xFF);
		token_sd[28] = (_uint8)((m_tokenDecode.wallet_min >> 8) & 0xFF);
		token_sd[29] = (_uint8)(m_tokenDecode.wallet_min & 0xFF);

		getImeCodeRightPaddingSpace(ime);
		memcpy(token_sd + 30, ime, 20);

		// ǩ��
		PenCipher::gmac4(ALG_DES, tac_key, token_sd, (_uint16)(len_token - 4), mac_now);
		memcpy(token_sd + len_token - 4, mac_now, 4);

		// ��������
		PenAES::Cipher(tac_key, token_sd, len_token);

		Base64::base64_encode(token_sd, len_token, p_token_base64, &len_token);
		* p_len_token_base64 = (short)(len_token);

	} while (0);

	return ret;
}

_uint16 TokenLize::TokenValid(const char * pTimeNow)
{
	_uint8 tm_now[7]		= {0};
	_uint16 ret				= INTER_SUCCED;

	do 
	{
		////##############################################################################################################
		//LOGE("#########externel EXPIRE:%02X%02X%02X%02X%02X%02X%02X, NOW:%02X%02X%02X%02X%02X%02X%02X, CREADIT:%d, %d, %d",
		//	m_tokenDecode.tm_expire[0], m_tokenDecode.tm_expire[1], m_tokenDecode.tm_expire[2], m_tokenDecode.tm_expire[3], 
		//	m_tokenDecode.tm_expire[4], m_tokenDecode.tm_expire[5], m_tokenDecode.tm_expire[6], 
		//	pTimeNow, 
		//	m_tokenDecode.credit_cnt, m_tokenDecode.credit_wallet, m_tokenDecode.wallet_min);


		if (!m_tokenInit)
		{
			ret = INTER_TOKEN_UNINIT;
			break;
		}
		
		if (m_tokenDecode.credit_wallet < m_tokenDecode.wallet_min)
		{
			ret = INTER_TOKEN_WALLET;
			break;
		}
		
		if (m_tokenDecode.credit_cnt <= 0)
		{
			ret = INTER_TOKEN_CNT;
			break;
		}

		time_string2bcd(pTimeNow, tm_now);

		if (memcmp(m_tokenDecode.tm_expire, tm_now, 7) < 0)
		{
			ret = INTER_TOKEN_EXPIRE;
			break;
		}

	} while (0);

	return ret;
}


_uint16 TokenLize::TokenValid_internel(_uint8_p p_timetrade)
{
	_uint16 ret				= INTER_SUCCED;

	////######################################################################################
	//char sztime[15] = {0};
	//short tmp1, tmp2, tmp3;
	//char cnt; 
	//sprintf(sztime, "%02X%02X%02X%02X%02X%02X%02X", 
	//	p_timetrade[0], p_timetrade[1], p_timetrade[2], p_timetrade[3], p_timetrade[4], p_timetrade[5], p_timetrade[6]);
	//getTokenInf(sztime, &tmp1, &tmp2, &tmp3, &cnt);

	////######################################################################################

	do 
	{

		////##############################################################################################################
		//LOGE("#########internel EXPIRE:%02X%02X%02X%02X%02X%02X%02X, NOW:%02X%02X%02X%02X%02X%02X%02X, CREADIT:%d, %d, %d",
		//	m_tokenDecode.tm_expire[0], m_tokenDecode.tm_expire[1], m_tokenDecode.tm_expire[2], m_tokenDecode.tm_expire[3], 
		//	m_tokenDecode.tm_expire[4], m_tokenDecode.tm_expire[5], m_tokenDecode.tm_expire[6], 
		//	p_timetrade[0], p_timetrade[1], p_timetrade[2], p_timetrade[3], p_timetrade[4], p_timetrade[5], p_timetrade[6], 
		//	m_tokenDecode.credit_cnt, m_tokenDecode.credit_wallet, m_tokenDecode.wallet_min);

		if (!m_tokenInit)
		{
			ret = INTER_TOKEN_UNINIT;
			break;
		}

		if (m_tokenDecode.credit_wallet < m_tokenDecode.wallet_min)
		{
			ret = INTER_TOKEN_WALLET;
			break;
		}

		if (m_tokenDecode.credit_cnt <= 0)
		{
			ret = INTER_TOKEN_CNT;
			break;
		}

		if (memcmp(m_tokenDecode.tm_expire, p_timetrade, 6) < 0)
		{
			ret = INTER_TOKEN_EXPIRE;
			break;
		}

	} while (0);

	return ret;
}

void TokenLize::getTokenInf(const char * pTimeNow, short * oddmins, short *  oddamount, short * minamount, char * oddcnt)
{
	_uint8 tm_now[7]		= {0};
	time_t exptime			= 0;
	time_t curtime			= 0;

	if (!m_tokenInit)
	{
		* oddmins = 0;
		* oddamount = 0;
		* oddcnt = 0;
		return;
	}

	*oddamount = m_tokenDecode.credit_wallet;
	*oddcnt = m_tokenDecode.credit_cnt;
	*minamount = m_tokenDecode.wallet_min;

	time_string2bcd(pTimeNow, tm_now);
	if (memcmp(m_tokenDecode.tm_expire, tm_now, 7) < 0)
	{
		*oddmins = 0;
	}
	else
	{
		exptime = time_bcd2time_t(m_tokenDecode.tm_expire);
		curtime = time_bcd2time_t(tm_now);
		*oddmins = (short)(difftime(exptime, curtime) / 60);
	}
}

_uint16 TokenLize::updateToken(short amount, _uint8_p p_time_trade)
{
	_uint16 ret				= INTER_SUCCED;

	do 
	{
		ret = TokenValid_internel(p_time_trade);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		if (m_tokenDecode.credit_wallet < amount)
			m_tokenDecode.credit_wallet = 0;
		else
			m_tokenDecode.credit_wallet -= amount;

		m_tokenDecode.credit_cnt --;

	} while (0);

	return ret;
}

void TokenLize::reset_token()
{
	memset(&m_tokenDecode, 0, sizeof(m_tokenDecode));
	m_tokenInit = false;
}

_uint16 TokenLize::simulate_tokenApply(char * pToken)
{
	_uint16 ret				= INTER_SUCCED;
	_uint32 len_token		= 64;
	_uint8 token_sd[64]		= {0};
	_uint8 tac_key[16]		= {0};
	_uint8 mac_now[4]		= {0};
	_uint8 tk_expire[7]		= {0};
	char ime[20] 			= {0};

	do 
	{
		// ��ȡTAC��Կ
		ret = InterfaceMgr::getTACKey(tac_key);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		// ��֯token����
		token_sd[0] = 0x01;
		memcpy(token_sd + 1, "\x11\x22\x33\x44\x55\x66\x77\x88\x99\xaa\xbb\xcc\xdd\xee\xff\x00", 16);
		token_sd[17] = 0x01;

		memcpy(token_sd + 18, "\x00\x01", 2);

		time_localadd2bcd(10, tk_expire);
		memcpy(token_sd + 20, tk_expire + 1, 5);

		token_sd[25] = 2;
		token_sd[26] = (_uint8)((700 >> 8) & 0xFF);
		token_sd[27] = (_uint8)(700 & 0xFF);
		token_sd[28] = (_uint8)((100 >> 8) & 0xFF);
		token_sd[29] = (_uint8)(100 & 0xFF);

		getImeCodeRightPaddingSpace(ime);
		memcpy(token_sd + 30, ime, 20);

		// ǩ��
		PenCipher::gmac4(ALG_DES, tac_key, token_sd, (_uint16)(len_token - 4), mac_now);
		memcpy(token_sd + len_token - 4, mac_now, 4);

		// ��������
		PenAES::Cipher(tac_key, token_sd, len_token);

		Base64::base64_encode(token_sd, len_token, pToken, &len_token);

	} while (0);

	return ret;
}

bool TokenLize::isCurrentPhoneToken(char * p_ime)
{
	char local_ime[20]	= {0};

	StorageMgr::getLocalIme((_uint8_p)(local_ime));

	return (strncmp(p_ime, local_ime, strlen(local_ime)) == 0);
}

void TokenLize::time_local2bcd(PTMLOCAL ptmsrc, _uint8_p p_tmbcd)
{
	_uint8 tmp =  (_uint8)((ptmsrc->tm_year + 1900) / 100);
	p_tmbcd[0] = (tmp % 0x0a) + (tmp / 0x0a * 0x10);
	tmp =  (_uint8)((ptmsrc->tm_year + 1900) % 100);
	p_tmbcd[1] = (tmp % 0x0a) + (tmp / 0x0a * 0x10);

	tmp = ptmsrc->tm_mon + 1;
	p_tmbcd[2] = (tmp % 0x0a) + (tmp / 0x0a * 0x10);
	p_tmbcd[3] = (ptmsrc->tm_mday % 0x0a) + (ptmsrc->tm_mday / 0x0a * 0x10);
	p_tmbcd[4] = (ptmsrc->tm_hour % 0x0a) + (ptmsrc->tm_hour / 0x0a * 0x10);
	p_tmbcd[5] = (ptmsrc->tm_min % 0x0a) + (ptmsrc->tm_min / 0x0a * 0x10);
	p_tmbcd[6] = (ptmsrc->tm_sec % 0x0a) + (ptmsrc->tm_sec / 0x0a * 0x10);
}

void TokenLize::time_bcd2local(_uint8_p p_tmbcd, PTMLOCAL ptmsrc)
{
	memset(ptmsrc, 0, sizeof(TMLOCAL));

	ptmsrc->tm_year = ((p_tmbcd[0] / 0x10 * 0x0a) + (p_tmbcd[0] % 0x10)) * 100;
	ptmsrc->tm_year += (p_tmbcd[1] / 0x10 * 0x0a) + (p_tmbcd[1] % 0x10);
	ptmsrc->tm_year -= 1900;

	ptmsrc->tm_mon = (p_tmbcd[2] / 0x10 * 0x0a) + (p_tmbcd[2] % 0x10) - 1;

	ptmsrc->tm_mday = (p_tmbcd[3] / 0x10 * 0x0a) + (p_tmbcd[3] % 0x10);

	ptmsrc->tm_hour = (p_tmbcd[4] / 0x10 * 0x0a) + (p_tmbcd[4] % 0x10);

	ptmsrc->tm_min = (p_tmbcd[5] / 0x10 * 0x0a) + (p_tmbcd[5] % 0x10);

	ptmsrc->tm_sec = (p_tmbcd[6] / 0x10 * 0x0a) + (p_tmbcd[6] % 0x10);
}

void TokenLize::time_string2bcd(const char * p_string_time, _uint8_p p_bcd_time)
{
	for (_uint32 i=0;i<strlen(p_string_time);i+=2)
	{
		p_bcd_time[i/2] = (_uint8)((p_string_time[i] - 0x30) * 0x10 + (p_string_time[i + 1] - 0x30));
	}
}

void TokenLize::time_localadd2bcd(short mins_add, _uint8_p p_tmbcd)
{
	time_t rawtime			= 0;
	PTMLOCAL timeinfo		= NULL;

	time(&rawtime);
	rawtime += mins_add * 60;
	timeinfo = localtime(&rawtime);
	time_local2bcd(timeinfo, p_tmbcd);
}


time_t TokenLize::time_bcd2time_t(_uint8_p p_tmbcd)
{
	TMLOCAL tmlocal		= {0};

	time_bcd2local(p_tmbcd, &tmlocal);
	return mktime(&tmlocal);
}

void TokenLize::getImeCodeRightPaddingSpace(char * pPaddingIme)
{
	char szTemp[32] = {0};
	StorageMgr::getLocalIme((_uint8_p)(pPaddingIme));
	sprintf(szTemp, "%-20s", pPaddingIme);
	memcpy(pPaddingIme, szTemp, 20);
}
