package de.gupta.commons.utility.properties;

import de.gupta.aletheia.collection.folding.Loom;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

//this class is too long. really needed or can be refactored?
public final class LayeredProperties
{
	private final Map<String, String> resolved;

	public static Builder builder()
	{
		return new Builder();
	}

	public Optional<String> get(final String key)
	{
		Objects.requireNonNull(key, "key must not be null");
		return Optional.ofNullable(resolved.get(key));
	}

	public String require(final String key)
	{
		Objects.requireNonNull(key, "key must not be null");
		return Optional.ofNullable(resolved.get(key))
		               .orElseThrow(() -> new NoSuchElementException("No value present for key: " + key));
	}

	public String get(final String key, final String fallback)
	{
		Objects.requireNonNull(key, "key must not be null");
		Objects.requireNonNull(fallback, "fallback must not be null");
		return resolved.getOrDefault(key, fallback);
	}

	private LayeredProperties(final Map<String, String> resolved)
	{
		this.resolved = resolved;
	}

	// does builder need really be public? we already have a static method for accessing it. can it remain package private?
	public static final class Builder
	{
		private final List<PropertySource> sources = new ArrayList<>();

		public Builder withClasspathResource(final String resourcePath)
		{
			Objects.requireNonNull(resourcePath, "resourcePath must not be null");
			if (resourcePath.isBlank())
			{
				throw new IllegalArgumentException("resourcePath must not be blank");
			}
			sources.add(new PropertySource.ClasspathResource(resourcePath));
			return this;
		}

		public Builder withFile(final Path path)
		{
			Objects.requireNonNull(path, "path must not be null");
			sources.add(new PropertySource.FilesystemPath(path));
			return this;
		}

		public Builder withSystemProperties()
		{
			sources.add(new PropertySource.SystemPropertiesSource());
			return this;
		}

		public LayeredProperties build()
		{
			var ordered = reorderSystemPropertiesLast(sources);
			Map<String, String> empty = Map.of();
			var merged = Loom.thread(ordered)
			                 .weave(empty, (acc, source) -> merge(acc, load(source)));
			// rather use static factor mehtod?
			return new LayeredProperties(merged);
		}

		private static List<PropertySource> reorderSystemPropertiesLast(final List<PropertySource> sources)
		{
			var nonSystem = sources.stream()
			                       .filter(s -> !(s instanceof PropertySource.SystemPropertiesSource))
			                       .toList();
			var hasSystem = sources.stream()
			                       .anyMatch(PropertySource.SystemPropertiesSource.class::isInstance);
			if (!hasSystem)
			{
				return nonSystem;
			}
			var result = new ArrayList<>(nonSystem);
			result.add(new PropertySource.SystemPropertiesSource());
			return List.copyOf(result);
		}

		private static Map<String, String> load(final PropertySource source)
		{
			return switch (source)
			{
				case PropertySource.ClasspathResource r -> loadClasspath(r.resourcePath());
				case PropertySource.FilesystemPath f -> loadFile(f.path());
				case PropertySource.SystemPropertiesSource _ -> loadSystemProperties();
			};
		}

		private static Map<String, String> loadClasspath(final String resourcePath)
		{
			var stream = Thread.currentThread()
			                   .getContextClassLoader()
			                   .getResourceAsStream(resourcePath);
			// multiple if else - does't Aletheia offer some nice abstractions and functional pipelines for this? some combinaion of Unfolding/Fallible etc.?
			if (stream == null)
			{
				return Map.of();
			}
			try (stream)
			{
				var props = new Properties();
				props.load(stream);
				return toStringMap(props);
			}
			catch (IOException _)
			{
				return Map.of();
			}
		}

		private static Map<String, String> loadFile(final Path path)
		{
			if (!Files.exists(path) || !Files.isReadable(path))
			{
				return Map.of();
			}
			try (var input = Files.newInputStream(path))
			{
				var props = new Properties();
				props.load(input);
				return toStringMap(props);
			}
			catch (IOException _)
			{
				return Map.of();
			}
		}

		private static Map<String, String> loadSystemProperties()
		{
			return toStringMap(System.getProperties());
		}

		private static Map<String, String> merge(final Map<String, String> base,
		                                         final Map<String, String> overlay)
		{
			var result = new LinkedHashMap<>(base);
			result.putAll(overlay);
			return Collections.unmodifiableMap(result);
		}

		private static Map<String, String> toStringMap(final Properties properties)
		{
			return properties.stringPropertyNames()
			                 .stream()
			                 .filter(key -> !key.isEmpty())
			                 .collect(Collectors.toUnmodifiableMap(key -> key, properties::getProperty));
		}

		private Builder()
		{
		}
	}
}