package com.qlik.automation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

public class PropertiesUtil {
	final static Logger logger = Logger.getLogger(PropertiesUtil.class);

	public static Properties getApplicationProperties () {
		Properties prop = new Properties();
		try {
			InputStream input = null;
			String contentBuilderPath = System.getProperty("user.dir")+"/resources/automation.properties";
			input = new FileInputStream(contentBuilderPath);
	    	prop.load(input);	    		
		} catch (Exception ex) {
			logger.error(ex,ex);
		}
		return prop;
	}
	
}
