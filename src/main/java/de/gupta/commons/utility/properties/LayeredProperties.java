package de.gupta.commons.utility.properties;

import de.gupta.aletheia.collection.folding.Loom;

import java.nio.file.Path;
import java.util.*;

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
			                 .weave(empty, (acc, source) -> merge(acc, source.load()));
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

		private static Map<String, String> merge(final Map<String, String> base,
		                                         final Map<String, String> overlay)
		{
			var result = new LinkedHashMap<>(base);
			result.putAll(overlay);
			return Collections.unmodifiableMap(result);
		}

		private Builder()
		{
		}
	}
}