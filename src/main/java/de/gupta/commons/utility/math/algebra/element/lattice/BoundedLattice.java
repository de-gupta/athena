package de.gupta.commons.utility.math.algebra.element.lattice;

public interface BoundedLattice<E extends BoundedLattice<E>> extends Lattice<E>
{
	default E supremum()
	{
		return top();
	}

	E top();

	default E infimum()
	{
		return bottom();
	}

	E bottom();
}