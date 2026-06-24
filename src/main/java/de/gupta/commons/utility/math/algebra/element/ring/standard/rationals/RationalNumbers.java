package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ring.Field;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;

public interface RationalNumbers extends Field<RationalNumbers>
{
	IntegralNumber numerator();

	IntegralNumber denominator();
}