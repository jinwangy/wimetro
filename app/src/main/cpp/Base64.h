#pragma once

#include "Types.h"

class Base64
{
public:
	Base64(void);
	~Base64(void);

	static void base64_encode(_uint8_p p2encode , _uint32 len2encode, char * p_encode, _uint32_p lenencode);
	static void base64_decode(const char * p2decode, _uint32 len2decode, _uint8_p p_decode, _uint32_p lendecode);

protected:
	static bool is_base64(_uint8 c);
};

