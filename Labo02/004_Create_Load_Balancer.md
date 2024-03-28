### Deploy the elastic load balancer

In this task you will create a load balancer in AWS that will receive
the HTTP requests from clients and forward them to the Drupal
instances.

![Schema](./img/CLD_AWS_INFA.PNG)

## Task 01 Prerequisites for the ELB

* Create a dedicated security group

|Key|Value|
|:--|:--|
|Name|SG-DEVOPSTEAM[XX]-LB|
|Inbound Rules|Application Load Balancer|
|Outbound Rules|Refer to the infra schema|

```bash
[INPUT]
aws ec2 create-security-group --group-name SG-DEVOPSTEAM06-LB --description "Security group for DEVOPSTEAM06" --vpc-id vpc-03d46c285a2af77ba

aws ec2 authorize-security-group-ingress --group-id sg-04a7f6eb59caca89d --protocol tcp --port 8080 --cidr 10.0.0.0/28 --tag-specifications 'ResourceType=security-group-rule,Tags=[{Key=Description,Value="Allow traffic from DMZ"}]'

Pas de inbound rules je crois ?

[OUTPUT]
{
    "GroupId": "sg-04a7f6eb59caca89d"
}

Deuxième commande :

{
    "Return": true,
    "SecurityGroupRules": [
        {
            "SecurityGroupRuleId": "sgr-0434568e220ef04e1",
            "GroupId": "sg-04a7f6eb59caca89d",
            "GroupOwnerId": "709024702237",
            "IsEgress": false,
            "IpProtocol": "tcp",
            "FromPort": 8080,
            "ToPort": 8080,
            "CidrIpv4": "10.0.0.0/28",
            "Tags": [
                {
                    "Key": "Description",
                    "Value": "Allow traffic from DMZ"
                }
            ]
        }
    ]
}

```

* Create the Target Group

|Key|Value|
|:--|:--|
|Target type|Instances|
|Name|TG-DEVOPSTEAM[XX]|
|Protocol and port|Refer to the infra schema|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Protocol version|HTTP1|
|Health check protocol|HTTP|
|Health check path|/|
|Port|Traffic port|
|Healthy threshold|2 consecutive health check successes|
|Unhealthy threshold|2 consecutive health check failures|
|Timeout|5 seconds|
|Interval|10 seconds|
|Success codes|200|

```bash
[INPUT]
aws elbv2 create-target-group --name TG-DEVOPSTEAM06 --target-type instance --protocol HTTP --protocol-version HTTP1 --port 8080 --vpc-id vpc-03d46c285a2af77ba --health-check-path / --health-check-interval-seconds 10 --health-check-timeout-seconds 5 --healthy-threshold-count 2 --unhealthy-threshold-count 2 --matcher HttpCode=200


[OUTPUT]
{
    "TargetGroups": [
        {
            "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM06/a1848959aed57401",
            "TargetGroupName": "TG-DEVOPSTEAM06",
            "Protocol": "HTTP",
            "Port": 8080,
            "VpcId": "vpc-03d46c285a2af77ba",
            "HealthCheckProtocol": "HTTP",
            "HealthCheckPort": "traffic-port",
            "HealthCheckEnabled": true,
            "HealthCheckIntervalSeconds": 10,
            "HealthCheckTimeoutSeconds": 5,
            "HealthyThresholdCount": 2,
            "UnhealthyThresholdCount": 2,
            "HealthCheckPath": "/",
            "Matcher": {
                "HttpCode": "200"
            },
            "TargetType": "instance",
            "ProtocolVersion": "HTTP1",
            "IpAddressType": "ipv4"
        }
    ]
}

Deuxième commande : 



```


## Task 02 Deploy the Load Balancer

