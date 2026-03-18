package de.gupta.commons.utility.math.algebra.element.adapter;

import de.gupta.commons.utility.math.algebra.element.binary.Group;
import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

public abstract class StructuredGroupElement<E extends StructuredGroupElement<E>> implements Group<E>
{
	@Override
	public final E combine(final E other)
	{
		return structure().combine(self(), other);
	}

	@Override
	public final E identity()
	{
		return structure().identity();
	}

	@Override
	public final E inverse()
	{
		return structure().inverse(self());
	}

	protected abstract GroupStructure<E> structure();

	protected abstract E self();
}