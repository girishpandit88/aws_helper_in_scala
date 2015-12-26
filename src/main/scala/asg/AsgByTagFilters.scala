package asg

import com.amazonaws.services.autoscaling.model.AutoScalingGroup

class AsgByTagFilters(autoScalingGroups : AutoScalingGroups) extends AsgByTagFiltersTrait{
	def this() {
		this(new AutoScalingGroups())
	}

	override def asgByFilters(filters: Map[String, String]): Set[AutoScalingGroup] =
		autoScalingGroups.getAutoScalingGroupsByTags(filters)
}

trait AsgByTagFiltersTrait {
	def asgByFilters(filters: Map[String, String]): Set[AutoScalingGroup]
}
