#include "HCEPrepay.h"
#include <stdio.h>
#include "Apdu.h"
#include "ConstDef.h"
#include "PenCipher.h"
#include "InterfaceMgr.h"
#include "TokenLize.h"

#ifdef _WIN32
#include <Windows.h>

BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		break;
	}
	return TRUE;
}
#endif

short ProcessApdu(char * p_apdu_in, short len_in, char * p_apdu_out, short * len_out, char * trade_type, char * p_trade_data, short * p_token_status)
{
	_uint16 ret = SW_SUCCED;
	g_internelErr = INTER_SUCCED;

	Apdu apduin((_uint8_p)p_apdu_in, (_uint16)len_in);

	if (InterfaceMgr::IsTicketExsit())
	{
		switch (apduin.INS)
		{
		case INS_SELECT:
			ret = InterfaceMgr::Select(apduin);
			break;
		case INS_READ_BIN:
			ret = InterfaceMgr::ReadBin(apduin);
			break;
		case INS_UPDATE_BIN:
			ret = InterfaceMgr::UpdateBin(apduin);
			break;
		case INS_READ_REC:
			ret = InterfaceMgr::ReadRec(apduin);
			break;
		case INS_UPDATE_REC:
			ret = InterfaceMgr::UpdateRec(apduin, (_uint8_p)p_apdu_in);
			break;
		case INS_BALANCE:
			ret = InterfaceMgr::GetBalance(apduin);
			break;
		case INS_INIT_TRANS:
			ret = InterfaceMgr::InitPurchase(apduin);
			break;
		case INS_PURCHASE:
			ret = InterfaceMgr::Purchase(apduin, (_uint8_p)trade_type, (PACC_TRADE)p_trade_data, (_uint16_p)p_token_status);;
			break;
		case INS_CHALLENGE:
			ret = InterfaceMgr::GetChallenge(apduin);
			break;
		case INS_GET_TRANS_CODE:
			ret = InterfaceMgr::GetLastTac(apduin);
			break;
		}
	}
	else
	{
		apduin.SW = SW_CONDITIONS_NOT_SATISFIED;
	}

	*len_out = apduin.MakeRespondApdu((_uint8_p)p_apdu_out);

	return ret;
}

short SetLocalParam(const char * pdir, const char * p_sys_cache, const char * pime, short verLowest, const char * pAccount, char * p_data_CRC)
{
	g_internelErr = StorageMgr::SetDirectory(pdir, p_sys_cache);
	if (VAR_EQUAL(g_internelErr, INTER_SUCCED))
	{
		g_internelErr = StorageMgr::CheckLocalCard(pAccount, pime, verLowest, InterfaceMgr::getCardInstance());
	}

	return (short)g_internelErr;

}

short CreateDefaultCard()
{
	InterfaceMgr::SetTicketExsit(false);
	g_internelErr = InterfaceMgr::Create_WHMTR();
	return (short)g_internelErr;
}

short UpdateCard_MF_05(char * p_file_data, short len_data)
{
	g_internelErr = InterfaceMgr::Update_MF_05((_uint8_p)p_file_data, (_uint16)len_data);
	return (short)g_internelErr;
}

short UpdateCard_ADF01_key(char type, char index, char ver, char algo, char err_cnt, char * p_key, char len_key)
{
	g_internelErr = InterfaceMgr::Update_ADF01_key(type, index, ver, algo, err_cnt, (_uint8_p)p_key, len_key);
	return (short)g_internelErr;
}

short UpdateCard_ADF01_wallet(int balance, short off_cnt, short on_cnt, char forceUpdate)
{
	g_internelErr = InterfaceMgr::Update_ADF01_wallet(balance, off_cnt, on_cnt, forceUpdate);
	return (short)g_internelErr;
}

short UpdateCard_ADF01_15(char * p_file_data, short len_data)
{
	g_internelErr = InterfaceMgr::Update_ADF01_15((_uint8_p)p_file_data, (_uint16)len_data);
	return (short)g_internelErr;
}

short UpdateCard_ADF01_17(char id, char * p_file_data, short len_data)
{
	g_internelErr = InterfaceMgr::Update_ADF01_17(id, (_uint8_p)p_file_data, len_data);
	return (short)g_internelErr;
}

