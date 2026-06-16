package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record JoinSemilatticeStructureLaws<E>(JoinSemilatticeStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("join(a, a) == a (idempotence)", this::idempotence),
				dynamicTest("join(a, b) == join(b, a) (commutativity)", this::commutativity),
				dynamicTest("join(join(a, b), c) == join(a, join(b, c)) (associativity)", this::associativity)
		);
	}

	private void idempotence()
	{
		assertThat(subject.join(a, a)).as("join(a, a)").isEqualTo(a);
		assertThat(subject.join(b, b)).as("join(b, b)").isEqualTo(b);
	}

	private void commutativity()
	{
		assertThat(subject.join(a, b)).as("join(a, b)").isEqualTo(subject.join(b, a));
	}

	private void associativity()
	{
		assertThat(subject.join(subject.join(a, b), c))
				.as("join(join(a, b), c)")
				.isEqualTo(subject.join(a, subject.join(b, c)));
	}
}
