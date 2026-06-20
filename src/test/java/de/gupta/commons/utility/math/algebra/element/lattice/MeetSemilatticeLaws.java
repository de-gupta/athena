package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface MeetSemilatticeLaws<E extends MeetSemilattice<E>>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void meetIsIdempotent(@ForAll("elements") E a)
	{
		assertThat(a.meet(a)).as("a.meet(a)").isEqualTo(a);
	}

	@Property
	default void meetIsCommutative(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(a.meet(b)).as("a.meet(b)").isEqualTo(b.meet(a));
	}

	@Property
	default void meetIsAssociative(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(a.meet(b).meet(c)).as("a.meet(b).meet(c)").isEqualTo(a.meet(b.meet(c)));
	}
}