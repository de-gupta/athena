package de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.radical.EstimatorBasedRoot;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumber;
import de.gupta.commons.utility.math.algebra.element.ring.standard.rationals.RationalNumberEstimators;
import de.gupta.commons.utility.math.algebra.structure.ordered.OrderedFieldStructure;
import de.gupta.commons.utility.math.algebra.structure.radical.RadicalStructure;

public interface RationalNumberStructure extends OrderedFieldStructure<RationalNumber>, RadicalStructure<RationalNumber>
{
	@Override
	default RationalNumber root(final RationalNumber element, final int degree,
	                            final ApproximationStrategy<RationalNumber> whenToStop,
	                            final RoundingStrategy<RationalNumber> rounding)
	{
		return EstimatorBasedRoot.compute(element, degree, RationalNumberEstimators.initialEstimate(element, degree),
				RationalNumberEstimators.adaptEstimator(Estimator.newton()),
				RationalNumberEstimators.adapt(whenToStop), rounding);
	}
}