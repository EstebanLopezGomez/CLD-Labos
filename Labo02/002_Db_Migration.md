# Database migration

In this task you will migrate the Drupal database to the new RDS database instance.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 - Securing current Drupal data

### [Get Bitnami MariaDb user's password](https://docs.bitnami.com/aws/faq/get-started/find-credentials/)

```bash
[INPUT]
//help : path /home/bitnami/bitnami_credentials

// Connect with ssh in 2 different terminals
Terminal 1 : ssh devopsteam06@15.188.43.46 -i CLD_KEY_DMZ_DEVOPSTEAM06.pem -L 2223:10.0.6.9:22
Terminal 2 : ssh bitnami@localhost -p 2223 -i CLD_KEY_DRUPAL_DEVOPSTEAM06.pem

cd /home/bitnami
nano bitnami_credentials

(or use 'cat' for read only)

[OUTPUT]
Welcome to the Bitnami package for Drupal

**************************
The default username and password is 'user' and '@ZrIvhzjgTL9'.
**************************

You can also use this password to access the databases and any other component the stack includes.

Please refer to https://docs.bitnami.com/ for more details.
```

### Get Database Name of Drupal

```bash
[INPUT]
//add string connection

mariadb -u root -p -e "show databases;"

[OUTPUT]

Enter password: @ZrIvhzjgTL9
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
| mysql              |
| performance_schema |
| sys                |
| test               |
+--------------------+
```

### [Dump Drupal DataBases](https://mariadb.com/kb/en/mariadb-dump/)

```bash
[INPUT]
mariadb-dump -u root -p --databases bitnami_drupal > bitnami_drupal_dump.sql

On vérifie que le dump a bien fonctionné
tail bitnami_drupal_dump.sql

[OUTPUT]
// Tail output
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-21 16:38:12
```

### Create the new Data base on RDS

```sql
[INPUT]
mariadb -u root -p -e "CREATE DATABASE bitnami_drupal"
```

### [Import dump in RDS db-instance](https://mariadb.com/kb/en/restoring-data-from-dump-files/)

Note : you can do this from the Drupal Instance. Do not forget to set the "-h" parameter.

```sql
[INPUT]
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p < bitnami_drupal_dump.sql

// Connexion
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p

// On affiche les tables
use bitnami_drupal;
show tables;

[OUTPUT]
+----------------------------------+
| Tables_in_bitnami_drupal         |
+----------------------------------+
| block_content                    |
| block_content__body              |
| block_content_field_data         |
| block_content_field_revision     |
| block_content_revision           |
| block_content_revision__body     |
| cache_bootstrap                  |
| cache_config                     |
| cache_container                  |
| cache_data                       |
| cache_default                    |
| cache_discovery                  |
| cache_dynamic_page_cache         |
| cache_entity                     |
| cache_menu                       |
| cache_page                       |
| cache_render                     |
| cache_toolbar                    |
| cachetags                        |
| comment                          |
| comment__comment_body            |
| comment_entity_statistics        |
| comment_field_data               |
| config                           |
| file_managed                     |
| file_usage                       |
| help_search_items                |
| history                          |
| key_value                        |
| menu_link_content                |
| menu_link_content_data           |
| menu_link_content_field_revision |
| menu_link_content_revision       |
| menu_tree                        |
| node                             |
| node__body                       |
| node__comment                    |
| node__field_image                |
| node__field_tags                 |
| node_access                      |
| node_field_data                  |
| node_field_revision              |
| node_revision                    |
| node_revision__body              |
| node_revision__comment           |
| node_revision__field_image       |
| node_revision__field_tags        |
| path_alias                       |
| path_alias_revision              |
| router                           |
| search_dataset                   |
| search_index                     |
| search_total                     |
| semaphore                        |
| sequences                        |
| sessions                         |
| shortcut                         |
| shortcut_field_data              |
| shortcut_set_users               |
| taxonomy_index                   |
| taxonomy_term__parent            |
| taxonomy_term_data               |
| taxonomy_term_field_data         |
| taxonomy_term_field_revision     |
| taxonomy_term_revision           |
| taxonomy_term_revision__parent   |
| user__roles                      |
| user__user_picture               |
| users                            |
| users_data                       |
| users_field_data                 |
| watchdog                         |
+----------------------------------+
72 rows in set (0.001 sec)
```

