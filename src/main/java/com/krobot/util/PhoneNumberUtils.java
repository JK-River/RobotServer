package com.krobot.util;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.krobot.common.Constants;
import com.krobot.service.sms.SmsService;

public class PhoneNumberUtils implements Constants {

	private static final Logger LOGGER = LoggerFactory.getLogger(SmsService.class);

	private static String PHONE_NUMBER_REGEX = "(^1\\d{10}$)|(^+861\\d{10}$)";

	private static String SHORT_PHONE_NUMBER_REGEX = "^1\\d{10}$";

	private static PhoneNumberUtil PN_Util = PhoneNumberUtil.getInstance();

	public static String parseNumber(String number) {
		if (!Pattern.matches(PHONE_NUMBER_REGEX, number)) {
			LOGGER.error("invalid phone number. {}", number);
			return null;
		}
		if (Pattern.matches(SHORT_PHONE_NUMBER_REGEX, number)) {
			number = "+86" + number;
		}
		try {
			PhoneNumber phoneNumber = PN_Util.parse(number, "CH");
			if (PN_Util.isValidNumber(phoneNumber)) {
				return String.valueOf(phoneNumber.getNationalNumber());
			}
			LOGGER.error("invalid phone number. {}", number);
			return null;
		} catch (NumberParseException e) {
			LOGGER.error("invalid phone number. {}", number);
			return null;
		}
	}
}
