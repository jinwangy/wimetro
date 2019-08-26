#include "MF.h"
#include "ConstDef.h"
#include "StorageMgr.h"
#include "InterfaceMgr.h"
#include "TokenLize.h"

MF::MF(void)
{
	m_pElement = NULL;
	CancelTransaction();
}

MF::~MF(void)
{
}

void MF::Create(_uint8_p ptr, _uint32& offset, PMF_ELEMENT pAttr)
{
	m_pElement = (PMF_ELEMENT)(ptr + offset);
	offset += sizeof(MF_ELEMENT);
	IF_EFFECT_COPY(m_pElement, pAttr, sizeof(MF_ELEMENT));
}

void MF::FromFileModule(_uint8_p pModule, _uint32& offset)
{
	m_pElement = (PMF_ELEMENT)(pModule + offset);
	offset += sizeof(MF_ELEMENT);
}

void MF::Destory()
{
	m_binList.clear();
	m_adfList.clear();
	m_pElement = NULL;
}

void MF::InsertBinary(EFBinary bin)
{
	m_binList.push_back(bin);
}

void MF::InsertAdf(ADF adf)
{
	m_adfList.push_back(adf);
}

vector<ADF>& MF::getListADF()
{
	return m_adfList;
}

vector<EFBinary>& MF::getListBin()
{
	return m_binList;
}

void MF::WalletBindKey(_uint16 SFI)
{
	for (_uint8 i=0;i<m_adfList.size();i++)
	{
		if (m_adfList[i].IsTarget(SFI))
		{
			m_adfList[i].getWallet()->BindFiles(m_adfList[i].getKey(), m_adfList[i].getDetails());
		}
	}
}

bool MF::IsTarget(_uint8_p pname, _uint8 lenName)
{
	bool ret = false;

	do 
	{
		if (VAR_EQUAL(m_pElement, NULL))
			break;

		if (!VAR_EQUAL(lenName, m_pElement->len_name))
			break;
		
		if (!ARRAY_EQUAL(m_pElement->mf_name, pname, lenName))
			break;
		
		ret = true;

	} while (0);

	return ret;
}

ADF * MF::getSelectedADF()
{
	return m_pSelectedAdf;
}

ADF * MF::getADF(_uint16 id)
{
	ADF * ret = NULL;
	for (_uint16 i=0;i<m_adfList.size();i++)
	{
		if (m_adfList[i].IsTarget(id))
		{
			ret = &m_adfList[i];
			break;
		}
	}

	return ret;
}

EFBinary * MF::getBin(_uint8 efid)
{
	EFBinary * ret = NULL;

	for (_uint8 i=0;i<m_binList.size();i++)
	{
		if (m_binList[i].IsTarget(efid))
		{
			ret = &m_binList[i];
			break;
		}
	}

	return ret;
}

_uint16 MF::GetChallenge_IN(_uint8 len, _uint8_p p_rd)
{
	RandGenerator rdGener;
	rdGener.GenerateSecureRnd(len);
	rdGener.GetRndValue(p_rd);

	return SW_SUCCED;
}

_uint16 MF::Select_IN(_uint8_p pname, _uint8 len)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		m_pSelectedAdf = NULL;

		if (IsTarget(pname, len))
		{
			ret = SW_SUCCED;
			break;
		}

		for (_uint8 i= 0; i < m_adfList.size(); i++)
		{
			if (m_adfList[i].IsTarget(pname, len))
			{
				ret = SW_SUCCED;
				m_pSelectedAdf = &m_adfList[i];
				break;
			}
		}

	} while (0);

	return ret;
}

_uint16 MF::SelectEx_IN(_uint16 SFI)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		m_pSelectedAdf = NULL;

		if (VAR_EQUAL(m_pElement->file_id, SFI))
		{
			ret = SW_SUCCED;
			break;
		}

		for (_uint8 i= 0; i < m_adfList.size(); i++)
		{
			if (m_adfList[i].IsTarget(SFI))
			{
				ret = SW_SUCCED;
				m_pSelectedAdf = &m_adfList[i];
				break;
			}
		}

	} while (0);

	return ret;
}

