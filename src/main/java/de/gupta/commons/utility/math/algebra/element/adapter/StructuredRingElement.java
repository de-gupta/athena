package de.gupta.commons.utility.math.algebra.element.adapter;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;
import de.gupta.commons.utility.math.algebra.structure.ring.RingStructure;

public abstract class StructuredRingElement<E extends StructuredRingElement<E>> implements Ring<E>
{
	@Override
	public final E zero()
	{
		return structure().zero();
	}

	protected abstract RingStructure<E> structure();

	@Override
	public final E add(final E other)
	{
		return structure().add(self(), other);
	}

	protected abstract E self();

	@Override
	public final E one()
	{
		return structure().one();
	}

	@Override
	public final E multiply(final E other)
	{
		return structure().multiply(self(), other);
	}

	@Override
	public final E negate()
	{
		return structure().negate(self());
	}
}