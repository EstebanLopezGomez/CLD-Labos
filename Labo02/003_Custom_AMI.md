# Custom AMI and Deploy the second Drupal instance

In this task you will update your AMI with the Drupal settings and deploy it in the second availability zone.

## Task 01 - Create AMI

### [Create AMI](https://awscli.amazonaws.com/v2/documentation/api/latest/reference/ec2/create-image.html)

Note : stop the instance before

|Key|Value for GUI Only|
|:--|:--|
|Name|AMI_DRUPAL_DEVOPSTEAM[XX]_LABO02_RDS|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 stop-instances --instance-id i-04ab2783ab75bde97
[OUTPUT]
{
    "StoppingInstances": [
        {
            "CurrentState": {
                "Code": 64,
                "Name": "stopping"
            },
            "InstanceId": "i-04ab2783ab75bde97",
            "PreviousState": {
                "Code": 16,
                "Name": "running"
            }
        }
    ]
}

[INPUT]
aws ec2 create-image --instance-id i-04ab2783ab75bde97 --name "AMI_DRUPAL_DEVOPSTEAM06_LABO02_RDS" --description "AMI_DRUPAL_DEVOPSTEAM06_LABO02_RDS"
[OUTPUT]
{
    "ImageId": "ami-0e643c60cd590f15a"
}
```

## Task 02 - Deploy Instances

* Restart Drupal Instance in Az1

* Deploy Drupal Instance based on AMI in Az2

|Key|Value for GUI Only|
|:--|:--|
|Name|EC2_PRIVATE_DRUPAL_DEVOPSTEAM[XX]_B|
|Description|Same as name value|

```bash
[INPUT]
aws ec2 run-instances --image-id ami-0e643c60cd590f15a --count 1 --instance-type t3.micro --key-name CLD_KEY_DRUPAL_DEVOPSTEAM06 --security-group-ids sg-0cc9cc8ad225a4abe --subnet-id subnet-047f793626e0a808a --private-ip-address 10.0.6.140 --tag-specifications 'ResourceType=instance,Tags=[{Key=Name,Value=EC2_PRIVATE_DRUPAL_DEVOPSTEAM06_B}]'

