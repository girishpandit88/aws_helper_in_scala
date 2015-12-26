package asg

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient
import com.amazonaws.services.autoscaling.model.{DescribeAutoScalingGroupsRequest, AutoScalingGroup}
import scala.collection.JavaConverters._

class AutoScalingGroups(autoScalingAsyncClient: AmazonAutoScalingAsyncClient) extends AutoScalingGroupsTrait{
	def this() {
		this(new AmazonAutoScalingAsyncClient(new DefaultAWSCredentialsProviderChain))
	}
	override def getAutoScalingGroups(nextToken: Option[String]): Set[AutoScalingGroup] = {
		val request = new DescribeAutoScalingGroupsRequest()
		nextToken.foreach(request.setNextToken)
		val result = autoScalingAsyncClient.describeAutoScalingGroups(request)
		val autoScalingGroups = result.getAutoScalingGroups.asScala.toSet
		Option(result.getNextToken) match {
			case None => autoScalingGroups
			case token: Some[String] => autoScalingGroups ++ getAutoScalingGroups(token)
		}
	}
}

trait AutoScalingGroupsTrait{
	def getAutoScalingGroups(nextToken: Option[String] = None): Set[AutoScalingGroup]
}
