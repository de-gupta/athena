package de.gupta.commons.utility.math.algebra.structure.binary.notation.additive;

import de.gupta.commons.utility.math.algebra.structure.binary.GroupStructure;

public interface AdditiveGroupStructure<E> extends AdditiveMonoidStructure<E>, GroupStructure<E>
{
	default E negate(final E element)
	{
		return inverse(element);
	}

	default E subtract(final E left, final E right)
	{
		return divide(left, right);
	}
}
