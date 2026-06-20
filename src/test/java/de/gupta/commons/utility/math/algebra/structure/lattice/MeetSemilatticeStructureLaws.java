package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface MeetSemilatticeStructureLaws<E>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void meetIsIdempotent(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, a)).as("meet(a, a)").isEqualTo(a);
	}

	MeetSemilatticeStructure<E> subject();

	@Property
	default void meetIsCommutative(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(subject().meet(a, b)).as("meet(a, b)").isEqualTo(subject().meet(b, a));
	}

	@Property
	default void meetIsAssociative(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(subject().meet(subject().meet(a, b), c))
				.as("meet(meet(a, b), c)")
				.isEqualTo(subject().meet(a, subject().meet(b, c)));
	}
}