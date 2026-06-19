package de.gupta.commons.utility.math.algebra.element.module;

import de.gupta.commons.utility.math.algebra.element.binary.notation.additive.AdditiveAbelianGroup;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface Module<M extends Module<M, R>, R extends Ring<R>> extends AdditiveAbelianGroup<M>
{
	M scale(final R scalar);
}