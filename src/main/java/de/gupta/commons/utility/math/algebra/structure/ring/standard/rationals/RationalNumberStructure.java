package de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedFieldStructure;
import de.gupta.commons.utility.math.algebra.structure.radical.RadicalStructure;
import de.gupta.commons.utility.math.algebra.structure.radical.RadicalStructureFactory;

public interface RationalNumberStructure extends OrderedFieldStructure<RationalNumber>, RadicalStructure<RationalNumber>
{
	@Override
	default RationalNumber root(RationalNumber element, int degree, ApproximationStrategy<RationalNumber> whenToStop,
	                            RoundingStrategy<RationalNumber> rounding)
	{
		return RadicalStructureFactory.using(Estimator.newton(), this).root(element, degree, whenToStop, rounding);
	}
}