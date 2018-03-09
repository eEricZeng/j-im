package org.tio.im.common.cache.caffeine;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import cn.hutool.core.util.ClassLoaderUtil;

/**
 * @author WChao
 * @date 2018年3月9日 上午1:06:33
 */
public class CaffeineConfigurationFactory {
	
    private static final Logger LOG = LoggerFactory.getLogger(CaffeineConfigurationFactory.class.getName());

    private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "caffeine.properties";
    
    /**
     * Constructor.
     */
    private CaffeineConfigurationFactory() {

    }

    /**
     * Configures a bean from an property file.
     */
    public static List<CaffeineConfiguration> parseConfiguration(final File file) throws Exception {
        if (file == null) {
            throw new Exception("Attempt to configure caffeine from null file.");
        }

        LOG.debug("Configuring caffeine from file: {}", file);
        List<CaffeineConfiguration> configurations = new ArrayList<CaffeineConfiguration>();
        try {
        	Prop prop = PropKit.use(file);
    		Properties props = prop.getProperties();
    		for(String key : props.stringPropertyNames()){
    			configurations.add(new CaffeineConfiguration(key , prop));
    		}
        } catch (Exception e) {
            throw new Exception("Error configuring from " + file + ". Initial cause was " + e.getMessage(), e);
        } finally {
        }
        return configurations;
    }

    /**
     * Configures a bean from an property file in the classpath.
     */
    public static List<CaffeineConfiguration> parseConfiguration() throws Exception {
        ClassLoader standardClassloader = ClassLoaderUtil.getContextClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url == null) {
            url = CaffeineConfigurationFactory.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url != null) {
            LOG.debug("Configuring caffeine from caffeine.properties found in the classpath: " + url);
        } else {
        	LOG.warn("No configuration found. Configuring caffeine from current packet caffeine.properties"
                    + " found in the classpath: {}", url);
        }
        List<CaffeineConfiguration> configurations = parseConfiguration(new File(url.getFile()));
        return configurations;
    }
}
