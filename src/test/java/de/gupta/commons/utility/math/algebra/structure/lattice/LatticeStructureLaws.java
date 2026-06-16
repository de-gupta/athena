package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record LatticeStructureLaws<E>(LatticeStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				Stream.concat(
						new MeetSemilatticeStructureLaws<>(subject, a, b, c).tests(),
						new JoinSemilatticeStructureLaws<>(subject, a, b, c).tests()
				),
				Stream.of(
						dynamicTest("meet(a, join(a, b)) == a (absorption)", this::meetAbsorption),
						dynamicTest("join(a, meet(a, b)) == a (absorption)", this::joinAbsorption)
				)
		);
	}

	private void meetAbsorption()
	{
		assertThat(subject.meet(a, subject.join(a, b))).as("meet(a, join(a, b))").isEqualTo(a);
	}

	private void joinAbsorption()
	{
		assertThat(subject.join(a, subject.meet(a, b))).as("join(a, meet(a, b))").isEqualTo(a);
	}
}
