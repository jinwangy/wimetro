#include "PenCipher.h"
#include <string.h>
#include "ConstDef.h"


const _uint8 IP_Table[64] =  
{
	58, 50, 42, 34, 26, 18, 10, 2,  
	60, 52, 44, 36, 28, 20, 12, 4,  
	62, 54, 46, 38, 30, 22, 14, 6,  
	64, 56, 48, 40, 32, 24, 16, 8,  
	57, 49, 41, 33, 25, 17,  9, 1,  
	59, 51, 43, 35, 27, 19, 11, 3,  
	61, 53, 45, 37, 29, 21, 13, 5,  
	63, 55, 47, 39, 31, 23, 15, 7
}; 

const _uint8 IPR_Table[64] = 
{
	40, 8, 48, 16, 56, 24, 64, 32,  
	39, 7, 47, 15, 55, 23, 63, 31,  
	38, 6, 46, 14, 54, 22, 62, 30,  
	37, 5, 45, 13, 53, 21, 61, 29,  
	36, 4, 44, 12, 52, 20, 60, 28,  
	35, 3, 43, 11, 51, 19, 59, 27,  
	34, 2, 42, 10, 50, 18, 58, 26,  
	33, 1, 41,  9, 49, 17, 57, 25
};

const _uint8 E_Table[48] = 
{  
	32,  1,  2,  3,  4,  5,  
	4,  5,  6,  7,  8,  9,  
	8,  9, 10, 11, 12, 13,  
	12, 13, 14, 15, 16, 17,  
	16, 17, 18, 19, 20, 21,  
	20, 21, 22, 23, 24, 25,  
	24, 25, 26, 27, 28, 29,  
	28, 29, 30, 31, 32,  1  
};

const _uint8 P_Table[32] =
{  
	16, 7, 20, 21,  
	29, 12, 28, 17,  
	1,  15, 23, 26,  
	5,  18, 31, 10,  
	2,  8, 24, 14,  
	32, 27, 3,  9,  
	19, 13, 30, 6,  
	22, 11, 4,  25  
};

const _uint8 PC1_Table[56] =  
{  
	57, 49, 41, 33, 25, 17,  9,  
	1, 58, 50, 42, 34, 26, 18,  
	10,  2, 59, 51, 43, 35, 27,  
	19, 11,  3, 60, 52, 44, 36,  
	63, 55, 47, 39, 31, 23, 15,  
	7, 62, 54, 46, 38, 30, 22,  
	14,  6, 61, 53, 45, 37, 29,  
	21, 13,  5, 28, 20, 12,  4  
};  

const _uint8 PC2_Table[48] =  
{  
	14, 17, 11, 24,  1,  5,  
	3, 28, 15,  6, 21, 10,  
	23, 19, 12,  4, 26,  8,  
	16,  7, 27, 20, 13,  2,  
	41, 52, 31, 37, 47, 55,  
	30, 40, 51, 45, 33, 48,  
	44, 49, 39, 56, 34, 53,  
	46, 42, 50, 36, 29, 32  
};  

const _uint8 LOOP_Table[16] =  
{
	1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1  
};  

