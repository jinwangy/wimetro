package com.wimetro.qrcode.common.utils;

//import cn.weipass.biz.wht.WhtCpucard;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * 
 * @author houj
 *
 */
public final class HEX {

	/**
	 * HEX
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] hexToBytes(String s) {
		s = s.toUpperCase();
		int len = s.length() / 2;
		int ii = 0;
		byte[] bs = new byte[len];
		char c;
		int h;
		for (int i = 0; i < len; i++) {
			c = s.charAt(ii++);
			if (c <= '9') {
				h = c - '0';
			} else {
				h = c - 'A' + 10;
			}
			h <<= 4;
			c = s.charAt(ii++);
			if (c <= '9') {
				h |= c - '0';
			} else {
				h |= c - 'A' + 10;
			}
			bs[i] = (byte) h;
		}
		return bs;
	}

	private final static char[] CS = "0123456789ABCDEF".toCharArray();

	/**
	 * @param bs
	 * @return
	 */
	public static String bytesToHex(byte[] bs) {
		char[] cs = new char[bs.length * 2];
		int io = 0;
		for (int n : bs) {
			cs[io++] = CS[(n >> 4) & 0xF];
			cs[io++] = CS[(n >> 0) & 0xF];
		}
		return new String(cs);
	}

	public static String bytesToHex(byte[] bs, int len) {
		char[] cs = new char[len * 2];
		int io = 0;
		for (int i = 0; i < len; i++) {
			int n = bs[i];
			cs[io++] = CS[(n >> 4) & 0xF];
			cs[io++] = CS[(n >> 0) & 0xF];
		}
		return new String(cs);
	}

	/**
	 * 
	 * @param bs	:\xAB\xAC\xAD--->"AB AC AD "
	 * @param gap ：插入的分隔符
	 * @return
	 */
	public static String bytesToHex(byte[] bs, char gap) {
		char[] cs = new char[bs.length * 3];
		int io = 0;
		for (int n : bs) {
			cs[io++] = CS[(n >> 4) & 0xF];
			cs[io++] = CS[(n >> 0) & 0xF];
			cs[io++] = gap;
		}
		return new String(cs);
	}

	public static String bytesToHex(byte[] bs, char gap, int len) {
		char[] cs = new char[len * 3];
		int io = 0;
		for (int i = 0; i < len; i++) {
			int n = bs[i];
			cs[io++] = CS[(n >> 4) & 0xF];
			cs[io++] = CS[(n >> 0) & 0xF];
			cs[io++] = gap;
		}
		return new String(cs);
	}

	/**
	 * 
	 * 
	 * @param bs
	 * @param bytePerLine
	 * @return
	 */
	public static String bytesToCppHex(byte[] bs, int bytePerLine) {

		if (bytePerLine <= 0 || bytePerLine >= 65536) {
			bytePerLine = 65536;
		}
		int lines = 0;
		if (bytePerLine < 65536) {
			lines = (bs.length + bytePerLine - 1) / bytePerLine;
		}

		char[] cs = new char[bs.length * 5 + lines * 3];
		int io = 0;
		int ic = 0;
		for (int n : bs) {
			cs[io++] = '0';
			cs[io++] = 'x';
			cs[io++] = CS[(n >> 4) & 0xF];
			cs[io++] = CS[(n >> 0) & 0xF];
			cs[io++] = ',';
			if (bytePerLine < 65536) {
				if (++ic >= bytePerLine) {
					ic = 0;
					cs[io++] = '/';
					cs[io++] = '/';
					cs[io++] = '\n';
				}
			}
		}
		if (bytePerLine < 65536) {
			if (io < cs.length) {
				cs[io++] = '/';
				cs[io++] = '/';
				cs[io++] = '\n';
			}
		}
		return new String(cs);
	}

	public static String toLeHex(int n, int byteCount) {
		char[] rs = new char[byteCount * 2];
		int io = 0;
		for (int i = 0; i < byteCount; i++) {
			rs[io++] = CS[(n >> 4) & 0xF];
			rs[io++] = CS[(n >> 0) & 0xF];
			n >>>= 8;
		}
		return new String(rs);
	}
	
