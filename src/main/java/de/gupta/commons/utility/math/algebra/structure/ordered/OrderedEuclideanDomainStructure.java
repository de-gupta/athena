package de.gupta.commons.utility.math.algebra.structure.ordered;

import de.gupta.commons.utility.math.algebra.structure.ring.EuclideanDomainStructure;

public interface OrderedEuclideanDomainStructure<E>
		extends EuclideanDomainStructure<E>, OrderedRingStructure<E>
{
	default E euclideanQuotient(final E dividend, final E divisor)
	{
		return quotient(dividend, divisor);
	}
}
