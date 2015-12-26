package ec2

import asg.AsgHelper.AsgFilter
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.model.Instance
import com.amazonaws.services.ec2.model.{DescribeInstancesRequest, Reservation}
import com.amazonaws.services.ec2.{AmazonEC2Client, model}

import scala.collection.JavaConverters._
import scala.collection.mutable

object EC2Helper {
	def main(args: Array[String]) {
		val ec2Helper = new EC2Helper()
		ec2Helper.ec2Filter(Map("Name" -> "sample-app", "environment" -> "dev"))
			.foreach(instance => println(instance.getPublicIpAddress + " " + instance.getKeyName))
	}

	class EC2Helper(amazonEC2Client: AmazonEC2Client) extends EC2HelperTrait {
		def this() {
			this(new AmazonEC2Client(new DefaultAWSCredentialsProviderChain))
		}

		def getAsgInstances(filters: Map[String, String]) = {
			var instances = new mutable.MutableList[Instance]
			val filteredAsgs = new AsgFilter().asgByFilters(filters)
			filteredAsgs.foreach(asg => {
				asg.getInstances.asScala.foreach(instances+=_)
			})
			instances
		}

		private def describeEc2InstanceReservations(instance: Instance): mutable.Buffer[Reservation] = {
			amazonEC2Client.describeInstances(new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId)).getReservations
		}.asScala

		override def ec2Filter(filters: Map[String, String]): mutable.MutableList[model.Instance] = {
			val filteredInstances = getAsgInstances(filters)
			var ec2Instances = new mutable.MutableList[com.amazonaws.services.ec2.model.Instance]
			filteredInstances.foreach(describeEc2InstanceReservations(_)
				.foreach(_.getInstances.asScala.foreach(ec2Instances += _)))
			ec2Instances
		}
	}

	trait EC2HelperTrait {
		def ec2Filter(filters: Map[String, String]): mutable.MutableList[model.Instance]
	}

}
