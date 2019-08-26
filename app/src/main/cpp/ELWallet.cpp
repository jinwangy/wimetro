#include "ELWallet.h"
#include "ConstDef.h"

#ifndef _WIN32
#include <android/log.h>
#include <stdio.h>

#define LOG_TAG "log_from_jni"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#endif

ELWallet::ELWallet(void)
{
	m_pElement = NULL;
}

ELWallet::~ELWallet(void)
{
}

void ELWallet::Create(_uint8_p pAttr, _uint32& offset, PWALLET_ELEMENT pElement)
{
	m_pElement = (PWALLET_ELEMENT)(pAttr + offset);
	offset += sizeof(WALLET_ELEMENT);

	IF_EFFECT_COPY(m_pElement, pElement, sizeof(WALLET_ELEMENT));
}

void ELWallet::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PWALLET_ELEMENT)(pModule + offset);
	offset += sizeof(WALLET_ELEMENT);
}

void ELWallet::BindFiles(EFKey * pKey, ELInearfix * pDetail)
{
	m_adfKey = pKey;
	m_tradeDetail = pDetail;
}

_uint16 ELWallet::getEP(_uint32& balance)
{
	if (VAR_EQUAL(m_pElement, NULL))
		return INTER_FILE_NOT_EXSIST;

	balance = (_uint32)(m_pElement->EP_balance[0] << 24);
	balance += (_uint32)(m_pElement->EP_balance[1] << 16);
	balance += (_uint32)(m_pElement->EP_balance[2] << 8);
	balance += (_uint32)(m_pElement->EP_balance[3]);

	return INTER_SUCCED;
}

_uint16 ELWallet::getEPOnCnt(_uint16& on_cnt)
{
	if (VAR_EQUAL(m_pElement, NULL))
		return INTER_FILE_NOT_EXSIST;

	on_cnt = (_uint16)((m_pElement->EP_online[0] << 8) + m_pElement->EP_online[1]);
	
	return INTER_SUCCED;
}

_uint16 ELWallet::getEPOffCnt(_uint16& off_cnt)
{
	if (VAR_EQUAL(m_pElement, NULL))
		return INTER_FILE_NOT_EXSIST;

	off_cnt = (_uint16)((m_pElement->EP_offline[0] << 8) + m_pElement->EP_offline[1]);
	
	return INTER_SUCCED;
}

_uint16 ELWallet::Update(_uint32 balance, _uint16 off_cnt, _uint16 on_cnt, bool oldfail)
{
	_uint16 tmpcnt = 0;

	if (VAR_EQUAL(m_pElement, NULL))
		return INTER_FILE_NOT_EXSIST;

	tmpcnt = (_uint16)((m_pElement->EP_offline[0] << 8) + m_pElement->EP_offline[1]);
	if (tmpcnt > off_cnt && oldfail)
		return INTER_VERSION_OLD;

	m_pElement->EP_balance[0] = (_uint8)((balance >> 24) & 0xFF);
	m_pElement->EP_balance[1] = (_uint8)((balance >> 16) & 0xFF);
	m_pElement->EP_balance[2] = (_uint8)((balance >> 8) & 0xFF);
	m_pElement->EP_balance[3] = (_uint8)(balance & 0xFF);

	m_pElement->EP_offline[0] = (_uint8)((off_cnt >> 8) & 0xFF);
	m_pElement->EP_offline[1] = (_uint8)(off_cnt & 0xFF);

	m_pElement->EP_online[0] = (_uint8)((on_cnt >> 8) & 0xFF);
	m_pElement->EP_online[1] = (_uint8)(on_cnt & 0xFF);

	return INTER_SUCCED;
}

_uint16 ELWallet::decrease(_uint8_p data, bool flag)
{
	short i, t1, t2, ads;
	ads = (short)0;
	for(i = 3; i >= 0; i--)
	{
		t1 = (_uint16)(m_pElement->EP_balance[i] & 0xFF);

		t2 = (_uint16)(data[i] & 0xFF);
			
		if(t2 > t1) {
			ads = (_uint16)1;
		}
		t1 = (_uint16)(t1 - t2 - ads);

		if(flag) {
			m_pElement->EP_balance[i] = (_uint8)(t1 % 256);
		}
	}
		
	return ads;
}
_uint16 ELWallet::init4purchase(_uint8 gtp_type, _uint8_p data_in, _uint8_p data_out)
{
	_uint16 rc = SW_SUCCED;

	m_pElement->gtp_type = gtp_type;
		
	memcpy(pTemp42, data_in + 1, 4);

	memcpy(pTemp81, data_in + 5, 6);
	
	//???????????????????????
	//rc = decrease(pTemp42, false);
	//if(rc != (short)0)
	//	return (short)2;//???????2
		
	if (m_adfKey->getKey(CONSUME_KEY, 0x01, &verID, &algID, pTemp16, true) != 0)
	{
		rc = SW_E_INTERNAL;
		return rc;
	}
		
	m_rdGenerator.GenerateSecureRnd(4);
	m_rdGenerator.GetRndValue(pTemp32);

	memcpy(data_out, m_pElement->EP_balance, 4);

	memcpy(data_out + 4, m_pElement->EP_offline, 2);

	memset(data_out + 6, 0, 3);

	data_out[9] = verID;
	data_out[10] = algID;

	m_rdGenerator.GetRndValue(data_out + 11);

	memset(m_pElement->tac, 0, sizeof(m_pElement->tac));

	return SW_SUCCED;	
}

