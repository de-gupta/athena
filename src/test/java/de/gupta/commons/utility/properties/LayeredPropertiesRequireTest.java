package de.gupta.commons.utility.properties;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("LayeredProperties#require(String)")
final class LayeredPropertiesRequireTest
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
		@DisplayName("returns the resolved value directly")
		void returnsTheResolvedValueDirectly()
		{
			var result = properties.require("app.name");

			assertThat(result)
					.as("present key should return its value directly without Optional wrapping")
					.isEqualTo("test-app");
		}
	}

	@Nested
	@DisplayName("when key is absent")
	final class WhenKeyIsAbsent
	{
		@Test
		@DisplayName("throws NoSuchElementException with the key name in the message")
		void throwsNoSuchElementExceptionWithTheKeyNameInTheMessage()
		{
			assertThatThrownBy(() -> properties.require("nonexistent.key"))
					.as("absent key should throw NoSuchElementException")
					.isInstanceOf(NoSuchElementException.class)
					.hasMessageContaining("nonexistent.key");
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
			assertThatThrownBy(() -> properties.require(null))
					.as("null key should throw NullPointerException")
					.isInstanceOf(NullPointerException.class);
		}
	}
}