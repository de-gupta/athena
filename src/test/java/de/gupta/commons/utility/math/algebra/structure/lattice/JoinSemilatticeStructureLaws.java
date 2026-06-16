package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface JoinSemilatticeStructureLaws<E>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void joinIsIdempotent(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, a)).as("join(a, a)").isEqualTo(a);
	}

	JoinSemilatticeStructure<E> subject();

	@Property
	default void joinIsCommutative(@ForAll("elements") E a, @ForAll("elements") E b)
	{
		assertThat(subject().join(a, b)).as("join(a, b)").isEqualTo(subject().join(b, a));
	}

	@Property
	default void joinIsAssociative(@ForAll("elements") E a, @ForAll("elements") E b, @ForAll("elements") E c)
	{
		assertThat(subject().join(subject().join(a, b), c))
				.as("join(join(a, b), c)")
				.isEqualTo(subject().join(a, subject().join(b, c)));
	}
}