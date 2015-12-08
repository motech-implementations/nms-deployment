# nms-deployment
For collectd to gather database statistics the following user needs to be created:

CREATE USER 'collectd'@'localhost' IDENTIFIED BY '{{ collectd_database_password }}';
-- Give appropriate permissions
-- ("GRANT USAGE" is synonymous to "no privileges")
GRANT USAGE ON *.* TO 'collectd'@'localhost';
-- Permissions for the MasterStats and SlaveStats options
GRANT REPLICATION CLIENT ON *.* TO 'collectd'@'localhost';

collectd_database_password is in group_vars/all

To install collectd on all servers, connect them and install facette on the health monitor run

ansible-playbook collectd.yml -i /etc/ansible/hosts.yml --ask-pass --ask-vault-pass --tags collectd

