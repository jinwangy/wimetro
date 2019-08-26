#include "EFRecord.h"
#include "ConstDef.h"

EFRecord::EFRecord(void)
{
	m_pElement = NULL;
}


EFRecord::~EFRecord(void)
{
}

void EFRecord::Create(_uint8_p pAttr, _uint32& offset, PREC_ELEMENT pElement, _uint8_p pRecord, _uint16 lenRecord)
{
	m_pElement = (PREC_ELEMENT)(pAttr + offset);
	offset += sizeof(REC_ELEMENT);
	IF_EFFECT_COPY(m_pElement, pElement, sizeof(REC_ELEMENT));

	_uint8_p pData = pAttr + offset;

	IF_EFFECT_COPY(pData, pRecord, lenRecord);
	offset += m_pElement->ef_Size;

	m_recTab.push_back(make_pair(lenRecord, pData));
	m_pElement->cnt += 1;
}

void EFRecord::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PREC_ELEMENT)(pModule + offset);
	offset += sizeof(REC_ELEMENT);

	_uint16 lenRecord = 0;
	memcpy(&lenRecord, pModule + offset, sizeof(lenRecord));
	offset += sizeof(lenRecord);

	_uint8_p pRecord = pModule + offset;

	offset += m_pElement->ef_Size;

	m_recTab.push_back(make_pair(lenRecord, pRecord));

	//m_pElement->cnt += 1;直接PREC_ELEMENT已经有个数，不能自加
}

void EFRecord::AddRecord(_uint8_p pAttr, _uint32& offset, _uint8_p pRecord, _uint16 lenRecord)
{

	_uint8_p pData = pAttr + offset;

	memcpy(pData + 2, pRecord, lenRecord);
	offset += lenRecord;

	m_recTab.push_back(make_pair(lenRecord, pData));
	m_pElement->cnt += 1;
}

void EFRecord::FillData(_uint8 val, _uint8 num)
{
	_uint16 offset = 0;

	//for (_uint8 i=0;i<m_recTab.size(); i++)
	//{
	//	if (VAR_EQUAL(num, i + 1))
	//	{
	//		memset(m_pData + offset, val, m_recTab[i]);
	//	}
	//	offset += m_recTab[i];
	//}
}

_uint16 EFRecord::Read(_uint8 num, _uint8_p buff, _uint16& len)
{
	_uint16 ret = SW_RECORD_NOT_FOUND;
	_uint8_p pData = NULL;

	pData = (_uint8_p)m_recTab[num - 1].second;

	if (len > m_recTab[num - 1].first)
	{
		ret = SW_E_INTERNAL;
	}
	else
	{
		if (VAR_EQUAL(len, 0))
			len = m_recTab[num - 1].first;

		memcpy(buff, pData, len);
		ret = SW_SUCCED;
	}

	return ret;
}

_uint16 EFRecord::Write(_uint8 num, _uint8_p data, _uint16 len, _uint8_p old)
{
	_uint16 ret = SW_SUCCED;
	_uint8_p pData = NULL;

	pData = (_uint8_p)m_recTab[num - 1].second;

	if (len > m_recTab[num - 1].first)
	{
		ret = SW_E_INTERNAL;
	}
	else
	{
		if (VAR_EQUAL(len, 0))
			len = (_uint8)(m_recTab[num - 1].first);

		// save old file data
		if (!VAR_EQUAL(old, NULL))
		{
			memcpy(old, pData, len);
		}

		memcpy(pData, data, len);

		ret = SW_SUCCED;
	}

	return ret;
}

bool EFRecord::IsTarget(_uint8 sfi)
{
	return VAR_EQUAL(m_pElement->sfi, sfi);
}

_uint16 EFRecord::check_acr(_uint8 status)
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

_uint16 EFRecord::check_acw(_uint8 status)
{
	_uint16 ret = SW_SUCCED;

	_uint8 mx = (_uint8)((m_pElement->acw >> 4) & 0x0F);
	_uint8 mn = (_uint8)(m_pElement->acw & 0x0F);

	if (status >= mn && status <= mx)
	{
		ret = SW_SECURITY_STATUS_NOT_SATISFIED;
	}

	return ret;
}

_uint8_p EFRecord::getRecord(_uint8 id) const
{
	return (_uint8_p)m_recTab[id - 1].second;
}
