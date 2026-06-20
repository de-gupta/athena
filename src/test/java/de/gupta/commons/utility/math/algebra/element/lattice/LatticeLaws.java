package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface LatticeLaws<E extends Lattice<E>> extends MeetSemilatticeLaws<E>, JoinSemilatticeLaws<E>
{
	@Property
	default void meetAbsorbs(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(a.meet(a.join(b))).as("a.meet(a.join(b))").isEqualTo(a);
	}

	@Property
	default void joinAbsorbs(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(a.join(a.meet(b))).as("a.join(a.meet(b))").isEqualTo(a);
	}
}