// ���������ļ��ṹ

#pragma once

#include "Types.h"
#include <vector>
#include <string.h>
using namespace std;

#pragma pack(push, 1)

// ======================================================
// ˵������������Ŀ¼�洢������
// �������ͣ���ITEM��׺�����ļ�������
// ������Կ
// �����䳤��¼
// ����ѭ����¼
// ��Щ��Ŀû�е����ķ������ԣ��������ļ���ʽ��������
// ======================================================
//
// ======================= ������Կ ======================
typedef struct {
	_uint8		classic;			// ��Կ����
	_uint8		identfy;			// ��Կ����
	_uint8		version;			// �汾
	_uint8		algo;				// �㷨��ʶ
	_uint8		err_max;			// ��֤��������
	_uint8		len;				// 
	_uint8		key[16];
}KEY_ITEM, * PKEY_ITEM;

// ======================= ����ѭ�� ======================
typedef struct {
	_uint16		trade_cnt;			// �ѻ�/�����������
	_uint32		amount;				// ���׽��
	_uint8		trade_type;			// ��������
	_uint8		pos_id[6];			// �����ն�
	_uint8		trade_time[7];		// ����ʱ��
	_uint8		rfu[3];				// ����
} LOOP_ITEM;

// ======================= ������¼ ======================
//typedef struct {
//	_uint8		num;				// ��¼��
//	_uint16		len;				// ��¼����
//} REC_ITEM;

// =======================================================
// Ԫ�ļ�������ļ�����С��λ���е����ķ������ԣ���Ĭ�ϣ����أ�
// ���ļ�MF
// Ӧ���ļ�ADF
// �������ļ�
// ��Կ�ļ�
// Ǯ��
// ѭ���ļ�
// �䳤��¼�ļ�
// ========================================================

typedef struct {
	_uint16 dataVersion;			// ���ݰ汾
	_uint8 cardStatus;				// ��Ƭ״̬
	_uint8 RFU[64];					// Ԥ��
}CARD_EXTRA, * PCARD_EXTRA;

// ========================= MFԪ ==========================
typedef struct {
	_uint16		file_id;
	_uint8		app_type;
	_uint8		rfu;
	_uint8		atr_sfi;
	_uint8		dir_sfi;
	_uint8		fci_sfi;
	_uint8		acw;
	_uint8		rld_kid;
	_uint8		bld_kid;
	_uint8		limit_verify;
	_uint8		len_name;
	_uint8		mf_name[16];
	_uint32		adf_off;
	CARD_EXTRA	extra;
} MF_ELEMENT, * PMF_ELEMENT;

// ========================= ADFԪ =========================
typedef struct {
	_uint16		file_id;
	_uint16		file_size;
	_uint8		rfu[4];
	_uint8		fci_sfi;
	_uint8		acw;
	_uint8		rld_kid;
	_uint8		bld_kid;
	_uint8		limit_verify;
	_uint8		len_name;
	_uint8		adf_name[16];

	_uint32		key_off;	// ��Կ��Ϣ���ƫ�ƣ�4��
	_uint32		rec_off;	// ��¼�ļ�ƫ�ƣ�4��
	_uint32		loop_off;	// ѭ���ļ�ƫ�ƣ�4��
	_uint32		wallet_off;	// Ǯ����Ϣƫ�ƣ�4��
	_uint32		bin_off;	// bin�ļ�����ƫ��

} ADF_ELEMENT, * PADF_ELEMENT;

// ========================= ������Ԫ =========================
typedef struct {
	_uint8		ef_sfi;				// ���ļ���ʶ��
	_uint16		sfi;				// �ļ���ʶ��
	_uint8		acr;				// ��ȡ����
	_uint8		acw;				// ��ȡ����
	_uint16		len;				// �ļ���С
} BIN_ELEMENT, * PBIN_ELEMENT;

// ========================= Ǯ��Ԫ =========================
typedef struct {
	_uint8		EP_balance[4];			// Ǯ�����
	_uint8		EP_offline[2];			// �����������
	_uint8		EP_online[2];			// �ѻ��������
	_uint32		overdraft;				// ͸֧���
	_uint8		tac[4];					// TAC��
	_uint8		mac2[4];				// MAC2
	_uint8		gtp_type;				// �������ͣ�02����ֵ��06����ͨ���ѣ�09����������
	_uint8		gtp_ready;				// �Ƿ������������TAC���´����ѳ�ʼ��������
}WALLET_ELEMENT, * PWALLET_ELEMENT;

