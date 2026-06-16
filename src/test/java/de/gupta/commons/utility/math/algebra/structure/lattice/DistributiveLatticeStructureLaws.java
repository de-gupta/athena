package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface DistributiveLatticeStructureLaws<E> extends BoundedLatticeStructureLaws<E>
{
	@Property
	default void meetDistributesOverJoin(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(subject().meet(a, subject().join(b, c)))
				.as("meet(a, join(b, c))")
				.isEqualTo(subject().join(subject().meet(a, b), subject().meet(a, c)));
	}

	@Override
	DistributiveLatticeStructure<E> subject();

	@Property
	default void joinDistributesOverMeet(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(subject().join(a, subject().meet(b, c)))
				.as("join(a, meet(b, c))")
				.isEqualTo(subject().meet(subject().join(a, b), subject().join(a, c)));
	}
}