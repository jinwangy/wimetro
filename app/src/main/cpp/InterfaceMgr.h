#pragma once
#include "MF.h"
#include "ADF.h"
#include "StorageMgr.h"

class InterfaceMgr
{
private:
	static MF m_RootDir;
	static _uint8 m_logicID[8];
	static bool m_TicketExsit;

public:
	InterfaceMgr(void);

	~InterfaceMgr(void);

	static _uint16 GetChallenge(Apdu& apduin);
	static _uint16 Select(Apdu& apduin);
	static _uint16 ReadBin (Apdu& apduin);
	static _uint16 UpdateBin(Apdu& apduin);
	static _uint16 ReadRec(Apdu& apduin);
	static _uint16 UpdateRec(Apdu& apduin, _uint8_p p_apduinInit);
	static _uint16 GetBalance(Apdu& apduin);
	static _uint16 InitPurchase(Apdu& apduin);
	static _uint16 Purchase(Apdu& apduin, _uint8_p pTradeType, PACC_TRADE ptrade, _uint16_p p_tkstauts);
	static _uint16 GetLastTac(Apdu& apduin);

	static _uint16 Create_WHMTR();
	static _uint16 Update_MF_05(_uint8_p p_file_data, _uint16 len_data);
	static _uint16 Update_ADF01_key(_uint8 type, _uint8 index, _uint8 ver, _uint8 algo, _uint8 err_cnt, _uint8_p p_key, _uint8 len_key);
	static _uint16 Update_ADF01_wallet(_uint32 balance, _uint16 off_cnt, _uint16 on_cnt, char forceUpdate);
	static _uint16 Update_ADF01_15(_uint8_p p_file_data, _uint16 len_data);
	static _uint16 Update_ADF01_17(_uint8 id, _uint8_p p_file_data, _uint16 len_data);
	static void Update_end(_uint16 verCurrent);

	static _uint16 GetWallet_WHMTR(_uint16 adf_sfi, _uint32_p p_balance, _uint16_p p_offline_cnt, _uint16_p p_online_cnt);
	static _uint16 GetRecordMetro_WHMTR(_uint16 adf_sfi, _uint8_p p_metro, _uint16 len_metro);

	static MF * getCardInstance();

	static void getLogicID(_uint8_p pIn);
	static void setLogicID(_uint8_p pIn);

	static _uint16 setStatus(_uint8 st);

	static void SetTicketExsit(bool bresult);
	static _uint16 IsTicketExsit();

	static _uint16 getTACKey(_uint8_p pKey);
};

