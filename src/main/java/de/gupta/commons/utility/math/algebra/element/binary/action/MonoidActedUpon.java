package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Monoid;

public interface MonoidActedUpon<S extends Monoid<S>, X extends MonoidActedUpon<S, X>>
		extends SemigroupActedUpon<S, X>
{
}
