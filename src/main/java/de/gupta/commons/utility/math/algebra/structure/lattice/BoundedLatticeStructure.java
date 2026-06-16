package de.gupta.commons.utility.math.algebra.structure.lattice;

public interface BoundedLatticeStructure<E> extends LatticeStructure<E>
{
	E top();

	E bottom();
}
