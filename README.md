# nms-deployment

###Installation OF NMS Builds

Steps to upgrade both bundles and configurations 

1) Copy the NMS bundles don't copy testing.jar on health monitoring server at /opt/repo/NMS

2) Execute following in /opt/nms_deployement 
```sh
  ansible-playbook  site.yml -i hosts --ask-vault-pass --tags motechdeploy--ask-sudo-pass
```

###MOTECH Configuration changes 

Steps to upgrade only configurations

```sh
ansible-playbook  site.yml -i hosts --ask-vault-pass --tags webconfig --ask-sudo-pass
```

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
