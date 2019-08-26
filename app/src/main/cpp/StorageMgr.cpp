#include "StorageMgr.h"
#include "ConstDef.h"
#include "PenMD5.h"
#include "InterfaceMgr.h"
#include "PenAES.h"
#include "TokenLize.h"
#include <limits.h>
#include <string.h>

_uint8 StorageMgr::m_CardMemory[SIZE_CARD_MEM]	= {0};
char StorageMgr::m_appFolder[PATH_LENGTH]		= {0};
char StorageMgr::m_cacheFolder[PATH_LENGTH]		= {0};
char StorageMgr::m_localIME[20]					= {0};
char StorageMgr::m_hceID[20]					= {0};

PWALLET_ELEMENT StorageMgr::m_pWallet			= NULL;
PCOMPLEX_TRADE StorageMgr::m_pComlex			= NULL;


StorageMgr::StorageMgr(void)
{
}


StorageMgr::~StorageMgr(void)
{
}

_uint16 StorageMgr::SetDirectory(const char * pSaveFolder, const char * p_sys_cache)
{
	_uint16 ret	= INTER_SUCCED;
	_uint8 len	= 0;

	// ���汾�ز�����ͬʱ���Լ��ر��ؿ�Ƭ��Ϣ
	do 
	{
		len = (_uint8)(strlen(pSaveFolder));
		if (len + 1 > sizeof(m_appFolder))
		{
			ret = INTER_PARAM_INVALID;
			break;
		}
		strcpy(m_appFolder, pSaveFolder);
		//DeleteOldCardFile(pSaveFolder);

		len = (_uint8)(strlen(p_sys_cache));
		if (len + 1 > sizeof(m_cacheFolder))
		{
			ret = INTER_PARAM_INVALID;
			break;
		}
		strcpy(m_cacheFolder, p_sys_cache);

	} while (0);

	return ret;
}

_uint16 StorageMgr::CheckLocalCard(const char * pAccount, const char * pPhoneIme, _uint16 verLowest, MF * pInstance)
{
	_uint16 ret	= INTER_SUCCED;

	if (memcmp(pAccount, m_hceID, 12) != 0)
		g_tokenMgr.reset_token();

	memcpy(m_hceID, pAccount, strlen(pAccount));
	memcpy(m_localIME, pPhoneIme, strlen(pPhoneIme));


	ret = UnSerialize(pAccount, pPhoneIme, verLowest, pInstance);
	if (VAR_EQUAL(ret, INTER_SUCCED))
	{
		InterfaceMgr::SetTicketExsit(true);
	}

	return ret;
}

_uint8_p StorageMgr::AllocCardMemory(_uint32 sz)
{
	memset(m_CardMemory, 0, SIZE_CARD_MEM);

	return m_CardMemory;
}

_uint16 StorageMgr::Serialize(MF * p_root)
{
	_uint16 ret				= INTER_SUCCED;
	char szPathName[0xFF]	= {0};
	FILE * fp				= NULL;
	int fileStatus			= 0;

	do 
	{
		// ���ж��Ƿ���ڣ��������򴴽������������Ƿ��д��
		ret = CheckDirectory(m_appFolder, true);
		if (!VAR_EQUAL(fileStatus, 0))
		{
			ret = INTER_FILE_NOT_EXSIST;
			break;
		}

		sprintf(szPathName, "%s/%s", m_appFolder, STORAGE_NAME);
		fileStatus = __ACCESS(szPathName, 0x00);
		if (VAR_EQUAL(fileStatus, 0))
		{
			remove(szPathName);
		}

		fp = fopen(szPathName, "wb+");
		if (VAR_EQUAL(fp, NULL))
		{
			ret = INTER_FILE_OPEN;
			break;
		}

		if (fseek(fp, 0, SEEK_SET) != 0)
		{
			ret = INTER_FILE_WRITE;
			break;
		}

		ret = WriteBinListForMF(p_root, fp);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		ret = WriteADFForMF(p_root, fp);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		ret = WriteMFBase(p_root, fp);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		ret = WriteOffsetForADF(p_root, fp);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		if (!VAR_EQUAL(fp, NULL))
		{
			fclose(fp);
			fp = NULL;
		}

		ret = CaculatMD5(szPathName);

	} while (0);

	if (!VAR_EQUAL(fp, NULL))
		fclose(fp);

	return ret;
}

