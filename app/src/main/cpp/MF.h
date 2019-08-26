// 主文件

#pragma once

#include "Types.h"
#include "FileStruct.h"
#include "EFBinary.h"
#include "EFKey.h"
#include "ADF.h"

class MF
{
public:
	MF(void);
	~MF(void);

	void Create(_uint8_p ptr, _uint32& offset, PMF_ELEMENT pAttr);

	void FromFileModule(_uint8_p pModule, _uint32& offset);

	void Destory();

	void InsertBinary(EFBinary bin);

	void InsertAdf(ADF adf);

	void WalletBindKey(_uint16 SFI);

	bool IsTarget(_uint8_p pname, _uint8 lenName);

	ADF * getSelectedADF();

	ADF * getADF(_uint16 id);

	vector<ADF>& getListADF();

	vector<EFBinary>& getListBin();

	EFBinary * getBin(_uint8 efid);

	PMF_ELEMENT getElement()
	{
		return m_pElement;
	}

	// 读写器交互部分
	_uint16 GetChallenge_IN(_uint8 len, _uint8_p p_rd);

	_uint16 Select_IN(_uint8_p pname, _uint8 len);

	_uint16 SelectEx_IN(_uint16 SFI);

	_uint16 ReadBin_IN(_uint8 sfi, _uint16 offset, _uint16& len, _uint8_p p_inf);

	_uint16 UpdateBin_IN(_uint8 sfi, _uint16 offset, _uint16 size, _uint8_p p_inf);

	_uint16 ReadRec_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint16& len, bool internel);

	_uint16 UpdateRec_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint8 len, bool bMac, _uint8_p pApduInit, _uint16 lenApdu);

	_uint16 UpdateRecEx_IN(_uint8 sfi, _uint8 id, _uint8_p p_inf, _uint8 len);

	_uint16 GetBalance_IN(_uint8_p p_rd);

	_uint16 InitPurchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp);

	_uint16 InitComplexPurchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp);

	_uint16 Purchase_IN(_uint8_p p_apdu_data, _uint8_p p_rsp, PACC_TRADE paccTrade, _uint16_p p_tkstauts);

	_uint16 GetTansCode_IN(_uint8 ttype, _uint8_p p_apdu_data, _uint8_p p_rsp);

protected:
	_uint8 ChannelSecurity(_uint8_p pApduInit, _uint16 lenApdu, _uint8 keycla, _uint8 keyId);

	bool CheckCardStatus();

	// 事务开始
	void BeginTransaction();

	// 执行并结束事务
	_uint16 EndTransaction(_uint8_p p_samid, _uint8_p p_tac, _uint8_p ptime, PACC_TRADE paccTradep, _uint16_p p_tkstauts);

	// 取消事务，并恢复事务现场，清空堆栈
	void CancelTransaction();

	// 终止事务,清空堆栈
	void StopTransaction();

	// 事务堆栈保护
	bool TransactionPush(_uint8 sfi, _uint8 num, _uint8_p pdata, _uint16 len, _uint16& rdRet);

	void make_trade(_uint8_p p_samid, _uint8_p p_tac, _uint8_p ptime, _uint8_p pAdf1701_o, _uint8_p pAdf1701_n, PACC_TRADE paccTrade);

private:
	// 事务标识
	bool m_Transaction;

	ADF	*		m_pSelectedAdf;

	// 文件属性
	PMF_ELEMENT	m_pElement;

	vector<EFBinary> m_binList;

	vector<ADF> m_adfList;

	ACC_TRADE m_TradeAcc;

	vector<TRANS_STACK>	m_transList;

};
