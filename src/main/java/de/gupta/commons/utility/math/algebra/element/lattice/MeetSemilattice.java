package de.gupta.commons.utility.math.algebra.element.lattice;

@FunctionalInterface
public interface MeetSemilattice<E extends MeetSemilattice<E>>
{
	default E and(final E other)
	{
		return meet(other);
	}

	E meet(E other);
}