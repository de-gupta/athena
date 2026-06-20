package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import de.gupta.commons.utility.math.ordering.Ordering;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// a, b, c must satisfy a ≤ b ≤ c in the subject's order
public record PartialOrderStructureLaws<E>(PartialOrderStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("compare(x, x) == EQUAL for all x (reflexivity)", this::reflexivity),
				dynamicTest("compare(a, b) == LESS_THAN implies compare(b, a) == GREATER_THAN (antisymmetry)",
						this::antisymmetry),
				dynamicTest("a ≤ b and b ≤ c implies a ≤ c (transitivity)", this::transitivity),
				dynamicTest("leq, lt, geq, gt, isComparable are consistent with compare",
						this::defaultsConsistentWithCompare)
		);
	}

	private void reflexivity()
	{
		assertThat(subject.compare(a, a)).as("compare(a, a)").isEqualTo(OrderRelation.EQUAL);
		assertThat(subject.compare(b, b)).as("compare(b, b)").isEqualTo(OrderRelation.EQUAL);
		assertThat(subject.compare(c, c)).as("compare(c, c)").isEqualTo(OrderRelation.EQUAL);
	}

	private void antisymmetry()
	{
		if (subject.compare(a, b) == OrderRelation.LESS_THAN)
			assertThat(subject.compare(b, a)).as("compare(b, a) when compare(a, b) == LESS_THAN")
			                                 .isEqualTo(OrderRelation.GREATER_THAN);
		if (subject.compare(b, c) == OrderRelation.LESS_THAN)
			assertThat(subject.compare(c, b)).as("compare(c, b) when compare(b, c) == LESS_THAN")
			                                 .isEqualTo(OrderRelation.GREATER_THAN);
	}

	private void transitivity()
	{
		if (subject.leq(a, b) && subject.leq(b, c))
			assertThat(subject.leq(a, c)).as("a ≤ b and b ≤ c must imply a ≤ c").isEqualTo(true);
	}

	private void defaultsConsistentWithCompare()
	{
		Ordering ab = subject.compare(a, b);
		Ordering ba = subject.compare(b, a);
		assertThat(subject.leq(a, b)).as("leq(a, b)")
		                             .isEqualTo(ab instanceof OrderRelation r && r.isLessThanOrEqualTo());
		assertThat(subject.lt(a, b)).as("lt(a, b)")
		                            .isEqualTo(ab instanceof OrderRelation r && r.isLessThan());
		assertThat(subject.geq(b, a)).as("geq(b, a)")
		                             .isEqualTo(ba instanceof OrderRelation r && r.isGreaterThanOrEqualTo());
		assertThat(subject.gt(b, a)).as("gt(b, a)")
		                            .isEqualTo(ba instanceof OrderRelation r && r.isGreaterThan());
		assertThat(subject.compare(a, b)).as("isComparable(a, b)").isInstanceOf(OrderRelation.class);
	}
}