package de.gupta.commons.utility.math.ordering;

public record Incomparable() implements Ordering
{
	public static final Incomparable INSTANCE = new Incomparable();

	@Override
	public boolean isIncomparable()
	{
		return true;
	}

	@Override
	public boolean isComparable()
	{
		return false;
	}
}