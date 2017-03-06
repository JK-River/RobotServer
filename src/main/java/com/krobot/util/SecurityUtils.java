package com.krobot.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobot.common.Constants;

public class SecurityUtils implements Constants {

	private final static Logger LOGGER = LoggerFactory.getLogger(SecurityUtils.class);

	public static String md5Hex(String data) {
		return DigestUtils.md5Hex(data);
	}

}
