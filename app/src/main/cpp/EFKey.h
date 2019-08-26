// 密钥

#pragma once

#include "Types.h"
#include "FileStruct.h"

#define CARD_MCTRL_KEY			(_uint8)0x01	// 卡片主控密钥
#define CARD_MAINT_KEY			(_uint8)0x02	// 卡片维护密钥
#define APP_MCTRL_KEY			(_uint8)0x03	// 应用主控密钥
#define APP_MAINT_KEY			(_uint8)0x04	// 应用维护密钥
#define CONSUME_KEY				(_uint8)0x05	// 消费密钥
#define CHARGE_KEY				(_uint8)0x06	// 圈存密钥
#define TAC_KEY					(_uint8)0x07	// TAC密钥
#define PIN_KEY					(_uint8)0x08	// PIN重装密钥
#define SUBCENTER_ATH_KEY		(_uint8)0x09	// 分中心认证密钥

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

	// 密钥长度集合
	vector<PKEY_ITEM> m_keyTab;
};

