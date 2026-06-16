package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface DistributiveLatticeLaws<E extends DistributiveLattice<E>> extends BoundedLatticeLaws<E>
{
	@Property
	default void meetDistributesOverJoin(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(a.meet(b.join(c))).as("a.meet(b.join(c))").isEqualTo(a.meet(b).join(a.meet(c)));
	}

	@Property
	default void joinDistributesOverMeet(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(a.join(b.meet(c))).as("a.join(b.meet(c))").isEqualTo(a.join(b).meet(a.join(c)));
	}
}
