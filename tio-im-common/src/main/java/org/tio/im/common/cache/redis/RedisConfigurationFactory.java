package org.tio.im.common.cache.redis;

import java.io.File;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import cn.hutool.core.util.ClassLoaderUtil;

/**
 * @author WChao
 * @date 2018年3月9日 上午1:06:33
 */
public class RedisConfigurationFactory {
	
    private static final Logger LOG = LoggerFactory.getLogger(RedisConfigurationFactory.class.getName());

    private static final String DEFAULT_CLASSPATH_CONFIGURATION_FILE = "redis.properties";
    
    /**
     * Constructor.
     */
    private RedisConfigurationFactory() {

    }

    /**
     * Configures a bean from an property file.
     */
    public static RedisConfiguration parseConfiguration(final File file) throws Exception {
        if (file == null) {
            throw new Exception("Attempt to configure redis from null file.");
        }

        LOG.debug("Configuring redis from file: {}", file);
        RedisConfiguration configuration = null;
        try {
        	Prop prop = PropKit.use(file);
        	configuration = new RedisConfiguration(prop);
        } catch (Exception e) {
            throw new Exception("Error configuring from " + file + ". Initial cause was " + e.getMessage(), e);
        } finally {
        }
        return configuration;
    }

    /**
     * Configures a bean from an property file in the classpath.
     */
    public static RedisConfiguration parseConfiguration() throws Exception {
        ClassLoader standardClassloader = ClassLoaderUtil.getContextClassLoader();
        URL url = null;
        if (standardClassloader != null) {
            url = standardClassloader.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url == null) {
            url = RedisConfigurationFactory.class.getResource(DEFAULT_CLASSPATH_CONFIGURATION_FILE);
        }
        if (url != null) {
            LOG.debug("Configuring redis from redis.properties found in the classpath: " + url);
        } else {
        	LOG.warn("No configuration found. Configuring redis from current packet redis.properties"
                    + " found in the classpath: {}", url);
        }
        RedisConfiguration configuration = parseConfiguration(new File(url.getFile()));
        return configuration;
    }
}