_uint16 MF::ReadBin_IN(_uint8 sfi, _uint16 offset, _uint16& len, _uint8_p p_inf)
{
	_uint16 ret = SW_SUCCED;
	EFBinary * ptmpBin = NULL;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (m_pSelectedAdf != NULL) {
			ptmpBin = m_pSelectedAdf->getBin(sfi);
		}
		else {
			ptmpBin = getBin(sfi);
		}

		if (VAR_EQUAL(ptmpBin, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = ptmpBin->check_acr(0);
		if (!VAR_EQUAL(ret, SW_SUCCED))
			break;

		ret = ptmpBin->Read(offset, len, p_inf);

	} while (0);

	return ret;
}

_uint16 MF::UpdateBin_IN(_uint8 sfi, _uint16 offset, _uint16 size, _uint8_p p_inf)
{
	_uint16 ret			= SW_SUCCED;
	EFBinary * ptmpBin	= NULL;
	_uint8 tmp[256]		= {0};

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (m_pSelectedAdf != NULL) {
			ptmpBin = m_pSelectedAdf->getBin(sfi);
		}
		else {
			ptmpBin = getBin(sfi);
		}


		if (VAR_EQUAL(ptmpBin, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = ptmpBin->check_acw(0);
		if (!VAR_EQUAL(ret, SW_SUCCED))
			break;

		ret = ptmpBin->Write(offset, size, p_inf, tmp);
		if (!VAR_EQUAL(ret, SW_SUCCED))
		{
			break;
		}

		if (StorageMgr::Serialize(this) != INTER_SUCCED)
		{
			ptmpBin->Write(offset, size, tmp, NULL);
			ret = SW_E_INTERNAL;
		}

	} while (0);

	return ret;
}

_uint16 MF::ReadRec_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint16& len, bool internel)
{
	_uint16 ret			= SW_SUCCED;
	EFRecord * ptmpRec	= NULL;
	ADF * pselADF		= m_pSelectedAdf;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (internel && !m_adfList.empty())
			pselADF = &m_adfList[0];

		if (VAR_EQUAL(pselADF, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ptmpRec = pselADF->getRecord();
		if (!ptmpRec->IsTarget(sfi))
		{
			ret = SW_FILE_NOT_FOUND;
			break;
		}
		
		ret = ptmpRec->check_acr(0);
		if (!VAR_EQUAL(ret, SW_SUCCED))
			break;

		ret = ptmpRec->Read(id, p_inf, len);

	} while (0);

	return ret;
}

_uint16 MF::UpdateRec_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint8 len, bool bMac, _uint8_p pApduInit, _uint16 lenApdu)
{
	_uint16 ret			= SW_SUCCED;
	_uint8 status		= 0;
	EFRecord * ptmpRec	= NULL;
	_uint8 tmp[128]		= {0};

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ptmpRec = m_pSelectedAdf->getRecord();
		if (!ptmpRec->IsTarget(sfi))
		{
			ret = SW_FILE_NOT_FOUND;
			break;
		}

		if (bMac)
		{
			status = ChannelSecurity(pApduInit, lenApdu, APP_MAINT_KEY, 0x01);

			ret = ptmpRec->check_acw(status);
			if (!VAR_EQUAL(ret, SW_SUCCED))
				break;

			// ��ȥMAC�ĳ���
			len -= 4;
		}

		// �������ѱ����ֳ���ֱ�ӷ���
		if (TransactionPush(sfi, id, p_inf, len, ret))
		{
			break;
		}

		ret = ptmpRec->Write(id, p_inf, len, tmp);
		if (!VAR_EQUAL(ret, SW_SUCCED))
		{
			break;
		}

		StorageMgr::setComplex((PCOMPLEX_TRADE)p_inf);
		if (StorageMgr::Serialize(this) != INTER_SUCCED)
		{
			ptmpRec->Write(id, tmp, len, NULL);
			ret = SW_E_INTERNAL;
			g_internelErr = INTER_FILE_WRITE;
		}

	} while (0);

	return ret;
}

_uint16 MF::UpdateRecEx_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint8 len)
{
	_uint16 ret = 0x6A83;
	_uint8 status = 0;
	EFRecord * ptmpRec = NULL;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ptmpRec = m_pSelectedAdf->getRecord();
		if (!ptmpRec->IsTarget(sfi))
		{
			ret = SW_FILE_NOT_FOUND;
			break;
		}

	} while (0);

	return ret;
}

_uint16 MF::GetBalance_IN(_uint8_p p_rd)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = m_pSelectedAdf->getWallet()->getEP(p_rd);

	} while (0);

	return ret;
}

_uint16 MF::InitPurchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = m_pSelectedAdf->getWallet()->init4purchase(0x06, p_apdu_data, p_rsp);

		if (!VAR_EQUAL(ret, SW_SUCCED))
			m_pSelectedAdf = NULL;

	} while (0);

	return ret;
}