### [Get the current Drupal connection string parameters](https://www.drupal.org/docs/8/api/database-api/database-configuration)

```bash
[INPUT]
//help : same settings.php as before
cat stack/drupal/sites/default/settings.php

[OUTPUT]
//at the end of the file you will find connection string parameters
//'username' => 'bn_drupal',
//'password' => 'da4ec121fbd6ba30e1162c0b50d0d4a773ffb8f030821770b47f8ff50429fef0',
```

### Replace the current host with the RDS FQDN

```
//settings.php
On ouvre le fichier avec nano

$databases['default']['default'] = array (
   [...] 
  'host' => 'dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com',
   [...] 
);
```

### [Create the Drupal Users on RDS Data base](https://mariadb.com/kb/en/create-user/)

Note : only calls from both private subnets must be approved.
* [By Password](https://mariadb.com/kb/en/create-user/#identified-by-password)
* [Account Name](https://mariadb.com/kb/en/create-user/#account-names)
* [Network Mask](https://cric.grenoble.cnrs.fr/Administrateurs/Outils/CalculMasque/)

```sql
[INPUT]
CREATE USER bn_drupal@'10.0.[XX].0/[Subnet Mask - A]]' IDENTIFIED BY '<Drupal password>';

GRANT ALL PRIVILEGES ON bitnami_drupal.* TO '<yourNewUser>';

//DO NOT FOREGT TO FLUSH PRIVILEGES


// On se connecte au SSH
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p

CREATE USER bn_drupal@'10.0.6.0/255.255.255.240' IDENTIFIED BY 'DEVOPSTEAM06!';
GRANT ALL PRIVILEGES ON bitnami_drupal.* TO 'bn_drupal'@'10.0.6.0/255.255.255.240';

[OUTPUT]
Query OK, 0 rows affected (0.003 sec)
```

```sql
//validation
[INPUT]
SHOW GRANTS for 'bn_drupal'@'10.0.6.0/255.255.255.240';

[OUTPUT]
+--------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.6.0/255.255.255.240                                                                                   |
+--------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON . TO bn_drupal@10.0.6.0/255.255.255.240 IDENTIFIED BY PASSWORD '*FD750E6FECC9DC0C2E3DA98985F70481F4AB5EE7' |
| GRANT ALL PRIVILEGES ON bitnami_drupal.* TO bn_drupal@10.0.6.0/255.255.255.240                                            |
+--------------------------------------------------------------------------------------------------------------------+
2 rows in set (0.001 sec)
```

### Validate access (on the drupal instance)

```sql
[INPUT]
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u bn_drupal -p
show databases;

[OUTPUT]
+--------------------+
| Database           |
+--------------------+
| bitnami_drupal     |
| information_schema |
+--------------------+
2 rows in set (0.001 sec)
```

* Repeat the procedure to enable the instance on subnet 2 to also talk to your RDS instance.

TODO : 	- Changer le masque de l'user de 0 à .240
		- Refaire l'opération avec le subnet B 
SUBNET B :
SQL
[INPUT]
CREATE USER bn_drupal@'10.0.6.128/255.255.255.240' IDENTIFIED BY 'da4ec121fbd6ba30e1162c0b50d0d4a773ffb8f030821770b47f8ff50429fe
f0';

GRANT ALL PRIVILEGES ON bitnami_drupal.* TO 'bn_drupal'@'10.0.6.128/255.255.255.240';

SHOW GRANTS for 'bn_drupal'@'10.0.6.140/255.255.255.240';

[OUTPUT]
+-----------------------------------------------------------------------------------------------------------------------------------+
| Grants for bn_drupal@10.0.6.128/255.255.255.240                                                                                   |
+-----------------------------------------------------------------------------------------------------------------------------------+
| GRANT USAGE ON *.* TO `bn_drupal`@`10.0.6.128/255.255.255.240` IDENTIFIED BY PASSWORD '*DC24853B02A54931D1902B9CBFB53CDB5CCFDEBC' |
| GRANT ALL PRIVILEGES ON `bitnami_drupal`.* TO `bn_drupal`@`10.0.6.128/255.255.255.240`                                            |
+-----------------------------------------------------------------------------------------------------------------------------------+
  
