package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.EuclideanDomain;

public interface OrderedEuclideanDomain<E extends OrderedEuclideanDomain<E>>
		extends EuclideanDomain<E>, OrderedRing<E>
{
	default E euclideanQuotient(final E divisor)
	{
		return quotient(divisor);
	}
}