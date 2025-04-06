package io.skylab.logging_module.provider;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NestedMdcJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {

	public NestedMdcJsonProvider() {
		super();
		setFieldName("mdc");
	}

	@Override
	public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
		Map<String, String> mdcMap = event.getMDCPropertyMap();
		if (mdcMap == null || mdcMap.isEmpty()) {
			return;
		}

		if (getFieldName() != null) {
			generator.writeObjectFieldStart(getFieldName());
		}

		// Map to hold our nested structure
		Map<String, Object> resultMap = new HashMap<>();

		// Process each MDC entry
		for (Map.Entry<String, String> entry : mdcMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();

			// Split the key by dots
			String[] parts = key.split("\\.");

			if (parts.length == 1) {
				// No nesting, just add to root
				resultMap.put(key, value);
			} else {
				// Handle nested keys
				addNestedValue(resultMap, parts, 0, value);
			}
		}

		// Write the entire structure
		writeMap(generator, resultMap);

		if (getFieldName() != null) {
			generator.writeEndObject();
		}
	}

	// Helper method to recursively build the nested structure
	@SuppressWarnings("unchecked")
	private void addNestedValue(Map<String, Object> map, String[] keyParts, int index, String value) {
		String currentPart = keyParts[index];

		if (index == keyParts.length - 1) {
			// This is the last part of the key, so set the value
			map.put(currentPart, value);
		} else {
			// We need to go deeper
			Map<String, Object> nestedMap = (Map<String, Object>) map.computeIfAbsent(
					currentPart, k -> new HashMap<>());

			// Recursive call for the next level
			addNestedValue(nestedMap, keyParts, index + 1, value);
		}
	}

	// Helper method to write a map to JSON
	private void writeMap(JsonGenerator generator, Map<String, Object> map) throws IOException {
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof Map) {
				// Handle nested map - safer casting
				generator.writeObjectFieldStart(key);

				Map<String, Object> nestedMap = new HashMap<>();
				// Copy values from original map to ensure type safety
				for (Map.Entry<?, ?> nestedEntry : ((Map<?, ?>) value).entrySet()) {
					if (nestedEntry.getKey() instanceof String) {
						nestedMap.put((String) nestedEntry.getKey(), nestedEntry.getValue());
					}
				}

				writeMap(generator, nestedMap);
				generator.writeEndObject();
			} else {
				// Write simple value - make sure it's a string
				String stringValue = (value != null) ? value.toString() : "";
				generator.writeStringField(key, stringValue);
			}
		}
	}
}