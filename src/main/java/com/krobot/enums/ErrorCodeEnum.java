package com.krobot.enums;

public enum ErrorCodeEnum {

	OK(0, "命令成功执行"), //

	UNKNOWN_ERROR(100001, "出了点问题，请重新试试"), //

	INVALID_MOBILE_PHONE_ERROR(100101, "请输入正确的手机号码"), //

	ALREADY_REGISTERED_ERROR(100102, "手机号码已被注册"), //

	SMS_VERIFY_ERROR(100103, "验证码输入错误或已过期"), //

	SMS_VERIFY_CODE_GET_TOO_FREQUENT(100104, "请求验证码过于频繁"), //

	SMS_VERIFY_CODE_GET_TIMES_TOO_MUCH(100105, "您今天请求验证码的次数已经达到上限"), //

	SMS_VERIFY_CODE_GET_ERROR(100106, "获取验证码失败"), //

	;

	private int errorCode;

	private String errorMsg;

	private ErrorCodeEnum(int errorCode, String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}
}
