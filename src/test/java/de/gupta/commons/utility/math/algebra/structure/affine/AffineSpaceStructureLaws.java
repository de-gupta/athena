package de.gupta.commons.utility.math.algebra.structure.affine;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface AffineSpaceStructureLaws<A, D extends AdditiveAbelianGroup<D>>
{
	@Provide
	Arbitrary<A> elements();

	@Property
	default void translateByDisplacementReturnsTarget(@ForAll("elements") final A a,
	                                                  @ForAll("elements") final A b)
	{
		assertThat(structure().translate(a, structure().displacement(a, b)))
				.as("translate(a, displacement(a, b)) == b")
				.isEqualTo(b);
	}

	AffineSpaceStructure<A, D> structure();

	@Property
	default void displacementIsAntisymmetric(@ForAll("elements") final A a,
	                                         @ForAll("elements") final A b)
	{
		assertThat(structure().displacement(a, b))
				.as("displacement(a,b) == displacement(b,a).negate()")
				.isEqualTo(structure().displacement(b, a).negate());
	}

	@Property
	default void displacementIsAdditive(@ForAll("elements") final A a,
	                                    @ForAll("elements") final A b,
	                                    @ForAll("elements") final A c)
	{
		assertThat(structure().displacement(a, c))
				.as("displacement(a,c) == displacement(a,b).add(displacement(b,c))")
				.isEqualTo(structure().displacement(a, b).add(structure().displacement(b, c)));
	}
}