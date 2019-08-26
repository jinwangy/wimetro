#include "EFBinary.h"
#include "ConstDef.h"

EFBinary::EFBinary(void)
{
	m_pElement = NULL;
}


EFBinary::~EFBinary(void)
{
}

void EFBinary::Create(_uint8_p pAttr, _uint32& offset, PBIN_ELEMENT pElement, _uint8_p pData)
{
	m_pElement = (PBIN_ELEMENT)(pAttr + offset);
	offset += sizeof(BIN_ELEMENT);

	IF_EFFECT_COPY(m_pElement, pElement, sizeof(BIN_ELEMENT));

	m_pData = pAttr + offset;
	offset += m_pElement->len;

	IF_EFFECT_COPY(m_pData, pData, m_pElement->len);
}

void EFBinary::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PBIN_ELEMENT)(pModule + offset);
	offset += sizeof(BIN_ELEMENT);

	m_pData = pModule + offset;
	offset += m_pElement->len;
}

void EFBinary::Destory()
{
	m_pElement = NULL;
	m_pData = NULL;
}

void EFBinary::SetData(_uint8_p pData)
{
	memcpy(m_pData, pData, m_pElement->len);
}

_uint16 EFBinary::Read(_uint16 off, _uint16& len, _uint8_p buff)
{
	_uint16 ret = SW_SUCCED;

	do 
	{
		if (len + off > m_pElement->len)
		{
			ret = SW_E_INTERNAL;
			break;
		}
		
		if (VAR_EQUAL(len, 0))
		{
			len = m_pElement->len - off;
		}
		memcpy(buff, m_pData + off, len);

	} while (0);

	return ret;
}

_uint16 EFBinary::Write(_uint16 off, _uint16 dl, _uint8_p data, _uint8_p old)
{
	_uint16 ret = SW_SUCCED;

	if (dl + off > m_pElement->len)
	{
		ret = SW_E_INTERNAL;
	}
	else
	{
		if (!VAR_EQUAL(old, NULL))
		{
			memcpy(old, m_pData + off, m_pElement->len - off);
		}
		memcpy(m_pData + off, data, dl);
	}
	return ret;
}

bool EFBinary::IsTarget(_uint8 sfi)
{
	return VAR_EQUAL(m_pElement->sfi, sfi);
}

_uint16 EFBinary::check_acr(_uint8 status)
{
	_uint16 ret = SW_SUCCED;

	_uint8 mx = (_uint8)((m_pElement->acr >> 4) & 0x0F);
	_uint8 mn = (_uint8)(m_pElement->acr & 0xFF);

	if (status >= mn && status <= mx)
	{
		ret = SW_SECURITY_STATUS_NOT_SATISFIED;
	}

	return ret;
}

_uint16 EFBinary::check_acw(_uint8 status)
{
	_uint16 ret = SW_SUCCED;

	_uint8 mx = (_uint8)((m_pElement->acw >> 4) & 0x0F);
	_uint8 mn = (_uint8)(m_pElement->acw & 0xFF);

	if (status >= mn && status <= mx)
	{
		ret = SW_SECURITY_STATUS_NOT_SATISFIED;
	}

	return ret;
}
