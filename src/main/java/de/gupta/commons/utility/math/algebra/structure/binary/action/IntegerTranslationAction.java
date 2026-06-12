package de.gupta.commons.utility.math.algebra.structure.binary.action;

public enum IntegerTranslationAction implements GroupAction<Integer, Integer>
{
	INSTANCE;

	@Override
	public Integer act(final Integer actor, final Integer point)
	{
		return actor + point;
	}

	@Override
	public Integer multiply(final Integer left, final Integer right)
	{
		return left + right;
	}

	@Override
	public Integer identity()
	{
		return 0;
	}

	@Override
	public Integer inverse(final Integer element)
	{
		return -element;
	}
}
