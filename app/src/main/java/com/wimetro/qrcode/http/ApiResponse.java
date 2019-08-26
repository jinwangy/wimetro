package com.wimetro.qrcode.http;

import java.io.Serializable;
import java.util.List;

public class ApiResponse<T> implements Serializable {

	public enum Type {
		JSON_OBJECT, JSON_ARRAY, STRING, INTEGER, UNKNOW
	}

	private boolean status;

	private Integer code;

	private String msg;

	private String raw;

	private Type type;

	private T object;

	private List<T> list;

	private Integer number;

	public ApiResponse() {
	}

	public T getFirstObject() {
		if (list != null && list.size() > 0) {
			return list.get(0);

		} else if (getObject() != null) {
			return getObject();
		}
		return null;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getRaw() {
		return raw;
	}

	public void setRaw(String raw) {
		this.raw = raw;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ApiResponse [status=" + status + ", code=" + code + ", msg="
				+ msg + ", raw=" + raw + ", type=" + type + ", object="
				+ object + ", list=" + list + ", number=" + number + "]";
	}

}
