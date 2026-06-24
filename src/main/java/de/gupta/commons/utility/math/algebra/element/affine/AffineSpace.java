package de.gupta.commons.utility.math.algebra.element.affine;

public interface AffineSpace<A extends AffineSpace<A, D>, D>
{
	D displacementTo(final A other);

	A translate(final D displacement);
}