package de.gupta.commons.utility.math.algebra.structure.algebra;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface AlgebraStructureLaws<R extends Ring<R>, A>
{
	@Provide
	Arbitrary<R> scalars();

	@Provide
	Arbitrary<A> elements();

	@Property
	default void embedIsAdditiveHomomorphism(@ForAll("scalars") final R r1,
	                                         @ForAll("scalars") final R r2)
	{
		assertThat(structure().embed(r1.add(r2)))
				.as("embed(r1 + r2) == embed(r1) + embed(r2)")
				.isEqualTo(structure().add(structure().embed(r1), structure().embed(r2)));
	}

	AlgebraStructure<R, A> structure();

	@Property
	default void embedIsMultiplicativeHomomorphism(@ForAll("scalars") final R r1,
	                                               @ForAll("scalars") final R r2)
	{
		assertThat(structure().embed(r1.multiply(r2)))
				.as("embed(r1 * r2) == embed(r1) * embed(r2)")
				.isEqualTo(structure().multiply(structure().embed(r1), structure().embed(r2)));
	}

	@Property
	default void embedPreservesUnit(@ForAll("scalars") final R r)
	{
		assertThat(structure().embed(r.one()))
				.as("embed(oneR) == oneA")
				.isEqualTo(structure().one());
	}

	@Property
	default void embeddedScalarsCommute(@ForAll("scalars") final R r,
	                                    @ForAll("elements") final A b)
	{
		final A embedded = structure().embed(r);
		assertThat(structure().multiply(embedded, b))
				.as("embed(r) * b == b * embed(r)")
				.isEqualTo(structure().multiply(b, embedded));
	}
}