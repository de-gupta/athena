package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BoundedLatticeStructureLaws<E> extends LatticeStructureLaws<E>
{
	@Property
	default void topIsIdentityForMeet(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, subject().top())).as("meet(a, top())").isEqualTo(a);
	}

	@Override
	BoundedLatticeStructure<E> subject();

	@Property
	default void bottomIsIdentityForJoin(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, subject().bottom())).as("join(a, bottom())").isEqualTo(a);
	}

	@Property
	default void bottomAbsorbsMeet(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, subject().bottom())).as("meet(a, bottom())").isEqualTo(subject().bottom());
	}

	@Property
	default void topAbsorbsJoin(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, subject().top())).as("join(a, top())").isEqualTo(subject().top());
	}
}