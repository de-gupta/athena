package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record BoundedLatticeLaws<E extends BoundedLattice<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new LatticeLaws<>(a, b, c).tests(),
				Stream.of(
						dynamicTest("a.meet(a.top()) == a (top is identity for meet)", this::topIdentityForMeet),
						dynamicTest("a.join(a.bottom()) == a (bottom is identity for join)",
								this::bottomIdentityForJoin),
						dynamicTest("a.meet(a.bottom()) == bottom() (bottom absorbs meet)", this::bottomAbsorbsMeet),
						dynamicTest("a.join(a.top()) == top() (top absorbs join)", this::topAbsorbsJoin)
				)
		);
	}

	private void topIdentityForMeet()
	{
		assertThat(a.meet(a.top())).as("a.meet(a.top())").isEqualTo(a);
	}

	private void bottomIdentityForJoin()
	{
		assertThat(a.join(a.bottom())).as("a.join(a.bottom())").isEqualTo(a);
	}

	private void bottomAbsorbsMeet()
	{
		assertThat(a.meet(a.bottom())).as("a.meet(a.bottom())").isEqualTo(a.bottom());
	}

	private void topAbsorbsJoin()
	{
		assertThat(a.join(a.top())).as("a.join(a.top())").isEqualTo(a.top());
	}
}