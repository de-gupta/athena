package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record BoundedLatticeStructureLaws<E>(BoundedLatticeStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new LatticeStructureLaws<>(subject, a, b, c).tests(),
				Stream.of(
						dynamicTest("meet(a, top()) == a (top is identity for meet)", this::topIdentityForMeet),
						dynamicTest("join(a, bottom()) == a (bottom is identity for join)",
								this::bottomIdentityForJoin),
						dynamicTest("meet(a, bottom()) == bottom() (bottom absorbs meet)", this::bottomAbsorbsMeet),
						dynamicTest("join(a, top()) == top() (top absorbs join)", this::topAbsorbsJoin)
				)
		);
	}

	private void topIdentityForMeet()
	{
		assertThat(subject.meet(a, subject.top())).as("meet(a, top())").isEqualTo(a);
	}

	private void bottomIdentityForJoin()
	{
		assertThat(subject.join(a, subject.bottom())).as("join(a, bottom())").isEqualTo(a);
	}

	private void bottomAbsorbsMeet()
	{
		assertThat(subject.meet(a, subject.bottom())).as("meet(a, bottom())").isEqualTo(subject.bottom());
	}

	private void topAbsorbsJoin()
	{
		assertThat(subject.join(a, subject.top())).as("join(a, top())").isEqualTo(subject.top());
	}
}