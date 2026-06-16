package de.gupta.commons.utility.math.algebra.structure.lattice;

@FunctionalInterface
public interface JoinSemilatticeStructure<E>
{
	E join(E left, E right);
}
