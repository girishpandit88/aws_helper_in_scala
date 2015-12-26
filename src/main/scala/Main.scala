import asg.AsgByTagFilters
import ec2.EC2InstancesByFilter

object Main {
	def main(args: Array[String]) {
//		val filteredAsg = new AsgByTagFilters().asgByFilters(Map("Name" -> "sample-app", "environment" -> "dev"))
//		filteredAsg.foreach(asg => println(asg.getAutoScalingGroupName))
		val ec2Helper = new EC2InstancesByFilter()
//		val ec2InstancesByFilters = ec2Helper.ec2InstancesByFilter(Map("Name" -> "sample-app", "environment" -> "dev"))
//		ec2InstancesByFilters.foreach(instance => println(instance.getPublicIpAddress + " " + instance.getKeyName))
		val ec2InstanceIpsByFilters = ec2Helper.ec2InstanceIPsByFilter(Map("service-name" -> "optel", "service-environment" -> "optel-load"))
		ec2InstanceIpsByFilters.foreach(println(_))
	}
}
