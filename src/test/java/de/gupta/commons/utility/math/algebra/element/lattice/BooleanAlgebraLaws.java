package de.gupta.commons.utility.math.algebra.element.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record BooleanAlgebraLaws<E extends BooleanAlgebra<E>>(E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new DistributiveLatticeLaws<>(a, b, c).tests(),
				Stream.of(
						dynamicTest("a.meet(a.complement()) == a.bottom() (complement law for meet)",
								this::complementLawForMeet),
						dynamicTest("a.join(a.complement()) == a.top() (complement law for join)",
								this::complementLawForJoin),
						dynamicTest("a.xor(a) == a.bottom() (self-xor is bottom)", this::selfXorIsBottom),
						dynamicTest("a.xor(a.complement()) == a.top() (xor with complement is top)",
								this::xorWithComplementIsTop)
				)
		);
	}

	private void complementLawForMeet()
	{
		assertThat(a.meet(a.complement())).as("a.meet(a.complement())").isEqualTo(a.bottom());
	}

	private void complementLawForJoin()
	{
		assertThat(a.join(a.complement())).as("a.join(a.complement())").isEqualTo(a.top());
	}

	private void selfXorIsBottom()
	{
		assertThat(a.xor(a)).as("a.xor(a)").isEqualTo(a.bottom());
	}

	private void xorWithComplementIsTop()
	{
		assertThat(a.xor(a.complement())).as("a.xor(a.complement())").isEqualTo(a.top());
	}
}
