#ifdef _WIN32
#ifdef HCEPREPAY_EXPORTS
#define HCEPREPAY_API __declspec(dllexport)
#else
#define HCEPREPAY_API __declspec(dllimport)
#endif
#else
#define HCEPREPAY_API
#endif

#include "Types.h"

#ifdef __cplusplus
extern "C"
{
#endif

	HCEPREPAY_API short ProcessApdu(char * p_apdu_in, short len_in, char * p_apdu_out, short * len_out, char * trade_type, char * p_trade_data, short * p_token_status);

	// ���ñ��ز�����ͬʱ��鿨�ļ��Ƿ���ڣ������ھʹ���
	HCEPREPAY_API short SetLocalParam(const char * pdir, const char * p_sys_cache, const char * pime, short verLowest, const char * pAccount, char * p_data_CRC);

	// �����ļ������ھ��ȵ��ñ��ӿڣ�Ȼ�����UpdateCardFile
	HCEPREPAY_API short CreateDefaultCard();

	HCEPREPAY_API short UpdateCard_MF_05(char * p_file_data, short len_data);
	HCEPREPAY_API short UpdateCard_ADF01_key(char type, char index, char ver, char algo, char err_cnt, char * p_key, char len_key);
	//forceUpdate: 0-��ǿ�Ƹ��£����ؽ��׼����Ƚϴ��ʱ�򲻸��£���1-ǿ�Ƹ��£����ܱ��ؽ��׼�����ǿ�Ƹ��¿����ݣ�
	HCEPREPAY_API short UpdateCard_ADF01_wallet(int balance, short off_cnt, short on_cnt, char forceUpdate);
	HCEPREPAY_API short UpdateCard_ADF01_15(char * p_file_data, short len_data);
	HCEPREPAY_API short UpdateCard_ADF01_17(char id, char * p_file_data, short len_data);
	HCEPREPAY_API void UpdateCard_end(short verCurrent);

	// ��ȡǮ����Ϣ
	HCEPREPAY_API short GetWalletInfo(short adf_sfi, int * p_balance, short * p_offline_cnt, short * p_online_cnt);

	// ��ȡ�����ļ�01��¼-����������Ϣ�ļ�
	HCEPREPAY_API short GetRecordMetro(short adf_sfi, char * p_metro, short len_metro);

	// p_logicID, string 16B
	// p_balance, int����
	// p_offline_cnt, short����
	// p_online_cnt,short����
	// p_metro,HEX�����У�����len_metro
	HCEPREPAY_API short GetUploadInfo(char * p_logicID, int * p_balance, short * p_offline_cnt, short * p_online_cnt, char * p_metro, short len_metro, char * p_data_CRC);

	// ���ÿ�Ƭ�ѻ�״̬����UpdateCard_end֮ǰ����
	// 0x00-��ʼ״̬��CreateDefaultCard�ɹ���Ϊ��״̬����״̬�Ŀ���cos�����Ʋ���ʹ��
	// 0x01-δ����״̬����APP�յ�����������Ϣ������δ֧���Ľ��ף����ø�״̬�����ƹ��ڣ�δ��¼���ѵǳ���ʱ�������ø�״̬
	// 0x02-����״̬�����ã��˻���������Ƭ����ͬ���������ø�״̬
	// 0xFF-δ֪״̬������ʹ�ã�Ʊ��δ���أ���ȡ�������״̬
	HCEPREPAY_API short SetCardStatus(char newStatus);

	// �жϿ��Ƿ����
	HCEPREPAY_API short CardExsit();

	// ���±���token
	HCEPREPAY_API short UpdateLocalToken(const char * p_NewToken, short lenToken);
	// Token��Ч���ж�
	HCEPREPAY_API short CheckToken(const char * p_time_now);
	// ��ȡTokenʣ�������Ϣ��ʣ��ʱ�䣬ʣ���ʣ�����
	HCEPREPAY_API void GetTokenOddInfo(const char * p_time_now, short * oddmins, short * oddamount, short * minamount, char * oddcnt);
	// ��ȡ��������֤Token
	HCEPREPAY_API short GetTokenForServerVerify(char * pToken, short * len_token);
	// ��ʱ�ӿڣ�ģ����������
	HCEPREPAY_API void tmpTokenApply(char * pToken, short * len_token);

	// ��ȡ�ڲ�������
	HCEPREPAY_API short getInternelErrorCode();

#ifdef _WIN32
	HCEPREPAY_API void TestMac(_uint8 Alg, _uint8_p pKey, _uint8_p pData, _uint16 len, _uint8_p pMac, _uint8_p pivec);
#endif

#ifdef __cplusplus
};
#endif