_uint16 StorageMgr::Serialize(_uint16 verCurrent, MF * p_root)
{
	PMF_ELEMENT pElement = p_root->getElement();
	if (!VAR_EQUAL(pElement, NULL))
	{
		pElement->extra.dataVersion = verCurrent;

		return Serialize(p_root);
	}

	return INTER_CARD_NOT_EXSIST;
}

_uint16 StorageMgr::WriteBinListForMF(MF * p_MF, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	vector<EFBinary> binList = p_MF->getListBin();
	_uint8 bincnt = (_uint8)binList.size();

	lentowrite = sizeof(bincnt);
	lenwritten = fwrite(&bincnt, lentowrite, 1, fp);
	if (VAR_EQUAL(1, lenwritten))
	{
		for (_uint8 i=0;i<bincnt;i++)
		{
			PBIN_ELEMENT pElement = binList[i].getElement();
			lentowrite = sizeof(*pElement);
			lenwritten = fwrite(pElement, lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}

			lentowrite = (size_t)(pElement->len);
			lenwritten = fwrite(binList[i].getDataPtr(), lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}
		}
	}
	else
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteADFForMF(MF * pRoot, FILE * fp)
{
	_uint16 ret			= INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;
	
	vector<ADF>& listAdf = pRoot->getListADF();
	_uint8 cnt = (_uint8)(listAdf.size());

	// ADFĿ¼��Ϣƫ��
	PMF_ELEMENT pElement = pRoot->getElement();
	pElement->adf_off = (_uint32)ftell(fp);

	lentowrite = sizeof(cnt);
	lenwritten = fwrite(&cnt, lentowrite, 1, fp);
	if (VAR_EQUAL(1, lenwritten))
	{
		// дADFͷ
		for (_uint8 i=0;i<cnt;i++)
		{
			PADF_ELEMENT pElement = listAdf[i].getElement();
			lentowrite = sizeof(ADF_ELEMENT);
			lenwritten = fwrite(pElement, lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}
		}

		// дADF���ļ�
		for (_uint8 i=0;i<cnt;i++)
		{
			PADF_ELEMENT pElement = listAdf[i].getElement();

			pElement->key_off = (_uint32)ftell(fp);
			EFKey * pKey = listAdf[i].getKey();
			ret = WriteKeyForADF(pKey, fp);
			if (ret != INTER_SUCCED)	break;

			pElement->wallet_off = (_uint32)ftell(fp);
			ELWallet * pWallet = listAdf[i].getWallet();
			ret = WriteWalletForADF(pWallet, fp);
			if (ret != INTER_SUCCED)	break;

			pElement->rec_off = (_uint32)ftell(fp);
			EFRecord * pRecord = listAdf[i].getRecord();
			ret = WriteRecordForADF(pRecord, fp);
			if (ret != INTER_SUCCED)	break;

			pElement->loop_off = (_uint32)ftell(fp);
			ELInearfix * pLoop = listAdf[i].getDetails();
			ret = WriteDetailForADF(pLoop, fp);
			if (ret != INTER_SUCCED)	break;

			pElement->bin_off = (_uint32)ftell(fp);
			vector<EFBinary>& binList = listAdf[i].getListBin();
			ret = WriteBinListForADF(binList, fp);
			if (ret != INTER_SUCCED)	break;
		}

	}
	else
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteKeyForADF(EFKey * pKey, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	vector<PKEY_ITEM>& keylist = pKey->getKeyList();
	_uint8 keycnt = (_uint8)keylist.size();
	lentowrite = sizeof(keycnt);
	lenwritten = fwrite(&keycnt, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}
	else
	{

		for (_uint8 i=0;i<keycnt;i++)
		{
			lentowrite = sizeof(KEY_ITEM);
			lenwritten = fwrite(keylist[i], lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}
		}
	}

	return ret;
}

_uint16 StorageMgr::WriteWalletForADF(ELWallet * pWallet, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	WALLET_ELEMENT * pele = pWallet->getElement();
	lentowrite = sizeof(*pele);
	lenwritten = fwrite(pele, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteRecordForADF(EFRecord * pRecord, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	_uint16 lenRec		= 0;
	_uint8_p pRec		= NULL;

	PREC_ELEMENT pElement = pRecord->getElement();
	lentowrite = sizeof(REC_ELEMENT);
	lenwritten = fwrite(pElement, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}
	else
	{
		vector<RECINF>& reclist = pRecord->getRecordList();
		for (_uint8 i=0;i<pElement->cnt;i++)
		{
			lenRec = reclist[i].first;
			pRec = (_uint8_p)(reclist[i].second);

			lentowrite = sizeof(lenRec);
			lenwritten = fwrite(&lenRec, lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
			}

			lentowrite = lenRec;
			lenwritten = fwrite(pRec, lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
			}
		}
	}

	return ret;
}

_uint16 StorageMgr::WriteDetailForADF(ELInearfix * pLoop, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	PLP_ELEMENT pElement = pLoop->getElement();
	lentowrite = sizeof(LP_ELEMENT);
	lenwritten = fwrite(pElement, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}

	lentowrite = ELInearfix::LOOP_COUNTER * sizeof(LOOP_ITEM);
	lenwritten = fwrite(pLoop->getDataPtr(), lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteBinListForADF(vector<EFBinary>& binList, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	_uint8 bincnt = (_uint8)binList.size();

	lentowrite = sizeof(bincnt);
	lenwritten = fwrite(&bincnt, lentowrite, 1, fp);
	if (VAR_EQUAL(1, lenwritten))
	{
		for (_uint8 i=0;i<bincnt;i++)
		{
			BIN_ELEMENT * pbinEle = binList[i].getElement();
			lentowrite = sizeof(*pbinEle);
			lenwritten = fwrite(pbinEle, lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}

			lentowrite = (size_t)(pbinEle->len);
			lenwritten = fwrite(binList[i].getDataPtr(), lentowrite, 1, fp);
			if (!VAR_EQUAL(1, lenwritten))
			{
				ret = INTER_FILE_WRITE;
				break;
			}
		}
	}
	else
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteMFBase(MF * p_MF, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	MF_ELEMENT * pele = p_MF->getElement();
	lentowrite = sizeof(* pele);
	lenwritten = fwrite(pele, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}

	_uint32 mfbase_size = (_uint32)sizeof(MF_ELEMENT);
	lentowrite = sizeof(mfbase_size);
	lenwritten = fwrite(&mfbase_size, lentowrite, 1, fp);
	if (!VAR_EQUAL(1, lenwritten))
	{
		ret = INTER_FILE_WRITE;
	}

	return ret;
}

_uint16 StorageMgr::WriteOffsetForADF(MF * p_MF, FILE * fp)
{
	_uint16 ret = INTER_SUCCED;
	size_t	lentowrite	= 0;
	size_t	lenwritten	= 0;

	PMF_ELEMENT pElement = p_MF->getElement();
	vector<ADF>& adfList = p_MF->getListADF();

	if (fseek(fp, pElement->adf_off + 1, SEEK_SET) != 0)
		return INTER_FILE_WRITE;

	// ��дADFͷ
	for (_uint8 i=0;i<adfList.size();i++)
	{
		PADF_ELEMENT pElement = adfList[i].getElement();
		lentowrite = sizeof(ADF_ELEMENT);
		lenwritten = fwrite(pElement, lentowrite, 1, fp);
		if (!VAR_EQUAL(1, lenwritten))
		{
			ret = INTER_FILE_WRITE;
			break;
		}
	}

	return ret;
}

////////////////////////////////////////////////////////////////////////////////////////////////

_uint16 StorageMgr::UnSerialize(const char * pAccount, const char * pPhoneIme, _uint16 verLowest, MF * p_root)
{
	_uint16 ret				= INTER_SUCCED;
	_uint32 off				= 0;
	_uint32 filesize		= 0;
	_uint32 mfbase_size		= 0;
	int fileStatus			= 0;
	char szPathName[0xFF]	= {0};

	do 
	{
		p_root->Destory();

		ret = CheckDirectory(m_appFolder, true);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
		{
			break;
		}
		sprintf(szPathName, "%s/%s", m_appFolder, STORAGE_NAME);

		ret = CheckMD5(szPathName);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		ret = GetFileMemory(szPathName, filesize);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		if (filesize < sizeof(MF_ELEMENT))
		{
			ret = INTER_MEMORY_LACK;
			break;
		}

		off = filesize - sizeof(mfbase_size);
		memcpy(&mfbase_size, m_CardMemory + off, sizeof(mfbase_size));
		if (!VAR_EQUAL(mfbase_size, sizeof(MF_ELEMENT)))
		{
			ret = INTER_DATA_ERR;
			break;
		}

		off = filesize  - sizeof(mfbase_size) - sizeof(MF_ELEMENT);
		ret = ReadMFBase(p_root, m_CardMemory, off);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		off = 0;
		ret = ReadBinListForMF(p_root, m_CardMemory, off);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		off = p_root->getElement()->adf_off;
		if (off >= filesize)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadADFForMF(p_root, m_CardMemory, filesize, off);
		if (!VAR_EQUAL(ret, INTER_SUCCED))	break;

		p_root->WalletBindKey(0x1001);

		PMF_ELEMENT pElement = p_root->getElement();
		if (pElement->extra.dataVersion < verLowest)
		{
			ret = INTER_DATA_OVER;
			break;
		}

	} while (0);

	if (!VAR_EQUAL(ret, INTER_SUCCED))
	{
		p_root->Destory();
	}

	return ret;
}

_uint16 StorageMgr::GetFileMemory(char * pPathName, _uint32& sizeMem)
{
	_uint16 ret				= INTER_SUCCED;
	int fileStatus			= 0;
	FILE * fp				= NULL;

	// ���������ļ�, ���ж��Ƿ���ڣ��������򴴽������������Ƿ��д��
	do 
	{
		fp = fopen(pPathName, "rb+");
		if (fp == NULL)
		{
			ret = INTER_FILE_OPEN;
			break;
		}

		sizeMem = FileSize(fp);
		if (fseek(fp, 0, SEEK_SET) != 0)
		{
			ret = INTER_FILE_READ;
			break;
		}
		_uint32 read_cnt = (_uint32)fread(m_CardMemory, 1, sizeMem, fp);
		if (read_cnt > sizeMem || read_cnt == 0)
		{
			ret = INTER_FILE_READ;
			break;
		}

	} while (0);

	if (!VAR_EQUAL(fp, NULL))
		fclose(fp);

	return ret;
}

_uint16 StorageMgr::ReadADFForMF(MF * pRoot, _uint8_p pMemory, _uint32 sizeMemory, _uint32& offMemory)
{
	_uint16 ret = INTER_SUCCED;
	_uint32 off = 0;
	
	_uint8 cnt = pMemory[offMemory];
	offMemory += 1;
	for (_uint8 i=0;i<cnt;i++)
	{
		ADF adf;
		adf.Create(pMemory, offMemory, NULL);

		pRoot->InsertAdf(adf);
	}

	vector<ADF>& adfList = pRoot->getListADF();
	for (_uint8 i=0;i<cnt;i++)
	{
		PADF_ELEMENT pElement = adfList[i].getElement();

		EFKey * pKey = adfList[i].getKey();
		off = pElement->key_off;
		if (off >= sizeMemory)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadKeyForADF(pKey, pMemory, off);
		if (ret != INTER_SUCCED)	break;

		ELWallet * pWallet = adfList[i].getWallet();
		off = pElement->wallet_off;
		if (off >= sizeMemory)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadWalletForADF(pWallet, pMemory, off);
		if (ret != INTER_SUCCED)	break;

		EFRecord * pRecord = adfList[i].getRecord();
		off = pElement->rec_off;
		if (off >= sizeMemory)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadRecordForADF(pRecord, pMemory, off);
		if (ret != INTER_SUCCED)	break;

		ELInearfix * pLoop = adfList[i].getDetails();
		off = pElement->loop_off;
		if (off >= sizeMemory)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadDetailForADF(pLoop, pMemory, off);
		if (ret != INTER_SUCCED)	break;

		off = pElement->bin_off;
		if (off >= sizeMemory)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		ret = ReadBinListForADF(adfList[i], pMemory, off);
		if (ret != INTER_SUCCED)	break;
	}

	return ret;
}

_uint16 StorageMgr::ReadBinListForMF(MF * pRoot, _uint8_p pMemory, _uint32& offMemory)
{
	_uint16 ret = INTER_SUCCED;

	_uint8 cnt = pMemory[offMemory];
	offMemory += 1;

	for (_uint8 i=0;i<cnt;i++)
	{
		EFBinary bin;
		bin.FromFileModule(pMemory, offMemory);
		pRoot->InsertBinary(bin);

		if (bin.IsTarget(0x05))
		{
			InterfaceMgr::setLogicID(bin.getDataPtr() + 8);
		}
	}

	return ret;
}

_uint16 StorageMgr::ReadKeyForADF(EFKey * pKey, _uint8_p pMemory, _uint32& offset)
{
	_uint16 ret = INTER_SUCCED;

	_uint8 cnt = pMemory[offset];
	offset += 1;

	for (_uint8 i=0;i<cnt;i++)
	{
		pKey->AddKey(pMemory, offset, false);
	}

	return ret;
}

_uint16 StorageMgr::ReadWalletForADF(ELWallet * pWallet, _uint8_p pMemory, _uint32& offset)
{
	_uint16 ret = INTER_SUCCED;

	pWallet->FromFileModule(pMemory, offset);

	return ret;
}

_uint16 StorageMgr::ReadRecordForADF(EFRecord * pRecord, _uint8_p pMemory, _uint32& offset)
{
	_uint16 ret = INTER_SUCCED;
	_uint16 len = 0;
	pRecord->FromFileModule(pMemory, offset);

	return ret;
}

_uint16 StorageMgr::ReadDetailForADF(ELInearfix * pLoop, _uint8_p pMemory, _uint32& offset)
{
	_uint16 ret = INTER_SUCCED;

	pLoop->FromFileModule(pMemory, offset);

	return ret;
}

_uint16 StorageMgr::ReadBinListForADF(ADF& adf, _uint8_p pMemory, _uint32& offMemory)
{
	_uint16 ret = INTER_SUCCED;

	_uint8 cnt = pMemory[offMemory];
	offMemory += 1;

	for (_uint8 i=0;i<cnt;i++)
	{
		EFBinary bin;
		bin.FromFileModule(pMemory, offMemory);
		adf.InsertBinary(bin);
	}

	return ret;
}

_uint16 StorageMgr::ReadMFBase(MF * p_MF, _uint8_p pMemory, _uint32& offMemory)
{
	_uint16 ret = INTER_SUCCED;

	p_MF->FromFileModule(pMemory, offMemory);

	return ret;
}

_uint32 StorageMgr::FileSize(FILE * fp)
{
	long posold = ftell(fp);
	if (fseek(fp, 0, SEEK_END) != 0)
		return 0;

	_uint32 sz = (_uint32)ftell(fp);
	fseek(fp, posold, SEEK_SET);

	return sz;
}

_uint16 StorageMgr::CheckMD5(char * PathName)
{
	_uint16 ret							= INTER_SUCCED;
	_uint8 md5src[16]					= {0};
	_uint8 data[CARD_BUFFER_MAX]		= {0};
	_uint8 md5targ[16]					= {0};
	_uint32 datasize					= 0;
	_uint32 stepsize					= 0;
	_uint32 offset						= 0;

	do 
	{
		datasize = CARD_BUFFER_MAX;

		stepsize = strlen(m_hceID);
		memcpy(data + offset, m_hceID, stepsize);
		offset += stepsize;
		datasize -= stepsize;

		stepsize = strlen(m_localIME);
		memcpy(data + offset, m_localIME, stepsize);
		offset += stepsize;
		datasize -= stepsize;

		ret = get_MD5_data(PathName, data + offset, datasize, stepsize);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;
		offset += stepsize;
		datasize -= stepsize;

		ret = read_MD5(md5src);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		PenMD5 MD5(data, offset);
		MD5.getMD5_Hex(md5targ);

		if (memcmp(md5targ, md5src, 16) != 0)
		{
			ret = INTER_DATA_MD5;
			break;
		}

	} while (0);

	return ret;
}

_uint16 StorageMgr::CaculatMD5(char * PathName)
{
	_uint16 ret							= INTER_SUCCED;
	_uint8 data[CARD_BUFFER_MAX]		= {0};
	_uint8 md5targ[16]					= {0};
	_uint32 datasize					= 0;
	_uint32 stepsize					= 0;
	_uint32 offset						= 0;

	do 
	{
		datasize = CARD_BUFFER_MAX;

		stepsize = strlen(m_hceID);
		memcpy(data + offset, m_hceID, stepsize);
		offset += stepsize;
		datasize -= stepsize;

		stepsize = strlen(m_localIME);
		memcpy(data + offset, m_localIME, stepsize);
		offset += stepsize;
		datasize -= stepsize;

		ret = get_MD5_data(PathName, data + offset, datasize, stepsize);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;
		offset += stepsize;
		datasize -= stepsize;

		PenMD5 MD5(data, offset);
		MD5.getMD5_Hex(md5targ);

		ret = save_MD5(md5targ);

	} while (0);

	return ret;
}

void StorageMgr::setWallet(PWALLET_ELEMENT pWallet)
{
	m_pWallet = pWallet;
}

void StorageMgr::setComplex(PCOMPLEX_TRADE pComplex)
{
	m_pComlex = pComplex;
}

//// ���ص�ʱ�򣬼�������ΪNULL����Ҫ���ļ��ж�ȡ�������꣬���׺󽫿�Ƭʵʱ���ݸ��¹���
//_uint16 StorageMgr::get_MD5_factor(_uint8_p p_fac_out, _uint32& sizeOut, _uint8_p pmd5src)
//{
//	char szFacPathName[PATH_LENGTH] = {0};
//	_uint8 data[512]					= {0};
//	_uint32 lenrd						= 0;
//	FILE * fpFac						= NULL;
//	_uint16 ret							= INTER_FILE_READ;
//
//	do 
//	{
//		if (sizeof(WALLET_ELEMENT) + sizeof(COMPLEX_TRADE) > sizeOut)
//		{
//			ret = INTER_MEMORY_LACK;
//			break;
//		}
//
//		sizeOut = 0;
//
//		sprintf(szFacPathName, "%s/%s", m_cacheFolder, STORAGE_NAME);
//
//		fpFac = fopen(szFacPathName, "rb+");
//		if (fpFac == NULL)
//		{
//			ret = INTER_FILE_OPEN;
//			break;
//		}
//
//		lenrd = FileSize(fpFac);
//		if (fread(data, 1, lenrd, fpFac) != lenrd)
//			break;
//
//		memcpy(p_fac_out, data, sizeof(WALLET_ELEMENT));
//		sizeOut += sizeof(WALLET_ELEMENT);
//
//		memcpy(p_fac_out + sizeOut, data + sizeOut, sizeof(COMPLEX_TRADE));
//		sizeOut += sizeof(COMPLEX_TRADE);
//
//		memcpy(pmd5src, data + sizeOut, 16);
//
//		ret = INTER_SUCCED;
//
//	} while (0);
//
//	if (!VAR_EQUAL(fpFac, NULL))
//		fclose(fpFac);
//
//	return ret;
//}
//
//_uint16 StorageMgr::get_MD5_factorEx(_uint8_p p_fac_out, _uint32& sizeOut)
//{
//	char szFacPathName[PATH_LENGTH] = {0};
//	_uint8 data[512]				= {0};
//	_uint32 lenrd					= 0;
//	FILE * fpFac					= NULL;
//	_uint16 ret						= INTER_FILE_READ;
//
//	do 
//	{
//		if (sizeof(WALLET_ELEMENT) + sizeof(COMPLEX_TRADE) > sizeOut)
//		{
//			ret = INTER_MEMORY_LACK;
//			break;
//		}
//
//		sizeOut = 0;
//
//		sprintf(szFacPathName, "%s/%s", m_cacheFolder, STORAGE_NAME);
//
//		fpFac = fopen(szFacPathName, "rb+");
//		if (VAR_EQUAL(fpFac, NULL))
//			ret = INTER_FILE_OPEN;
//		else if (fread(data, 1, lenrd, fpFac) != lenrd)
//			ret = INTER_FILE_READ;
//
//		if (VAR_EQUAL(m_pWallet, NULL))
//		{
//			if (!VAR_EQUAL(ret, INTER_SUCCED))
//				break;
//			memcpy(p_fac_out, data, sizeof(WALLET_ELEMENT));
//		}
//		else
//		{
//			memcpy(p_fac_out, m_pWallet, sizeof(WALLET_ELEMENT));
//		}
//		sizeOut += sizeof(WALLET_ELEMENT);
//
//		if (VAR_EQUAL(m_pComlex, NULL))
//		{
//			if (!VAR_EQUAL(ret, INTER_SUCCED))
//				break;
//			memcpy(p_fac_out + sizeOut, data + sizeOut, sizeof(COMPLEX_TRADE));
//		}
//		else
//		{
//			memcpy(p_fac_out + sizeOut, m_pComlex, sizeof(COMPLEX_TRADE));
//		}
//		sizeOut += sizeof(COMPLEX_TRADE);
//
//		ret = INTER_SUCCED;
//
//	} while (0);
//
//	if (!VAR_EQUAL(fpFac, NULL))
//		fclose(fpFac);
//
//	return ret;
//}
_uint16 StorageMgr::read_MD5(_uint8_p p_MD5)
{
	char szFacPathName[PATH_LENGTH] = {0};
	FILE * fpFac					= NULL;
	_uint16 ret						= INTER_FILE_WRITE;

	do 
	{
		ret = CheckDirectory(m_cacheFolder, true);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		sprintf(szFacPathName, "%s/%s", m_cacheFolder, STORAGE_NAME);

		fpFac = fopen(szFacPathName, "rb+");
		if (fpFac == NULL)
		{
			ret = INTER_FILE_OPEN;
			break;
		}

		if (fread(p_MD5, 1, 32, fpFac) != 32)
			break;

		ret = INTER_SUCCED;

	} while (0);

	if (!VAR_EQUAL(fpFac, NULL))
		fclose(fpFac);

	return ret;
}

_uint16 StorageMgr::save_MD5(_uint8_p p_MD5)
{
	char szFacPathName[PATH_LENGTH] = {0};
	FILE * fpFac					= NULL;
	_uint16 ret						= INTER_FILE_WRITE;

	do 
	{
		ret = CheckDirectory(m_cacheFolder, true);
		if (!VAR_EQUAL(ret, INTER_SUCCED))
			break;

		sprintf(szFacPathName, "%s/%s", m_cacheFolder, STORAGE_NAME);

		fpFac = fopen(szFacPathName, "wb+");
		if (fpFac == NULL)
		{
			ret = INTER_FILE_OPEN;
			break;
		}

		if (fwrite(p_MD5, 1, 32, fpFac) != 32)
			break;

		ret = INTER_SUCCED;

	} while (0);

	if (!VAR_EQUAL(fpFac, NULL))
		fclose(fpFac);

	return ret;
}

_uint16 StorageMgr::get_MD5_data(char * PathName, _uint8_p pdata, _uint32 sizeData, _uint32& sizeStep)
{
	_uint16 ret				= INTER_SUCCED;
	int fileStatus			= 0;
	_uint32 offset			= 0;
	FILE * fp				= NULL;

	// ���������ļ�, ���ж��Ƿ���ڣ��������򴴽������������Ƿ��д��
	do 
	{
		fp = fopen(PathName, "rb+");
		if (fp == NULL)
		{
			ret = INTER_FILE_OPEN;
			break;
		}

		sizeStep = FileSize(fp);
		if (sizeStep > sizeData)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}


		_uint32 sizerd = (_uint32)fread(pdata, 1, sizeStep, fp);
		if (sizeStep > sizerd || sizerd == 0)
		{
			ret = INTER_FILE_READ;
			break;
		}

		sizeData -= sizeStep;
		offset += sizeStep;

		// 默认二次添加地铁信息
		sizeStep = 59;
		if (sizeData < sizeStep)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		memcpy(pdata + offset, pdata + 116, sizeStep);
		sizeData -= sizeStep;
		offset += sizeStep;

		// 默认二次添加钱包信息
		sizeStep = sizeof(WALLET_ELEMENT);
		if (sizeData < sizeStep)
		{
			ret = INTER_MEMORY_LACK;
			break;
		}
		memcpy(pdata + offset, pdata + 244, sizeStep);
		sizeData -= sizeStep;
		offset += sizeStep;

		sizeStep = offset;

	} while (0);

	if (!VAR_EQUAL(fp, NULL))
		fclose(fp);

	return ret;
}

//void StorageMgr::DeleteOldCardFile(const char * pFolder)
//{
//	char szPathName[PATH_LENGTH]	= {0};
//
//	sprintf(szPathName, "%s/%s", pFolder, STORAGE_NAME);
//	remove(szPathName);
//}

_uint16 StorageMgr::CheckDirectory(char * pDir, bool failedCreate)
{
	char * ptemp		= NULL;
	char szDir[0xFF]	= {0};
	_uint16 ret			= INTER_SUCCED;

	if (VAR_EQUAL(pDir[0], '/'))
		ptemp = pDir + 1;
	else
		ptemp = pDir;

	while (ptemp != NULL) 
	{
		ptemp = strchr(ptemp, '/');
		if (VAR_EQUAL(ptemp, NULL))
		{
			strcpy(szDir, pDir);
		}
		else
		{
			memset(szDir, '\0', sizeof(szDir));
			strncpy(szDir, pDir, strlen(pDir) - strlen(ptemp));
			ptemp ++;
		}

		if (__ACCESS(szDir, 0x00) != 0)
		{
			if (failedCreate)
			{
				if (__MKDIR(szDir, 0755) != 0)
				{
					ret = INTER_FILE_CREATE;
					break;
				}
			}
			else
			{
				ret = INTER_FILE_NOT_EXSIST;
				break;
			}
		}
	}

	return ret;
}

char * StorageMgr::getAppFolder()
{
	return (char *)m_appFolder;
}

void StorageMgr::getLocalIme(_uint8_p pIme)
{
	memcpy(pIme, m_localIME, strlen(m_localIME));
}
