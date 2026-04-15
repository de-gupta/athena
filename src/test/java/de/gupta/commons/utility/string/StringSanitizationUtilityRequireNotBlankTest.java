package de.gupta.commons.utility.string;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringSanitizationUtility - requireNotBlank Tests")
final class StringSanitizationUtilityRequireNotBlankTest
{
	// -------------------------------------------------------------------------
	// requireNotBlank(String, Supplier<RuntimeException>)
	// -------------------------------------------------------------------------

	@Nested
	@DisplayName("requireNotBlank(String, Supplier) - Non-blank inputs (no exception expected)")
	final class SupplierOverload_ValidInputTests
	{
		@ParameterizedTest(name = "{1}")
		@MethodSource("nonBlankInputProvider")
		@DisplayName("Should not throw for non-blank input")
		void requireNotBlank_supplier_doesNotThrow(String input, String description)
		{
			assertThatCode(() ->
					StringSanitizationUtility.requireNotBlank(input,
							() -> new IllegalArgumentException("should not be thrown")))
					.as(description)
					.doesNotThrowAnyException();
		}

		private static Stream<Arguments> nonBlankInputProvider()
		{
			return Stream.of(
					Arguments.of("hello", "Plain word should not throw"),
					Arguments.of(" hello", "Leading space with content should not throw"),
					Arguments.of("hello ", "Trailing space with content should not throw"),
					Arguments.of(" hello ", "Surrounding spaces with content should not throw"),
					Arguments.of("a", "Single non-whitespace character should not throw"),
					Arguments.of("123", "Numeric string should not throw"),
					Arguments.of("!@#$%", "Special characters should not throw"),
					Arguments.of("hello world", "String with internal space should not throw"),
					Arguments.of("\u00A0",
							"Non-breaking space (U+00A0) is not blank per isBlank() and should not throw"),
					Arguments.of("\u200B", "Zero-width space (U+200B) is not blank per isBlank() and should not throw"),
					Arguments.of("こんにちは", "Unicode characters should not throw"),
					Arguments.of("😀", "Emoji should not throw")
			);
		}
	}

