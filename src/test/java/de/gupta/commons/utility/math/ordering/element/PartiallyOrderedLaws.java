package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// a, b, c must satisfy a ≤ b ≤ c in the partial order
public record PartiallyOrderedLaws<E extends PartiallyOrdered<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("x.compare(x) == EQUAL for all x (reflexivity)", this::reflexivity),
				dynamicTest("a.compare(b) == LESS_THAN implies b.compare(a) == GREATER_THAN (antisymmetry)",
						this::antisymmetry),
				dynamicTest("a ≤ b and b ≤ c implies a ≤ c (transitivity)", this::transitivity),
				dynamicTest("leq, lt, geq, gt, isComparableTo are consistent with compare",
						this::defaultsConsistentWithCompare)
		);
	}

	private void reflexivity()
	{
		assertThat(a.compare(a)).as("a.compare(a)").isEqualTo(OrderRelation.EQUAL);
		assertThat(b.compare(b)).as("b.compare(b)").isEqualTo(OrderRelation.EQUAL);
		assertThat(c.compare(c)).as("c.compare(c)").isEqualTo(OrderRelation.EQUAL);
	}

	private void antisymmetry()
	{
		if (a.compare(b) == OrderRelation.LESS_THAN)
			assertThat(b.compare(a)).as("b.compare(a) when a.compare(b) == LESS_THAN")
			                        .isEqualTo(OrderRelation.GREATER_THAN);
		if (b.compare(c) == OrderRelation.LESS_THAN)
			assertThat(c.compare(b)).as("c.compare(b) when b.compare(c) == LESS_THAN")
			                        .isEqualTo(OrderRelation.GREATER_THAN);
	}

	private void transitivity()
	{
		if (a.leq(b) && b.leq(c))
			assertThat(a.leq(c)).as("a ≤ b and b ≤ c must imply a ≤ c").isEqualTo(true);
	}

	private void defaultsConsistentWithCompare()
	{
		assertThat(a.leq(b)).as("a.leq(b)").isEqualTo(a.compare(b).isLessThanOrEqualTo());
		assertThat(a.lt(b)).as("a.lt(b)").isEqualTo(a.compare(b).isLessThan());
		assertThat(b.geq(a)).as("b.geq(a)").isEqualTo(b.compare(a).isGreaterThanOrEqualTo());
		assertThat(b.gt(a)).as("b.gt(a)").isEqualTo(b.compare(a).isGreaterThan());
		assertThat(a.compare(b)).as("a.isComparableTo(b)").isNotEqualTo(OrderRelation.INCOMPARABLE);
	}
}