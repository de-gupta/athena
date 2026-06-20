package de.gupta.commons.utility.math.algebra.laws.ring;

import de.gupta.commons.utility.math.algebra.laws.binary.AssociativeLaw;
import de.gupta.commons.utility.math.algebra.laws.binary.CommutativeLaw;
import de.gupta.commons.utility.math.algebra.laws.binary.IdentityLaw;
import de.gupta.commons.utility.math.algebra.laws.binary.InverseLaw;

public interface RingLaws<E> extends AssociativeLaw<E>, IdentityLaw<E>, InverseLaw<E>, CommutativeLaw<E>,
		DistributiveLaw<E>
{
}