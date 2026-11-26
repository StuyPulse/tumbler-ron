/************************* PROJECT RON *************************/
/* Copyright (c) 2026 StuyPulse Robotics. All rights reserved. */
/* Use of this source code is governed by an MIT-style license */
/* that can be found in the repository LICENSE file.           */
/***************************************************************/
package com.stuypulse.robot.util.talonfx.logged;

import edu.wpi.first.units.Measure;
import edu.wpi.first.util.WPISerializable;
import edu.wpi.first.util.struct.StructSerializable;
import edu.wpi.first.wpilibj.util.Color;
import java.util.Arrays;
import java.util.Objects;
import org.littletonrobotics.junction.LogTable;

/**
 *
 *
 * <h2>Log Table Utility</h2>
 *
 * <p>Utility methods for reading and writing dynamically typed values to and from a {@code
 * LogTable}.
 *
 * <p>This centralizes type handling without having to duplicate the same if else logic in multiple
 * places.
 *
 * @author Faizaan (https://github.com/Faizaan-J)
 */
public final class LogTableUtil {
    private LogTableUtil() {}

    /**
     * Puts a value into a {@code LogTable} with the given key, automatically handling the type of
     * the value.
     *
     * @param table the {@code LogTable} to put the value into
     * @param key The key where the value will be stored.
     * @param value The value to store in the {@code LogTable}. If it's not a supported type, it
     *     will be converted to a string.
     */
    public static void put(LogTable table, String key, Object value) {
        Objects.requireNonNull(table, "LogTable cannot be null");
        Objects.requireNonNull(key, "Logging key cannot be null");
        if (value != null && value.getClass().isArray()) {
            putArray(table, key, value);
        } else if (value instanceof Measure<?> measure) {
            table.put(key, measure);
        } else if (value instanceof WPISerializable serializable) {
            table.put(key, serializable);
        } else if (value instanceof Color color) {
            table.put(key, color);
        } else if (value instanceof Boolean) {
            table.put(key, (Boolean) value);
        } else if (value instanceof Double) {
            table.put(key, (Double) value);
        } else if (value instanceof Integer) {
            table.put(key, (Integer) value);
        } else if (value instanceof Long) {
            table.put(key, (Long) value);
        } else if (value instanceof String) {
            table.put(key, (String) value);
        } else if (value instanceof Enum<?> enumerator) {
            putEnum(table, key, enumerator);
        } else if (value instanceof Record record) {
            table.put(key, record);
        } else {
            // turn into string for unsupported types, can't be replayed tho.
            table.put(key, value == null ? "null" : value.toString());
        }
    }

