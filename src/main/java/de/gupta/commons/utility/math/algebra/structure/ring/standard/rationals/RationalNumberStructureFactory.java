package de.gupta.commons.utility.math.algebra.structure.ring.standard.rationals;

public final class RationalNumberStructureFactory
{
	public static RationalNumberStructure instance()
	{
		return RationalNumberStructureImpl.INSTANCE;
	}

	private RationalNumberStructureFactory()
	{
	}
}