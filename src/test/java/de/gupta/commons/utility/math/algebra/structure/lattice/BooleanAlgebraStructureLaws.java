package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record BooleanAlgebraStructureLaws<E>(BooleanAlgebraStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new DistributiveLatticeStructureLaws<>(subject, a, b, c).tests(),
				Stream.of(
						dynamicTest("meet(a, complement(a)) == bottom() (complement law for meet)",
								this::complementLawForMeet),
						dynamicTest("join(a, complement(a)) == top() (complement law for join)",
								this::complementLawForJoin),
						dynamicTest("xor(a, a) == bottom() (self-xor is bottom)", this::selfXorIsBottom),
						dynamicTest("xor(a, complement(a)) == top() (xor with complement is top)",
								this::xorWithComplementIsTop)
				)
		);
	}

	private void complementLawForMeet()
	{
		assertThat(subject.meet(a, subject.complement(a)))
				.as("meet(a, complement(a))")
				.isEqualTo(subject.bottom());
	}

	private void complementLawForJoin()
	{
		assertThat(subject.join(a, subject.complement(a)))
				.as("join(a, complement(a))")
				.isEqualTo(subject.top());
	}

	private void selfXorIsBottom()
	{
		assertThat(subject.xor(a, a)).as("xor(a, a)").isEqualTo(subject.bottom());
	}

	private void xorWithComplementIsTop()
	{
		assertThat(subject.xor(a, subject.complement(a)))
				.as("xor(a, complement(a))")
				.isEqualTo(subject.top());
	}
}
