# nms-deployment

Installation OF NMS Builds
1) Copy the NMS bundles don't copy testing.jar on health monitoring server at /opt/repo/NMS
2) Execute following in /opt/nms_deployement 
  ansible-playbook  site.yml -i hosts --ask-vault-pass --tags motechdeploy--ask-sudo-pass

Change in Configuration like change in SMS properties

1) Update the required configuration in the     /opt/nms_deployment/roles/web/templates/*.j2 file
2) Execute following in /opt/nms_deployement

ansible-playbook  site.yml --ask-vault-pass --tags webconfig --ask-sudo-pass

For collectd to gather database statistics the following user needs to be created:

```sql
CREATE USER 'collectd'@'localhost' IDENTIFIED BY '{{ collectd_database_password }}';

-- Give appropriate permissions
-- ("GRANT USAGE" is synonymous to "no privileges") 
GRANT USAGE ON *.* TO 'collectd'@'localhost';

-- Permissions for the MasterStats and SlaveStats options
GRANT REPLICATION CLIENT ON *.* TO 'collectd'@'localhost';
```

collectd_database_password is in group_vars/all

To install collectd on all servers, connect them and install facette on the health monitor run

ansible-playbook collectd.yml -i hosts --ask-pass --ask-vault-pass --tags collectd