[Source](https://aws.amazon.com/elasticloadbalancing/)

* Create the Load Balancer

|Key|Value|
|:--|:--|
|Type|Application Load Balancer|
|Name|ELB-DEVOPSTEAM99|
|Scheme|Internal|
|Ip Address type|IPv4|
|VPC|Refer to the infra schema|
|Security group|Refer to the infra schema|
|Listeners Protocol and port|Refer to the infra schema|
|Target group|Your own target group created in task 01|

Provide the following answers (leave any
field not mentioned at its default value):

```bash
[INPUT]
aws elbv2 create-load-balancer --name ELB-DEVOPSTEAM06 --scheme internal --ip-address-type ipv4 --subnets subnet-0253e0ee80bd5f30d subnet-047f793626e0a808a --security-group sg-04a7f6eb59caca89d --type application

aws elbv2 create-listener --load-balancer-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM06/97a578c36ad66a3a --protocol HTTP --port 8080 --default-actions Type=forward,TargetGroupArn=arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM06/a1848959aed57401

aws elbv2 register-targets --target-group-arn arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM06/a1848959aed57401 --targets Id=i-0a37272caf40b181c Id=i-04ab2783ab75bde97

[OUTPUT]
{
    "LoadBalancers": [
        {
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM06/97a578c36ad66a3a",
            "DNSName": "internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com",
            "CanonicalHostedZoneId": "Z3Q77PNBQS71R4",
            "CreatedTime": "2024-03-28T16:53:24.420000+00:00",
            "LoadBalancerName": "ELB-DEVOPSTEAM06",
            "Scheme": "internal",
            "VpcId": "vpc-03d46c285a2af77ba",
            "State": {
                "Code": "provisioning"
            },
            "Type": "application",
            "AvailabilityZones": [
                {
                    "ZoneName": "eu-west-3a",
                    "SubnetId": "subnet-0253e0ee80bd5f30d",
                    "LoadBalancerAddresses": []
                },
                {
                    "ZoneName": "eu-west-3b",
                    "SubnetId": "subnet-047f793626e0a808a",
                    "LoadBalancerAddresses": []
                }
            ],
            "SecurityGroups": [
                "sg-04a7f6eb59caca89d"
            ],
            "IpAddressType": "ipv4"
        }
    ]
}

Deuxième commande :

{
    "Listeners": [
        {
            "ListenerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:listener/app/ELB-DEVOPSTEAM06/97a578c36ad66a3a/d1077cd208f9594b",
            "LoadBalancerArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:loadbalancer/app/ELB-DEVOPSTEAM06/97a578c36ad66a3a",
            "Port": 8080,
            "Protocol": "HTTP",
            "DefaultActions": [
                {
                    "Type": "forward",
                    "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM06/a1848959aed57401",
                    "ForwardConfig": {
                        "TargetGroups": [
                            {
                                "TargetGroupArn": "arn:aws:elasticloadbalancing:eu-west-3:709024702237:targetgroup/TG-DEVOPSTEAM06/a1848959aed57401",
                                "Weight": 1
                            }
                        ],
                        "TargetGroupStickinessConfig": {
                            "Enabled": false
                        }
                    }
                }
            ]
        }
    ]
}
```

* Get the ELB FQDN (DNS NAME - A Record)

```bash
[INPUT]
aws elbv2 describe-load-balancers --names ELB-DEVOPSTEAM06 | Select-String -Pattern '"DNSName": "' | ForEach-Object { $_ -replace '.*"DNSName": "(.*?)".*', '$1' }

[OUTPUT]
internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com
```

* Get the ELB deployment status

Note : In the EC2 console select the Target Group. In the
       lower half of the panel, click on the **Targets** tab. Watch the
       status of the instance go from **unused** to **initial**.

* Ask the DMZ administrator to register your ELB with the reverse proxy via the private teams channel

* Update your string connection to test your ELB and test it

```bash
//connection string updated
ssh devopsteam06@15.188.43.46 -i CLD_KEY_DMZ_DEVOPSTEAM06.pem -L 1234:internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com:8080
```

* Test your application through your ssh tunneling

```bash
[INPUT]
curl localhost:1234

[OUTPUT]
La commande affiche un long site web
```

#### Questions - Analysis

* On your local machine resolve the DNS name of the load balancer into
  an IP address using the `nslookup` command (works on Linux, macOS and Windows). Write
  the DNS name and the resolved IP Address(es) into the report.

```
//TODO
nslookup internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com

Name:   internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com
Address: 10.0.6.140
Name:   internal-ELB-DEVOPSTEAM06-894117530.eu-west-3.elb.amazonaws.com
Address: 10.0.6.9
```

* From your Drupal instance, identify the ip from which requests are sent by the Load Balancer.

Help : execute `tcpdump port 8080`

```
Output :
18:54:23.418360 IP 10.0.6.9.46236 > provisioner-local.http-alt: Flags [P.], seq 1:130, ack 1, win 106, options [nop,nop,TS val 5724521169 ecr 845259034], length 129: HTTP: GET / HTTP/1.1
```

* In the Apache access log identify the health check accesses from the
  load balancer and copy some samples into the report.

```
Commande :
tail /opt/bitnami/apache2/logs/access_log

Output : 
10.0.6.140 - - [28/Mar/2024:19:00:56 +0000] "GET / HTTP/1.1" 200 5142
10.0.6.9 - - [28/Mar/2024:19:01:01 +0000] "GET / HTTP/1.1" 200 5142
10.0.6.140 - - [28/Mar/2024:19:01:06 +0000] "GET / HTTP/1.1" 200 5142
10.0.6.9 - - [28/Mar/2024:19:01:11 +0000] "GET / HTTP/1.1" 200 5142
10.0.6.140 - - [28/Mar/2024:19:01:16 +0000] "GET / HTTP/1.1" 200 5142
10.0.6.9 - - [28/Mar/2024:19:01:21 +0000] "GET / HTTP/1.1" 200 5142
```
