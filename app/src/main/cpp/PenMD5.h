#pragma once

#include "Types.h"


#define BLOCK_SIZE			64


class PenMD5
{
public:
	PenMD5();  
	PenMD5(_uint8_p pSrctext, _uint32 len);  
	PenMD5& finalize();  

	void update(const _uint8_p buf, _uint32 length);

	void getMD5_Hex(_uint8_p pMD5);

private:
	void init();  

	void transform(const _uint8 block[BLOCK_SIZE]);  
	static void decode(_uint32 output[], const _uint8 input[], _uint32 len);  
	static void encode(_uint8 output[], const _uint32 input[], _uint32 len);  

	bool finalized;

	_uint8 buffer[BLOCK_SIZE];	// bytes that didn't fit in last 64 byte chunk  
	_uint32 count[2];			// 64bit counter for number of bits (lo, hi)  
	_uint32 state[4];			// digest so far  
	_uint8 digest[16];			// the result  

	// low level logic operations  
	_uint32 F(_uint32 x, _uint32 y, _uint32 z);  
	_uint32 G(_uint32 x, _uint32 y, _uint32 z);  
	_uint32 H(_uint32 x, _uint32 y, _uint32 z);  
	_uint32 I(_uint32 x, _uint32 y, _uint32 z);  
	_uint32 rotate_left(_uint32 x, int n);  
	void FF(_uint32 &a, _uint32 b, _uint32 c, _uint32 d, _uint32 x, _uint32 s, _uint32 ac);  
	void GG(_uint32 &a, _uint32 b, _uint32 c, _uint32 d, _uint32 x, _uint32 s, _uint32 ac);  
	void HH(_uint32 &a, _uint32 b, _uint32 c, _uint32 d, _uint32 x, _uint32 s, _uint32 ac);  
	void II(_uint32 &a, _uint32 b, _uint32 c, _uint32 d, _uint32 x, _uint32 s, _uint32 ac); 
};

