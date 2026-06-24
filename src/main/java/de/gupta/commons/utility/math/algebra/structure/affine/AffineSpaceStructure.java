package de.gupta.commons.utility.math.algebra.structure.affine;

public interface AffineSpaceStructure<A, D>
{
	D displacement(final A from, final A to);

	A translate(final A start, final D displacement);
}