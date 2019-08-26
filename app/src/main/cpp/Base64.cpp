#include "Base64.h"
#include <ctype.h>
#include <string>

static const std::string base64_chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

Base64::Base64(void)
{
}

Base64::~Base64(void)
{
}

bool Base64::is_base64(_uint8 c)
{  
	return (isalnum(c) || (c == '+') || (c == '/'));
}

void Base64::base64_encode(_uint8_p p2encode , _uint32 len2encode, char * p_encode, _uint32_p lenencode)
{
	_uint32 i = 0;  
	_uint32 j = 0;
	_uint32 k = 0;
	_uint8 char_array_3[3];  
	_uint8 char_array_4[4];  

	while (len2encode--)
	{  
		char_array_3[i++] = *(p2encode++);
		if (i == 3)
		{  
			char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;  
			char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);  
			char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);  
			char_array_4[3] = char_array_3[2] & 0x3f;  

			for(i = 0; (i <4) ; i++)  
				p_encode[k++] = base64_chars[char_array_4[i]];

			i = 0;  
		}  
	}  

	if (i)  
	{  
		for(j = i; j < 3; j++)
			char_array_3[j] = '\0';  

		char_array_4[0] = (char_array_3[0] & 0xfc) >> 2;  
		char_array_4[1] = ((char_array_3[0] & 0x03) << 4) + ((char_array_3[1] & 0xf0) >> 4);  
		char_array_4[2] = ((char_array_3[1] & 0x0f) << 2) + ((char_array_3[2] & 0xc0) >> 6);  
		char_array_4[3] = char_array_3[2] & 0x3f;  

		for (j = 0; (j < i + 1); j++)  
			p_encode[k++] = base64_chars[char_array_4[j]];  

		while((i++ < 3))  
			p_encode[k++] = '=';  
	}
	* lenencode = k;
}

void Base64::base64_decode(const char * p2decode, _uint32 len2decode, _uint8_p p_decode, _uint32_p lendecode)
{
	int i = 0;  
	int j = 0;
	int k = 0;
	int in_ = 0;  
	_uint8 char_array_4[4];
	_uint8 char_array_3[3];  

	while (len2decode-- && ( p2decode[in_] != '=') && is_base64(p2decode[in_]))
	{  
		char_array_4[i++] = p2decode[in_]; in_++;  
		if (i == 4)
		{  
			for (i = 0; i <4; i++)  
				char_array_4[i] = base64_chars.find(char_array_4[i]);  

			char_array_3[0] = (char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4);  
			char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);  
			char_array_3[2] = ((char_array_4[2] & 0x3) << 6) + char_array_4[3];  

			for (i = 0; (i < 3); i++)  
				p_decode[k++] = char_array_3[i];  
			i = 0;  
		}  
	}  

	if (i) {  
		for (j = i; j <4; j++)  
			char_array_4[j] = 0;  

		for (j = 0; j <4; j++)  
			char_array_4[j] = base64_chars.find(char_array_4[j]);  

		char_array_3[0] = (char_array_4[0] << 2) + ((char_array_4[1] & 0x30) >> 4);  
		char_array_3[1] = ((char_array_4[1] & 0xf) << 4) + ((char_array_4[2] & 0x3c) >> 2);  
		char_array_3[2] = ((char_array_4[2] & 0x3) << 6) + char_array_4[3];  

		for (j = 0; (j < i - 1); j++)
			p_decode[k++] = char_array_3[j];  
	}

	* lendecode = k;
}
