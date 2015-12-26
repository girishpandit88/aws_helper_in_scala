package ec2

import asg.AsgByTagFilters
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.model.Instance
import com.amazonaws.services.ec2.{AmazonEC2AsyncClient, AmazonEC2Client}
import com.amazonaws.services.ec2.model.{DescribeInstancesRequest, Reservation}

import scala.collection.JavaConverters._
import scala.collection.mutable.HashSet

class EC2InstanceReservations(amazonEC2AsyncClient: AmazonEC2AsyncClient) extends EC2InsancesTrait{
	def this(){
		this(new AmazonEC2AsyncClient(new DefaultAWSCredentialsProviderChain))
	}

	override def describeEc2InstanceReservations(instance: Instance): Set[Reservation] = {
		amazonEC2AsyncClient.describeInstances(
			new DescribeInstancesRequest().withInstanceIds(instance.getInstanceId)).getReservations.asScala.toSet
	}
}
trait EC2InsancesTrait{
	def describeEc2InstanceReservations(instance: Instance): Set[Reservation]
}
