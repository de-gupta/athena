package de.gupta.commons.utility.math.algebra.element.binary.notation.additive;

import de.gupta.commons.utility.math.algebra.element.binary.Group;

public interface AdditiveGroup<E extends AdditiveGroup<E>> extends AdditiveMonoid<E>, Group<E>
{
	default E negate()
	{
		return inverse();
	}

	default E subtract(final E other)
	{
		return divide(other);
	}

}
