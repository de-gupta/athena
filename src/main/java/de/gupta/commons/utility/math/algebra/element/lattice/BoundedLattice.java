package de.gupta.commons.utility.math.algebra.element.lattice;

public interface BoundedLattice<E extends BoundedLattice<E>> extends Lattice<E>
{
	E supremum();

	E infimum();
}