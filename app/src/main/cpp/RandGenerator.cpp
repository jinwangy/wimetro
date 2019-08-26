#include "RandGenerator.h"
#include <ctime>
#include <stdlib.h>

_uint8	RandGenerator::m_Rand[64] = {0};


RandGenerator::RandGenerator(void)
{
	m_bValid = false;
	m_Len = 0x04;
}

RandGenerator::~RandGenerator(void)
{
}

//------------------------------------------------
void RandGenerator::GenerateSecureRnd(_uint8 len)
{
	if(len >= 0x04 || len <= 0x40)
		m_Len = len;

	GetTargLenRandom(m_Rand, len);
	m_bValid = true;
}
//------------------------------------------------
_uint8 RandGenerator::GetRndValue(_uint8_p bf)
{
	_uint8 ret = 0x00;

	if (m_bValid)
	{
		memcpy(bf, m_Rand, m_Len);
		ret = m_Len;
	}

	return ret;
}
//------------------------------------------------
_uint8 RandGenerator::GetRndValue(_uint8_p bf, _uint8 boff)
{
	_uint8 ret = 0x00;

	if (m_bValid)
	{
		memcpy(bf + boff, m_Rand, m_Len);
		ret = m_Len;
	}

	return ret;
}
//------------------------------------------------
void RandGenerator::RevokeRnd()
{
	m_bValid = false;
}
//------------------------------------------------
bool RandGenerator::IsVaild()
{
	return m_bValid;
}
//------------------------------------------------
_uint8 RandGenerator::sizeOfRnd()
{
	return m_Len;
}

// ??????????????
void RandGenerator::GetTargLenRandom(_uint8_p pRand, _uint8 len)
{
	memset(pRand, 0, len);

	srand((_uint32)(time(0)));
	for (_uint8 i = 0; i < len; i += 4)
	{
		_uint32 rd = rand();
		memcpy(pRand + 4 * i, &rd, 4);
	}
}
