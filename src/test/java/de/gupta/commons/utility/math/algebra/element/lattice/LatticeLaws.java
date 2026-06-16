package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record LatticeLaws<E extends Lattice<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				Stream.concat(
						new MeetSemilatticeLaws<>(a, b, c).tests(),
						new JoinSemilatticeLaws<>(a, b, c).tests()
				),
				Stream.of(
						dynamicTest("a.meet(a.join(b)) == a (absorption)", this::meetAbsorption),
						dynamicTest("a.join(a.meet(b)) == a (absorption)", this::joinAbsorption)
				)
		);
	}

	private void meetAbsorption()
	{
		assertThat(a.meet(a.join(b))).as("a.meet(a.join(b))").isEqualTo(a);
	}

	private void joinAbsorption()
	{
		assertThat(a.join(a.meet(b))).as("a.join(a.meet(b))").isEqualTo(a);
	}
}
