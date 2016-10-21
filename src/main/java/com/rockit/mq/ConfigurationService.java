package com.rockit.mq;
import java.util.logging.*;
import javax.annotation.Nonnull;
import org.apache.commons.configuration.*;
/**
 * This calls provides confiuration mechanism
 */
public class ConfigurationService {
	
	private static Configuration configuration;
	private static Logger logger = Logger.getAnonymousLogger();
	
	private ConfigurationService() {}
	
	@Nonnull
	public static Configuration getConfiguration() {
		
		if (configuration == null)
			try {
				configuration = new PropertiesConfiguration("configuration.properties");
			} catch (ConfigurationException e) {
				logger.log(Level.SEVERE, "Configuration file does not exist...!!", e);
			}
		return configuration;
	}
}
