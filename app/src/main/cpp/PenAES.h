#pragma once

#include "Types.h"

class PenAES
{
public:
	PenAES(void);
	~PenAES(void);

public:
	// 加密，传入的数组大小必须是16字节的整数倍
	static void Cipher(_uint8_p pkey, _uint8_p input, int len_input);

	// 解密，传入的数组也必须是16字节的整数倍
	static void InvCipher(_uint8_p pkey, _uint8_p input, int len_input);

	// 加密，传入的数组大小必须是16字节
	static _uint8_p Cipher(_uint8 fac1,_uint8 fac2, _uint8_p input);

	// 解密，传入的数组也必须是16字节
	static _uint8_p InvCipher(_uint8 fac1,_uint8 fac2, _uint8_p input);

	// 加密，传入的数组大小必须是16字节
	static _uint8_p Cipher(_uint8_p input);

	// 解密，传入的数组也必须是16字节
	static _uint8_p InvCipher(_uint8_p input);

protected:
	static void KeyInit(_uint8 fac1,_uint8 fac2);

	// 可以传入数组，大小必须是16的整数倍，如果不是将会越界操作；如果不传length而默认为0，那么将按照字符串处理，遇'\0'结束
	static _uint8_p Cipher(_uint8_p input, int length);

	// 必须传入数组和大小，必须是16的整数倍
	static _uint8_p InvCipher(_uint8_p input, int length);

private:
	static _uint8 m_Sbox[256];
	static _uint8 m_InvSbox[256];
	static _uint8 m_w[11][4][4];

	static void KeyExpansion(_uint8_p key, _uint8 w[][4][4]);
	static _uint8 FFmul(_uint8 a, _uint8 b);

	static void SubBytes(_uint8 state[][4]);
	static void ShiftRows(_uint8 state[][4]);
	static void MixColumns(_uint8 state[][4]);
	static void AddRoundKey(_uint8 state[][4], _uint8 k[][4]);

	static void InvSubBytes(_uint8 state[][4]);
	static void InvShiftRows(_uint8 state[][4]);
	static void InvMixColumns(_uint8 state[][4]);

	// 通过密钥因子扩展查表获得原始密钥
	static void KeyFromTable(const char * pFactor, _uint8 fac1,_uint8 fac2, _uint8_p pInitKey);

};

