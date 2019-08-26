// 电子钱包

#pragma once

#include "Types.h"
#include "RandGenerator.h"
#include "PenCipher.h"
#include "ELInearfix.h"
#include "EFKey.h"
#include "Apdu.h"

class ELWallet
{
public:
	ELWallet(void);
	~ELWallet(void);

	void Create(_uint8_p pAttr, _uint32& offset, PWALLET_ELEMENT pElement);
	void FromFileModule(_uint8_p pModule, _uint32& offset);
	void BindFiles(EFKey * pKey, ELInearfix * pDetail);
	_uint16 getEP(_uint8_p data);
	_uint16 get_tac_last(_uint8 ttype, _uint8_p pcnt, _uint8_p pout);
	_uint16 init4purchase(_uint8 gtp_type, _uint8_p data_in, _uint8_p data_out);
	_uint16 purchase(_uint8_p data_in, _uint8_p data_out);

	_uint16 getEP(_uint32& balance);
	_uint16 getEPOnCnt(_uint16& on_cnt);
	_uint16 getEPOffCnt(_uint16& off_cnt);
	_uint16 Update(_uint32 balance, _uint16 off_cnt, _uint16 on_cnt, bool oldfail);

	PWALLET_ELEMENT getElement() 
	{
		return m_pElement;
	}

protected:
	bool can_gtp();
	_uint16 match_tn(_uint8 tt, _uint16 us);
	_uint16 decrease(_uint8_p data, bool flag);
	void double_byte_add(_uint8_p bytes, _uint16 ad = 1);

private:
	// 文件属性
	PWALLET_ELEMENT m_pElement;

	RandGenerator	m_rdGenerator;
	EFKey *			m_adfKey;
	ELInearfix *	m_tradeDetail;
	_uint8 pTemp41[4];
	_uint8 pTemp42[4];   
	_uint8 pTemp81[8]; 
	_uint8 pTemp82[8];
	_uint8 pTemp16[32];
	_uint8 pTemp32[32];

	_uint8 verID;        //密钥版本号
	_uint8 algID;        //算法标识符

};

