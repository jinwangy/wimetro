#include "ELInearfix.h"
#include "ConstDef.h"

ELInearfix::ELInearfix(void)
{
	m_pElement = NULL;
}


ELInearfix::~ELInearfix(void)
{
}
void ELInearfix::Create(_uint8_p pAttr, _uint32& offset, PLP_ELEMENT pElement, _uint8_p pData)
{
	m_pElement = (PLP_ELEMENT)(pAttr + offset);
	offset += sizeof(LP_ELEMENT);

	IF_EFFECT_COPY(m_pElement, pElement, sizeof(LP_ELEMENT));

	m_pData = pAttr + offset;
	offset += m_pElement->ef_Size;

	//IF_EFFECT_COPY(m_pData, pData, m_pElement->ef_Size);
}

void ELInearfix::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PLP_ELEMENT)(pModule + offset);
	offset += sizeof(LP_ELEMENT);

	m_pData = pModule + offset;
	offset += m_pElement->ef_Size;
}

void ELInearfix::SetData(_uint8_p pData)
{
	memcpy(m_pData + sizeof(LP_ELEMENT), pData, m_pElement->ef_Size);
}

_uint16 ELInearfix::Read(_uint8 ptr, _uint8_p buff)
{
	_uint16 ret = SW_SUCCED;
	_uint8 fixSize = (_uint8)sizeof(LOOP_ITEM);

	if (ptr > LOOP_COUNTER)
	{
		ret = SW_E_INTERNAL;
	}
	else
	{
		ptr = fixSize * (ptr - 1);
		memcpy(buff, m_pData + ptr, fixSize);
	}

	return ret;
}

_uint16 ELInearfix::Write(LOOP_ITEM * p_item)
{
	_uint16 ret = SW_SUCCED;
	_uint8 fixSize = (_uint8)sizeof(LOOP_ITEM);

	m_pElement->ptr = (m_pElement->ptr + 1) % (LOOP_COUNTER - 1);

	memcpy(m_pData + m_pElement->ptr * fixSize, p_item, fixSize);

	return ret;
}

_uint16 ELInearfix::check_acr(_uint8 status)
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

_uint16 ELInearfix::check_acw(_uint8 status)
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