[OUTPUT]
{
    "Groups": [],
    "Instances": [
        {
            "AmiLaunchIndex": 0,
            "ImageId": "ami-0e643c60cd590f15a",
            "InstanceId": "i-0a37272caf40b181c",
            "InstanceType": "t3.micro",
            "KeyName": "CLD_KEY_DRUPAL_DEVOPSTEAM06",
            "LaunchTime": "2024-03-22T09:39:00+00:00",
            "Monitoring": {
                "State": "disabled"
            },
            "Placement": {
                "AvailabilityZone": "eu-west-3b",
                "GroupName": "",
                "Tenancy": "default"
            },
            "PrivateDnsName": "ip-10-0-6-140.eu-west-3.compute.internal",
            "PrivateIpAddress": "10.0.6.140",
            "ProductCodes": [],
            "PublicDnsName": "",
            "State": {
                "Code": 0,
                "Name": "pending"
            },
            "StateTransitionReason": "",
            "SubnetId": "subnet-047f793626e0a808a",
            "VpcId": "vpc-03d46c285a2af77ba",
            "Architecture": "x86_64",
            "BlockDeviceMappings": [],
            "ClientToken": "68906d7c-606e-4506-bcc9-f80198e8a42f",
            "EbsOptimized": false,
            "EnaSupport": true,
            "Hypervisor": "xen",
            "NetworkInterfaces": [
                {
                    "Attachment": {
                        "AttachTime": "2024-03-22T09:39:00+00:00",
                        "AttachmentId": "eni-attach-0f96c12636734bfc5",
                        "DeleteOnTermination": true,
                        "DeviceIndex": 0,
                        "Status": "attaching",
                        "NetworkCardIndex": 0
                    },
                    "Description": "",
                    "Groups": [
                        {
                            "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM06",
                            "GroupId": "sg-0cc9cc8ad225a4abe"
                        }
                    ],
                    "Ipv6Addresses": [],
                    "MacAddress": "0a:ae:61:d7:86:5f",
                    "NetworkInterfaceId": "eni-04c5e180cc5f67ae7",
                    "OwnerId": "709024702237",
                    "PrivateIpAddress": "10.0.6.140",
                    "PrivateIpAddresses": [
                        {
                            "Primary": true,
                            "PrivateIpAddress": "10.0.6.140"
                        }
                    ],
                    "SourceDestCheck": true,
                    "Status": "in-use",
                    "SubnetId": "subnet-047f793626e0a808a",
                    "VpcId": "vpc-03d46c285a2af77ba",
                    "InterfaceType": "interface"
                }
            ],
            "RootDeviceName": "/dev/xvda",
            "RootDeviceType": "ebs",
            "SecurityGroups": [
                {
                    "GroupName": "SG-PRIVATE-DRUPAL-DEVOPSTEAM06",
                    "GroupId": "sg-0cc9cc8ad225a4abe"
                }
            ],
            "SourceDestCheck": true,
            "StateReason": {
                "Code": "pending",
                "Message": "pending"
            },
            "Tags": [
                {
                    "Key": "Name",
                    "Value": "EC2_PRIVATE_DRUPAL_DEVOPSTEAM06_B"
                }
            ],
            "VirtualizationType": "hvm",
            "CpuOptions": {
                "CoreCount": 1,
                "ThreadsPerCore": 2
            },
            "CapacityReservationSpecification": {
                "CapacityReservationPreference": "open"
            },
            "MetadataOptions": {
                "State": "pending",
                "HttpTokens": "optional",
                "HttpPutResponseHopLimit": 1,
                "HttpEndpoint": "enabled",
                "HttpProtocolIpv6": "disabled",
                "InstanceMetadataTags": "disabled"
            },
            "EnclaveOptions": {
                "Enabled": false
            },
            "PrivateDnsNameOptions": {
                "HostnameType": "ip-name",
                "EnableResourceNameDnsARecord": false,
                "EnableResourceNameDnsAAAARecord": false
            },
            "MaintenanceOptions": {
                "AutoRecovery": "default"
            },
            "CurrentInstanceBootMode": "legacy-bios"
        }
    ],
    "OwnerId": "709024702237",
    "ReservationId": "r-031b11c1c8d423c63"
}
```

## Task 03 - Test the connectivity

### Update your ssh connection string to test

* add tunnels for ssh and http pointing on the B Instance

```bash
//updated string connection
ssh devopsteam06@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM06.pem -L 2224:10.0.6.140:22 -L 8081:10.0.6.140:8081
```

## Check SQL Accesses

```sql
[INPUT]

connect to drupal from new terminal:
ssh bitnami@localhost -p 2224 -i CLD_KEY_DRUPAL_DEVOPSTEAM06.pem


//sql string connection from A
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p
[OUTPUT]
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 349
Server version: 10.11.6-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]>
```

```sql
[INPUT]
//sql string connection from B
mariadb -h dbi-devopsteam06.cshki92s4w5p.eu-west-3.rds.amazonaws.com -u admin -p
[OUTPUT]
Welcome to the MariaDB monitor.  Commands end with ; or \g.
Your MariaDB connection id is 345
Server version: 10.11.6-MariaDB managed by https://aws.amazon.com/rds/

Copyright (c) 2000, 2018, Oracle, MariaDB Corporation Ab and others.

Type 'help;' or '\h' for help. Type '\c' to clear the current input statement.

MariaDB [(none)]>
```

### Check HTTP Accesses

```bash
//connection string updated
ssh devopsteam06@15.188.43.46 -i ~/.ssh/CLD_KEY_DMZ_DEVOPSTEAM06.pem -L 2224:10.0.6.140:22 -L 888:10.0.6.140:8080
```

### Read and write test through the web app

* Login in both webapps (same login)

* Change the users' email address on a webapp... refresh the user's profile page on the second and validated that they are communicating with the same db (rds).

* Observations ?

```
L'adresse email a été mise à jour dans les deux webapps, on peut donc affirmer que les deux sont bien connectés à la RDS
```

### Change the profil picture

* Observations ?

```
La photo de profil n'est pas mise à jour sur la deuxième webapp, on peut donc penser que l'image est stockée localement dans chaque webapp 
```
