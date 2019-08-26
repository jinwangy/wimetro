// errors.h

#pragma once

#include "Types.h"

extern _uint16 g_internelErr;

#ifdef _WIN32
#pragma warning(disable : 4996)
#include<io.h>
#include <direct.h>
#define __ACCESS				_access
#define __MKDIR(a, b)			mkdir(a)
#define PATH_LENGTH				255

#define LOGE(...)

#else
#include<unistd.h>
#include <sys/stat.h>
#define __ACCESS				access
#define __MKDIR(a, b)			mkdir(a, b)
#define PATH_LENGTH				_POSIX_PATH_MAX

#include <android/log.h>

#define LOG_TAG "log_from_jni"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#endif


//----------- ָ������CLA ------------------------------
#define CLA_1				(_uint8)0x00
#define CLA_2				(_uint8)0x04
#define CLA_3				(_uint8)0x80
#define CLA_4				(_uint8)0x84

//----------- INS Byte ------------------------------
#define INS_SELECT			(_uint8)0xA4
#define INS_VERIFY       	(_uint8)0x20
#define INS_CHALLENGE    	(_uint8)0x84
#define INS_PIN_CHANGE		(_uint8)0x5E
#define INS_PIN_UNBLOCK   	(_uint8)0x24
#define INS_CARD_LOCK     	(_uint8)0x26
#define INS_READ_BIN     	(_uint8)0xB0
#define INS_READ_REC     	(_uint8)0xB2
#define INS_UPDATE_BIN   	(_uint8)0xD6
#define INS_UPDATE_REC   	(_uint8)0xDC
#define INS_FIRE_LOCK     	(_uint8)0xF6
#define INS_BALANCE       	(_uint8)0x5C
#define INS_INIT_TRANS    	(_uint8)0x50
#define INS_PURCHASE     	(_uint8)0x54
#define INS_LOAD         	(_uint8)0x52
#define INS_APP_BLOCK     	(_uint8)0x1E
#define INS_APP_UNBLOCK		(_uint8)0x18
#define INS_CARD_BLOCK		(_uint8)0x16
#define INS_GET_TRANS_CODE	(_uint8)0x5A

//----------- SW Code ------------------------------
#define SW_SUCCED								(_uint16)0x9000				// �ɹ�
#define SW_E_INTERNAL							(_uint16)0x6581
#define SW_WRONG_LENGTH							(_uint16)0x6700				// Lc����
#define SW_E_OPFTYPE							(_uint16)0x6981
#define	SW_SECURITY_STATUS_NOT_SATISFIED		(_uint16)0x6982				// �����㰲ȫ״̬
#define SW_CONDITIONS_NOT_SATISFIED				(_uint16)0x6985				// ������ʹ������
#define SW_E_SMDATA								(_uint16)0x6988
#define SW_E_FTYPE								(_uint16)0x6a02
#define SW_E_PINBLKED							(_uint16)0x6a83
#define SW_FILE_NOT_FOUND						(_uint16)0x6A82				// �ļ�δ�ҵ�
#define SW_RECORD_NOT_FOUND						(_uint16)0x6A83				// δ�ҵ���¼
#define SW_WRONG_P1P2							(_uint16)0x6A86				// P1P2����
#define SW_E_REFDATA							(_uint16)0x6a88
#define SW_E_APPBLK								(_uint16)0x6A81				// APP����
#define SW_E_UPCARD								(_uint16)0x6A74
#define SW_E_UPCARDSIO							(_uint16)0x6A78
#define SW_WRONG_DATA							(_uint16)0x6A80				// �������������
#define SW_INS_NOT_SUPPORTED					(_uint16)0x6D00				// INS����
#define SW_CLA_NOT_SUPPORTED					(_uint16)0x6E00				// CLA����

#define SW_WRONG_MAC							(_uint16)0x9302				// MAC����



///////////////////////////////////////////////////////////////////////////////////////

