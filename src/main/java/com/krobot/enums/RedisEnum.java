package com.krobot.enums;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.MINUTES;

public enum RedisEnum {

	REGISTER_MOBILE_PHONES("register_mobile_phones", DAYS.toSeconds(Integer.MAX_VALUE), DataTypeEnum.SET),

	SMS_SEND_TIME_FROM_phoneno("sms_send_time", MINUTES.toSeconds(10), DataTypeEnum.OBJECT),

	SMS_SEND_TIMES_FROM_phoneno_date("sms_send_times", DAYS.toSeconds(1), DataTypeEnum.OBJECT),

	SMS_VERIFY_CODES_FROM_phoneno("sms_verify_codes", MINUTES.toSeconds(10), DataTypeEnum.LIST),
	
	;

	private String key;

	private long expireTime;

	private DataTypeEnum dataType;

	private RedisEnum(String key, long expireTime, DataTypeEnum dataType) {
		this.key = key;
		this.expireTime = expireTime;
		this.dataType = dataType;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public String getKey() {
		return key;
	}

	public DataTypeEnum getDataType() {
		return dataType;
	}

}
