package de.gupta.commons.utility.math.ordering.element;

public interface AffinelyOrdered<E extends AffinelyOrdered<E, D>, D>
		extends TotallyOrdered<E>
{
	D displacementTo(E other);

	E translate(D displacement);
}