package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BoundedLatticeLaws<E extends BoundedLattice<E>> extends LatticeLaws<E>
{
	@Property
	default void supremumIsIdentityForMeet(@ForAll("elements") E a)
	{
		assertThat(a.meet(a.supremum())).as("a.meet(a.top())").isEqualTo(a);
	}

	@Property
	default void infimumIsIdentityForJoin(@ForAll("elements") E a)
	{
		assertThat(a.join(a.infimum())).as("a.join(a.bottom())").isEqualTo(a);
	}

	@Property
	default void infimumAbsorbsMeet(@ForAll("elements") E a)
	{
		assertThat(a.meet(a.infimum())).as("a.meet(a.bottom())").isEqualTo(a.infimum());
	}

	@Property
	default void supremumAbsorbsJoin(@ForAll("elements") E a)
	{
		assertThat(a.join(a.supremum())).as("a.join(a.top())").isEqualTo(a.supremum());
	}
}