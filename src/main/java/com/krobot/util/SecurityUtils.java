package com.krobot.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.krobot.common.Constants;

public class SecurityUtils implements Constants {

	public static String md5Hex(String data) {
		return DigestUtils.md5Hex(data);
	}

}
