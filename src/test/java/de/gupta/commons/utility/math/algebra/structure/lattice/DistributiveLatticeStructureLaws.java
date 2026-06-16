package de.gupta.commons.utility.math.algebra.structure.lattice;

import org.junit.jupiter.api.DynamicTest;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public record DistributiveLatticeStructureLaws<E>(DistributiveLatticeStructure<E> subject, E a, E b, E c)
{
	public Stream<DynamicTest> tests()
	{
		return Stream.concat(
				new BoundedLatticeStructureLaws<>(subject, a, b, c).tests(),
				Stream.of(
						dynamicTest("meet(a, join(b, c)) == join(meet(a, b), meet(a, c)) (meet distributes over join)",
								this::meetDistributesOverJoin),
						dynamicTest("join(a, meet(b, c)) == meet(join(a, b), join(a, c)) (join distributes over meet)",
								this::joinDistributesOverMeet)
				)
		);
	}

	private void meetDistributesOverJoin()
	{
		assertThat(subject.meet(a, subject.join(b, c)))
				.as("meet(a, join(b, c))")
				.isEqualTo(subject.join(subject.meet(a, b), subject.meet(a, c)));
	}

	private void joinDistributesOverMeet()
	{
		assertThat(subject.join(a, subject.meet(b, c)))
				.as("join(a, meet(b, c))")
				.isEqualTo(subject.meet(subject.join(a, b), subject.join(a, c)));
	}
}
