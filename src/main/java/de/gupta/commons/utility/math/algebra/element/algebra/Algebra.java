package de.gupta.commons.utility.math.algebra.element.algebra;

import de.gupta.commons.utility.math.algebra.element.module.Module;
import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface Algebra<R extends Ring<R>, A extends Algebra<R, A>> extends Ring<A>, Module<A, R>
{
	A embed(R scalar);
}