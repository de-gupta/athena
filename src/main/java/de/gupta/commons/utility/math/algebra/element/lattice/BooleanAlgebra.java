package de.gupta.commons.utility.math.algebra.element.lattice;

public interface BooleanAlgebra<E extends BooleanAlgebra<E>> extends DistributiveLattice<E>
{
	default E xor(final E other)
	{
		return join(other).meet(meet(other).complement());
	}

	E complement();

	default E not()
	{
		return complement();
	}
}