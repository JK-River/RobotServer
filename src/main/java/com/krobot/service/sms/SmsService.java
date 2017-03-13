package com.krobot.service.sms;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Range;
import com.google.gson.JsonObject;
import com.krobot.common.ApplicationException;
import com.krobot.enums.ErrorCodeEnum;
import com.krobot.enums.RedisEnum;
import com.krobot.service.BaseService;
import com.krobot.service.HttpService;
import com.krobot.service.PropertyConfigurer;
import com.krobot.service.RedisService;
import com.krobot.util.DateTimeUtils;
import com.krobot.util.PhoneNumberUtils;
import com.krobot.util.SecurityUtils;

@Service
public class SmsService extends BaseService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

	private static final String TENCENT_SMS_URL = "https://yun.tim.qq.com/v3/tlssmssvr/sendsms?sdkappid=%s";

	@Autowired
	private RedisService redisService;

	@Autowired
	private PropertyConfigurer propertyConfigurer;

	@Autowired
	private HttpService httpService;

	public void sendVerifyCode(String phoneNumber) throws ApplicationException {
		phoneNumber = PhoneNumberUtils.parseNumber(phoneNumber);
		if (null == phoneNumber) {
			throw new ApplicationException(ErrorCodeEnum.INVALID_MOBILE_PHONE_ERROR, phoneNumber);
		}

		String sendTime = redisService.get(RedisEnum.SMS_SEND_TIME_FROM_phoneno, phoneNumber);
		if (null != sendTime) {
			long lastSendTime = Long.valueOf(sendTime);
			long expireTime = lastSendTime + propertyConfigurer.getIntValue("sms.send.interval");
			if (!Range.closed(lastSendTime, expireTime).contains(System.currentTimeMillis())) {
				throw new ApplicationException(ErrorCodeEnum.SMS_VERIFY_CODE_GET_TOO_FREQUENT, phoneNumber,
						lastSendTime);
			}
		}

		String sendTimes = redisService.get(RedisEnum.SMS_SEND_TIMES_FROM_phoneno_date, phoneNumber,
				DateTimeUtils.getStringCurDate());
		if (null != sendTimes) {
			if (Integer.parseInt(sendTimes) >= propertyConfigurer.getIntValue("sms.daily.send.times")) {
				throw new ApplicationException(ErrorCodeEnum.SMS_VERIFY_CODE_GET_TIMES_TOO_MUCH, phoneNumber);
			}
		}

		clearVerifyCache(phoneNumber);

		String verifyCode = this.getVerifyCode();
		for (int i = 0; i < propertyConfigurer.getIntValue("sms.verify.times"); i++) {
			redisService.rpush(RedisEnum.SMS_VERIFY_CODES_FROM_phoneno, phoneNumber, verifyCode);
		}
		redisService.set(RedisEnum.SMS_SEND_TIME_FROM_phoneno, phoneNumber, String.valueOf(System.currentTimeMillis()));
		redisService.incr(RedisEnum.SMS_SEND_TIMES_FROM_phoneno_date, phoneNumber, DateTimeUtils.getStringCurDate());

		sendVerifyCode(phoneNumber, verifyCode);
	}

	private String getVerifyCode() {
		String nanoTime = String.valueOf(System.nanoTime());
		return nanoTime.substring(nanoTime.length() - 6);
	}

	public void clearVerifyCache(String phoneNumber) {
		redisService.del(RedisEnum.SMS_SEND_TIME_FROM_phoneno, phoneNumber);
		redisService.del(RedisEnum.SMS_VERIFY_CODES_FROM_phoneno, phoneNumber);
	}

	private void sendVerifyCode(String phoneNumber, String verifyCode) {
		JsonObject phoneNumberJo = new JsonObject();
		phoneNumberJo.addProperty("nationcode", "86");
		phoneNumberJo.addProperty("phone", phoneNumber);

		JsonObject postData = new JsonObject();
		postData.add("tel", phoneNumberJo);
		postData.addProperty("msg", String.format(propertyConfigurer.getValue("tencent.sms.msg"), verifyCode));
		postData.addProperty("sig",
				SecurityUtils.md5Hex(propertyConfigurer.getValue("tencent.sms.sdk.appkey").concat(phoneNumber)));

		String postUrl = String.format(TENCENT_SMS_URL, propertyConfigurer.getValue("tencent.sms.sdk.appid"));
		httpService.postJson(postUrl, postData.toString());
	}

	public void checkVerifyCode(String phoneNumber, String verifyCode) throws ApplicationException {
		String cachVerifyCode = redisService.lpop(RedisEnum.SMS_VERIFY_CODES_FROM_phoneno, phoneNumber);
		if (!ObjectUtils.equals(verifyCode, cachVerifyCode)) {
			LOGGER.info("Sms verify error. {}, {}, {}", phoneNumber, verifyCode, cachVerifyCode);
			throw new ApplicationException(ErrorCodeEnum.SMS_VERIFY_ERROR, "nequals");
		}
	}
}
