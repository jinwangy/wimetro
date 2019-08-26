// ��Կ

#pragma once

#include "Types.h"
#include "FileStruct.h"

#define CARD_MCTRL_KEY			(_uint8)0x01	// ��Ƭ������Կ
#define CARD_MAINT_KEY			(_uint8)0x02	// ��Ƭά����Կ
#define APP_MCTRL_KEY			(_uint8)0x03	// Ӧ��������Կ
#define APP_MAINT_KEY			(_uint8)0x04	// Ӧ��ά����Կ
#define CONSUME_KEY				(_uint8)0x05	// ������Կ
#define CHARGE_KEY				(_uint8)0x06	// Ȧ����Կ
#define TAC_KEY					(_uint8)0x07	// TAC��Կ
#define PIN_KEY					(_uint8)0x08	// PIN��װ��Կ
#define SUBCENTER_ATH_KEY		(_uint8)0x09	// ��������֤��Կ

class EFKey
{
public:
	EFKey(void);
	~EFKey(void);

	void AddKey(_uint8_p pkeyInf, _uint32& offset, bool encrypt);
	void AddKey(_uint8_p pkeyInf, _uint32& offset, _uint8 cla, _uint8 id, _uint8 ver, _uint8 ag, _uint8_p pkey, _uint8 len, bool encrypt);
	_uint16 SetKey(_uint8 cla, _uint8 id, _uint8 ver, _uint8 ag, _uint8_p pkey, _uint8 len);
	_uint16 Update(_uint8 type, _uint8 index, _uint8 ver, _uint8 algo, _uint8 err_cnt, _uint8_p p_key, _uint8 len_key, bool encrypt);
	 _uint16 getKey(_uint8 cla, _uint8 indexkey, _uint8_p pver, _uint8_p palgo, _uint8_p pkey, bool decrypt);

	 vector<PKEY_ITEM>& getKeyList() {
		 return m_keyTab;
	 }

private:

	// ��Կ���ȼ���
	vector<PKEY_ITEM> m_keyTab;
};

