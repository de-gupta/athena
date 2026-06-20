package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BoundedLatticeStructureLaws<E> extends LatticeStructureLaws<E>
{
	@Property
	default void supremumIsIdentityForMeet(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, subject().supremum())).as("meet(a, top())").isEqualTo(a);
	}

	@Override
	BoundedLatticeStructure<E> subject();

	@Property
	default void infimumIsIdentityForJoin(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, subject().infimum())).as("join(a, bottom())").isEqualTo(a);
	}

	@Property
	default void infimumAbsorbsMeet(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, subject().infimum())).as("meet(a, bottom())").isEqualTo(subject().infimum());
	}

	@Property
	default void supremumAbsorbsJoin(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, subject().supremum())).as("join(a, top())").isEqualTo(subject().supremum());
	}
}