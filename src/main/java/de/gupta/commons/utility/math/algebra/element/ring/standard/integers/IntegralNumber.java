package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;
import de.gupta.commons.utility.math.algebra.element.radical.Estimator;
import de.gupta.commons.utility.math.algebra.element.radical.Radical;

public sealed interface IntegralNumber extends OrderedEuclideanDomain<IntegralNumber>, Radical<IntegralNumber>
		permits IntegralNumberImpl
{
	long value();

	@Override
	default Estimator<IntegralNumber> estimator()
	{
		return Estimator.newton();
	}
}