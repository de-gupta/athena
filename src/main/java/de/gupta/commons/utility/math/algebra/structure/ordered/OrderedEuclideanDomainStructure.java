package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.element.ordered.EuclideanRoundingStrategy;
import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

public interface OrderedEuclideanDomainStructure<E>
		extends EuclideanDomainStructure<E>, OrderedRingStructure<E>
{
	default E euclideanQuotient(final E dividend, final E divisor, final EuclideanRoundingStrategy strategy)
	{
		return strategy.round(divideWithRemainder(dividend, divisor), this);
	}
}