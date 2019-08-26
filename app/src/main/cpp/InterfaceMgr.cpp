#include "InterfaceMgr.h"
#include "ConstDef.h"

_uint16 g_internelErr				= 0;

MF InterfaceMgr::m_RootDir;
_uint8 InterfaceMgr::m_logicID[8]	= {0};
bool InterfaceMgr::m_TicketExsit	= false;


InterfaceMgr::InterfaceMgr(void)
{
}

InterfaceMgr::~InterfaceMgr(void)
{
}

_uint16 InterfaceMgr::GetChallenge(Apdu& apduin)
{
	do 
	{
		if(!VAR_EQUAL(apduin.CLA, 0x00))
		{
			apduin.SW = SW_CLA_NOT_SUPPORTED;
			break;
		}

		if (!VAR_EQUAL(apduin.P1, 0x00) && !VAR_EQUAL(apduin.P2, 0x00))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		if (!VAR_EQUAL(apduin.Le, 0x04))
		{
			apduin.SW = SW_WRONG_DATA;
			break;
		}

		apduin.SW = m_RootDir.GetChallenge_IN((_uint8)(apduin.Le), apduin.rsp);

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::Select(Apdu& apduin)
{
	if (VAR_EQUAL(apduin.P1, 0x00))
	{
		_uint16 SFI = (_uint16)((apduin.data[0] << 8) + apduin.data[1]);
		apduin.SW = m_RootDir.SelectEx_IN(SFI);
	}
	else if (VAR_EQUAL(apduin.P1, 0x04))
	{
		apduin.SW = m_RootDir.Select_IN(apduin.data, (_uint8)apduin.Lc);
	}
	else
	{
		apduin.SW = SW_WRONG_P1P2;
	}

	return apduin.SW;
}

_uint16 InterfaceMgr::ReadBin (Apdu& apduin)
{
	_uint8 fid;

	do 
	{
		if(!VAR_EQUAL(apduin.CLA, 0x00) && !VAR_EQUAL(apduin.CLA, 0x04))
			apduin.SW = SW_CLA_NOT_SUPPORTED;

		fid = (_uint8)(apduin.P1 & 0x80);
		if (VAR_EQUAL(fid, 0x80))
		{
			fid = (_uint8)(apduin.P1 & 0x1f);
		}
		else
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		apduin.SW = m_RootDir.ReadBin_IN(fid, apduin.P2, apduin.Le, apduin.rsp);

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::UpdateBin(Apdu& apduin)
{
	_uint8 fid;

	do 
	{
		if(!VAR_EQUAL(apduin.CLA, 0x00) && !VAR_EQUAL(apduin.CLA, 0x04))
			apduin.SW = SW_CLA_NOT_SUPPORTED;

		fid = (_uint8)(apduin.P1 & 0x80);
		if (VAR_EQUAL(fid, 0x80))
		{
			fid = (_uint8)(apduin.P1 & 0x1f);
		}
		else
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		apduin.SW = m_RootDir.UpdateBin_IN(fid, apduin.P2, apduin.Lc, apduin.data);

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::ReadRec(Apdu& apduin)
{
	_uint8 tb;

	do 
	{
		if(!VAR_EQUAL(apduin.CLA, 0x00))
		{
			apduin.SW = SW_CLA_NOT_SUPPORTED;
			break;
		}

		if(VAR_EQUAL(apduin.P1, 0x00) || !VAR_EQUAL((apduin.P2 & 0x07), 0x04))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		tb = (_uint8)(apduin.P2 >> 3);

		apduin.SW = m_RootDir.ReadRec_IN(tb, apduin.P1, apduin.rsp, apduin.Le, false);

		//if (!check_ac(ef18.acr))
		//{
		//	apduin.SW = SW_SECURITY_STATUS_NOT_SATISFIED;
		//	break;
		//}

		//if( apduin.Le > 0 )
		//{
		//	if ( apduin.Le != ef18.recordLength() )
		//		apduin.SW = 0x6c00 + ef18.recordLength();
		//}

		//apduin.Le = ef18.readrec(apduin.P1, apduin.data);

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::UpdateRec(Apdu& apduin, _uint8_p p_apduinInit)
{
	_uint8 tb;
	bool bmac = false;

	do
	{
		if (VAR_EQUAL(apduin.CLA, 0x04) || VAR_EQUAL(apduin.CLA, 0x84))
		{
			bmac = true;
		}
		else if(!VAR_EQUAL(apduin.CLA, 0x00) && !VAR_EQUAL(apduin.CLA, 0x80))
		{
			apduin.SW = SW_CLA_NOT_SUPPORTED;
			break;
		}

		if(!VAR_EQUAL((apduin.P2 & 0x07), 0x00) && !VAR_EQUAL((apduin.P2 & 0x07), 0x04))
		//if(VAR_EQUAL(apduin.P1, 0x00) || !VAR_EQUAL((apduin.P2 & 0x07), 0x00))
		//if(VAR_EQUAL(apduin.P1, 0x00) || !VAR_EQUAL((apduin.P2 & 0x07), 0x04))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		tb = (_uint8)(apduin.P2 >> 3);

		apduin.SW = m_RootDir.UpdateRec_IN(tb, apduin.P1, apduin.data, (_uint8)apduin.Lc, bmac, p_apduinInit, apduin.Lc + 5);

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::GetBalance(Apdu& apduin)
{
	apduin.SW = m_RootDir.GetBalance_IN(apduin.rsp);
	return apduin.SW;
}

_uint16 InterfaceMgr::InitPurchase(Apdu& apduin)
{
	if (!VAR_EQUAL(apduin.CLA, 0x80))
		apduin.SW = SW_CLA_NOT_SUPPORTED;
	if (VAR_EQUAL(apduin.P1, 0x01))
		apduin.SW = m_RootDir.InitPurchase_IN(apduin.data, apduin.rsp);
	else if (VAR_EQUAL(apduin.P1, 0x03))
		apduin.SW = m_RootDir.InitComplexPurchase_IN(apduin.data, apduin.rsp);
	else
		apduin.SW = SW_WRONG_P1P2;

	if (VAR_EQUAL(apduin.SW, SW_SUCCED))
		apduin.Le = 0x0F;

	return apduin.SW;
}

_uint16 InterfaceMgr::Purchase(Apdu& apduin, _uint8_p pTradeType, PACC_TRADE ptrade, _uint16_p p_tkstauts)
{
	do 
	{
		if (!VAR_EQUAL(apduin.CLA, 0x80))
		{
			apduin.SW = SW_CLA_NOT_SUPPORTED;
			break;
		}

		if (!VAR_EQUAL(apduin.P1, 0x01) || !VAR_EQUAL(apduin.P2, 0x00))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		if (!VAR_EQUAL(apduin.Lc, 0x0F))
		{
			apduin.SW = SW_WRONG_LENGTH;
			break;
		}

		apduin.SW = m_RootDir.Purchase_IN(apduin.data, apduin.rsp, ptrade, p_tkstauts);

		if (!VAR_EQUAL(apduin.SW, SW_SUCCED))		break;

		PenCipher::String2Hexs(ptrade->c_trade_type, 2, pTradeType);

		apduin.Le = 0x08;

	} while (0);

	return apduin.SW;
}

_uint16 InterfaceMgr::GetLastTac(Apdu& apduin)
{
	_uint16 token_status		= 0;

	do 
	{
		if (!VAR_EQUAL(apduin.CLA, 0x80))
		{
			apduin.SW = SW_CLA_NOT_SUPPORTED;
			break;
		}

		if (!VAR_EQUAL(apduin.P1, 0x01))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		if (!VAR_EQUAL(apduin.P2, 0x03) && !VAR_EQUAL(apduin.P2, 0x06) && !VAR_EQUAL(apduin.P2, 0x09))
		{
			apduin.SW = SW_WRONG_P1P2;
			break;
		}

		if (!VAR_EQUAL(apduin.Lc, 0x02))
		{
			apduin.SW = SW_WRONG_LENGTH;
			break;
		}

		apduin.SW = m_RootDir.Purchase_IN(apduin.data, apduin.rsp, NULL, &token_status);

		if (apduin.SW != SW_SUCCED)
			break;

		apduin.Le = 0x08;

	} while (0);

	return apduin.SW;
}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////
// 创建缺省的地铁CPU卡格式
_uint16 InterfaceMgr::Create_WHMTR()
{
	_uint16 ret			= INTER_SUCCED;
	char sztmp[1024]	= {0};
	_uint8 ptmp[512]	= {0};
	_uint8_p pWHMTR		= NULL;
	_uint32 off			= 0;

	do 
	{
		m_RootDir.Destory();

		pWHMTR	= StorageMgr::AllocCardMemory(0);
		if (VAR_EQUAL(pWHMTR, NULL))
		{
			ret = INTER_MEM_MALLOC;
			break;
		}

		// MF
		MF_ELEMENT mfelt	= {0};
		mfelt.file_id		= 0x3F00;
		mfelt.app_type		= 0;
		mfelt.rfu			= 0;
		mfelt.atr_sfi		= 0;
		mfelt.dir_sfi		= 0;
		mfelt.fci_sfi		= 0;
		mfelt.acw			= 0x0F;		// 外部不可写
		mfelt.rld_kid		= 0;
		mfelt.bld_kid		= 0;
		mfelt.limit_verify	= 0;
		mfelt.len_name		= (_uint8)strlen(STORAGE_NAME);
		memcpy(mfelt.mf_name, STORAGE_NAME, mfelt.len_name);
		m_RootDir.Create(pWHMTR, off, &mfelt);

		// MF-05
		EFBinary bin05;
		BIN_ELEMENT be05 = {0};
		be05.ef_sfi = 0x05;
		be05.sfi = 0x0005;
		be05.len = 0x1E;
		be05.acr = 0xF0;	// 自由读
		be05.acw = 0x0F;	// 外部不可写

		//sprintf(sztmp, "000400270002000003202000800044922017082220170822000301AABBCC");
		sprintf(sztmp, "000000000000000000000000000000000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		
		bin05.Create(pWHMTR, off, &be05, ptmp);
		m_RootDir.InsertBinary(bin05);

		memcpy(m_logicID, ptmp + 8, 8);

		// ADF1
		ADF adf1;
		ADF_ELEMENT aef1	= {0};
		aef1.file_id		= 0x1001;
		aef1.file_size		= 0x00;
		aef1.fci_sfi		= 0x00;
		aef1.acw			= 0xF0;		// 外部不可写
		aef1.rld_kid		= 0x00;
		aef1.bld_kid		= 0x00;
		aef1.limit_verify	= 0x00;
		aef1.len_name		= 0x00;
		adf1.Create(pWHMTR, off, &aef1);

		// ADF1-key
		EFKey * pKey = adf1.getKey();

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, APP_MAINT_KEY, 0x01, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, CONSUME_KEY, 0x01, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, CONSUME_KEY, 0x02, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, CONSUME_KEY, 0x03, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, CONSUME_KEY, 0x04, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, CONSUME_KEY, 0x05, 0x01, 0x00, ptmp, 16, true);

		sprintf(sztmp, "00000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);
		pKey->AddKey(pWHMTR, off, TAC_KEY, 0x00, 0x00, 0x00, ptmp, 16, true);

		// ADF1-18
		ELInearfix * pLoop = adf1.getDetails();
		LP_ELEMENT le18		= {0};
		le18.ef_sfi			= 0x18;	
		le18.sfi			= 0x0018;	
		le18.acr			= 0xF0;	
		le18.acw			= 0x0F;	
		le18.ef_Size		= 0x17 * 0x0A;
		le18.ptr			= 0x00;
		pLoop->Create(pWHMTR, off, &le18, NULL);

		// ADF1-wallet
		ELWallet * pWallet = adf1.getWallet();
		WALLET_ELEMENT wllt	= {0};
		memcpy(wllt.EP_balance, "\x00\x00\x03\xE8", 4);
		pWallet->Create(pWHMTR, off, &wllt);

		// ADF1-17
		EFRecord * pComplex = adf1.getRecord();
		REC_ELEMENT re17	= {0};
		re17.ef_sfi			= 0x17;	
		re17.sfi			= 0x0017;	
		re17.acr			= 0xF0;	
		re17.acw			= 0xF1;	
		re17.ef_Size		= 0xA0;
		re17.cnt			= 0;

		//sprintf(sztmp, "013B0000012D00041100751710131108590F20171013010011DB7D0004110000000F171013110800000000000000012D007517082811590000C8000200");
		sprintf(sztmp, "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);

		pComplex->Create(pWHMTR, off, &re17, ptmp, (_uint32)strlen(sztmp) / 2);
	
		// ADF1-15
		EFBinary bin15;
		BIN_ELEMENT be15	= {0};
		be15.ef_sfi			= 0x15;
		be15.sfi			= 0x0015;
		be15.len			= 0x30;
		be15.acr			= 0xF0;	// 自由读
		be15.acw			= 0x0F;	// 外部不可写

		//sprintf(sztmp, "20170822171424201901041714240000000000000000000075012E000000012017082217142400000000000300000000");
		sprintf(sztmp, "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		PenCipher::String2Hexs(sztmp, (_uint32)strlen(sztmp), ptmp);

		bin15.Create(pWHMTR, off, &be15, ptmp);
		adf1.InsertBinary(bin15);

		// ADF1-16
		EFBinary bin16;
		BIN_ELEMENT be16	= {0};
		be16.ef_sfi			= 0x16;
		be16.sfi			= 0x0016;
		be16.len			= 0x50;
		be16.acr			= 0xF0;	// 自由读
		be16.acw			= 0x0F;	// 外部不可写

		bin16.Create(pWHMTR, off, &be16, ptmp);
		adf1.InsertBinary(bin16);

		m_RootDir.InsertAdf(adf1);

		m_RootDir.WalletBindKey(0x1001);

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::GetWallet_WHMTR(_uint16 adf_sfi, _uint32_p p_balance, _uint16_p p_offline_cnt, _uint16_p p_online_cnt)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		if (!m_TicketExsit)
			break;

		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(adf_sfi);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		ELWallet * pWallet = pAdf->getWallet();

		ret = pWallet->getEP(*p_balance);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		ret = pWallet->getEPOffCnt(*p_offline_cnt);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;
		
		ret = pWallet->getEPOnCnt(*p_online_cnt);

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::GetRecordMetro_WHMTR(_uint16 adf_sfi, _uint8_p p_metro, _uint16 len_metro)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		if (!m_TicketExsit)
			break;

		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(adf_sfi);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		ret = m_RootDir.ReadRec_IN(0x17, 0x01, p_metro, len_metro, true);
		if (!VAR_EQUAL(ret, SW_SUCCED))
		{
			ret = INTER_CARD_NOT_EXSIST;
			break;
		}

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

MF * InterfaceMgr::getCardInstance()
{
	return &m_RootDir;
}

_uint16 InterfaceMgr::Update_MF_05(_uint8_p p_file_data, _uint16 len_data)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		EFBinary * pBin = m_RootDir.getBin(0x05);
		if (VAR_EQUAL(pBin, NULL))
			break;

		if (pBin->Write(0, len_data, p_file_data, NULL) != SW_SUCCED)
		{
			ret = INTER_CARD_WRITE;
			break;
		}

		memcpy(m_logicID, p_file_data + 8, 8);

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::Update_ADF01_key(_uint8 type, _uint8 index, _uint8 ver, _uint8 algo, _uint8 err_cnt, _uint8_p p_key, _uint8 len_key)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(0x1001);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		EFKey * pKey = pAdf->getKey();	
		ret = pKey->Update(type, index, ver, algo, err_cnt, p_key, len_key, true);

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::Update_ADF01_wallet(_uint32 balance, _uint16 off_cnt, _uint16 on_cnt, char forceUpdate)
{
	_uint16 ret			= INTER_CARD_NOT_EXSIST;
	bool localOldUpd	= true;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(0x1001);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		if (VAR_EQUAL(forceUpdate, 1))
			localOldUpd = false;

		ret = pAdf->getWallet()->Update(balance, off_cnt, on_cnt, localOldUpd);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		StorageMgr::setWallet(pAdf->getWallet()->getElement());

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::Update_ADF01_15(_uint8_p p_file_data, _uint16 len_data)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(0x1001);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		EFBinary * pBin = pAdf->getBin(0x15);
		if (VAR_EQUAL(pBin, NULL))
			break;

		if (pBin->Write(0, len_data, p_file_data, NULL) != SW_SUCCED)
		{
			ret = INTER_CARD_WRITE;
			break;
		}

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

_uint16 InterfaceMgr::Update_ADF01_17(_uint8 id, _uint8_p p_file_data, _uint16 len_data)
{
	_uint16 ret = INTER_CARD_NOT_EXSIST;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(0x1001);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		EFRecord * ptmpRec = pAdf->getRecord();
		if (!ptmpRec->IsTarget(0x17))
			break;

		if (ptmpRec->Write(id, p_file_data, (_uint8)len_data, NULL) != SW_SUCCED)
		{
			ret = INTER_CARD_WRITE;
			break;
		}

		StorageMgr::setComplex((PCOMPLEX_TRADE)ptmpRec->getRecord(id));

		ret = INTER_SUCCED;

	} while (0);

	return ret;
}

void InterfaceMgr::Update_end(_uint16 verCurrent)
{
	short sresult = StorageMgr::Serialize(verCurrent, &m_RootDir);
	if (VAR_EQUAL(sresult, INTER_SUCCED))
	{
		m_TicketExsit = true;
	}
}

void InterfaceMgr::getLogicID(_uint8_p pIn)
{
	memcpy(pIn, m_logicID, 8);
}

void InterfaceMgr::setLogicID(_uint8_p pIn)
{
	memcpy(m_logicID, pIn, 8);
}

_uint16 InterfaceMgr::setStatus(_uint8 st)
{
	_uint16 ret = INTER_SUCCED;

	PMF_ELEMENT pElement = m_RootDir.getElement();
	if (!VAR_EQUAL(pElement, NULL))
	{
		pElement->extra.cardStatus = st;
		ret = StorageMgr::Serialize(&m_RootDir);
	}
	else
	{
		ret = INTER_CARD_NOT_EXSIST;
	}

	return ret;
}

void InterfaceMgr::SetTicketExsit(bool bresult)
{
	m_TicketExsit = bresult;
}

_uint16 InterfaceMgr::IsTicketExsit()
{
	return m_TicketExsit;
}

_uint16 InterfaceMgr::getTACKey(_uint8_p pKey)
{
	_uint16 ret		= INTER_CARD_NOT_EXSIST;
	_uint8 verID	= 0;
	_uint8 algID	= 0;

	do 
	{
		PMF_ELEMENT pElement = m_RootDir.getElement();
		if (VAR_EQUAL(pElement, NULL))
			break;

		ADF * pAdf = m_RootDir.getADF(0x1001);
		if (VAR_EQUAL(pAdf, NULL))
			break;

		EFKey * pKeyObj = pAdf->getKey();
		ret = pKeyObj->getKey(TAC_KEY, 0x00, &verID, &algID, pKey, true);

	} while (0);

	return ret;
}
