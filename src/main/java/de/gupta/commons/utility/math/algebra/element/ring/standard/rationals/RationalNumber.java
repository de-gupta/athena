package de.gupta.commons.utility.math.algebra.element.ring.standard.rationals;

import de.gupta.commons.utility.math.algebra.element.module.ScalarDivisible;
import de.gupta.commons.utility.math.algebra.element.module.ScalarQuotientable;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedField;
import de.gupta.commons.utility.math.algebra.element.ordered.RoundingStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.radical.EstimatorBasedRoot;
import de.gupta.commons.utility.math.algebra.element.radical.Radical;
import de.gupta.commons.utility.math.algebra.element.ring.standard.integers.IntegralNumber;

public sealed interface RationalNumber
		extends OrderedField<RationalNumber>, ScalarQuotientable<RationalNumber, RationalNumber>,
		ScalarDivisible<RationalNumber, Long>, Radical<RationalNumber>
		permits RationalNumberImpl
{
	IntegralNumber numerator();

	IntegralNumber denominator();

	@Override
	default RationalNumber root(final int degree, final ApproximationStrategy<RationalNumber> whenToStop,
	                            final RoundingStrategy<RationalNumber> rounding)
	{
		return EstimatorBasedRoot.compute(this, degree, RationalNumberEstimators.initialEstimate(this, degree),
				RationalNumberEstimators.adaptEstimator(Estimator.newton()),
				RationalNumberEstimators.adapt(whenToStop), rounding);
	}
}