// 记录文件

#pragma once

#include "Types.h"
#include "FileStruct.h"

typedef pair<_uint16, _uint8_p>	RECINF;

class EFRecord
{
public:
	EFRecord(void);
	~EFRecord(void);

	void Create(_uint8_p pAttr, _uint32& offset, PREC_ELEMENT pElement, _uint8_p pRecord, _uint16 lenRecord);
	void FromFileModule(_uint8_p pModule, _uint32& offset);
	void AddRecord(_uint8_p pAttr, _uint32& offset, _uint8_p pRecord, _uint16 lenRecord);
	void FillData(_uint8 val, _uint8 num);
	_uint16 Read(_uint8 num, _uint8_p buff, _uint16& len);
	_uint16 Write(_uint8 num, _uint8_p data, _uint16 len, _uint8_p old);

	bool IsTarget(_uint8 sfi);

	_uint16 check_acr(_uint8 status);
	_uint16 check_acw(_uint8 status);

	vector<RECINF>& getRecordList()
	{
		return m_recTab;
	}

	PREC_ELEMENT getElement()
	{
		return m_pElement;
	}

	_uint8_p getRecord(_uint8 id) const;


private:
	// 文件属性
	PREC_ELEMENT m_pElement;

	vector<RECINF> m_recTab;
};

