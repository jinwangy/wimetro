// º”Ω‚√‹À„∑®

#pragma once

#include "Types.h"


#define TEMP_PADDING_SIZE	1024

class PenCipher
{
public:
	PenCipher(void);
	~PenCipher(void);

	static void xorblock8(_uint8_p d1, _uint8_p d2, _uint32 len);

	static void notblock8(_uint8_p d1);

	static _uint16 pbocpadding(_uint8_p data, _uint16 len);

	static void cdes(_uint8_p akey, _uint8_p data, _uint16 dLen, _uint8_p r, _uint8 mode);

	static void tripledes(_uint8_p key3, _uint32 key_len, _uint8_p data, _uint16 dLen, _uint8_p Out, _uint8 mode); 

	static void gmac4(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 dl, _uint8_p mac, _uint8_p icv);

	static void gmac4(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 dl, _uint8_p mac);

	//###########################################
	//static void TTTMAC();

	static void diversify(_uint8_p MxK, _uint8_p factor, _uint8_p DxK);

	static _uint16 PBEncrypt(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 len, _uint8_p res);

	static _uint16 PBDecrypt(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 len, _uint8_p res);

	static void gen_SESPK(_uint8_p key, _uint8_p data, _uint16 dLen, _uint8_p r);

	static void String2Hexs(char * pString, _uint32 len, _uint8_p pHexs);

protected:
	static _uint8 Char2halfHex(char ch);

	static _uint8 Twochar2Hex(char * pchar);

	static void SetSubKey(_uint8 pSubKey[16][48], _uint8_p Key);

	static void Transform(_uint8_p Out, _uint8_p In, _uint8_p Table, int len);

	static void ByteToBit(_uint8_p Out, _uint8_p In, int bits);

	static void BitToByte(_uint8_p Out, _uint8_p In, int bits);

	static void RotateL(_uint8_p In, int len, int loop);

	static void DES(_uint8_p Out, _uint8_p In, _uint8 pSubKey[16][48], int Type);
	
	static void F_func(_uint8_p In, _uint8_p Ki);

	static void S_func(_uint8_p Out, _uint8_p In);
};

