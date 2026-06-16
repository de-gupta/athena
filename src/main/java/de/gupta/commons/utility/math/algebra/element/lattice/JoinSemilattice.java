package de.gupta.commons.utility.math.algebra.element.lattice;

@FunctionalInterface
public interface JoinSemilattice<E extends JoinSemilattice<E>>
{
	default E or(final E other)
	{
		return join(other);
	}

	E join(E other);
}