package org.nms.ldap.main;

/**
 * POJO class initialized from configuration properties file.
 *
 */
public class Resource {

    private String ldapServerUrl;
    private String ldapServerPort;
    private String ldapUser;
    private String ldapPassword;

    private String nmsServerUrl;
    private String nmsServerPort;
    private String nmsDataBase;
    private String nmsUser;
    private String nmsPassword;

    public String getLdapServerUrl() {
        return ldapServerUrl;
    }

    public void setLdapServerUrl(String ldapServerUrl) {
        this.ldapServerUrl = ldapServerUrl;
    }

    public String getLdapServerPort() {
        return ldapServerPort;
    }

    public void setLdapServerPort(String ldapServerPort) {
        this.ldapServerPort = ldapServerPort;
    }

    public String getLdapUser() {
        return ldapUser;
    }

    public void setLdapUser(String ldapUser) {
        this.ldapUser = ldapUser;
    }

    public String getLdapPassword() {
        return ldapPassword;
    }

    public void setLdapPassword(String ldapPassword) {
        this.ldapPassword = ldapPassword;
    }

    public String getNmsServerUrl() {
        return nmsServerUrl;
    }

    public void setNmsServerUrl(String nmsServerUrl) {
        this.nmsServerUrl = nmsServerUrl;
    }

    public String getNmsServerPort() {
        return nmsServerPort;
    }

    public void setNmsServerPort(String nmsServerPort) {
        this.nmsServerPort = nmsServerPort;
    }

    public String getNmsUser() {
        return nmsUser;
    }

    public void setNmsUser(String nmsUser) {
        this.nmsUser = nmsUser;
    }

    public String getNmsDataBase() {
        return nmsDataBase;
    }

    public void setNmsDataBase(String nmsDataBase) {
        this.nmsDataBase = nmsDataBase;
    }

    public String getNmsPassword() {
        return nmsPassword;
    }

    public void setNmsPassword(String nmsPassword) {
        this.nmsPassword = nmsPassword;
    }

}