#define MODE_ENCRYPT							(_uint8)0xF1
#define MODE_DECRYPT							(_uint8)0xF2
#define ALG_DES									(_uint8)0x01
#define ALG_3DES								(_uint8)0x03


///////////////////////////////////////////////////////////////////////////////////////
#define VAR_EQUAL(a, b)							(bool)(a == b)
#define ARRAY_EQUAL(a, b, c)					(bool)(memcmp(a, b, c) == 0)

#define IF_EFFECT_COPY(a, b, c) \
	if (b != NULL) \
		memcpy(a, b, c);

// ���ļ�����Ĵ�С
#define CARD_BUFFER_MAX			(_uint16)2048

// ���ѻ�״̬
#define OFFLINE_CARD_INIT		(_uint8)0x00	// ��ʼ״̬
#define OFFLINE_CARD_DISABLE	(_uint8)0x00	// ��ʼ״̬
#define OFFLINE_CARD_NORMAL		(_uint8)0x00	// ����״̬
#define OFFLINE_CARD_UNKNOWN	(_uint8)0xFF	// δ֪״̬


///////////////////////////////////////////////////////////////////////////////////////
#define INTER_SUCCED							(_uint16)0x0000			// �ɹ�
#define INTER_FILE_NOT_EXSIST					(_uint16)0xE001			// �ļ�������
#define INTER_FILE_ACCESS						(_uint16)0xE002			// �ļ�Ȩ�޴���
#define INTER_FILE_OPEN							(_uint16)0xE003			// �ļ��򿪴���
#define INTER_FILE_WRITE						(_uint16)0xE004			// �ļ�д����
#define INTER_FILE_READ							(_uint16)0xE005			// �ļ�������
#define INTER_MEM_MALLOC						(_uint16)0xE006			// �ڴ����ʧ��
#define INTER_CARD_NOT_EXSIST					(_uint16)0xE007			// ��Ƭ������
#define INTER_CARD_STATUS						(_uint16)0xE008
#define INTER_CARD_MATCH1						(_uint16)0xE009			// ���ؿ�Ƭ����ƥ���쳣(�Ǳ��˺�����)
#define INTER_CARD_MATCH2						(_uint16)0xE00A			// ���ؿ�Ƭ����ƥ���쳣(�Ǳ�������)
#define INTER_CARD_MATCH3						(_uint16)0xE00B			// ���ؿ�Ƭ����ƥ���쳣(����ȷ����ʷ����)
#define INTER_CARD_WRITE						(_uint16)0xE00C			// д������
#define INTER_PARAM_INVALID						(_uint16)0xE00D			// �����쳣
#define INTER_DATA_OVER							(_uint16)0xE00E			// ���ݰ汾����
#define INTER_DATA_MD5							(_uint16)0xE00F			// ������У�����
#define INTER_DATA_ERR							(_uint16)0xE000			// �������쳣��ƫ�Ƴ����ļ���С��
#define INTER_FILE_CREATE						(_uint16)0xE001			// �ļ������ļ��д���ʧ��
#define INTER_MEMORY_LACK						(_uint16)0xE002			// �ڴ治��
#define INTER_VERSION_OLD						(_uint16)0xE013			// Ŀ�����ݰ汾��
#define INTER_TOKEN_SIGN						(_uint16)0xE014			// ��Ч��TOKENǩ��
#define INTER_TOKEN_IME							(_uint16)0xE015			// �Ǳ��������token
#define INTER_TOKEN_EXPIRE						(_uint16)0xE016			// token������Ч��
#define INTER_TOKEN_WALLET						(_uint16)0xE017			// token����
#define INTER_TOKEN_CNT							(_uint16)0xE018			// token��β���
#define INTER_TOKEN_UNINIT						(_uint16)0xE019			// tokenδ��ʼ��
#define INTER_TOKEN_DOMAIN						(_uint16)0xE01A			// token�����
