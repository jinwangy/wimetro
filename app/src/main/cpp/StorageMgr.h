#pragma once

#include <stdio.h>
#include "Types.h"
#include "MF.h"
#include "ConstDef.h"

#define STORAGE_NAME					"WHPAY.HCE.DDF01"
#define SIZE_CARD_MEM					1024

class StorageMgr
{
public:
	StorageMgr(void);
	~StorageMgr(void);

	static _uint16 SetDirectory(const char * pSaveFolder, const char * p_sys_cache);
	static _uint16 CheckLocalCard(const char * pAccount, const char * pPhoneIme, _uint16 verLowest, MF * pInstance);
	static _uint16 Serialize(MF * p_root);
	static _uint16 Serialize(_uint16 verCurrent, MF * p_root);
	static _uint16 UnSerialize(const char * pAccount, const char * pPhoneIme, _uint16 verLowest, MF * p_root);
	static _uint8_p AllocCardMemory(_uint32 sz);

	static void setWallet(PWALLET_ELEMENT pWallet);
	static void setComplex(PCOMPLEX_TRADE pComplex);

	static char * getAppFolder();
	static _uint16 CheckDirectory(char * pDir, bool failedCreate);

	static void getLocalIme(_uint8_p pIme);

private:
	static _uint8 m_CardMemory[SIZE_CARD_MEM];
	static char m_appFolder[PATH_LENGTH];
	static char m_cacheFolder[PATH_LENGTH];
	static char m_localIME[20];
	static char m_hceID[20];
	static PWALLET_ELEMENT m_pWallet;
	static PCOMPLEX_TRADE m_pComlex;


protected:
	// 一般在首次保存或者卡片格式变动时需要重新计算偏移量是，按照这个重新写文件，否则直接一次更新到文件
	static _uint16 WriteBinListForMF(MF * p_MF, FILE * fp);
	static _uint16 WriteADFForMF(MF * pRoot, FILE * fp);
	static _uint16 WriteKeyForADF(EFKey * pKey, FILE * fp);
	static _uint16 WriteWalletForADF(ELWallet * pWallet, FILE * fp);
	static _uint16 WriteRecordForADF(EFRecord * pRecord, FILE * fp);
	static _uint16 WriteDetailForADF(ELInearfix * pLoop, FILE * fp);
	static _uint16 WriteBinListForADF(vector<EFBinary>& binList, FILE * fp);
	static _uint16 WriteMFBase(MF * p_MF, FILE * fp);
	static _uint16 WriteOffsetForADF(MF * p_MF, FILE * fp);

	////////////////////////////////////////////////////////////////////////////

	// 从已经保存的文件中分解卡片数据
	static _uint16 GetFileMemory(char * pPathName, _uint32& sizeMem);
	static _uint16 ReadBinListForMF(MF * pRoot, _uint8_p pMemory, _uint32& offMemory);
	static _uint16 ReadADFForMF(MF * pRoot, _uint8_p pMemory, _uint32 sizeMemory, _uint32& offMemory);
	static _uint16 ReadKeyForADF(EFKey * pKey, _uint8_p pMemory, _uint32& offset);
	static _uint16 ReadWalletForADF(ELWallet * pWallet, _uint8_p pMemory, _uint32& offset);
	static _uint16 ReadRecordForADF(EFRecord * pRecord, _uint8_p pMemory, _uint32& offset);
	static _uint16 ReadDetailForADF(ELInearfix * pLoop, _uint8_p pMemory, _uint32& offset);
	static _uint16 ReadBinListForADF(ADF& adf, _uint8_p pMemory, _uint32& offMemory);
	static _uint16 ReadMFBase(MF * p_MF, _uint8_p pMemory, _uint32& offMemory);

	static _uint32 FileSize(FILE * fp);
	static _uint16 CheckMD5(char * PathName);
	static _uint16 CaculatMD5(char * PathName);
	//static _uint16 get_MD5_factor(_uint8_p p_fac_out, _uint32& sizeOut, _uint8_p pmd5src);
	//static _uint16 get_MD5_factorEx(_uint8_p p_fac_out, _uint32& sizeOut);
	static _uint16 read_MD5(_uint8_p p_MD5);
	static _uint16 save_MD5(_uint8_p p_MD5);
	static _uint16 get_MD5_data(char * PathName, _uint8_p pdata, _uint32 sizeData, _uint32& sizeStep);
	
	//static void DeleteOldCardFile(const char * pFolder);
};

