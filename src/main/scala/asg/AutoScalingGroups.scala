package asg

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient
import com.amazonaws.services.autoscaling.model.{Instance, DescribeAutoScalingGroupsRequest, AutoScalingGroup}
import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.HashSet

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

	override def getAutoScalingGroupsByTags(filters: Map[String, String]): Set[AutoScalingGroup] = {
		var asgs = new AutoScalingGroups().getAutoScalingGroups(None)
		filters.foreach(p => {
			val filteredAsgList = new mutable.HashSet[AutoScalingGroup]
			asgs.foreach(asg => {
				val tags = asg.getTags.asScala
				tags.foreach(tag => {
					if (tag.getKey.equalsIgnoreCase(p._1) && tag.getValue.equals(p._2)) {
						filteredAsgList.add(asg)
					}
				})
			}
			)
			asgs = asgs.intersect(filteredAsgList)
		}
		)
		asgs
	}

	override def getAutoScalingGroupInstancesByTags(filters: Map[String, String]): Set[Instance] = {
		val autoScalingGroups = getAutoScalingGroupsByTags(filters)
		var instances = new HashSet[Instance]
		autoScalingGroups.foreach(asg => {
			asg.getInstances.asScala.foreach(instances += _)
		})
		instances.toSet
	}
}

trait AutoScalingGroupsTrait{
	def getAutoScalingGroups(nextToken: Option[String] = None): Set[AutoScalingGroup]
	def getAutoScalingGroupsByTags(filters: Map[String,String]): Set[AutoScalingGroup]
	def getAutoScalingGroupInstancesByTags(filters:Map[String,String]) : Set[Instance]

}
