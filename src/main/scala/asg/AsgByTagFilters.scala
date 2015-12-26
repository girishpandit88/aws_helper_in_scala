package asg

import com.amazonaws.services.autoscaling.model.AutoScalingGroup

import scala.collection.JavaConverters._
import scala.collection.mutable

class AsgByTagFilters(autoScalingGroups : AutoScalingGroups) extends AsgByTagFiltersTrait{
	def this() {
		this(new AutoScalingGroups())
	}

	override def asgByFilters(filters: Map[String, String]): Set[AutoScalingGroup] = {
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
}

trait AsgByTagFiltersTrait {
	def asgByFilters(filters: Map[String, String]): Set[AutoScalingGroup]
}
