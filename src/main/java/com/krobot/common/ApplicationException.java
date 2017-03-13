package com.krobot.common;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.krobot.enums.ErrorCodeEnum;

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	private int errorCode;

	private String errorMsg;

	private String detailErrorMsg;

	public ApplicationException(ErrorCodeEnum ec) {
		super(ec.getErrorMsg());
		this.errorCode = ec.getErrorCode();
		this.errorMsg = ec.getErrorMsg();
		this.detailErrorMsg = ec.getErrorMsg();
	}

	public ApplicationException(ErrorCodeEnum ec, Object... params) {
		super(ec.getErrorMsg());
		this.errorCode = ec.getErrorCode();
		this.errorMsg = ec.getErrorMsg();
		this.detailErrorMsg = detailErrorMsg(ec.getErrorMsg(), params);
	}

	private String detailErrorMsg(String errorMsg, Object... params) {
		if (null == params) {
			return errorMsg;
		}
		List<String> strParams = Lists.transform(Arrays.asList(params), new Function<Object, String>() {
			@Override
			public String apply(Object arg) {
				return new StringBuilder().append(" [").append(arg).append("] ").toString();
			}
		});
		return Joiner.on("").join(strParams);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public String getDetailErrorMsg() {
		return detailErrorMsg;
	}
}
