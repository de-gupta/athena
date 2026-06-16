package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record JoinSemilatticeLaws<E extends JoinSemilattice<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("a.join(a) == a (idempotence)", this::idempotence),
				dynamicTest("a.join(b) == b.join(a) (commutativity)", this::commutativity),
				dynamicTest("a.join(b).join(c) == a.join(b.join(c)) (associativity)", this::associativity)
		);
	}

	private void idempotence()
	{
		assertThat(a.join(a)).as("a.join(a)").isEqualTo(a);
		assertThat(b.join(b)).as("b.join(b)").isEqualTo(b);
	}

	private void commutativity()
	{
		assertThat(a.join(b)).as("a.join(b)").isEqualTo(b.join(a));
	}

	private void associativity()
	{
		assertThat(a.join(b).join(c)).as("a.join(b).join(c)").isEqualTo(a.join(b.join(c)));
	}
}
