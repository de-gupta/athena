package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BoundedLatticeLaws<E extends BoundedLattice<E>> extends LatticeLaws<E>
{
	@Property
	default void topIsIdentityForMeet(@ForAll("elements") E a)
	{
		assertThat(a.meet(a.top())).as("a.meet(a.top())").isEqualTo(a);
	}

	@Property
	default void bottomIsIdentityForJoin(@ForAll("elements") E a)
	{
		assertThat(a.join(a.bottom())).as("a.join(a.bottom())").isEqualTo(a);
	}

	@Property
	default void bottomAbsorbsMeet(@ForAll("elements") E a)
	{
		assertThat(a.meet(a.bottom())).as("a.meet(a.bottom())").isEqualTo(a.bottom());
	}

	@Property
	default void topAbsorbsJoin(@ForAll("elements") E a)
	{
		assertThat(a.join(a.top())).as("a.join(a.top())").isEqualTo(a.top());
	}
}
