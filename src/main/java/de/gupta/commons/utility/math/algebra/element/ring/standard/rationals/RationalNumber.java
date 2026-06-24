package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedAdditiveGroup;
import de.gupta.commons.utility.math.algebra.element.ring.Field;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;

public sealed interface RationalNumber extends Field<RationalNumber>, OrderedAdditiveGroup<RationalNumber>
		permits RationalNumberImpl
{
	IntegralNumber numerator();

	IntegralNumber denominator();
}