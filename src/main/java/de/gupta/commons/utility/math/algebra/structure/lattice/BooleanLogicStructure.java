package de.gupta.commons.utility.math.algebra.structure.lattice;

public enum BooleanLogicStructure implements BooleanAlgebraStructure<Boolean>
{
	INSTANCE;

	@Override
	public Boolean meet(final Boolean left, final Boolean right)
	{
		return left && right;
	}

	@Override
	public Boolean join(final Boolean left, final Boolean right)
	{
		return left || right;
	}

	@Override
	public Boolean complement(final Boolean element)
	{
		return !element;
	}

	@Override
	public Boolean top()
	{
		return true;
	}

	@Override
	public Boolean bottom()
	{
		return false;
	}
}
