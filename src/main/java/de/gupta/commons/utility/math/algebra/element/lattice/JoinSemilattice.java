package de.gupta.commons.utility.math.algebra.element.lattice;

public interface JoinSemilattice<E extends JoinSemilattice<E>>
{
	E join(E other);
}