void UpdateCard_end(short verCurrent)
{
	InterfaceMgr::Update_end(verCurrent);
}

short GetWalletInfo(short adf_sfi, int * p_balance, short * p_offline_cnt, short * p_online_cnt)
{
	g_internelErr = InterfaceMgr::GetWallet_WHMTR((_uint16)adf_sfi, (_uint32_p)p_balance, (_uint16_p)p_offline_cnt, (_uint16_p)p_online_cnt);
	return (short)g_internelErr;
}

short GetRecordMetro(short adf_sfi, char * p_metro, short len_metro)
{
	g_internelErr = InterfaceMgr::GetRecordMetro_WHMTR((_uint16)adf_sfi, (_uint8_p)p_metro, (_uint16)len_metro);
	return (short)g_internelErr;
}

short GetUploadInfo(char * p_logicID, int * p_balance, short * p_offline_cnt, short * p_online_cnt, char * p_metro, short len_metro, char * p_data_CRC)
{
	_uint8 blid[8]	= {0};
	char szid[17]	= {0};
	g_internelErr	= INTER_SUCCED; 

	do 
	{
		InterfaceMgr::getLogicID(blid);
		if (memcmp(blid, "\x00\x00\x00\x00\x00\x00\x00\x00", 8) == 0)
		{
			g_internelErr = INTER_CARD_NOT_EXSIST;
			break;
		}
		sprintf(szid, "%02X%02X%02X%02X%02X%02X%02X%02X", blid[0], blid[1], blid[2], blid[3], blid[4], blid[5], blid[6], blid[7]);
		memcpy(p_logicID, szid, 16);

		g_internelErr = InterfaceMgr::GetWallet_WHMTR(0x1001, (_uint32_p)p_balance, (_uint16_p)p_offline_cnt, (_uint16_p)p_online_cnt);
		if (!VAR_EQUAL(g_internelErr, INTER_SUCCED))
		{
			g_internelErr = INTER_CARD_NOT_EXSIST;
			break;
		}
		
		g_internelErr = InterfaceMgr::GetRecordMetro_WHMTR(0x1001, (_uint8_p)p_metro, (_uint16)len_metro);
		if (memcmp(p_metro, "\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00", 10) == 0)
			g_internelErr = INTER_CARD_NOT_EXSIST;

	} while (0);

	return g_internelErr;
}

short SetCardStatus(char newStatus)
{
	g_internelErr = InterfaceMgr::setStatus(newStatus);
	return (short)g_internelErr;
}

// 判断卡是否存在
short CardExsit()
{
	g_internelErr = INTER_CARD_NOT_EXSIST;

	if (InterfaceMgr::IsTicketExsit())
		g_internelErr = INTER_SUCCED;

	return (short)g_internelErr;
}

short UpdateLocalToken(const char * p_NewToken, short lenToken)
{
	g_internelErr = g_tokenMgr.setLocalToken(p_NewToken, lenToken);
	return (short)(g_internelErr);
}

short CheckToken(const char * p_time_now)
{
	g_internelErr = g_tokenMgr.TokenValid(p_time_now);
	return (short)(g_internelErr);
}

void GetTokenOddInfo(const char * p_time_now, short * oddmins, short * oddamount, short * minamount, char * oddcnt)
{
	g_tokenMgr.getTokenInf(p_time_now, oddmins, oddamount, minamount, oddcnt);
}

short GetTokenForServerVerify(char * pToken, short * len_token)
{
	g_internelErr = g_tokenMgr.getToken4Verify(pToken, len_token);
	return (short)g_internelErr;
}

void tmpTokenApply(char * pToken, short * len_token)
{
	g_tokenMgr.simulate_tokenApply(pToken);
	*len_token = 88;
}

short getInternelErrorCode()
{
	return g_internelErr;
}


#ifdef _WIN32
#include "PenCipher.h"
void TestMac(_uint8 Alg, _uint8_p pKey, _uint8_p pData, _uint16 len, _uint8_p pMac, _uint8_p pivec)
{
	//PenCipher::TTTMAC();
	//PenCipher::gmac4(Alg, pKey, pData, len, pMac, pivec);
}
#endif