	@Nested
	@DisplayName("requireNotBlank(String, Supplier) - Blank / null inputs (exception expected)")
	final class SupplierOverload_BlankInputTests
	{
		@ParameterizedTest(name = "{1}")
		@NullSource
		@DisplayName("Should throw when input is null")
		void requireNotBlank_supplier_nullThrows(String input)
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(input, () -> new IllegalArgumentException("null input")))
					.as("Null input should cause the supplied exception to be thrown")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("null input");
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("blankInputProvider")
		@DisplayName("Should throw for blank input")
		void requireNotBlank_supplier_blankThrows(String input, String description)
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(input, () -> new IllegalStateException("blank input")))
					.as(description)
					.isInstanceOf(IllegalStateException.class)
					.hasMessage("blank input");
		}

		private static Stream<Arguments> blankInputProvider()
		{
			return Stream.of(
					Arguments.of("", "Empty string should throw"),
					Arguments.of(" ", "Single space should throw"),
					Arguments.of("   ", "Multiple spaces should throw"),
					Arguments.of("\t", "Tab-only string should throw"),
					Arguments.of("\n", "Newline-only string should throw"),
					Arguments.of("\r", "Carriage-return-only string should throw"),
					Arguments.of("\r\n", "CRLF string should throw"),
					Arguments.of("\f", "Form-feed-only string should throw"),
					Arguments.of(" \t\n\r\f", "Mixed whitespace string should throw"),
					Arguments.of("\u2003", "Unicode EM SPACE (U+2003) is blank per isBlank() and should throw"),
					Arguments.of("\u2000", "Unicode EN QUAD (U+2000) is blank per isBlank() and should throw"),
					Arguments.of("\u2001", "Unicode EM QUAD (U+2001) is blank per isBlank() and should throw")
			);
		}
	}

	@Nested
	@DisplayName("requireNotBlank(String, Supplier) - Exception type and message propagation")
	final class SupplierOverload_ExceptionPropagationTests
	{
		@Test
		@DisplayName("Should propagate the exact RuntimeException supplied for null input")
		void requireNotBlank_supplier_propagatesExactExceptionType_null()
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(null,
							() -> new UnsupportedOperationException("custom message")))
					.isInstanceOf(UnsupportedOperationException.class)
					.hasMessage("custom message");
		}

		@Test
		@DisplayName("Should propagate the exact RuntimeException supplied for blank input")
		void requireNotBlank_supplier_propagatesExactExceptionType_blank()
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank("  ",
							() -> new UnsupportedOperationException("custom message")))
					.isInstanceOf(UnsupportedOperationException.class)
					.hasMessage("custom message");
		}

		@Test
		@DisplayName("Should propagate a custom RuntimeException subclass with cause")
		void requireNotBlank_supplier_customExceptionWithCause()
		{
			RuntimeException cause = new RuntimeException("root cause");

			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank("", () -> new IllegalArgumentException("wrapped", cause)))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage("wrapped")
					.hasCause(cause);
		}

		@Test
		@DisplayName("Supplier should not be invoked when input is non-blank")
		void requireNotBlank_supplier_notInvokedForNonBlank()
		{
			AtomicInteger supplierCallCount = new AtomicInteger(0);

			StringSanitizationUtility.requireNotBlank("valid", () ->
			{
				supplierCallCount.incrementAndGet();
				return new IllegalArgumentException("should not be called");
			});

			assertThatCode(() ->
			{
				if (supplierCallCount.get() != 0)
				{
					throw new AssertionError("Supplier was called " + supplierCallCount.get() + " times, expected 0");
				}
			}).doesNotThrowAnyException();
		}

		@Test
		@DisplayName("Supplier should be invoked exactly once when input is blank")
		void requireNotBlank_supplier_invokedExactlyOnceForBlank()
		{
			AtomicInteger supplierCallCount = new AtomicInteger(0);

			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank("", () ->
					{
						supplierCallCount.incrementAndGet();
						return new IllegalArgumentException("triggered");
					}))
					.isInstanceOf(IllegalArgumentException.class);

			assertThatCode(() ->
			{
				if (supplierCallCount.get() != 1)
				{
					throw new AssertionError("Supplier was called " + supplierCallCount.get() + " times, expected 1");
				}
			}).doesNotThrowAnyException();
		}
	}

	// -------------------------------------------------------------------------
	// requireNotBlank(String, String)
	// -------------------------------------------------------------------------

	@Nested
	@DisplayName("requireNotBlank(String, String) - Non-blank inputs (no exception expected)")
	final class MessageOverload_ValidInputTests
	{
		@ParameterizedTest(name = "{1}")
		@MethodSource("nonBlankInputProvider")
		@DisplayName("Should not throw for non-blank input")
		void requireNotBlank_message_doesNotThrow(String input, String description)
		{
			assertThatCode(() ->
					StringSanitizationUtility.requireNotBlank(input, "should not be thrown"))
					.as(description)
					.doesNotThrowAnyException();
		}

		private static Stream<Arguments> nonBlankInputProvider()
		{
			return Stream.of(
					Arguments.of("hello", "Plain word should not throw"),
					Arguments.of(" hello", "Leading space with content should not throw"),
					Arguments.of("hello ", "Trailing space with content should not throw"),
					Arguments.of(" hello ", "Surrounding spaces with content should not throw"),
					Arguments.of("a", "Single non-whitespace character should not throw"),
					Arguments.of("123", "Numeric string should not throw"),
					Arguments.of("!@#$%", "Special characters should not throw"),
					Arguments.of("hello world", "String with internal space should not throw"),
					Arguments.of("\u00A0", "Non-breaking space (U+00A0) is not blank and should not throw"),
					Arguments.of("\u200B", "Zero-width space (U+200B) is not blank and should not throw"),
					Arguments.of("こんにちは", "Unicode characters should not throw"),
					Arguments.of("😀", "Emoji should not throw")
			);
		}
	}

	@Nested
	@DisplayName("requireNotBlank(String, String) - Blank / null inputs (exception expected)")
	final class MessageOverload_BlankInputTests
	{
		private static final String MESSAGE = "value must not be blank";

		@ParameterizedTest(name = "Null input throws IllegalArgumentException")
		@NullSource
		@DisplayName("Should throw IllegalArgumentException when input is null")
		void requireNotBlank_message_nullThrows(String input)
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(input, MESSAGE))
					.as("Null input should throw IllegalArgumentException with the provided message")
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(MESSAGE);
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("blankInputProvider")
		@DisplayName("Should throw IllegalArgumentException for blank input")
		void requireNotBlank_message_blankThrows(String input, String description)
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(input, MESSAGE))
					.as(description)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(MESSAGE);
		}

		private static Stream<Arguments> blankInputProvider()
		{
			return Stream.of(
					Arguments.of("", "Empty string should throw"),
					Arguments.of(" ", "Single space should throw"),
					Arguments.of("   ", "Multiple spaces should throw"),
					Arguments.of("\t", "Tab-only string should throw"),
					Arguments.of("\n", "Newline-only string should throw"),
					Arguments.of("\r", "Carriage-return-only string should throw"),
					Arguments.of("\r\n", "CRLF string should throw"),
					Arguments.of("\f", "Form-feed-only string should throw"),
					Arguments.of(" \t\n\r\f", "Mixed whitespace string should throw"),
					Arguments.of("\u2003", "Unicode EM SPACE (U+2003) is blank and should throw"),
					Arguments.of("\u2000", "Unicode EN QUAD (U+2000) is blank and should throw"),
					Arguments.of("\u2001", "Unicode EM QUAD (U+2001) is blank and should throw")
			);
		}
	}

	@Nested
	@DisplayName("requireNotBlank(String, String) - Message propagation")
	final class MessageOverload_MessagePropagationTests
	{
		@ParameterizedTest(name = "{1}")
		@MethodSource("messagePropagationProvider")
		@DisplayName("Should use the provided message as the IllegalArgumentException message")
		void requireNotBlank_message_correctMessagePropagated(String message, String description)
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank("", message))
					.as(description)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(message);
		}

		@Test
		@DisplayName("Should throw with exact message for null input")
		void requireNotBlank_message_exactMessageForNull()
		{
			final String expectedMessage = "input is required";

			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank(null, expectedMessage))
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessage(expectedMessage);
		}

		@Test
		@DisplayName("Should always throw IllegalArgumentException, not a subclass, for blank input")
		void requireNotBlank_message_throwsIllegalArgumentException_notSubtype()
		{
			assertThatThrownBy(() ->
					StringSanitizationUtility.requireNotBlank("", "error"))
					.isExactlyInstanceOf(IllegalArgumentException.class);
		}

		private static Stream<Arguments> messagePropagationProvider()
		{
			return Stream.of(
					Arguments.of("must not be blank", "Plain message is propagated correctly"),
					Arguments.of("", "Empty message string is propagated correctly"),
					Arguments.of("   ", "Blank message string is propagated correctly"),
					Arguments.of("error: field 'name' is required", "Descriptive message is propagated correctly"),
					Arguments.of("value must not be blank or null",
							"Message containing 'null' is propagated correctly"),
					Arguments.of("Validation failed: input is blank", "Prefixed message is propagated correctly"),
					Arguments.of("unicode message: こんにちは", "Unicode in message is propagated correctly"),
					Arguments.of("special chars: !@#$%^&*()", "Special characters in message are propagated correctly")
			);
		}
	}

	// -------------------------------------------------------------------------
	// Cross-overload consistency
	// -------------------------------------------------------------------------

	@Nested
	@DisplayName("Cross-overload consistency Tests")
	final class CrossOverloadConsistencyTests
	{
		@ParameterizedTest(name = "{2}")
		@MethodSource("consistencyProvider")
		@DisplayName("Both overloads should agree on whether to throw for the same input")
		void requireNotBlank_bothOverloads_agreeOnThrow(String input, boolean expectsThrow, String description)
		{
			if (expectsThrow)
			{
				assertThatThrownBy(() ->
						StringSanitizationUtility.requireNotBlank(input, () -> new RuntimeException("supplier")))
						.as("Supplier overload should throw for: " + description)
						.isInstanceOf(RuntimeException.class);

				assertThatThrownBy(() ->
						StringSanitizationUtility.requireNotBlank(input, "message overload"))
						.as("Message overload should throw for: " + description)
						.isInstanceOf(IllegalArgumentException.class);
			}
			else
			{
				assertThatCode(() ->
						StringSanitizationUtility.requireNotBlank(input, () -> new RuntimeException("supplier")))
						.as("Supplier overload should not throw for: " + description)
						.doesNotThrowAnyException();

				assertThatCode(() ->
						StringSanitizationUtility.requireNotBlank(input, "message overload"))
						.as("Message overload should not throw for: " + description)
						.doesNotThrowAnyException();
			}
		}

		@ParameterizedTest(name = "{1}")
		@ValueSource(strings = {"hello", " a", "a ", "123", "!@#"})
		@DisplayName("Both overloads must not throw for clearly non-blank inputs")
		void requireNotBlank_bothOverloads_doNotThrow(String input)
		{
			assertThatCode(() -> StringSanitizationUtility.requireNotBlank(input, RuntimeException::new))
					.doesNotThrowAnyException();

			assertThatCode(() -> StringSanitizationUtility.requireNotBlank(input, "should not throw"))
					.doesNotThrowAnyException();
		}

		private static Stream<Arguments> consistencyProvider()
		{
			return Stream.of(
					// should throw
					Arguments.of(null, true, "null input"),
					Arguments.of("", true, "empty string"),
					Arguments.of(" ", true, "single space"),
					Arguments.of("\t", true, "tab only"),
					Arguments.of("\n", true, "newline only"),
					Arguments.of(" \t\n", true, "mixed whitespace"),
					// should not throw
					Arguments.of("hello", false, "plain word"),
					Arguments.of(" a", false, "leading space with content"),
					Arguments.of("a ", false, "trailing space with content"),
					Arguments.of("123", false, "numeric string"),
					Arguments.of("!@#$", false, "special characters"),
					Arguments.of("\u00A0", false, "non-breaking space (non-blank)"),
					Arguments.of("\u200B", false, "zero-width space (non-blank)")
			);
		}
	}
}