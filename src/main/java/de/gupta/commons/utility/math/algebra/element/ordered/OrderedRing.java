package de.gupta.commons.utility.math.algebra.element.ordered;

import de.gupta.commons.utility.math.algebra.element.ring.Ring;

public interface OrderedRing<E extends OrderedRing<E>> extends Ring<E>, OrderedAdditiveGroup<E>
{
}