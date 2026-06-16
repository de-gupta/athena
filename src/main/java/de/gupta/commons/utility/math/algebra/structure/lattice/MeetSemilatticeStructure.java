package de.gupta.commons.utility.math.algebra.structure.lattice;

@FunctionalInterface
public interface MeetSemilatticeStructure<E>
{
	default E and(E left, E right)
	{
		return meet(left, right);
	}

	E meet(E left, E right);
}