	/**
	 * 转换数据返回字符串
	 * @param n
	 * @param byteCount
	 * @return  讲整形 转换为 字符串格式  
	 */ 
	public static String toBeHex(int n, int byteCount) {
		char[] rs = new char[byteCount * 2];         
		int io = 0;
		n <<= (32 - byteCount * 8);
		for (int i = 0; i < byteCount; i++) {
			rs[io++] = CS[(n >> 28) & 0xF];
			rs[io++] = CS[(n >> 24) & 0xF];
			n <<= 8;
		}
		return new String(rs);
	}
	


//	 public static void main(String[] args) {
//	 byte[] bs =
//	 HEX.hexToBytes("00C6B0F084C0B05BBEC211A678114CA6C751C08FE99EE22D25F7DCC21440D4ECA193C3444D8A8C53DDE6FBD40AEB917B551119D61A6A347CEF64CAB7A9437D2D9B34478DCB256CDCDDBB8E2A4E2D5F631136C7AA91037898D65B83526D5BE1978C818B9DD60CD19D5007269B966407D7D05A9BAFFB0964BE4DDEC331D697D07C1B");
//	 System.out.println(bs.length);
//	
//	 String s =
//	 "80 94 db 61 fa 66 16 d0 c9 d4 3d 3b 81 b4 72 a0 98 35 40 9b 71 4a ef fc 16 15 32 bd 7f a7 ba 7f 12 e2 e6 21  03 87 b1 9f c5 bc 12 dd 63 5c 27 1b 11 73 e9 08 26 85 29 96 c6 57 e4 e5 e1 db f2 e5 40 ff c0 fa 6c 27 90 f2 23 c0  20 02 3f c0 ae de 30 e7 89 71 33 10 a7 eb 7b c6 41 1b 13 b4 0f 22 25 cc d4 a1 ba 1b 0c a0 8b 7b 5c cd be ed 36 d4  5e 64 63 66 62 18 26 89 d4 c1 8c 09 56 c3 33 f8 03";
//	 s = s.replace(" ", "");
//	 bs = HEX.hexToBytes(s);
//	 System.out.println(bs.length);
//	 }

	public static void main(String[] args) {
		System.out.println(toBeHex(0x3f01, 2));
		System.out.println(toBeHex(65535, 4));
		System.out.println(toLeHex(65535, 4));
		byte aa = 0x01;
	
		System.out.println(Integer.toHexString(aa));
		System.out.println(String.format("%02x", aa));
		
		byte[]  cmd = new byte[50];
		cmd = hexToBytes("00A400023F00");
		System.out.println(hexToBytes("00A400023F00"));
		System.out.printf("cmd:%02x%02x%02x%02x%02x%02x", cmd[0], cmd[1],cmd[2], cmd[3],cmd[4],cmd[5]);
	}



	private final static char[] mChars = "0123456789ABCDEF".toCharArray();
	private final static String mHexStr = "0123456789ABCDEF";
	/**
	 * 检查16进制字符串是否有效
	 * @param sHex String 16进制字符串
	 * @return boolean
	 */
	public static boolean checkHexStr(String sHex){
		String sTmp = sHex.toString().trim().replace(" ", "").toUpperCase(Locale.US);
		int iLen = sTmp.length();

		if (iLen > 1 && iLen%2 == 0){
			for(int i=0; i<iLen; i++)
				if (!mHexStr.contains(sTmp.substring(i, i+1)))
					return false;
			return true;
		}
		else
			return false;
	}