const _uint8 S_Box[8][4][16] =  
{
	{  
		{14,  4, 13,  1,  2, 15, 11,  8,  3, 10,  6, 12,  5,  9,  0,  7},  
		{ 0, 15,  7,  4, 14,  2, 13,  1, 10,  6, 12, 11,  9,  5,  3,  8},  
		{ 4,  1, 14,  8, 13,  6,  2, 11, 15, 12,  9,  7,  3, 10,  5,  0},  
		{15, 12,  8,  2,  4,  9,  1,  7,  5, 11,  3, 14, 10,  0,  6, 13}
	},
	{
		{15,  1,  8, 14,  6, 11,  3,  4,  9,  7,  2, 13, 12,  0,  5, 10},  
		{ 3, 13,  4,  7, 15,  2,  8, 14, 12,  0,  1, 10,  6,  9, 11,  5},  
		{ 0, 14,  7, 11, 10,  4, 13,  1,  5,  8, 12,  6,  9,  3,  2, 15},  
		{13,  8, 10,  1,  3, 15,  4,  2, 11,  6,  7, 12,  0,  5, 14,  9}  
	},  
	{  
		{10,  0,  9, 14,  6,  3, 15,  5,  1, 13, 12,  7, 11,  4,  2,  8},  
		{13,  7,  0,  9,  3,  4,  6, 10,  2,  8,  5, 14, 12, 11, 15,  1},  
		{13,  6,  4,  9,  8, 15,  3,  0, 11,  1,  2, 12,  5, 10, 14,  7},  
		{ 1, 10, 13,  0,  6,  9,  8,  7,  4, 15, 14,  3, 11,  5,  2, 12}  
	},  
	{  
		{ 7, 13, 14,  3,  0,  6,  9, 10,  1,  2,  8,  5, 11, 12,  4, 15},  
		{13,  8, 11,  5,  6, 15,  0,  3,  4,  7,  2, 12,  1, 10, 14,  9},  
		{10,  6,  9,  0, 12, 11,  7, 13, 15,  1,  3, 14,  5,  2,  8,  4},  
		{ 3, 15,  0,  6, 10,  1, 13,  8,  9,  4,  5, 11, 12,  7,  2, 14}  
	},  
	{  
		{ 2, 12,  4,  1,  7, 10, 11,  6,  8,  5,  3, 15, 13,  0, 14,  9},  
		{14, 11,  2, 12,  4,  7, 13,  1,  5,  0, 15, 10,  3,  9,  8,  6},  
		{ 4,  2,  1, 11, 10, 13,  7,  8, 15,  9, 12,  5,  6,  3,  0, 14},  
		{11,  8, 12,  7,  1, 14,  2, 13,  6, 15,  0,  9, 10,  4,  5,  3}  
	},  
	{  
		{12,  1, 10, 15,  9,  2,  6,  8,  0, 13,  3,  4, 14,  7,  5, 11},  
		{10, 15,  4,  2,  7, 12,  9,  5,  6,  1, 13, 14,  0, 11,  3,  8},  
		{ 9, 14, 15,  5,  2,  8, 12,  3,  7,  0,  4, 10,  1, 13, 11,  6},  
		{ 4,  3,  2, 12,  9,  5, 15, 10, 11, 14,  1,  7,  6,  0,  8, 13}  
	},  
	{  
		{ 4, 11,  2, 14, 15,  0,  8, 13,  3, 12,  9,  7,  5, 10,  6,  1},  
		{13,  0, 11,  7,  4,  9,  1, 10, 14,  3,  5, 12,  2, 15,  8,  6},  
		{ 1,  4, 11, 13, 12,  3,  7, 14, 10, 15,  6,  8,  0,  5,  9,  2},  
		{ 6, 11, 13,  8,  1,  4, 10,  7,  9,  5,  0, 15, 14,  2,  3, 12}  
	},  
	{  
		{13,  2,  8,  4,  6, 15, 11,  1, 10,  9,  3, 14,  5,  0, 12,  7},  
		{ 1, 15, 13,  8, 10,  3,  7,  4, 12,  5,  6, 11,  0, 14,  9,  2},  
		{ 7, 11,  4,  1,  9, 12, 14,  2,  0,  6, 10, 13, 15,  3,  5,  8},  
		{ 2,  1, 14,  7,  4, 10,  8, 13, 15, 12,  9,  0,  3,  5,  6, 11}  
	}  
};  

PenCipher::PenCipher(void)
{
}

PenCipher::~PenCipher(void)
{
}

void PenCipher::xorblock8(_uint8_p d1, _uint8_p d2, _uint32 len)
{
	for(_uint16 i = 0; i < len; i++) 
	{
		d1[i] ^= d2[i];
	}
}

void PenCipher::notblock8(_uint8_p d1 )
{
	for(_uint16 i = 0; i < 8; i++)
	{
		d1[i] = (_uint8)(~d1[i]);
	}
}

_uint16 PenCipher::pbocpadding(_uint8_p data, _uint16 len)
{
	_uint16 f;

	data[len] = (_uint8)(0x80);
	len ++;

	f = (_uint16)(len % 8);
	f = (_uint16)(8 - f);

	if (f != 8)
		memset(data + len, 0x00, f);
	else
		f = 0;

	return (_uint16)(len + f);
}

void PenCipher::cdes(_uint8_p akey, _uint8_p data, _uint16 dLen, _uint8_p r, _uint8 mode) 
{  
	int i,j;  

	_uint8 SubKey[16][48] = {0};  

	memset(SubKey, 0x00, sizeof(SubKey));  

	SetSubKey(SubKey, akey);  

	for (i=0,j=dLen>>3; i<j; ++i,r+=8,data+=8)  
	{  
		DES(r, data, SubKey, mode);
	}  
}  

