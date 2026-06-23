package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;

public interface AffinelyOrdered<E extends AffinelyOrdered<E, D>, D extends AdditiveAbelianGroup<D>>
		extends TotallyOrdered<E>
{
	D displacementTo(E other);

	E translate(D displacement);
}