	/**
	 * 字符串转换成十六进制字符串
	 * @param str String 待转换的ASCII字符串
	 * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
	 */
	public static String str2HexStr(String str){
		StringBuilder sb = new StringBuilder();
		byte[] bs = str.getBytes();

		for (int i = 0; i < bs.length; i++){
			sb.append(mChars[(bs[i] & 0xFF) >> 4]);
			sb.append(mChars[bs[i] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * 十六进制字符串转换成 ASCII字符串
	 * @param hexStr String Byte字符串
	 * @return String 对应的字符串
	 */
	public static String hexStr2Str(String hexStr){
		hexStr = hexStr.toString().trim().replace(" ", "").toUpperCase(Locale.US);
		char[] hexs = hexStr.toCharArray();
		byte[] bytes = new byte[hexStr.length() / 2];
		int iTmp = 0x00;;

		for (int i = 0; i < bytes.length; i++){
			iTmp = mHexStr.indexOf(hexs[2 * i]) << 4;
			iTmp |= mHexStr.indexOf(hexs[2 * i + 1]);
			bytes[i] = (byte) (iTmp & 0xFF);
		}
		return new String(bytes);
	}

	/**
	 * bytes转换成十六进制字符串
	 * @param b byte[] byte数组
	 * @param iLen int 取前N位处理 N=iLen
	 * @return String 每个Byte值之间空格分隔
	 */
	public static String byte2HexStr(byte[] b, int iLen){
		StringBuilder sb = new StringBuilder();
		for (int n=0; n<iLen; n++){
			sb.append(mChars[(b[n] & 0xFF) >> 4]);
			sb.append(mChars[b[n] & 0x0F]);
			sb.append(' ');
		}
		return sb.toString().trim().toUpperCase(Locale.US);
	}

	/**
	 * bytes字符串转换为Byte值
	 * @param src String Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
	 * @return byte[]
	 */
	public static byte[] hexStr2Bytes(String src){
        /*对输入值进行规范化整理*/
		src = src.trim().replace(" ", "").toUpperCase(Locale.US);
		//处理值初始化
		int m=0,n=0;
		int iLen=src.length()/2; //计算长度
		byte[] ret = new byte[iLen]; //分配存储空间

		for (int i = 0; i < iLen; i++){
			m=i*2+1;
			n=m+1;
			ret[i] = (byte)(Integer.decode("0x"+ src.substring(i*2, m) + src.substring(m,n)) & 0xFF);
		}
		return ret;
	}

	/**
	 * String的字符串转换成unicode的String
	 * @param strText String 全角字符串
	 * @return String 每个unicode之间无分隔符
	 * @throws Exception
	 */
	public static String strToUnicode(String strText)
			throws Exception
	{
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++){
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u");
			else // 低位在前面补00
				str.append("\\u00");
			str.append(strHex);
		}
		return str.toString();
	}

	/**
	 * unicode的String转换成String的字符串
	 * @param hex String 16进制值字符串 （一个unicode为2byte）
	 * @return String 全角字符串
	 */
	public static String unicodeToString(String hex){
		int t = hex.length() / 6;
		int iTmp = 0;
		StringBuilder str = new StringBuilder();
		for (int i = 0; i < t; i++){
			String s = hex.substring(i * 6, (i + 1) * 6);
			// 将16进制的string转为int
			iTmp = (Integer.valueOf(s.substring(2, 4), 16) << 8) | Integer.valueOf(s.substring(4), 16);
			// 将int转换为字符
			str.append(new String(Character.toChars(iTmp)));
		}
		return str.toString();
	}

	public static String bytesToAscii(byte[] bytes, int offset, int dateLen) {
		if ((bytes == null) || (bytes.length == 0) || (offset < 0) || (dateLen <= 0)) {
			return null;
		}
		if ((offset >= bytes.length) || (bytes.length - offset < dateLen)) {
			return null;
		}

		String asciiStr = null;
		byte[] data = new byte[dateLen];
		System.arraycopy(bytes, offset, data, 0, dateLen);
		try {
			asciiStr = new String(data, "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
		}
		return asciiStr;
	}

	public static String bytesToAscii(byte[] bytes, int dateLen) {
		return bytesToAscii(bytes, 0, dateLen);
	}

	public static String bytesToAscii(byte[] bytes) {
		return bytesToAscii(bytes, 0, bytes.length);
	}

}