void PenCipher::tripledes(_uint8_p key3, _uint32 key_len, _uint8_p data, _uint16 dLen, _uint8_p Out, _uint8 mode)
{  
	int i,j;  

	_uint8 SubKey[3][16][48] = {0};  

	_uint8 nKey;  

	nKey = (key_len>>3)>3 ? 3 : (key_len>>3);  

	memset(SubKey, 0x00, sizeof(SubKey));  

	for ( i=0; i<nKey; i++)  
	{  
		SetSubKey(SubKey[i], &key3[i<<3]);  
	}  

	if (VAR_EQUAL(nKey, 1))
	{  
		for (i=0,j=dLen>>3; i<j; ++i,Out+=8,data+=8)  
		{  
			DES(Out, data, SubKey[0], mode);  
		}  
	}  
	else if (VAR_EQUAL(nKey, 2)) 
	{  
		for (i=0,j=dLen>>3; i<j; ++i,Out+=8,data+=8)  
		{  
			DES(Out, data, SubKey[0], mode);  

			DES(Out, Out, SubKey[1], mode == MODE_ENCRYPT ? MODE_DECRYPT : MODE_ENCRYPT);  

			DES(Out, Out, SubKey[0], mode);  
		}  
	}  
	else if (VAR_EQUAL(nKey, 3))
	{  
		for (i=0,j=dLen>>3; i<j; ++i,Out+=8,data+=8)  
		{  
			DES(Out, data, SubKey[mode == MODE_ENCRYPT ? 2 : 0], mode);  

			DES(Out, Out, SubKey[1], mode == MODE_ENCRYPT ? MODE_DECRYPT : MODE_ENCRYPT);  

			DES(Out, Out, SubKey[mode == MODE_ENCRYPT ? 0 : 2], mode);  
		}  
	}  
}  

void PenCipher::gmac4(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 dl, _uint8_p mac, _uint8_p icv)
{
	_uint8 tbuf1[8]							= {0};
	_uint8 tbuf2[8]							= {0};
	_uint8 datapading[TEMP_PADDING_SIZE]	= {0};

	if ((dl - (dl % 8) + 8) > TEMP_PADDING_SIZE)
		return;

	memcpy(datapading, data, dl);

	dl = pbocpadding(datapading, dl);

	memcpy(tbuf1, icv, 8);

	for(_uint16 i = 0; i < dl; i += 8)
	{
		xorblock8(tbuf1, datapading + i, 8);

		cdes(key, tbuf1, 8, tbuf2, MODE_ENCRYPT);
		memcpy(tbuf1, tbuf2, 8);
	}

	if (VAR_EQUAL(alg, ALG_3DES))
	{
		xorblock8(tbuf1, datapading + dl - 8, 8);

		cdes(key + 8, tbuf1, 8, tbuf2, MODE_DECRYPT);
		cdes(key, tbuf2, 8, tbuf1, MODE_ENCRYPT);
	}

	memcpy(mac, tbuf1, 4);
}

void PenCipher::gmac4(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 dl, _uint8_p mac)
{
	_uint8 tbuf1[8]							= {0};
	_uint8 tbuf2[8]							= {0};
	_uint8 datapading[TEMP_PADDING_SIZE]	= {0};

	if ((dl - (dl % 8) + 8) > TEMP_PADDING_SIZE)
		return;

	memcpy(datapading, data, dl);


	dl = pbocpadding(datapading, dl);

	for (_uint16 i = 0; i < dl; i += 8)
	{
		xorblock8(tbuf1, datapading + i, 8);

		cdes(key, tbuf1, 8, tbuf2, MODE_ENCRYPT);

		memcpy(tbuf1, tbuf2, 8);
	}

	if (VAR_EQUAL(alg, ALG_3DES)) 
	{
		xorblock8(tbuf1, datapading + dl - 8, 8);

		cdes(key + 8, tbuf1, 8, tbuf2, MODE_DECRYPT);
		cdes(key, tbuf2, 8, tbuf1, MODE_ENCRYPT);
	}

	memcpy(mac, tbuf1, 4);
}

