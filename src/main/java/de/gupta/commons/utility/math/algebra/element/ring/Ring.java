package de.gupta.commons.utility.math.algebra.element.ring;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;

public interface Ring<E extends Ring<E>> extends Semiring<E>, AdditiveAbelianGroup<E>
{
	E negate();
}