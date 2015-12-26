import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.autoscaling.AmazonAutoScalingAsyncClient
import com.amazonaws.services.autoscaling.model.{AutoScalingGroup, DescribeAutoScalingGroupsRequest}

import scala.collection.JavaConverters._

object AsgHelper {

	def main(args: Array[String]) {
		val asgFilter = new AsgFilter()
		val filteredAsg = asgFilter.asgByFilters(Map("Name" -> "sample-app", "environment" -> "dev"))
		filteredAsg.foreach(asg => println(asg.getAutoScalingGroupName))
	}

	class AsgFilter(autoScalingAsyncClient: AmazonAutoScalingAsyncClient) extends AsgFilterTrait {
		def this() {
			this(new AmazonAutoScalingAsyncClient(new DefaultAWSCredentialsProviderChain))
		}

		private def getAutoScalingGroups(): collection.mutable.Buffer[AutoScalingGroup] = {
			autoScalingAsyncClient.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()).getAutoScalingGroups
		}.asScala

		override def asgByFilters(filters: Map[String, String]): collection.mutable.Buffer[AutoScalingGroup] = {
			var asgs = getAutoScalingGroups()
			filters.foreach(f = p => {
				val filteredAsgList = new collection.mutable.ArrayBuffer[AutoScalingGroup]

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
		def asgByFilters(filters: Map[String, String]): collection.mutable.Buffer[AutoScalingGroup]
	}

}
