package de.gupta.commons.utility.math.algebra.element.module;

import de.gupta.commons.utility.math.algebra.element.ring.Field;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface ScalarQuotientableLaws<E extends ScalarQuotientable<E, S>, S extends Field<S>>
{
	@Provide
	Arbitrary<E> nonZeroElements();

	@Property
	default void selfRatioIsOne(@ForAll("nonZeroElements") final E a)
	{
		assertThat(a.ratio(a))
				.as("a.ratio(a)")
				.isEqualTo(one());
	}

	S one();

	@Property
	default void ratioChains(@ForAll("nonZeroElements") final E a,
	                         @ForAll("nonZeroElements") final E b,
	                         @ForAll("nonZeroElements") final E c)
	{
		assertThat(c.ratio(a))
				.as("c.ratio(a) = c.ratio(b).multiply(b.ratio(a))")
				.isEqualTo(c.ratio(b).multiply(b.ratio(a)));
	}
}