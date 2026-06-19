package de.gupta.commons.utility.math.algebra.structure.ring;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("DivisionResult")
final class DivisionResultTest
{
	@Nested
	@DisplayName("when created with of")
	final class WhenCreatedWithOf
	{
		@Test
		@DisplayName("stores quotient and remainder")
		void storesQuotientAndRemainder()
		{
			var result = DivisionResult.of(3, 2);

			assertThat(result.quotient()).as("quotient").isEqualTo(3);
			assertThat(result.remainder()).as("remainder").isEqualTo(2);
		}
	}

	@Nested
	@DisplayName("when mapped with one mapper")
	final class WhenMappedWithOneMapper
	{
		@Test
		@DisplayName("maps quotient and remainder with the same function")
		void mapsQuotientAndRemainderWithTheSameFunction()
		{
			var result = DivisionResult.of(3, 2);

			var mapped = result.map(value -> "v=" + value);

			assertThat(mapped.quotient()).as("mapped quotient").isEqualTo("v=3");
			assertThat(mapped.remainder()).as("mapped remainder").isEqualTo("v=2");
		}
	}

	@Nested
	@DisplayName("when mapped with dedicated mappers")
	final class WhenMappedWithDedicatedMappers
	{
		@Test
		@DisplayName("maps quotient and remainder independently")
		void mapsQuotientAndRemainderIndependently()
		{
			var result = DivisionResult.of(3, 2);

			var mapped = result.map(value -> "q=" + value, value -> value * 10);

			assertThat(mapped.quotient()).as("mapped quotient").isEqualTo("q=3");
			assertThat(mapped.remainder()).as("mapped remainder").isEqualTo(20);
		}
	}
}
