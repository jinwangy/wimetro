#pragma once

#include "Types.h"


class Apdu
{
public:
	Apdu(void);
	Apdu(_uint8_p p_apdu_in, _uint16 len_in);
	~Apdu(void);

	bool CommandWithDatas();
	_uint16 MakeRespondApdu(_uint8_p p_out);

public:
	_uint8	CLA;
	_uint8	INS;
	_uint8	P1;
	_uint8	P2;
	_uint16	Lc;
	_uint8	data[512];
	_uint16 Le;
	_uint8	rsp[512];
	_uint16	SW;
};

