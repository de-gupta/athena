package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.element.ordered.DivisionConvention;
import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.radical.ApproximationStrategy;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.radical.EstimatorBasedRoot;
import de.gupta.commons.utility.math.algebra.element.radical.Radical;

public sealed interface IntegralNumber extends OrderedEuclideanDomain<IntegralNumber>, Radical<IntegralNumber>
		permits IntegralNumberImpl
{
	long value();

	@Override
	default IntegralNumber root(final int degree, final ApproximationStrategy<IntegralNumber> whenToStop,
	                            final DivisionConvention<IntegralNumber> convention)
	{
		return EstimatorBasedRoot.compute(this, degree, Estimator.newton(), whenToStop, convention);
	}
}