package de.gupta.commons.utility.math.algebra.element.affine;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface AffineSpaceLaws<A extends AffineSpace<A, D>, D extends AdditiveAbelianGroup<D>>
{
	@Provide
	Arbitrary<A> elements();

	@Property
	default void translateByDisplacementReturnsTarget(@ForAll("elements") final A a,
	                                                  @ForAll("elements") final A b)
	{
		assertThat(a.translate(a.displacementTo(b)))
				.as("a.translate(a.displacementTo(b)) == b")
				.isEqualTo(b);
	}

	@Property
	default void displacementIsAntisymmetric(@ForAll("elements") final A a,
	                                         @ForAll("elements") final A b)
	{
		assertThat(a.displacementTo(b))
				.as("a.displacementTo(b) == b.displacementTo(a).negate()")
				.isEqualTo(b.displacementTo(a).negate());
	}

	@Property
	default void displacementIsAdditive(@ForAll("elements") final A a,
	                                    @ForAll("elements") final A b,
	                                    @ForAll("elements") final A c)
	{
		assertThat(a.displacementTo(c))
				.as("a.displacementTo(c) == a.displacementTo(b).add(b.displacementTo(c))")
				.isEqualTo(a.displacementTo(b).add(b.displacementTo(c)));
	}
}