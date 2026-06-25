package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.laws.algebra.ScalarExtensionLaw;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface ScalarExtensionLaws<E extends AdditiveAbelianGroup<E>, R extends Ring<R>, F extends Algebra<R, F>>
		extends ScalarExtensionLaw<E, F>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void embedIsAdditiveHomomorphism(@ForAll("elements") final E a,
	                                         @ForAll("elements") final E b)
	{
		assertThat(extension().embed(a.add(b)))
				.as("embed(a + b) == embed(a) + embed(b)")
				.isEqualTo(extension().embed(a).add(extension().embed(b)));
	}

	ScalarExtension<E, F> extension();

	@Property
	default void embedMapsZeroToZero(@ForAll("elements") final E a)
	{
		assertThat(extension().embed(a.zero()))
				.as("embed(zero_E) == zero_F")
				.isEqualTo(extension().embed(a).zero());
	}

	@Property
	default void embedPreservesNegation(@ForAll("elements") final E a)
	{
		assertThat(extension().embed(a.negate()))
				.as("embed(-a) == -embed(a)")
				.isEqualTo(extension().embed(a).negate());
	}
}