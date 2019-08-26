// 应用文件

#pragma once

#include "Types.h"
#include "FileStruct.h"
#include "EFKey.h"
#include "ELWallet.h"
#include "EFRecord.h"
#include "EFBinary.h"


class ADF
{
public:
	ADF(void);
	~ADF(void);

	void Create(_uint8_p pAttr, _uint32& offset, PADF_ELEMENT pElement);

	void FromFileModule(_uint8_p pModule, _uint32& offset);

	void InsertBinary(EFBinary bin);

	bool IsTarget(_uint8_p p_name, _uint8 len);
	bool IsTarget(_uint16 SFI);

	EFKey * getKey();

	ELWallet * getWallet();

	// 复合文件
	EFRecord * getRecord();

	ELInearfix * getDetails();

	EFBinary * getBin(_uint8 id);

	vector<EFBinary>& getListBin();

	PADF_ELEMENT getElement() {
		return m_pElement;
	}

private:

	// 文件属性
	PADF_ELEMENT m_pElement;

	EFKey		m_keyAdf;

	ELWallet	m_walletAdf;

	// 复合文件
	EFRecord	m_ComplexAdf;

	// 交易明细（假定只有一个交易明细文件）
	ELInearfix	m_detailTrade;

	vector<EFBinary> m_binList;
};

