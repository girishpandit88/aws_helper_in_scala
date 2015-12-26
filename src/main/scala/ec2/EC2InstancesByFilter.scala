package ec2

import asg.AutoScalingGroups
import com.amazonaws.services.ec2.model

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.HashSet

class EC2InstancesByFilter(ec2Instances:EC2InstanceReservations,autoScalingGroups: AutoScalingGroups) extends EC2InstancesByFilterTrait {
	def this() {
		this(
			new EC2InstanceReservations(),
		new AutoScalingGroups())
	}

	override def ec2InstancesByFilter(filters: Map[String, String]): Set[model.Instance] = {
		val filteredInstances = autoScalingGroups.getAutoScalingGroupInstancesByTags(filters)
		val ec2InstancesByTagFilter = new HashSet[com.amazonaws.services.ec2.model.Instance]
		filteredInstances.foreach(
			ec2Instances.describeEc2InstanceReservations(_).foreach(_.getInstances.asScala.foreach(ec2InstancesByTagFilter.+=_)))
		ec2InstancesByTagFilter.toSet
	}

	override def ec2InstanceIPsByFilter(filters: Map[String, String]): Set[(String, String, String)] = {
		val ec2InstancesByFilters = ec2InstancesByFilter(filters)
		val ec2InstanceMetadata = new mutable.HashSet[(String,String,String)]
		ec2InstancesByFilters.foreach(instance=>ec2InstanceMetadata.add(instance.getInstanceId,instance.getPublicIpAddress,instance.getPrivateIpAddress))
		ec2InstanceMetadata.toSet
	}
}

trait EC2InstancesByFilterTrait {
	def ec2InstancesByFilter(filters: Map[String, String]): Set[model.Instance]
	def ec2InstanceIPsByFilter(filters: Map[String, String]): Set[(String,String,String)]
}