    /**
     * Puts an array value into a {@code LogTable}, dispatching to the corresponding array overload.
     * Falls back to a {@code String[]} for element types with no array overload such as a {@code
     * Measure} or boxed wrapper types.
     */
    private static void putArray(LogTable table, String key, Object array) {
        if (array instanceof boolean[] bools) {
            table.put(key, bools);
        } else if (array instanceof int[] ints) {
            table.put(key, ints);
        } else if (array instanceof long[] longs) {
            table.put(key, longs);
        } else if (array instanceof float[] floats) {
            table.put(key, floats);
        } else if (array instanceof double[] doubles) {
            table.put(key, doubles);
        } else if (array instanceof String[] strings) {
            table.put(key, strings);
        } else if (array instanceof byte[] bytes) {
            table.put(key, bytes);
        } else if (array instanceof Enum<?>[] enums) {
            putEnumArray(table, key, enums);
        } else if (array instanceof StructSerializable[] serializables) {
            table.put(key, serializables);
        } else if (array instanceof Record[] records) {
            table.put(key, records);
        } else if (array instanceof Object[] objects) {
            table.put(key, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
        } else {
            throw new IllegalArgumentException(
                    "Unsupported or invalid array type: "
                            + (array == null ? "null" : array.getClass().getName()));
        }
    }

    /**
     * Gets a value from a {@code LogTable} with the given key, automatically handling the type of
     * the value based on the provided default value.
     *
     * @param <T> The type of the value.
     * @param table The {@code LogTable} to get the value from.
     * @param key The key where the valid is stored.
     * @param defaultValue The default value to return if the key does not exist in the {@code
     *     LogTable} .
     * @return The value from the {@code LogTable} if it exists, otherwise the default value.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T get(LogTable table, String key, T defaultValue) {
        Objects.requireNonNull(table, "LogTable cannot be null");
        Objects.requireNonNull(key, "Logging key cannot be null");

        if (defaultValue != null && defaultValue.getClass().isArray()) {
            return (T) getArray(table, key, defaultValue);
        } else if (defaultValue instanceof Measure<?> measure) {
            return (T) table.get(key, measure);
        } else if (defaultValue instanceof WPISerializable serializable) {
            return (T) table.get(key, serializable);
        } else if (defaultValue instanceof Color color) {
            return (T) table.get(key, color);
        } else if (defaultValue instanceof Boolean b) {
            return (T) Boolean.valueOf(table.get(key, b));
        } else if (defaultValue instanceof Double d) {
            return (T) Double.valueOf(table.get(key, d));
        } else if (defaultValue instanceof Integer i) {
            return (T) Integer.valueOf(table.get(key, i));
        } else if (defaultValue instanceof Long l) {
            return (T) Long.valueOf(table.get(key, l));
        } else if (defaultValue instanceof String s) {
            return (T) table.get(key, s);
        } else if (defaultValue instanceof Enum<?> enumerator) {
            return (T) getEnum(table, key, enumerator);
        } else if (defaultValue instanceof Record record) {
            return (T) table.get(key, record);
        } else {
            return defaultValue;
        }
    }

    /**
     * Gets an array value from a {@code LogTable}, dispatching to the corresponding array overload.
     * Types with no array overload such as {@code Measure} or boxed wrapper types aren't
     * replayable, so the default array is returned back.
     */
    private static Object getArray(LogTable table, String key, Object defaultArray) {
        if (defaultArray instanceof boolean[] bools) {
            return table.get(key, bools);
        } else if (defaultArray instanceof int[] ints) {
            return table.get(key, ints);
        } else if (defaultArray instanceof long[] longs) {
            return table.get(key, longs);
        } else if (defaultArray instanceof float[] floats) {
            return table.get(key, floats);
        } else if (defaultArray instanceof double[] doubles) {
            return table.get(key, doubles);
        } else if (defaultArray instanceof String[] strings) {
            return table.get(key, strings);
        } else if (defaultArray instanceof byte[] bytes) {
            return table.get(key, bytes);
        } else if (defaultArray instanceof Enum<?>[] enums) {
            return getEnumArray(table, key, enums);
        } else if (defaultArray instanceof StructSerializable[] serializables) {
            return table.get(key, serializables);
        } else if (defaultArray instanceof Record[] records) {
            return table.get(key, records);
        } else if (defaultArray instanceof Object[] objects) {
            return table.get(
                    key, Arrays.stream(objects).map(String::valueOf).toArray(String[]::new));
        } else {
            return defaultArray;
        }
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> void putEnum(LogTable table, String key, Enum<?> value) {
        table.put(key, (E) value);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E getEnum(LogTable table, String key, Enum<?> defaultValue) {
        return table.get(key, (E) defaultValue);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> void putEnumArray(
            LogTable table, String key, Enum<?>[] value) {
        table.put(key, (E[]) value);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> E[] getEnumArray(
            LogTable table, String key, Enum<?>[] defaultValue) {
        return table.get(key, (E[]) defaultValue);
    }

    /**
     * Returns true if the value is a supported type for logging, false otherwise.
     *
     * @param value The value to check for logging support.
     * @return true if the value is a supported type for logging, false otherwise.
     */
    public static boolean isSupportedType(Object value) {
        if (value == null) {
            return false;
        }

        if (value.getClass().isArray()) {
            return isSupportedArrayType(value);
        }

        if (value instanceof Measure<?>) {
            return true;
        } else if (value instanceof WPISerializable) {
            return true;
        } else if (value instanceof Color) {
            return true;
        } else if (value instanceof Boolean) {
            return true;
        } else if (value instanceof Double) {
            return true;
        } else if (value instanceof Integer) {
            return true;
        } else if (value instanceof Long) {
            return true;
        } else if (value instanceof String) {
            return true;
        } else if (value instanceof Enum<?>) {
            return true;
        } else if (value instanceof Record) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isSupportedArrayType(Object value) {
        if (value instanceof boolean[]) {
            return true;
        } else if (value instanceof int[]) {
            return true;
        } else if (value instanceof long[]) {
            return true;
        } else if (value instanceof float[]) {
            return true;
        } else if (value instanceof double[]) {
            return true;
        } else if (value instanceof byte[]) {
            return true;
        } else if (value instanceof String[]) {
            return true;
        } else if (value instanceof Enum<?>[]) {
            return true;
        } else if (value instanceof StructSerializable[]) {
            return true;
        } else if (value instanceof Object[] array) {
            return array.getClass().getComponentType().isRecord();
        } else {
            return false;
        }
    }
}