// ========================= ��¼�ļ�Ԫ =========================
typedef struct {
	_uint8		ef_sfi;				// ���ļ���ʶ��
	_uint16		sfi;				// �ļ���ʶ��
	_uint8		acr;				// ��ȡ����
	_uint8		acw;				// ��ȡ����
	_uint16		ef_Size;			// �ļ���С
	_uint8		cnt;				// ��¼����
}REC_ELEMENT, * PREC_ELEMENT;

// ========================= ѭ���ļ�Ԫ =========================
typedef struct {
	_uint8		ef_sfi;				// ���ļ���ʶ��
	_uint16		sfi;				// �ļ���ʶ��
	_uint8		acr;				// ��ȡ����
	_uint8		acw;				// ��ȡ����
	_uint16		ef_Size;			// �ļ���С
	_uint8		ptr;				// ��ǰ��¼ָ��
}LP_ELEMENT, *PLP_ELEMENT;

///////////////////////////////////////////////////////////////////////////////////////////////

// ���ϼ�¼�ļ�01�ṹ�壬����д����
typedef struct {
	_uint8 rfu[4];				// BCD	�������ѱ�ʶ + HEX	��¼���� + BCD	��������������־λ + HEX	
	_uint8 entry_dev[2];		// HEX	��բ���豸���
	_uint8 entry_dev_seq[3];	// HEX	��բ�豸���ɵĽ�����ˮ��
	_uint8 entry_station[2];	// HEX	��բ��վ
	_uint8 entry_time[6];		// BCD	��բʱ��
	_uint8 card_status;			// HEX	��բ/��բ/�ϳ�/�³�/����/��վƱ ��־
	_uint8 daliy_cnt_date[4];	// BCD	�ճ�������������
	_uint8 daliy_cnt;			// HEX	�ճ�������
	_uint8 last_dev[4];			// HEX	���һ�ν����豸���
	_uint8 last_dev_seq[3];		// HEX	���һ�ν����豸���ɵĽ�����ˮ��
	_uint8 last_amount[3];		// HEX	���һ�ν��׽��
	_uint8 last_type;			// HEX	���һ�ν�������
	_uint8 last_time[5];		// BCD	���һ�ν���ʱ��
	_uint8 over_draft[4];		// HEX	͸֧�޶�
	_uint8 senior_flag;			// HEX	�������Ʊʹ�ñ�־
	_uint8 off_free_station[2];	// HEX	�ѻ���ѳ�վ��վ��
	_uint8 update_dev[2];		// HEX	�����豸���
	_uint8 update_station[2];	// HEX	���³�վ����
	_uint8 update_time[5];		// BCD	����ʱ��
	_uint8 update_amount[3];	// HEX	���½��
	_uint8 update_cnt[2];		// HEX	�����ܴ���
	_uint8 update_reason;		// HEX	����ԭ�����

}COMPLEX_TRADE, * PCOMPLEX_TRADE;

typedef struct {
	char a_trade_dev[8];		// �����豸��
	char a_trade_group[2];		// �����������
	char a_trade_seq[8];		// �������к�
	char c_trade_station[4];	// ����վ��
	char c_trade_type[2];		// ��������
	char c_ticket_type[4];		// Ʊ������
	char c_amount_sec[2];		// ���δ���
	char c_samid[8];			// SAM������
	char c_logicid[16];			// �߼�����
	char c_off_cnt[6];			// Ʊ�����׼���
	char c_amount[8];			// ���׽��
	char c_balance[8];			// Ʊ�����
	char c_trade_time[14];		// ����ʱ��
	char c_last_dev[8];			// �ϴν��׵��豸��
	char c_last_seq[8];			// �ϴν������к�
	char c_last_amount[8];		// �ϴν��׽��
	char c_last_time[14];		// �ϴν���ʱ��
	char c_tac[8];				// ������֤��
	char c_degrade[4];			// ����ģʽ
	char c_entry_station[4];	// ��բ��վ
	char c_entry_dev[4];		// ��բ�豸
	char c_entry_time[14];		// ��բʱ��
	char RFU[18];				// Ԥ��
}ACC_TRADE, * PACC_TRADE;

// ���ڸ�������������ı���ṹ
typedef struct {
	_uint8	sfi;
	_uint8	num;		// ��¼�ļ���Ч
	_uint8	data[128];
	_uint16	len;
	_uint8	old[128];

}TRANS_STACK, * PTRANS_STACK;

#pragma pack(pop)