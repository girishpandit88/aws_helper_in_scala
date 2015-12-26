package asg

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient
import com.amazonaws.services.autoscaling.model.{AutoScalingGroup, DescribeAutoScalingGroupsRequest}

import scala.collection.JavaConverters._
import scala.collection.mutable

object AsgHelper {

	def main(args: Array[String]) {
		val asgFilter = new AsgFilter()
		val filteredAsg = asgFilter.asgByFilters(Map("Name" -> "sample-app", "environment" -> "dev"))
		filteredAsg.foreach(asg => println(asg.getAutoScalingGroupName))
	}

	implicit class AsgFilter(autoScalingAsyncClient: AmazonAutoScalingAsyncClient) extends AsgFilterTrait {
		def this() {
			this(new AmazonAutoScalingAsyncClient(new DefaultAWSCredentialsProviderChain))
		}

		private def getAutoScalingGroups(nextToken: Option[String] = None): List[AutoScalingGroup] = {
			val request = new DescribeAutoScalingGroupsRequest()
			nextToken.foreach(request.setNextToken)
			val result = autoScalingAsyncClient.describeAutoScalingGroups(request)
			val autoScalingGroups = result.getAutoScalingGroups.asScala.toList
			Option(result.getNextToken) match {
				case None => autoScalingGroups
				case token: Some[String] => autoScalingGroups ++ getAutoScalingGroups(token)
			}
		}

		override def asgByFilters(filters: Map[String, String]): List[AutoScalingGroup] = {
			var asgs = getAutoScalingGroups(None)
			filters.foreach(p => {
				val filteredAsgList = new mutable.ArrayBuffer[AutoScalingGroup]

				asgs.foreach(asg => {
					val tags = asg.getTags.asScala
					tags.foreach(tag => {
						if (tag.getKey.equalsIgnoreCase(p._1) && tag.getValue.equals(p._2)) {
							filteredAsgList.append(asg)
						}
					})
				}
				)
				asgs = asgs.intersect(filteredAsgList)
			}
			)
			asgs
		}
	}

	trait AsgFilterTrait {
		def asgByFilters(filters: Map[String, String]): List[AutoScalingGroup]
	}

}
