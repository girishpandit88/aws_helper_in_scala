AWS Helper utilites in Scala
=========================================

This project is designed to teach myself scala. At the same time I'll be adding aws helper functions which I can use in my daily code

##AsgHelper
Usage of AsgHelper

```scala
val asgFilter = new AsgFilter()
val filteredAsg = asgFilter.asgByFilters(Map("TagKey1" -> "TagValue1", "TagKey2" -> "TagValue1"))
filteredAsg.foreach(asg => println(asg.getAutoScalingGroupName))
```

##EC2Helper
Usage of EC2Helper

```scala
val ec2Helper = new EC2Helper()
ec2Helper.ec2Filter(Map("Name" -> "sample-app", "environment" -> "dev"))
    .foreach(instance => println(instance.getPublicIpAddress + " " + instance.getKeyName))
```