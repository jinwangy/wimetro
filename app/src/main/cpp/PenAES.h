#pragma once

#include "Types.h"

class PenAES
{
public:
	PenAES(void);
	~PenAES(void);

public:
	// ���ܣ�����������С������16�ֽڵ�������
	static void Cipher(_uint8_p pkey, _uint8_p input, int len_input);

	// ���ܣ����������Ҳ������16�ֽڵ�������
	static void InvCipher(_uint8_p pkey, _uint8_p input, int len_input);

	// ���ܣ�����������С������16�ֽ�
	static _uint8_p Cipher(_uint8 fac1,_uint8 fac2, _uint8_p input);

	// ���ܣ����������Ҳ������16�ֽ�
	static _uint8_p InvCipher(_uint8 fac1,_uint8 fac2, _uint8_p input);

	// ���ܣ�����������С������16�ֽ�
	static _uint8_p Cipher(_uint8_p input);

	// ���ܣ����������Ҳ������16�ֽ�
	static _uint8_p InvCipher(_uint8_p input);

protected:
	static void KeyInit(_uint8 fac1,_uint8 fac2);

	// ���Դ������飬��С������16����������������ǽ���Խ��������������length��Ĭ��Ϊ0����ô�������ַ���������'\0'����
	static _uint8_p Cipher(_uint8_p input, int length);

	// ���봫������ʹ�С��������16��������
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

	// ͨ����Կ������չ�����ԭʼ��Կ
	static void KeyFromTable(const char * pFactor, _uint8 fac1,_uint8 fac2, _uint8_p pInitKey);

};

