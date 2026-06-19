package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveGroup;

public interface Ring<E extends Ring<E>> extends Semiring<E>, AdditiveGroup<E>
{
	E negate();
}