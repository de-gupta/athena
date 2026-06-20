package de.gupta.commons.utility.math.algebra.element.binary.action;

import de.gupta.commons.utility.math.algebra.element.binary.Group;

public interface GroupActedUpon<S extends Group<S>, X extends GroupActedUpon<S, X>>
		extends MonoidActedUpon<S, X>
{
}
