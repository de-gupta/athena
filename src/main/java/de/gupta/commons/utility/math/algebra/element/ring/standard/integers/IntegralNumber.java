package de.gupta.commons.utility.math.algebra.element.ring.standard.integers;

import de.gupta.commons.utility.math.algebra.element.ordered.OrderedEuclideanDomain;

public sealed interface IntegralNumber extends OrderedEuclideanDomain<IntegralNumber>
		permits IntegersAsEuclideanDomain
{
	long value();
}