package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface AlgebraLaws<R extends Ring<R>, A extends Algebra<R, A>>
{
	@Provide
	Arbitrary<R> scalars();

	@Provide
	Arbitrary<A> elements();

	@Property
	default void embedIsAdditiveHomomorphism(@ForAll("scalars") final R r1,
	                                         @ForAll("scalars") final R r2,
	                                         @ForAll("elements") final A a)
	{
		assertThat(a.embed(r1.add(r2)))
				.as("embed(r1 + r2) == embed(r1) + embed(r2)")
				.isEqualTo(a.embed(r1).add(a.embed(r2)));
	}

	@Property
	default void embedIsMultiplicativeHomomorphism(@ForAll("scalars") final R r1,
	                                               @ForAll("scalars") final R r2,
	                                               @ForAll("elements") final A a)
	{
		assertThat(a.embed(r1.multiply(r2)))
				.as("embed(r1 * r2) == embed(r1) * embed(r2)")
				.isEqualTo(a.embed(r1).multiply(a.embed(r2)));
	}

	@Property
	default void embedPreservesUnit(@ForAll("scalars") final R r,
	                                @ForAll("elements") final A a)
	{
		assertThat(a.embed(r.one()))
				.as("embed(oneR) == oneA")
				.isEqualTo(a.one());
	}

	@Property
	default void embeddedScalarsCommute(@ForAll("scalars") final R r,
	                                    @ForAll("elements") final A a,
	                                    @ForAll("elements") final A b)
	{
		final A embedded = a.embed(r);
		assertThat(embedded.multiply(b))
				.as("embed(r) * b == b * embed(r)")
				.isEqualTo(b.multiply(embedded));
	}
}