package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record MeetSemilatticeLaws<E extends MeetSemilattice<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("a.meet(a) == a (idempotence)", this::idempotence),
				dynamicTest("a.meet(b) == b.meet(a) (commutativity)", this::commutativity),
				dynamicTest("a.meet(b).meet(c) == a.meet(b.meet(c)) (associativity)", this::associativity)
		);
	}

	private void idempotence()
	{
		assertThat(a.meet(a)).as("a.meet(a)").isEqualTo(a);
		assertThat(b.meet(b)).as("b.meet(b)").isEqualTo(b);
	}

	private void commutativity()
	{
		assertThat(a.meet(b)).as("a.meet(b)").isEqualTo(b.meet(a));
	}

	private void associativity()
	{
		assertThat(a.meet(b).meet(c)).as("a.meet(b).meet(c)").isEqualTo(a.meet(b.meet(c)));
	}
}
