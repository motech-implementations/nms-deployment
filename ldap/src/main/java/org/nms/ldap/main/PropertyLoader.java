package org.nms.ldap.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used to initialize Resource class from property file.
 *
 */
public class PropertyLoader {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(PropertyLoader.class);
    private static final String PROP_FILE_NAME = "config.properties";

    private static final String LDAP_SERVER_URL = "ldap.server.url";
    private static final String LDAP_SERVER_PORT = "ldap.server.port";
    private static final String LDAP_USER = "ldap.user";
    private static final String LDAP_PASSWORD = "ldap.password";

    private static final String NMS_SERVER_URL = "nms.server.url";
    private static final String NMS_SERVER_PORT = "nms.server.port";
    private static final String NMS_USER = "nms.user";
    private static final String NMS_PASSWORD = "nms.password";
    private static final String NMS_DATABASE = "nms.database";

    static Resource initializeResource() {
        Resource resource = null;
        Properties prop = null;
        String executionPath = System.getProperty("user.dir");
        LOGGER.info("Reading config.properties file from location:"
                + executionPath);
        try (InputStream inputStream = new FileInputStream(executionPath
                + File.separator + PROP_FILE_NAME)) {
            prop = new Properties();
            prop.load(inputStream);
            resource = new Resource();
            resource.setLdapPassword(prop.getProperty(LDAP_PASSWORD));
            resource.setLdapServerPort(prop.getProperty(LDAP_SERVER_PORT));
            resource.setLdapServerUrl(prop.getProperty(LDAP_SERVER_URL));
            resource.setLdapUser(prop.getProperty(LDAP_USER));

            resource.setNmsDataBase(prop.getProperty(NMS_DATABASE));
            resource.setNmsUser(prop.getProperty(NMS_USER));
            resource.setNmsPassword(prop.getProperty(NMS_PASSWORD));
            resource.setNmsServerPort(prop.getProperty(NMS_SERVER_PORT));
            resource.setNmsServerUrl(prop.getProperty(NMS_SERVER_URL));

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return resource;

    }
}
