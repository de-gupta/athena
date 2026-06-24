package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedField;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;

public sealed interface RationalNumber extends OrderedField<RationalNumber>
		permits RationalNumberImpl
{
	IntegralNumber numerator();

	IntegralNumber denominator();
}