package de.gupta.commons.utility.math.ordering.element;

import de.gupta.commons.utility.math.ordering.OrderRelation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

@DisplayName("TotallyOrdered")
final class TotallyOrderedTest
{
	@TestFactory
	@DisplayName("integer element satisfies all total order laws")
	Stream<DynamicTest> integerElementSatisfiesAllTotalOrderLaws()
	{
		return new TotallyOrderedLaws<>(new IntegerElement(1), new IntegerElement(3), new IntegerElement(7)).tests();
	}

	private record IntegerElement(int value) implements TotallyOrdered<IntegerElement>
	{
		@Override
		public OrderRelation compare(final IntegerElement other)
		{
			int result = Integer.compare(value, other.value);
			return result < 0 ? OrderRelation.LESS_THAN
					: result > 0 ? OrderRelation.GREATER_THAN
					  : OrderRelation.EQUAL;
		}
	}
}