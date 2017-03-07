package com.krobot.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyConfigurer extends PropertyPlaceholderConfigurer {

	public Map<String, String> keyValues;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);

		keyValues = new HashMap<String, String>();
		for (Entry<Object, Object> entry : props.entrySet()) {
			keyValues.put(entry.getKey().toString(), entry.getValue().toString());
		}
	}

	public String getValue(String name) {
		return keyValues.get(name);
	}

}
