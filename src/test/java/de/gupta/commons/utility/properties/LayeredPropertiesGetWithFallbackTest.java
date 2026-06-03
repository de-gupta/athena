package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LayeredProperties#get(String, String)")
final class LayeredPropertiesGetWithFallbackTest
{
	private static final String CLASSPATH_RESOURCE = "de/gupta/commons/utility/properties/test-defaults.properties";

	private final LayeredProperties properties = LayeredProperties.builder()
	                                                              .withClasspathResource(CLASSPATH_RESOURCE)
	                                                              .build();

	@Nested
	@DisplayName("when key is present")
	final class WhenKeyIsPresent
	{
		@Test
		@DisplayName("returns the resolved value and ignores the fallback")
		void returnsTheResolvedValueAndIgnoresTheFallback()
		{
			var result = properties.get("app.name", "default-app");

			assertThat(result)
					.as("present key should return its resolved value, not the fallback")
					.isEqualTo("test-app");
		}
	}

	@Nested
	@DisplayName("when key is absent")
	final class WhenKeyIsAbsent
	{
		@Test
		@DisplayName("returns the fallback value")
		void returnsTheFallbackValue()
		{
			var result = properties.get("nonexistent.key", "my-fallback");

			assertThat(result)
					.as("absent key should return the supplied fallback")
					.isEqualTo("my-fallback");
		}
	}

	@Nested
	@DisplayName("with null arguments")
	final class WithNullArguments
	{
		@Test
		@DisplayName("throws NullPointerException when key is null")
		void throwsNullPointerExceptionWhenKeyIsNull()
		{
			assertThatThrownBy(() -> properties.get(null, "fallback"))
					.as("null key should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}

		@Test
		@DisplayName("throws NullPointerException when fallback is null")
		void throwsNullPointerExceptionWhenFallbackIsNull()
		{
			assertThatThrownBy(() -> properties.get("any.key", null))
					.as("null fallback should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}