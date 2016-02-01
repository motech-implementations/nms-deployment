# nms-deployment

###Installation of NMS Bundles

Steps to upgrade both bundles and configurations, These steps needs to be followed in NMS HEALTH monitor server

  - Copy the NMS bundles **don't copy testing.jar** on health monitoring server at 
```sh
  cp *.jar /opt/repo/NMS
```

  - Execute following in **/opt/nms_deployement**

```sh
  cd /opt/nms_deployement
  ansible-playbook  site.yml -i hosts --ask-vault-pass --tags motechdeploy--ask-sudo-pass
```

###NMS configuration changes only 

Steps to upgrade only configurations

```sh
ansible-playbook  site.yml -i hosts --ask-vault-pass --tags webconfig --ask-sudo-pass
```
###User Management 

User management in MOTECH-NMS servers can be done by following below steps

  - Update User name and attributes 
```sh
  ansible-vault edit group_vars/all
```
  - Execute ansible command at **/opt/nms_deployement**
```sh
  ansible-playbook usermange.yml -i hosts --ask-vault-pass --ask-su-pass
```
  - After Execute SSH private keys will be available in health monitor at **/tmp/** directory for particular host and user

###Collectd

For collectd to gather database statistics the following user needs to be created:

```sql
CREATE USER 'collectd'@'localhost' IDENTIFIED BY '{{ collectd_database_password }}';

-- Give appropriate permissions
-- ("GRANT USAGE" is synonymous to "no privileges") 
GRANT USAGE ON *.* TO 'collectd'@'localhost';

-- ("GRANT PROCESS" allows access to show processlist) 
GRANT PROCESS ON *.* TO 'collectd'@'localhost';

-- Permissions for the MasterStats and SlaveStats options
GRANT REPLICATION CLIENT ON *.* TO 'collectd'@'localhost';
```

collectd_database_password is in group_vars/all

To install collectd on all servers, connect them and install facette on the health monitor run
```sh
ansible-playbook collectd.yml -i hosts --ask-pass --ask-vault-pass --tags collectd
```

###DB Backup Script Deployement

Steps to deploy on Motech Slave server 

```sh
  ansible-playbook -vvv  site.yml -i hosts --ask-vault-pass --tags dbbackupscript  --ask-sudo-pass
```

Steps to deploy on Report Slave server
```sh
  ansible-playbook -vvv  site.yml -i hosts --ask-vault-pass --tags dbbackupscript_report  --ask-sudo-pass
```

###LDAP Plugin to manage STATE and Districts records in LDAP DB. 

Steps to compile and execute JAR file 

```sh
	mvn clean install 
```

Modify config.properties files and place at the same level where JAR file will be executed, Provide LDAP and DB credentials in this file
Sample file is available at [Resources file](../tree/master/ldap/src/main/resources/config.properties)
