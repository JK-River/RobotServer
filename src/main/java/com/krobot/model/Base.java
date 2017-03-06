package com.krobot.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.krobot.common.Constants;

public class Base implements Constants {

	@Override
	public String toString() {
		return (new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)).toString();
	}

}
