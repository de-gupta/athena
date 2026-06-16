package de.gupta.commons.utility.math.algebra.structure.lattice;

@FunctionalInterface
public interface JoinSemilatticeStructure<E>
{
	default E or(E left, E right)
	{
		return join(left, right);
	}

	E join(E left, E right);
}