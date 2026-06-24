package de.gupta.commons.utility.math.algebra.structure.module;

import de.gupta.commons.utility.math.algebra.element.ring.Field;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface ScalarQuotientableStructureLaws<E, S extends Field<S>>
{
	@Provide
	Arbitrary<E> nonZeroElements();

	@Property
	default void selfRatioIsOne(@ForAll("nonZeroElements") final E a)
	{
		assertThat(structure().ratio(a, a))
				.as("ratio(a, a)")
				.isEqualTo(one());
	}

	ScalarQuotientableStructure<E, S> structure();

	S one();

	@Property
	default void ratioChains(@ForAll("nonZeroElements") final E a,
	                         @ForAll("nonZeroElements") final E b,
	                         @ForAll("nonZeroElements") final E c)
	{
		assertThat(structure().ratio(c, a))
				.as("ratio(c,a) = ratio(c,b).multiply(ratio(b,a))")
				.isEqualTo(structure().ratio(c, b).multiply(structure().ratio(b, a)));
	}
}