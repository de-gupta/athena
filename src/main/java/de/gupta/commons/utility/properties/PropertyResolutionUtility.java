package de.gupta.commons.utility.properties;

import de.gupta.commons.utility.string.StringSearchUtility;

import java.util.Properties;
import java.util.SequencedCollection;

public final class PropertyResolutionUtility
{
	public static Properties overwriteOrSetKey(final Properties properties, final String key,
	                                           final SequencedCollection<String> values)
	{
		Properties result = new Properties();
		properties.stringPropertyNames()
		          .forEach(existingKey -> result.setProperty(existingKey, properties.getProperty(existingKey)));
		StringSearchUtility.firstNonBlank(values)
		                   .ifPresent(value -> result.setProperty(key, value));
		return result;
	}

	private PropertyResolutionUtility()
	{
	}
}