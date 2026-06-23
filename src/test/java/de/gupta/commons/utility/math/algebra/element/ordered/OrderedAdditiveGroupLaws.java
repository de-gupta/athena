package de.gupta.commons.utility.math.algebra.element.ordered;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import static org.assertj.core.api.Assertions.assertThat;

public interface OrderedAdditiveGroupLaws<E extends OrderedAdditiveGroup<E>>
{
	@Provide
	Arbitrary<E> elements();

	@Property
	default void positiveAndNegativePartsReconstructTheElement(@ForAll("elements") E value)
	{
		assertThat(value.positivePart().subtract(value.negativePart()))
				.as("value.positivePart() - value.negativePart()")
				.isEqualTo(value);
	}

	@Property
	default void absoluteValueIsTheSumOfPositiveAndNegativeParts(@ForAll("elements") E value)
	{
		assertThat(value.positivePart().add(value.negativePart()))
				.as("value.positivePart() + value.negativePart()")
				.isEqualTo(value.abs());
	}

	@Property
	default void positivePartIsAlwaysNonNegative(@ForAll("elements") E value)
	{
		assertThat(value.positivePart().isNonNegative())
				.as("value.positivePart().isNonNegative()")
				.isTrue();
	}

	@Property
	default void negativePartIsAlwaysNonNegative(@ForAll("elements") E value)
	{
		assertThat(value.negativePart().isNonNegative())
				.as("value.negativePart().isNonNegative()")
				.isTrue();
	}
}
