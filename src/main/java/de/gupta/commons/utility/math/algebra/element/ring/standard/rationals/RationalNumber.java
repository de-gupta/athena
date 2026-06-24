package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedField;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;

public sealed interface RationalNumber
		extends OrderedField<RationalNumber>, ScalarQuotientable<RationalNumber, RationalNumber>,
		ScalarDivisible<RationalNumber, Long>
		permits RationalNumberImpl
{
	IntegralNumber numerator();

	IntegralNumber denominator();
}