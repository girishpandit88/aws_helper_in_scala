package asg

import com.amazonaws.services.autoscaling.model.AutoScalingGroup
import org.scalamock.scalatest.MockFactory

import scala.collection.mutable.{HashSet}

class AsgByTagFiltersTest extends org.scalatest.FlatSpec with MockFactory {
//	"A asgByFilter" should "give asg filtered by given filter" in {
//		val getAutoscalingGroups = mock[AutoScalingGroups]
//		val token = None
//		val autoScalingGroups = mock[HashSet[AutoScalingGroup]]
//		(getAutoscalingGroups.getAutoScalingGroups _).when(token).returns(autoScalingGroups.toSet)
//		val filteredAsgs = new AsgByTagFilters()
//		assertResult(filteredAsgs.asgByFilters(Map()))(Set[AutoScalingGroup]) _
//	}

	"Calling new AsgFilter()" should "can new instance" in {
		val asgFilter = new AsgByTagFilters()
		assert(asgFilter.isInstanceOf[AsgByTagFilters])

	}
}