//void PenCipher::TTTMAC()
//{
//	char tmpchar[256]		= {0};
//	_uint8 sessionkey[16]	= {0};
//	_uint8 data[128]		= {0};
//	_uint8 inputdata[8]		= {0};
//
//	memcpy(tmpchar, "1E801C01EC0CDDFEFF5259B1A533CC14", 32);
//	String2Hexs(tmpchar, 32, sessionkey);
//
//	memcpy(tmpchar, "798189510000000004DC01BC41013B0000012D00049E00E71710171713151020171017030023409D00049F0000B410171017171300000000000000012D00E717101717110000C8000300800000000000", 160);
//	String2Hexs(tmpchar, 180, data);
//
//	for(int i=0; i<8; i++)
//		inputdata[i] = data[i] ^ data[8+i];
//	for(int i=2; i<10; i++)
//	{
//		cdes(sessionkey, inputdata, 8, inputdata, MODE_ENCRYPT);
//		for(int j=0; j<8; j++)
//			inputdata[j] ^= data[i*8 + j];
//	}
//	cdes(sessionkey, inputdata, 8, inputdata, MODE_ENCRYPT);
//	cdes(sessionkey+8, inputdata, 8, inputdata, MODE_DECRYPT);
//	cdes(sessionkey, inputdata, 8, inputdata, MODE_ENCRYPT);
//}

void PenCipher::diversify(_uint8_p MxK, _uint8_p factor, _uint8_p DxK)
{
	_uint8 tbuf1[8]	= {0};
	_uint8 tbuf2[8]	= {0};

	memcpy(tbuf2, factor, 8);

	tripledes(MxK, 8, tbuf2, 8, tbuf1, MODE_ENCRYPT);
	memcpy(DxK, tbuf1, 8);

	notblock8(tbuf2);

	tripledes(MxK, 8, tbuf2, 8, tbuf1, MODE_ENCRYPT);
	memcpy(DxK, tbuf1, 8);
}

_uint16 PenCipher::PBEncrypt(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 len, _uint8_p res)
{
	memcpy(res + 1, data, len);
	res[0] = (_uint8)len;

	if (((len + 1) % 8) > 0 )
		len = pbocpadding(res, len + 1);
	else
		len += 1;

	if (VAR_EQUAL(alg, ALG_3DES))
	{
		tripledes(key, 8, data, len, res, MODE_ENCRYPT);
	}
	else
	{
		cdes(key, data, len, res, MODE_ENCRYPT);
	}

	return len;
}

_uint16 PenCipher::PBDecrypt(_uint8 alg, _uint8_p key, _uint8_p data, _uint16 len, _uint8_p res)
{
	if (VAR_EQUAL(alg, ALG_3DES))
		tripledes(key, 8, data, len, res, MODE_DECRYPT);
	else
		cdes(key, data, len, res, MODE_DECRYPT);

	len = res[0];

	memcpy(res, res + 1, len);

	return len;
}

void PenCipher::gen_SESPK(_uint8_p key, _uint8_p data, _uint16 dLen, _uint8_p r)
{
	_uint8 temp1[8] = {0};
	_uint8 temp2[8] = {0};

	//3DES
	cdes(key, data, dLen, temp1, MODE_ENCRYPT);
	cdes(key + 8, temp1, dLen, temp2, MODE_DECRYPT);
	cdes(key, temp2, dLen, r, MODE_ENCRYPT);
}

_uint8 PenCipher::Char2halfHex(char ch)
{
#define SUB_CAPITAL			(_uint8)('A' - 0x0A)
#define SUB_SMALL			(_uint8)('a' - 0x0A)
#define SUB_NUMERIC			(_uint8)0x30		

	_uint8 half_byte_hex = 0;
	if (ch >= '0' && ch <= '9')
		half_byte_hex = (_uint8)(ch - SUB_NUMERIC);
	else if (ch >= 'a' && ch <= 'z')
		half_byte_hex = (_uint8)(ch - SUB_SMALL);
	else if (ch >= 'A' && ch <= 'Z')
		half_byte_hex = (_uint8)(ch - SUB_CAPITAL);

	return half_byte_hex;
}

_uint8 PenCipher::Twochar2Hex(char * pchar)
{
	return (_uint8)((Char2halfHex(pchar[0]) << 4) + Char2halfHex(pchar[1]));
}

void PenCipher::String2Hexs(char * pString, _uint32 len, _uint8_p pHexs)
{
	_uint32 ps, ph;

	if (len % 2 != 0)	len--;

	for (ps=0,ph=0; ps<len; ps+=2,ph++)
	{
		pHexs[ph] = Twochar2Hex(pString + ps);
	}
}

