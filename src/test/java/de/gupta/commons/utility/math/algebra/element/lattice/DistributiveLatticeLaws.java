package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record DistributiveLatticeLaws<E extends DistributiveLattice<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new BoundedLatticeLaws<>(a, b, c).tests(),
				Stream.of(
						dynamicTest("a.meet(b.join(c)) == a.meet(b).join(a.meet(c)) (meet distributes over join)",
								this::meetDistributesOverJoin),
						dynamicTest("a.join(b.meet(c)) == a.join(b).meet(a.join(c)) (join distributes over meet)",
								this::joinDistributesOverMeet)
				)
		);
	}

	private void meetDistributesOverJoin()
	{
		assertThat(a.meet(b.join(c)))
				.as("a.meet(b.join(c))")
				.isEqualTo(a.meet(b).join(a.meet(c)));
	}

	private void joinDistributesOverMeet()
	{
		assertThat(a.join(b.meet(c)))
				.as("a.join(b.meet(c))")
				.isEqualTo(a.join(b).meet(a.join(c)));
	}
}
