// 定义所有文件结构

#pragma once

#include "Types.h"
#include <vector>
#include <string.h>
using namespace std;

#pragma pack(push, 1)

// ======================================================
// 说明，按照树形目录存储，其中
// 基本类型（以ITEM后缀）的文件包含：
// 单条密钥
// 单条变长记录
// 单条循环记录
// 这些项目没有单独的访问属性，不能以文件形式单独存在
// ======================================================
//
// ======================= 单条密钥 ======================
typedef struct {
	_uint8		classic;			// 密钥分类
	_uint8		identfy;			// 密钥索引
	_uint8		version;			// 版本
	_uint8		algo;				// 算法标识
	_uint8		err_max;			// 认证错误限制
	_uint8		len;				// 
	_uint8		key[16];
}KEY_ITEM, * PKEY_ITEM;

// ======================= 单条循环 ======================
typedef struct {
	_uint16		trade_cnt;			// 脱机/联机交易序号
	_uint32		amount;				// 交易金额
	_uint8		trade_type;			// 交易类型
	_uint8		pos_id[6];			// 交易终端
	_uint8		trade_time[7];		// 交易时间
	_uint8		rfu[3];				// 保留
} LOOP_ITEM;

// ======================= 单条记录 ======================
//typedef struct {
//	_uint8		num;				// 记录号
//	_uint16		len;				// 记录长度
//} REC_ITEM;

// =======================================================
// 元文件，组成文件的最小单位，有单独的访问属性（或默认，隐藏）
// 根文件MF
// 应用文件ADF
// 二进制文件
// 密钥文件
// 钱包
// 循环文件
// 变长记录文件
// ========================================================

typedef struct {
	_uint16 dataVersion;			// 数据版本
	_uint8 cardStatus;				// 卡片状态
	_uint8 RFU[64];					// 预留
}CARD_EXTRA, * PCARD_EXTRA;

// ========================= MF元 ==========================
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

// ========================= ADF元 =========================
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

	_uint32		key_off;	// 密钥信息相对偏移（4）
	_uint32		rec_off;	// 记录文件偏移（4）
	_uint32		loop_off;	// 循环文件偏移（4）
	_uint32		wallet_off;	// 钱包信息偏移（4）
	_uint32		bin_off;	// bin文件描述偏移

} ADF_ELEMENT, * PADF_ELEMENT;

// ========================= 二进制元 =========================
typedef struct {
	_uint8		ef_sfi;				// 短文件标识符
	_uint16		sfi;				// 文件标识符
	_uint8		acr;				// 存取控制
	_uint8		acw;				// 存取控制
	_uint16		len;				// 文件大小
} BIN_ELEMENT, * PBIN_ELEMENT;

// ========================= 钱包元 =========================
typedef struct {
	_uint8		EP_balance[4];			// 钱包余额
	_uint8		EP_offline[2];			// 联机交易序号
	_uint8		EP_online[2];			// 脱机交易序号
	_uint32		overdraft;				// 透支额度
	_uint8		tac[4];					// TAC码
	_uint8		mac2[4];				// MAC2
	_uint8		gtp_type;				// 交易类型，02：充值，06：普通消费，09：复合消费
	_uint8		gtp_ready;				// 是否交易完成生成了TAC，下次消费初始化被覆盖
}WALLET_ELEMENT, * PWALLET_ELEMENT;

// ========================= 记录文件元 =========================
typedef struct {
	_uint8		ef_sfi;				// 短文件标识符
	_uint16		sfi;				// 文件标识符
	_uint8		acr;				// 存取控制
	_uint8		acw;				// 存取控制
	_uint16		ef_Size;			// 文件大小
	_uint8		cnt;				// 记录条数
}REC_ELEMENT, * PREC_ELEMENT;

// ========================= 循环文件元 =========================
typedef struct {
	_uint8		ef_sfi;				// 短文件标识符
	_uint16		sfi;				// 文件标识符
	_uint8		acr;				// 存取控制
	_uint8		acw;				// 存取控制
	_uint16		ef_Size;			// 文件大小
	_uint8		ptr;				// 当前记录指针
}LP_ELEMENT, *PLP_ELEMENT;

///////////////////////////////////////////////////////////////////////////////////////////////

// 复合记录文件01结构体，便于写交易
typedef struct {
	_uint8 rfu[4];				// BCD	复合消费标识 + HEX	记录长度 + BCD	复合消费锁定标志位 + HEX	
	_uint8 entry_dev[2];		// HEX	进闸机设备编号
	_uint8 entry_dev_seq[3];	// HEX	进闸设备生成的交易流水号
	_uint8 entry_station[2];	// HEX	进闸车站
	_uint8 entry_time[6];		// BCD	进闸时间
	_uint8 card_status;			// HEX	进闸/出闸/上车/下车/更新/出站票 标志
	_uint8 daliy_cnt_date[4];	// BCD	日乘坐计数的日期
	_uint8 daliy_cnt;			// HEX	日乘坐次数
	_uint8 last_dev[4];			// HEX	最后一次交易设备编号
	_uint8 last_dev_seq[3];		// HEX	最后一次交易设备生成的交易流水号
	_uint8 last_amount[3];		// HEX	最后一次交易金额
	_uint8 last_type;			// HEX	最后一次交易类型
	_uint8 last_time[5];		// BCD	最后一次交易时间
	_uint8 over_draft[4];		// HEX	透支限额
	_uint8 senior_flag;			// HEX	老人免费票使用标志
	_uint8 off_free_station[2];	// HEX	脱机免费出站车站号
	_uint8 update_dev[2];		// HEX	更新设备编号
	_uint8 update_station[2];	// HEX	更新车站编码
	_uint8 update_time[5];		// BCD	更新时间
	_uint8 update_amount[3];	// HEX	更新金额
	_uint8 update_cnt[2];		// HEX	更新总次数
	_uint8 update_reason;		// HEX	更新原因代码

}COMPLEX_TRADE, * PCOMPLEX_TRADE;

typedef struct {
	char a_trade_dev[8];		// 交易设备号
	char a_trade_group[2];		// 交易序列组号
	char a_trade_seq[8];		// 交易序列号
	char c_trade_station[4];	// 交易站点
	char c_trade_type[2];		// 交易类型
	char c_ticket_type[4];		// 票卡类型
	char c_amount_sec[2];		// 区段代码
	char c_samid[8];			// SAM卡编码
	char c_logicid[16];			// 逻辑卡号
	char c_off_cnt[6];			// 票卡交易计数
	char c_amount[8];			// 交易金额
	char c_balance[8];			// 票卡余额
	char c_trade_time[14];		// 交易时间
	char c_last_dev[8];			// 上次交易的设备号
	char c_last_seq[8];			// 上次交易序列号
	char c_last_amount[8];		// 上次交易金额
	char c_last_time[14];		// 上次交易时间
	char c_tac[8];				// 交易认证码
	char c_degrade[4];			// 降级模式
	char c_entry_station[4];	// 进闸车站
	char c_entry_dev[4];		// 进闸设备
	char c_entry_time[14];		// 进闸时间
	char RFU[18];				// 预留
}ACC_TRADE, * PACC_TRADE;

// 用于复合消费事务处理的保存结构
typedef struct {
	_uint8	sfi;
	_uint8	num;		// 记录文件有效
	_uint8	data[128];
	_uint16	len;
	_uint8	old[128];

}TRANS_STACK, * PTRANS_STACK;

#pragma pack(pop)