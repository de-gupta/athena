package de.gupta.commons.utility.math.ordering.structure;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

// a, b, c must satisfy a ≤ b ≤ c in the subject's order
public record TotalOrderStructureLaws<E>(TotalOrderStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new PartialOrderStructureLaws<>(subject, a, b, c).tests(),
				Stream.of(
						dynamicTest("all pairs produce OrderRelation (totality)", this::totality),
						dynamicTest("asDescriptivelyComparableStructure is consistent with compare",
								this::bridgeIsConsistent)
				)
		);
	}

	private void totality()
	{
		assertThat(subject.compare(a, b)).as("compare(a, b)").isInstanceOf(OrderRelation.class);
		assertThat(subject.compare(b, a)).as("compare(b, a)").isInstanceOf(OrderRelation.class);
		assertThat(subject.compare(a, c)).as("compare(a, c)").isInstanceOf(OrderRelation.class);
		assertThat(subject.compare(b, c)).as("compare(b, c)").isInstanceOf(OrderRelation.class);
	}

	private void bridgeIsConsistent()
	{
		var bridge = subject.asDescriptivelyComparableStructure();

		assertThat(bridge.compare(a, b))
				.as("bridge.compare(a, b)")
				.isEqualTo(subject.compare(a, b).toComparisonResult());
		assertThat(bridge.compare(b, c))
				.as("bridge.compare(b, c)")
				.isEqualTo(subject.compare(b, c).toComparisonResult());
	}
}
