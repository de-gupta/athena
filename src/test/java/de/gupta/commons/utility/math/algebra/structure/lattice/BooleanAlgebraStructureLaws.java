package de.gupta.commons.utility.math.algebra.structure.lattice;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

import static org.assertj.core.api.Assertions.assertThat;

public interface BooleanAlgebraStructureLaws<E> extends DistributiveLatticeStructureLaws<E>
{
	@Property
	default void complementLawForMeet(@ForAll("elements") E a)
	{
		assertThat(subject().meet(a, subject().complement(a)))
				.as("meet(a, complement(a))")
				.isEqualTo(subject().bottom());
	}

	@Override
	BooleanAlgebraStructure<E> subject();

	@Property
	default void complementLawForJoin(@ForAll("elements") E a)
	{
		assertThat(subject().join(a, subject().complement(a)))
				.as("join(a, complement(a))")
				.isEqualTo(subject().top());
	}

	@Property
	default void selfXorIsBottom(@ForAll("elements") E a)
	{
		assertThat(subject().xor(a, a)).as("xor(a, a)").isEqualTo(subject().bottom());
	}

	@Property
	default void xorWithComplementIsTop(@ForAll("elements") E a)
	{
		assertThat(subject().xor(a, subject().complement(a)))
				.as("xor(a, complement(a))")
				.isEqualTo(subject().top());
	}
}