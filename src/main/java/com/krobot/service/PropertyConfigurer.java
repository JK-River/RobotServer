package com.krobot.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.krobot.common.Constants;

public class PropertyConfigurer extends PropertyPlaceholderConfigurer implements Constants {

	public Map<String, String> propertyMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);

		propertyMap = new HashMap<String, String>();
		for (Object key : props.keySet()) {
			propertyMap.put(key.toString(), props.getProperty(key.toString()));
		}
	}

	public String getValue(String name) {
		return propertyMap.get(name);
	}

	public int getIntValue(String name) {
		return Integer.parseInt(propertyMap.get(name).trim());
	}
}
