package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MapUtility Tests")
final class MapUtilityTest
{
	@Nested
	@DisplayName("getOrThrow Tests")
	final class GetOrThrowTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("invalidLookupCases")
		@DisplayName("Test getOrThrow throws the supplied exception for invalid lookups")
		void testGetOrThrowInvalidLookup(final Map<String, Integer> map, final String key, final String description)
		{
			IllegalStateException suppliedException = new IllegalStateException("No value available for key: " + key);

			assertThatThrownBy(() -> MapUtility.getOrThrow(map, key, () -> suppliedException))
					.as(description)
					.isSameAs(suppliedException);
		}

		@Test
		@DisplayName("Test getOrThrow returns mapped value for present non-null key")
		void testGetOrThrowValidLookup()
		{
			Map<String, Integer> map = new HashMap<>();
			map.put("answer", 42);
			AtomicBoolean exceptionSupplierInvoked = new AtomicBoolean(false);

			Integer result = MapUtility.getOrThrow(map, "answer", () ->
			{
				exceptionSupplierInvoked.set(true);
				return new IllegalStateException("Exception supplier should not be invoked");
			});

			assertThat(result)
					.as("Present key with non-null mapped value should return that value")
					.isEqualTo(42);
			assertThat(exceptionSupplierInvoked)
					.as("Exception supplier should not be invoked for a valid lookup")
					.isFalse();
		}

		private static Stream<Arguments> invalidLookupCases()
		{
			Map<String, Integer> mapWithNullValue = new HashMap<>();
			mapWithNullValue.put("configured", null);

			return Stream.of(
					Arguments.of(
							Map.of("available", 1),
							"missing",
							"Missing key should throw the supplied exception"
					),
					Arguments.of(
							mapWithNullValue,
							"configured",
							"Key mapped to null should throw the supplied exception"
					)
			);
		}
	}

	@Nested
	@DisplayName("Constructor Tests")
	final class ConstructorTests
	{
		@Test
		@DisplayName("Test MapUtility constructor is private and not accessible")
		void testPrivateConstructor()
		{
			assertThatThrownBy(() -> MapUtility.class.getDeclaredConstructor().newInstance())
					.isInstanceOf(IllegalAccessException.class);
		}

		@Test
		@DisplayName("Test MapUtility class is final")
		void testClassIsFinal()
		{
			assertThat(MapUtility.class.getModifiers())
					.as("MapUtility class should be final")
					.satisfies(modifiers -> assertThat(Modifier.isFinal(modifiers)).isTrue());
		}
	}
}