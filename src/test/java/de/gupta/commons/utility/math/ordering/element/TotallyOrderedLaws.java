package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// a, b, c must satisfy a ≤ b ≤ c in the total order
public record TotallyOrderedLaws<E extends TotallyOrdered<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new PartiallyOrderedLaws<>(a, b, c).tests(),
				Stream.of(
						dynamicTest("all pairs produce OrderRelation (totality)", this::totality),
						dynamicTest("toComparisonResult is consistent with compare", this::bridgeIsConsistent)
				)
		);
	}

	private void totality()
	{
		assertThat(a.compare(b)).as("a.compare(b)").isInstanceOf(OrderRelation.class);
		assertThat(b.compare(a)).as("b.compare(a)").isInstanceOf(OrderRelation.class);
		assertThat(a.compare(c)).as("a.compare(c)").isInstanceOf(OrderRelation.class);
		assertThat(b.compare(c)).as("b.compare(c)").isInstanceOf(OrderRelation.class);
	}

	private void bridgeIsConsistent()
	{
		assertThat(a.toComparisonResult(b))
				.as("a.toComparisonResult(b)")
				.isEqualTo(a.compare(b).toComparisonResult());
		assertThat(b.toComparisonResult(c))
				.as("b.toComparisonResult(c)")
				.isEqualTo(b.compare(c).toComparisonResult());
	}
}
