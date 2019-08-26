#include "ADF.h"
#include "ConstDef.h"

ADF::ADF(void)
{
	m_pElement = NULL;
}


ADF::~ADF(void)
{
}

void ADF::Create(_uint8_p pAttr, _uint32& offset, PADF_ELEMENT pElement)
{
	m_pElement = (PADF_ELEMENT)(pAttr + offset);
	offset += sizeof(ADF_ELEMENT);

	IF_EFFECT_COPY(m_pElement, pElement, sizeof(ADF_ELEMENT));
}

void ADF::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PADF_ELEMENT)(pModule + offset);
	offset += sizeof(ADF_ELEMENT);
}

void ADF::InsertBinary(EFBinary bin)
{
	m_binList.push_back(bin);
}

bool ADF::IsTarget(_uint8_p p_name, _uint8 len)
{
	bool ret = false;

	if (VAR_EQUAL(len, m_pElement->len_name) && ARRAY_EQUAL(p_name, m_pElement->adf_name, len))
	{
		ret = true;
	}

	return ret;
}

bool ADF::IsTarget(_uint16 SFI)
{
	return VAR_EQUAL(m_pElement->file_id, SFI);
}

EFKey * ADF::getKey()
{
	return &m_keyAdf;
}

ELWallet * ADF::getWallet()
{
	return &m_walletAdf;
}

// ¸´ºÏÎÄ¼þ
EFRecord * ADF::getRecord()
{
	return &m_ComplexAdf;
}

ELInearfix * ADF::getDetails()
{
	return &m_detailTrade;
}

EFBinary * ADF::getBin(_uint8 id)
{
	EFBinary * ret = NULL;
	for (_uint8 i=0;i<m_binList.size();i++)
	{
		if (m_binList[i].IsTarget(id))
		{
			ret = &m_binList[i];
			break;
		}
	}

	return ret;
}

vector<EFBinary>& ADF::getListBin()
{
	return m_binList;
}
