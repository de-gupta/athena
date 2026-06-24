package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.algebra.element.affine.AffineSpace;

public interface AffinelyOrdered<A extends AffineSpace<A, D> & TotallyOrdered<A>, D> extends AffineSpace<A, D>,
		TotallyOrdered<A>
{
}