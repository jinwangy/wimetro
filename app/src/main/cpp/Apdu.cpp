#include <string.h>
#include "Apdu.h"
#include "ConstDef.h"
#include "Types.h"

Apdu::Apdu(void)
{
}

Apdu::~Apdu(void)
{
}

Apdu::Apdu(_uint8_p p_apdu_in, _uint16 len_in)
{
	CLA = p_apdu_in[0];
	INS = p_apdu_in[1];
	P1 = p_apdu_in[2];
	P2 = p_apdu_in[3];

	if (CommandWithDatas())
	{
		Lc = p_apdu_in[4];
		memcpy(data, p_apdu_in + 5, Lc);

		if (len_in < Lc + 6)
			Le = 0;
		else
			Le = p_apdu_in[Lc + 5];
	}
	else
	{
		Lc = 0;
		if (len_in < 5)
			Le = 0;
		else
			Le = p_apdu_in[4];
	}
}

bool Apdu::CommandWithDatas()
{
	bool ret = false;

	switch (INS)
	{
	case INS_VERIFY:
	case INS_UPDATE_BIN:
	case INS_UPDATE_REC:
	case INS_PURCHASE:
	case INS_LOAD:
	case INS_SELECT:
	case INS_INIT_TRANS:
	case INS_GET_TRANS_CODE:
		ret = true;
		break;
	default:
		break;
	}

	return ret;
}

_uint16 Apdu::MakeRespondApdu(_uint8_p p_out)
{
	_uint16 ret = 2;

	if (SW != SW_SUCCED)
	{
		Le = 0;
	}

	memcpy(p_out, rsp, Le);
	p_out[Le] = (_uint8)((SW >> 8) & 0xFF);
	p_out[Le + 1] = (_uint8)(SW & 0xFF);

	ret += Le;

	return ret;
}
