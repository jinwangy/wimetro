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


//----------- 指令类码CLA ------------------------------
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
#define SW_SUCCED								(_uint16)0x9000				// 成功
#define SW_E_INTERNAL							(_uint16)0x6581
#define SW_WRONG_LENGTH							(_uint16)0x6700				// Lc错误
#define SW_E_OPFTYPE							(_uint16)0x6981
#define	SW_SECURITY_STATUS_NOT_SATISFIED		(_uint16)0x6982				// 不满足安全状态
#define SW_CONDITIONS_NOT_SATISFIED				(_uint16)0x6985				// 不满足使用条件
#define SW_E_SMDATA								(_uint16)0x6988
#define SW_E_FTYPE								(_uint16)0x6a02
#define SW_E_PINBLKED							(_uint16)0x6a83
#define SW_FILE_NOT_FOUND						(_uint16)0x6A82				// 文件未找到
#define SW_RECORD_NOT_FOUND						(_uint16)0x6A83				// 未找到记录
#define SW_WRONG_P1P2							(_uint16)0x6A86				// P1P2错误
#define SW_E_REFDATA							(_uint16)0x6a88
#define SW_E_APPBLK								(_uint16)0x6A81				// APP锁定
#define SW_E_UPCARD								(_uint16)0x6A74
#define SW_E_UPCARDSIO							(_uint16)0x6A78
#define SW_WRONG_DATA							(_uint16)0x6A80				// 数据域参数错误
#define SW_INS_NOT_SUPPORTED					(_uint16)0x6D00				// INS错误
#define SW_CLA_NOT_SUPPORTED					(_uint16)0x6E00				// CLA错误

#define SW_WRONG_MAC							(_uint16)0x9302				// MAC错误



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

// 卡文件缓冲的大小
#define CARD_BUFFER_MAX			(_uint16)2048

// 卡脱机状态
#define OFFLINE_CARD_INIT		(_uint8)0x00	// 初始状态
#define OFFLINE_CARD_DISABLE	(_uint8)0x00	// 初始状态
#define OFFLINE_CARD_NORMAL		(_uint8)0x00	// 正常状态
#define OFFLINE_CARD_UNKNOWN	(_uint8)0xFF	// 未知状态


///////////////////////////////////////////////////////////////////////////////////////
#define INTER_SUCCED							(_uint16)0x0000			// 成功
#define INTER_FILE_NOT_EXSIST					(_uint16)0xE001			// 文件不存在
#define INTER_FILE_ACCESS						(_uint16)0xE002			// 文件权限错误
#define INTER_FILE_OPEN							(_uint16)0xE003			// 文件打开错误
#define INTER_FILE_WRITE						(_uint16)0xE004			// 文件写错误
#define INTER_FILE_READ							(_uint16)0xE005			// 文件读错误
#define INTER_MEM_MALLOC						(_uint16)0xE006			// 内存分配失败
#define INTER_CARD_NOT_EXSIST					(_uint16)0xE007			// 卡片不存在
#define INTER_CARD_STATUS						(_uint16)0xE008
#define INTER_CARD_MATCH1						(_uint16)0xE009			// 本地卡片数据匹配异常(非本账号数据)
#define INTER_CARD_MATCH2						(_uint16)0xE00A			// 本地卡片数据匹配异常(非本机数据)
#define INTER_CARD_MATCH3						(_uint16)0xE00B			// 本地卡片数据匹配异常(非正确的历史数据)
#define INTER_CARD_WRITE						(_uint16)0xE00C			// 写卡错误
#define INTER_PARAM_INVALID						(_uint16)0xE00D			// 参数异常
#define INTER_DATA_OVER							(_uint16)0xE00E			// 数据版本过期
#define INTER_DATA_MD5							(_uint16)0xE00F			// 卡数据校验错误
#define INTER_DATA_ERR							(_uint16)0xE000			// 卡数据异常，偏移超出文件大小等
#define INTER_FILE_CREATE						(_uint16)0xE001			// 文件或者文件夹创建失败
#define INTER_MEMORY_LACK						(_uint16)0xE002			// 内存不足
#define INTER_VERSION_OLD						(_uint16)0xE013			// 目标数据版本旧
#define INTER_TOKEN_SIGN						(_uint16)0xE014			// 无效的TOKEN签名
#define INTER_TOKEN_IME							(_uint16)0xE015			// 非本机申请的token
#define INTER_TOKEN_EXPIRE						(_uint16)0xE016			// token超出有效期
#define INTER_TOKEN_WALLET						(_uint16)0xE017			// token余额不足
#define INTER_TOKEN_CNT							(_uint16)0xE018			// token余次不足
#define INTER_TOKEN_UNINIT						(_uint16)0xE019			// token未初始化
#define INTER_TOKEN_DOMAIN						(_uint16)0xE01A			// token域错误
