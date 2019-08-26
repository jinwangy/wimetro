// 循环文件

#pragma once

#include "Types.h"
#include "FileStruct.h"

class ELInearfix
{
public:
	ELInearfix(void);
	~ELInearfix(void);

	void Create(_uint8_p pAttr, _uint32& offset, PLP_ELEMENT pElement, _uint8_p pData);
	void FromFileModule(_uint8_p pModule, _uint32& offset);
	void SetData(_uint8_p pData);
	_uint16 Read(_uint8 ptr, _uint8_p buff);
	_uint16 Write(LOOP_ITEM * p_item);

	_uint16 check_acr(_uint8 status);
	_uint16 check_acw(_uint8 status);

	static const _uint8 LOOP_COUNTER = 10;

	_uint8_p getDataPtr()
	{
		return m_pData;
	}

	PLP_ELEMENT getElement()
	{
		return m_pElement;
	}

private:

	// 文件属性
	PLP_ELEMENT m_pElement;

	// 文件在module中的指针
	_uint8_p m_pData;
};

