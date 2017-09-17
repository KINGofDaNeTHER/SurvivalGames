package me.kingofdanether.survivalgames.util;

import java.lang.reflect.Field;

public class ReflectionUtils {

	 public static void setValue(Object instance, String fieldName, Object value) throws Exception {
		 Field field = instance.getClass().getDeclaredField(fieldName);
		 field.setAccessible(true);
		 field.set(instance, value);
	 }
}