void PenCipher::SetSubKey(_uint8 pSubKey[16][48], _uint8_p Key)  
{  
	int i;  

	_uint8 K[64]	= {0};
	_uint8_p KL		= K;
	_uint8_p KR		= K + 28;  

	ByteToBit(K, Key, 64);  

	Transform(K, K, (_uint8_p)PC1_Table, 56);  

	for ( i=0; i<16; ++i)  
	{  
		RotateL(KL, 28, LOOP_Table[i]);  

		RotateL(KR, 28, LOOP_Table[i]);  

		Transform(pSubKey[i], K, (_uint8_p)PC2_Table, 48);  
	}  
}  

void PenCipher::Transform(_uint8_p Out, _uint8_p In, _uint8_p Table, int len)  
{  
	char szTmp[256] = {0};  
	int i;  

	if (!Out || !In || !Table) return;  
	if (len >= 256) return;  

	memset(szTmp, 0x00, sizeof(szTmp));  

	for (i=0; i<len; ++i)
	{
		szTmp[i] = In[Table[i]-1];  
	}

	memcpy(Out, szTmp, len);  
}  

void PenCipher::ByteToBit(_uint8_p Out, _uint8_p In, int bits)  
{  
	int i;  

	for (i=0; i<bits; ++i) 
	{
		Out[i] = (In[i>>3]>>(7 - i&7)) & 1;  
	}
}  

void PenCipher::BitToByte(_uint8_p Out, _uint8_p In, int bits)  
{  
	int i;  

	memset(Out, 0, bits>>3);  

	for (i=0; i<bits; ++i)
	{
		Out[i>>3] |= In[i]<<(7 - i&7);  
	}
}  

void PenCipher::RotateL(_uint8_p In, int len, int loop)  
{  
	_uint8 szTmp[256] = {0}; 

	if (len >= 256) return;  
	if (VAR_EQUAL(loop, 0) || loop>=256) return;  

	memset(szTmp, 0x00, sizeof(szTmp));  

	memcpy(szTmp, In, loop);  

	memmove(In, In+loop, len-loop);  

	memcpy(In+len-loop, szTmp, loop);  
}  

void PenCipher::DES(_uint8_p Out, _uint8_p In, _uint8 pSubKey[16][48], int Type)
{  
	int i;  

	_uint8 M[64]	= {0};
	_uint8_p ML		= M;
	_uint8_p MR		= M + 32;
	_uint8 szTmp[32] = {0};  

	ByteToBit(M, In, 64);  
	Transform(M, M, (_uint8_p)IP_Table, 64);  

	if (VAR_EQUAL(Type, MODE_ENCRYPT))
	{  
		for (i=0; i<16; ++i)  
		{  
			memcpy(szTmp, MR, 32);  

			F_func(MR, (_uint8_p)pSubKey[i]);  

			xorblock8(MR, ML, 32);  

			memcpy(ML, szTmp, 32);  
		}  
	}  
	else  
	{  
		for (i=15; i>=0; --i)  
		{  
			memcpy(szTmp, MR, 32); 

			F_func(MR, pSubKey[i]);  

			xorblock8(MR, ML, 32);  

			memcpy(ML, szTmp, 32);  
		}  
	}  

	RotateL(M, 64, 32);  

	Transform(M, M, (_uint8_p)IPR_Table, 64);  

	BitToByte(Out, M, 64);  
}  

void PenCipher::F_func(_uint8_p In, _uint8_p Ki)
{  
	_uint8 MR[48] = {0};  

	memset(MR, 0x00, sizeof(MR));

	Transform(MR, In, (_uint8_p)E_Table, 48);  
	xorblock8(MR, Ki, 48);  
	S_func(In, MR);  
	Transform(In, In, (_uint8_p)P_Table, 32);  
}  

void PenCipher::S_func(_uint8_p Out, _uint8_p In)  
{  
	int i,j,k,l;  

	for (i=0,j=0,k=0; i<8; ++i,In+=6,Out+=4)  
	{  
		j = (In[0]<<1) + In[5];  
		k = (In[1]<<3) + (In[2]<<2) + (In[3]<<1) + In[4];  

		for ( l=0; l<4; ++l)  
		{
			Out[l] = (S_Box[i][j][k]>>(3 - l)) & 1;  
		}
	}  
}  
