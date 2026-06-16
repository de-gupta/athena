package de.gupta.commons.utility.math.algebra.structure.lattice;

public interface BooleanAlgebraStructure<E> extends DistributiveLatticeStructure<E>
{
	default E xor(final E left, final E right)
	{
		return meet(join(left, right), complement(meet(left, right)));
	}

	E complement(E element);
}