_uint16 MF::InitComplexPurchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = m_pSelectedAdf->getWallet()->init4purchase(0x09, p_apdu_data, p_rsp);
		if (!VAR_EQUAL(ret, SW_SUCCED))
		{
			m_pSelectedAdf = NULL;
			break;
		}

		BeginTransaction();

	} while (0);

	return ret;
}

_uint16 MF::Purchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp, PACC_TRADE paccTrade, _uint16_p p_tkstauts)
{
	_uint16 ret			= 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		g_internelErr = g_tokenMgr.TokenValid_internel(p_apdu_data + 4);
		if (!VAR_EQUAL(g_internelErr, INTER_SUCCED))
		{
			ret = SW_E_INTERNAL;
			*p_tkstauts = g_internelErr;
			break;
		}

		ret = m_pSelectedAdf->getWallet()->purchase(p_apdu_data, p_rsp);

		if (VAR_EQUAL(ret, SW_SUCCED))
		{
			EndTransaction(p_apdu_data, p_rsp, p_apdu_data + 4, paccTrade, p_tkstauts);

			if (StorageMgr::Serialize(this) != INTER_SUCCED)
			{
				CancelTransaction();
				ret = SW_E_INTERNAL;
				g_internelErr = INTER_FILE_WRITE;
			}
		}
		else
		{
			m_pSelectedAdf = NULL;
			StopTransaction();
		}

	} while (0);

	return ret;
}

_uint16 MF::GetTansCode_IN(_uint8 ttype, _uint8_p p_apdu_data, _uint8_p p_rsp)
{
	_uint16 ret = 0x6A83;

	do 
	{
		if (!CheckCardStatus())
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			g_internelErr = INTER_CARD_STATUS;
			break;
		}

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			ret = SW_CONDITIONS_NOT_SATISFIED;
			break;
		}

		ret = m_pSelectedAdf->getWallet()->get_tac_last(ttype, p_apdu_data, p_rsp);

	} while (0);

	return ret;
}

_uint8 MF::ChannelSecurity(_uint8_p pApduInit, _uint16 lenApdu, _uint8 keycla, _uint8 keyId)
{
	_uint8 initVec[8]	= {0};
	_uint8 keyMac[16]	= {0};
	_uint16 len			= lenApdu - 4;
	_uint8 Mac[8]		= {0};
	_uint8 ret			= 0;
	_uint8 alg			= 0;
	_uint8 ver			= 0;

	RandGenerator rd;

	do 
	{
		rd.GetRndValue(initVec);

		if (VAR_EQUAL(m_pSelectedAdf, NULL))
		{
			break;
		}

		EFKey * pKey = m_pSelectedAdf->getKey();
		if (VAR_EQUAL(pKey, NULL))
		{
			break;
		}

		pKey->getKey(keycla, keyId, &ver, &alg, keyMac, true);

		PenCipher::gmac4(ALG_DES, keyMac, pApduInit, len, Mac, initVec);
		if (memcmp(pApduInit + len, Mac, 4) == 0)
		{
			ret = 0x01;
		}

	} while (0);

	return 0x00;
}

bool MF::CheckCardStatus()
{
	if (!VAR_EQUAL(m_pElement, NULL))
	{
		if (VAR_EQUAL(m_pElement->extra.cardStatus, OFFLINE_CARD_NORMAL))
		{
			return true;
		}
	}
	return false;
}

void MF::BeginTransaction()
{
	m_transList.clear();
	m_Transaction = true;
}

_uint16 MF::EndTransaction(_uint8_p p_samid, _uint8_p p_tac, _uint8_p ptime, PACC_TRADE paccTrade, _uint16_p p_tkstauts)
{
	_uint16	ret			= SW_E_INTERNAL;
	EFRecord * ptmpRec	= NULL;

	// ִ������
	if (m_Transaction && !VAR_EQUAL(m_pSelectedAdf, NULL))
	{
		for (vector<TRANS_STACK>::iterator itr = m_transList.begin(); itr != m_transList.end(); ++itr)
		{
			EFRecord * ptmpRec = m_pSelectedAdf->getRecord();
			if (!ptmpRec->IsTarget(itr->sfi))
			{
				break;
			}

			if (ptmpRec->Write(itr->num, itr->data, itr->len, itr->old) != SW_SUCCED)
			{
				ret = INTER_CARD_WRITE;
				break;
			}

			if (VAR_EQUAL(itr->num, 0x01))
			{
				StorageMgr::setComplex((PCOMPLEX_TRADE)(itr->data));
				make_trade(p_samid, p_tac, ptime, itr->old, itr->data, paccTrade);
				*p_tkstauts = g_tokenMgr.TokenValid_internel(ptime);
			}
		}
	}

	return ret;
}

