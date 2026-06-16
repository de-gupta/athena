package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record MeetSemilatticeStructureLaws<E>(MeetSemilatticeStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.of(
				dynamicTest("meet(a, a) == a (idempotence)", this::idempotence),
				dynamicTest("meet(a, b) == meet(b, a) (commutativity)", this::commutativity),
				dynamicTest("meet(meet(a, b), c) == meet(a, meet(b, c)) (associativity)", this::associativity)
		);
	}

	private void idempotence()
	{
		assertThat(subject.meet(a, a)).as("meet(a, a)").isEqualTo(a);
		assertThat(subject.meet(b, b)).as("meet(b, b)").isEqualTo(b);
	}

	private void commutativity()
	{
		assertThat(subject.meet(a, b)).as("meet(a, b)").isEqualTo(subject.meet(b, a));
	}

	private void associativity()
	{
		assertThat(subject.meet(subject.meet(a, b), c))
				.as("meet(meet(a, b), c)")
				.isEqualTo(subject.meet(a, subject.meet(b, c)));
	}
}
