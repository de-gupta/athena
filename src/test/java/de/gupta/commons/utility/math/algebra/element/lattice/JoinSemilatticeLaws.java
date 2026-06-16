package de.gupta.commons.utility.math.algebra.element.lattice;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface JoinSemilatticeLaws<E extends JoinSemilattice<E>>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void joinIsIdempotent(@ForAll("elements") E a)
	{
		assertThat(a.join(a)).as("a.join(a)").isEqualTo(a);
	}

	@Property
	default void joinIsCommutative(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(a.join(b)).as("a.join(b)").isEqualTo(b.join(a));
	}

	@Property
	default void joinIsAssociative(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(a.join(b).join(c)).as("a.join(b).join(c)").isEqualTo(a.join(b.join(c)));
	}
}
