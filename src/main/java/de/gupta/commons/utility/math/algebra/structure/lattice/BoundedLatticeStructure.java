package de.gupta.commons.utility.math.algebra.structure.lattice;

public interface BoundedLatticeStructure<E> extends LatticeStructure<E>
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