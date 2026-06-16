package de.gupta.commons.utility.math.algebra.element.lattice;

public interface MeetSemilattice<E extends MeetSemilattice<E>>
{
	E meet(E other);
}