void MF::CancelTransaction()
{
	EFRecord * ptmpRec	= NULL;

	// �����ֳ��ָ�
	if (m_Transaction && !VAR_EQUAL(m_pSelectedAdf, NULL))
	{
		for (vector<TRANS_STACK>::iterator itr = m_transList.begin(); itr != m_transList.end(); ++itr)
		{
			EFRecord * ptmpRec = m_pSelectedAdf->getRecord();
			if (!ptmpRec->IsTarget(itr->sfi))
			{
				break;
			}

			if (ptmpRec->Write(itr->num, itr->old, itr->len, NULL) != SW_SUCCED)
			{
				break;
			}
		}
	}

	m_transList.clear();
	m_Transaction = false;
}

void MF::StopTransaction()
{
	m_transList.clear();
	m_Transaction = false;
}


bool MF::TransactionPush(_uint8 sfi, _uint8 num, _uint8_p pdata, _uint16 len, _uint16& rdRet)
{
	TRANS_STACK stack = {0};

	if (m_Transaction)
	{
		stack.sfi = sfi;
		stack.num = num;
		stack.len = len;
		memcpy(stack.data, pdata, len);

		m_transList.push_back(stack);
	}

	return m_Transaction;
}

void MF::make_trade(_uint8_p p_samid, _uint8_p p_tac, _uint8_p ptime, _uint8_p pAdf1701_o, _uint8_p pAdf1701_n, PACC_TRADE paccTrade)
{
	_uint32 utmp				= 0;
	_uint32 trade_amount		= 0;
	char	szTmp[32]			= {0};
	_uint8 logicID[8]			= {0};
	PWALLET_ELEMENT pElement	= NULL;
	PCOMPLEX_TRADE ptradeO		= NULL;
	PCOMPLEX_TRADE ptradeN		= NULL;

	if (!VAR_EQUAL(paccTrade, NULL))
	{
		ptradeO = (PCOMPLEX_TRADE)pAdf1701_o;
		ptradeN = (PCOMPLEX_TRADE)pAdf1701_n;

		memset(paccTrade, '0', sizeof(ACC_TRADE));

		utmp = (_uint32)(ptradeN->last_dev[0] << 24);
		utmp = (_uint32)(ptradeN->last_dev[1] << 16);
		utmp += (_uint32)(ptradeN->last_dev[2] << 8);
		utmp += (_uint32)(ptradeN->last_dev[3]);
		sprintf(szTmp, "%08d", utmp);
		memcpy(paccTrade->a_trade_dev, szTmp, 8);

		//paccTrade->a_trade_group[2];		

		utmp = (_uint32)(ptradeN->last_dev_seq[0] << 16);
		utmp += (_uint32)(ptradeN->last_dev_seq[1] << 8);
		utmp += (_uint32)(ptradeN->last_dev_seq[2]);
		sprintf(szTmp, "%-8d", utmp);
		memcpy(paccTrade->a_trade_seq, szTmp, 8);

		//paccTrade->c_trade_station[4];	

		memcpy(paccTrade->c_ticket_type, "0201", 4);

		//paccTrade->c_amount_sec[2];

		sprintf(szTmp, "%02X%02X%02X%02X", p_samid[0], p_samid[1], p_samid[2], p_samid[3]);
		memcpy(paccTrade->c_samid, szTmp, 8);

		InterfaceMgr::getLogicID(logicID);
		sprintf(szTmp, "%02X%02X%02X%02X%02X%02X%02X%02X",
			logicID[0], logicID[1], logicID[2], logicID[3], logicID[4], logicID[5], logicID[6], logicID[7]);
		memcpy(paccTrade->c_logicid, szTmp, 16);

		pElement = m_pSelectedAdf->getWallet()->getElement();
		utmp = (_uint16)(pElement->EP_offline[0] << 8);
		utmp += pElement->EP_offline[1];
		sprintf(szTmp, "%-6d", utmp);
		memcpy(paccTrade->c_off_cnt, szTmp, 6);

		trade_amount = (_uint32)(ptradeN->last_amount[0] << 16);
		trade_amount += (_uint32)(ptradeN->last_amount[1] << 8);
		trade_amount += (_uint32)(ptradeN->last_amount[2]);
		sprintf(szTmp, "%-8d", trade_amount);
		memcpy(paccTrade->c_amount, szTmp, 8);

		utmp = (_uint32)(pElement->EP_balance[0] << 24);
		utmp += (_uint32)(pElement->EP_balance[1] << 16);
		utmp += (_uint32)(pElement->EP_balance[2] << 8);
		utmp += (_uint32)(pElement->EP_balance[3]);
		sprintf(szTmp, "%-8d", utmp);
		memcpy(paccTrade->c_balance, szTmp, 8);

		sprintf(szTmp, "%02X%02X%02X%02X%02X%02X%02X", 
			ptime[0], ptime[1], ptime[2], ptime[3], ptime[4], ptime[5], ptime[6]);
		memcpy(paccTrade->c_trade_time, szTmp, 14);		


		utmp = (_uint32)(ptradeO->last_dev[0] << 24);
		utmp = (_uint32)(ptradeO->last_dev[1] << 16);
		utmp += (_uint32)(ptradeO->last_dev[2] << 8);
		utmp += (_uint32)(ptradeO->last_dev[3]);
		sprintf(szTmp, "%08d", utmp);
		memcpy(paccTrade->c_last_dev, szTmp, 8);			

		utmp = (_uint32)(ptradeO->last_dev_seq[0] << 16);
		utmp += (_uint32)(ptradeO->last_dev_seq[1] << 8);
		utmp += (_uint32)(ptradeO->last_dev_seq[2]);
		sprintf(szTmp, "%-8d", utmp);
		memcpy(paccTrade->c_last_seq, szTmp, 8);			

		utmp = (_uint32)(ptradeO->last_amount[0] << 16);
		utmp += (_uint32)(ptradeO->last_amount[1] << 8);
		utmp += (_uint32)(ptradeO->last_amount[2]);
		sprintf(szTmp, "%-8d", utmp);
		memcpy(paccTrade->c_last_amount, szTmp, 8);		

		sprintf(szTmp, "20%02X%02X%02X%02X%02X00", 
			ptradeO->last_time[0], ptradeO->last_time[1], ptradeO->last_time[2], ptradeO->last_time[3], ptradeO->last_time[4]);
		memcpy(paccTrade->c_last_time, szTmp, 14);		

		sprintf(szTmp, "%02X%02X%02X%02X", p_tac[0], p_tac[1], p_tac[2], p_tac[3]);
		memcpy(paccTrade->c_tac, szTmp, 8);

		//paccTrade->c_degrade[4];

		utmp = (_uint32)(ptradeN->entry_station[0] << 8);
		utmp += (_uint32)(ptradeN->entry_station[1]);
		sprintf(szTmp, "%04d", utmp);
		memcpy(paccTrade->c_entry_station, szTmp ,4);	

		utmp += (_uint32)(ptradeN->entry_dev[0] << 8);
		utmp += (_uint32)(ptradeN->entry_dev[1]);
		sprintf(szTmp, "%04d", utmp);
		memcpy(paccTrade->c_entry_dev, szTmp, 4);		

		sprintf(szTmp, "20%02X%02X%02X%02X%02X%02X", 
			ptradeN->entry_time[0], ptradeN->entry_time[1], ptradeN->entry_time[2], ptradeN->entry_time[3], ptradeN->entry_time[4], ptradeN->entry_time[5]);
		memcpy(paccTrade->c_entry_time, szTmp , 14);		

		//paccTrade->RFU[18];

		if (VAR_EQUAL(ptradeN->card_status, 0x0F))
		{
			memcpy(paccTrade->c_trade_type, "26", 2);
			memcpy(paccTrade->c_trade_station, paccTrade->c_entry_station, 4);
		}
		else if (VAR_EQUAL(ptradeN->card_status, 0x10))
		{
			memcpy(paccTrade->c_trade_type, "27", 2);
			memcpy(paccTrade->c_trade_station, paccTrade->a_trade_dev, 4);
		}
		else if (VAR_EQUAL(ptradeN->card_status, 0x0C) || VAR_EQUAL(ptradeN->card_status, 0x0D) || VAR_EQUAL(ptradeN->card_status, 0x0E))
		{
			memcpy(paccTrade->c_trade_type, "17", 2);
			utmp = (_uint32)(ptradeN->update_station[0] << 8);
			utmp += (_uint32)(ptradeN->update_station[1]);
			sprintf(szTmp, "%04d", utmp);
			memcpy(paccTrade->c_trade_station, szTmp, 4);
		}

		StorageMgr::setWallet(pElement);
		g_tokenMgr.updateToken((short)(trade_amount), ptime);
	}
}