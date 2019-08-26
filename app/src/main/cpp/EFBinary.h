// 二进制文件

#pragma once

#include "Types.h"
#include "FileStruct.h"

class EFBinary
{
public:
	EFBinary(void);
	~EFBinary(void);

	void Create(_uint8_p pAttr, _uint32& offset, PBIN_ELEMENT pElement, _uint8_p pData);
	void FromFileModule(_uint8_p pModule, _uint32& offset);
	void Destory();
	void SetData(_uint8_p pData);

	_uint16 Read(_uint16 off, _uint16& len, _uint8_p buff);
	_uint16 Write(_uint16 off, _uint16 dl, _uint8_p data, _uint8_p old);

	_uint16 check_acr(_uint8 status);
	_uint16 check_acw(_uint8 status);

	bool IsTarget(_uint8 sfi);

	PBIN_ELEMENT getElement()
	{
		return m_pElement;
	}

	_uint8_p getDataPtr()
	{
		return m_pData;
	}

private:
	// 文件属性
	PBIN_ELEMENT m_pElement;

	// 文件在module中的指针
	_uint8_p m_pData;
};

