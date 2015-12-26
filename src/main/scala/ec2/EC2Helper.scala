package ec2

import asg.AsgByTagFilters
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.model.Instance
import com.amazonaws.services.ec2.model.{DescribeInstancesRequest, Reservation}
import com.amazonaws.services.ec2.{AmazonEC2Client, model}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.HashSet


class EC2Helper(amazonEC2Client: AmazonEC2Client) extends EC2HelperTrait {
	def this() {
		this(new AmazonEC2Client(new DefaultAWSCredentialsProviderChain))
	}

	def getAsgInstances(filters: Map[String, String]) = {
		var instances = new HashSet[Instance]
		val filteredAsgs = new AsgByTagFilters().asgByFilters(filters)
		filteredAsgs.foreach(asg => {
			asg.getInstances.asScala.foreach(instances += _)
		})
		instances
	}

	private def describeEc2InstanceReservations(instance: Instance): List[Reservation] = {
		amazonEC2Client.describeInstances(
			new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId)).getReservations.asScala.toList
	}

	override def ec2InstancesByFilter(filters: Map[String, String]): Set[model.Instance] = {
		val filteredInstances = getAsgInstances(filters)
		val ec2Instances = new HashSet[com.amazonaws.services.ec2.model.Instance]
		filteredInstances.foreach(
			describeEc2InstanceReservations(_).foreach(_.getInstances.asScala.foreach(ec2Instances.+=_)))
		ec2Instances.toSet
	}

	override def ec2InstanceIPsByFilter(filters: Map[String, String]): Set[(String, String, String)] = {
		val ec2InstancesByFilters = ec2InstancesByFilter(filters)
		val ec2InstanceMetadata = new mutable.HashSet[(String,String,String)]
		ec2InstancesByFilters.foreach(instance=>ec2InstanceMetadata.add(instance.getInstanceId,instance.getPublicIpAddress,instance.getPrivateIpAddress))
		ec2InstanceMetadata.toSet
	}
}

trait EC2HelperTrait {
	def ec2InstancesByFilter(filters: Map[String, String]): Set[model.Instance]
	def ec2InstanceIPsByFilter(filters: Map[String, String]): Set[(String,String,String)]
}