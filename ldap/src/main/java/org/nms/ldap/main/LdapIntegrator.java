package org.nms.ldap.main;

import java.io.IOException;

import org.apache.directory.api.ldap.model.entry.DefaultEntry;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.AddRequest;
import org.apache.directory.api.ldap.model.message.AddRequestImpl;
import org.apache.directory.api.ldap.model.message.AddResponse;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * class provide interaction with LDAP server
 *
 */
public class LdapIntegrator {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LdapIntegrator.class);
    static LdapConnection connection = null;

    /**
     * initialize LDAP Connection
     * @throws LdapException 
     */
    static void initializeLdapConnection(Resource resource) throws LdapException {
        connection = new LdapNetworkConnection(resource.getLdapServerUrl(),
                Integer.valueOf(resource.getLdapServerPort()));
        connection.bind(resource.getLdapUser(), resource.getLdapPassword());
    }

    /**
     * Close LDAP Connection
     */
    static void closeLdapConnection() {
        if (connection != null) {
            try {
                connection.unBind();
                connection.close();
            } catch (LdapException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

    }

    static AddResponse addentry(String dn, Object... elements) {
        Entry entry;
        AddResponse response = null;
        try {
            entry = new DefaultEntry(dn, elements);
            AddRequest addRequest = new AddRequestImpl();
            addRequest.setEntry(entry);
            response = connection.add(addRequest);
            if (response == null) {
                LOGGER.error("Getting null response while adding entry into LDAP for dn: "
                        + dn);
            } else if ("ENTRY_ALREADY_EXISTS".equalsIgnoreCase(response
                    .getLdapResult().getResultCode().name())) {
                LOGGER.warn("Entry Already exists in LDAP for dn: " + dn);
            } else if (!"SUCCESS".equalsIgnoreCase(response.getLdapResult()
                    .getResultCode().name())) {
                LOGGER.error("Error occured while adding entry into LDAP for dn: "
                        + dn);
                LOGGER.error(response.toString());
            }
        } catch (LdapException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return response;

    }
}
