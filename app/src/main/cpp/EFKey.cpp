#include "EFKey.h"
#include "ConstDef.h"
#include "PenAES.h"

EFKey::EFKey(void)
{

}


EFKey::~EFKey(void)
{
}

void EFKey::AddKey(_uint8_p pkeyInf, _uint32& offset, bool encrypt)
{
	PKEY_ITEM pItem = (PKEY_ITEM)(pkeyInf + offset);
	offset += sizeof(KEY_ITEM);

	if (encrypt)
	{
		PenAES::Cipher(pItem->classic, pItem->identfy, pItem->key);
	}

	m_keyTab.push_back(pItem);
}

void EFKey::AddKey(_uint8_p pkeyInf, _uint32& offset, _uint8 cla, _uint8 id, _uint8 ver, _uint8 ag, _uint8_p pkey, _uint8 len, bool encrypt)
{
	PKEY_ITEM pItem = (PKEY_ITEM)(pkeyInf + offset);
	offset += sizeof(KEY_ITEM);

	pItem->classic	= cla;
	pItem->identfy	= id;
	pItem->version	= ver;
	pItem->algo		= ag;
	pItem->len		= len;
	memcpy(pItem->key, pkey, len);
	if (encrypt)
	{
		PenAES::Cipher(pItem->classic, pItem->identfy, pItem->key);
	}

	m_keyTab.push_back(pItem);
}

_uint16 EFKey::SetKey(_uint8 cla, _uint8 id, _uint8 ver, _uint8 ag, _uint8_p pkey, _uint8 len)
{
	_uint16 ret = SW_SUCCED;
	_uint8 keycnt = (_uint8)m_keyTab.size();

	if (len > 16 || keycnt < 1)
	{
		ret = SW_CONDITIONS_NOT_SATISFIED;
	}
	else
	{
		PKEY_ITEM pItem = m_keyTab.at(keycnt - 1);
		pItem->classic	= cla;
		pItem->identfy	= id;
		pItem->version	= ver;
		pItem->algo		= ag;
		pItem->len		= len;
		memcpy(pItem->key, pkey, len);
	}

	return ret;
}

_uint16 EFKey::Update(_uint8 type, _uint8 index, _uint8 ver, _uint8 algo, _uint8 err_cnt, _uint8_p p_key, _uint8 len_key, bool encrypt)
{
	_uint16 ret = INTER_FILE_NOT_EXSIST;
	for (_uint8 i=0;i<m_keyTab.size();i++)
	{
		PKEY_ITEM pItem = m_keyTab[i];
		if (VAR_EQUAL(pItem->classic, type) && VAR_EQUAL(pItem->identfy, index))
		{
			m_keyTab[i]->version = ver;
			m_keyTab[i]->algo = algo;
			m_keyTab[i]->err_max = err_cnt;
			m_keyTab[i]->len = len_key;
			memcpy(m_keyTab[i]->key, p_key, len_key);

			if (encrypt)
			{
				PenAES::Cipher(type, index, m_keyTab[i]->key);
			}

			ret = INTER_SUCCED;
			break;
		}
	}

	return ret;
}

_uint16 EFKey::getKey(_uint8 cla, _uint8 indexkey, _uint8_p pver, _uint8_p palgo, _uint8_p pkey, bool decrypt)
{
	_uint16 ret = 1;
	for (_uint8 i=0;i<m_keyTab.size();i++)
	{
		const PKEY_ITEM pItem = m_keyTab[i];
		if (VAR_EQUAL(pItem->classic, cla) && VAR_EQUAL(pItem->identfy, indexkey))
		{
			*pver = pItem->version;
			*palgo = pItem->algo;
			memcpy(pkey, pItem->key, pItem->len);

			if (decrypt)
			{
				PenAES::InvCipher(pItem->classic, pItem->identfy, pkey);
			}

			ret = 0;
			break;
		}
	}

	return ret;
}