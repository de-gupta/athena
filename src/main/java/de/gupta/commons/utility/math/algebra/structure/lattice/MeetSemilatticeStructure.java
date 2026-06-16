package de.gupta.commons.utility.math.algebra.structure.lattice;

@FunctionalInterface
public interface MeetSemilatticeStructure<E>
{
	E meet(E left, E right);
}
