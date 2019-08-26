// 随机数发生器

#pragma once


#include "Types.h"

class RandGenerator
{
public:
	RandGenerator(void);
	~RandGenerator(void);

	void GenerateSecureRnd(_uint8 len);

	_uint8 GetRndValue(_uint8_p bf);
	
	_uint8 GetRndValue(_uint8_p bf, _uint8 boff);
	
	void RevokeRnd();
	
	bool IsVaild();

	_uint8 sizeOfRnd();

private:
	static _uint8	m_Rand[64];
	bool	m_bValid;
	_uint8	m_Len;

	// 根据长度获取随机数
	void GetTargLenRandom(_uint8_p pRand, _uint8 len);
};