_uint16 ELWallet::purchase(_uint8_p data_in, _uint8_p data_out)
{
	_uint16 rc = SW_SUCCED;
	do 
	{
		memcpy(pTemp32 + 4, m_pElement->EP_offline, 2);
		memcpy(pTemp32 + 6, data_in + 2, 2);
		PenCipher::gen_SESPK(pTemp16, pTemp32, 8, pTemp82);

		//MAC1
		memcpy(pTemp32, pTemp42, 4);
		pTemp32[4] = m_pElement->gtp_type;
		memcpy(pTemp32 + 5, pTemp81, 6);
		memcpy(pTemp32 + 11, data_in + 4, 7);
		PenCipher::gmac4(ALG_DES, pTemp82, pTemp32, 18, pTemp41);
		if(memcmp(data_in + 11, pTemp41, 4) != 0)
		{
			rc = SW_WRONG_MAC;
			break;
		}

		double_byte_add(m_pElement->EP_offline);

		//?????????????????????
		//rc = decrease(pTemp42, true);
		//if(rc != (short)0)
		//	return (short)2;

		//MAC2
		memcpy(pTemp32, pTemp42, 4);

		PenCipher::gmac4(ALG_DES, pTemp82, pTemp32, 0x04, pTemp41);

		//TAC
		memcpy(pTemp32, pTemp42, 4);
		pTemp32[4] = m_pElement->gtp_type;
		memcpy(pTemp32 + 5, pTemp81, 6);
		memcpy(pTemp32 + 11, data_in, 4);
		memcpy(pTemp32 + 15, data_in + 4, 7);

		//TAC
		if (m_adfKey->getKey(TAC_KEY, 0x00, &verID, &algID, pTemp16, true) != 0)
		{
			rc = SW_E_INTERNAL;
			break;
		}

		memcpy(pTemp82, pTemp16 + 5, 8);
		PenCipher::xorblock8(pTemp82, pTemp16, 13);

		_uint8 temp[16] = {0};

		memcpy(temp, pTemp16 + 13, 8);

		//tac + mac2
		memcpy(data_out + 4, pTemp41, 4);
		PenCipher::gmac4(ALG_DES, temp, pTemp32, 22, data_out);

		memcpy(m_pElement->tac, data_out, 4);
		m_pElement->gtp_ready = true;

	} while (0);

	// Forbid next purchase without init
	m_rdGenerator.RevokeRnd();
	memset(pTemp16, 0, sizeof(pTemp16));

	return rc;
}

// ???????TAC??
bool ELWallet::can_gtp()
{
    return (m_pElement->gtp_ready != 0x00); 
}
    
_uint16 ELWallet::match_tn(_uint8 tt, _uint16 us)
{
	_uint16 ret = SW_CONDITIONS_NOT_SATISFIED;
	_uint16 temp = 0;

	do 
	{
		if (m_pElement->gtp_type != tt)
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}
		if (VAR_EQUAL(tt, 0x02))
		{
			temp = (_uint16)((m_pElement->EP_online[0] << 8) + m_pElement->EP_online[1]);
			if (VAR_EQUAL(temp, us))
				ret = SW_SUCCED;
			break;	
		}
		if (VAR_EQUAL(tt, 0x06) || VAR_EQUAL(tt, 0x09))
		{
			temp = (_uint16)((m_pElement->EP_offline[0] << 8) + m_pElement->EP_offline[1]);
			if (VAR_EQUAL(temp, us))
				ret = SW_SUCCED;
			break;	
		}

	} while (0);

	return ret;
}

_uint16 ELWallet::get_tac_last(_uint8 ttype, _uint8_p pcnt, _uint8_p pout)
{
	_uint16 ret = SW_E_INTERNAL;

	do 
	{
		if (!VAR_EQUAL(m_pElement->gtp_ready, 0))		break;

		if (!VAR_EQUAL(ttype, m_pElement->gtp_type))	break;

		if (VAR_EQUAL(ttype, 0x03))
		{
			if (ARRAY_EQUAL(pcnt, m_pElement->EP_online, 2))
			{
				memcpy(pout, m_pElement->tac, 4);
				memcpy(pout + 4, m_pElement->mac2, 4);
				m_pElement->gtp_ready = false;
				ret = SW_SUCCED;
			}
			break;
		}

		if (VAR_EQUAL(ttype, 0x06) || VAR_EQUAL(ttype, 0x09))
		{
			if (ARRAY_EQUAL(pcnt, m_pElement->EP_offline, 2))
			{
				memcpy(pout, m_pElement->tac, 4);
				memcpy(pout + 4, m_pElement->mac2, 4);
				m_pElement->gtp_ready = false;
				ret = SW_SUCCED;
			}
			break;
		}

	} while (0);

	return ret;
}

_uint16 ELWallet::getEP(_uint8_p data)
{
	memcpy(data, m_pElement->EP_balance, 4);
	return SW_SUCCED;
}

void ELWallet::double_byte_add(_uint8_p bytes, _uint16 ad/* = 1*/)
{
	_uint16 rc = (_uint16)((bytes[0] << 8) + bytes[1]);
	rc += ad;
	bytes[0] = (_uint8)((rc >> 8) & 0xFF);
	bytes[1] = (_uint8)(rc & 0xFF);
}
