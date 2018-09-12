/* Copyright (c) 2018 lib4j
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package org.libx4j.jsonx.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.fastjax.util.Annotations;
import org.fastjax.util.JavaIdentifiers;

public class JsonxUtil {
  public static Method getGetMethod(final Class<?> cls, final String propertyName) {
    return getMethod(cls, propertyName, null);
  }

  public static Method getSetMethod(final Field field, final String propertyName) {
    return getMethod(field.getDeclaringClass(), propertyName, field.getType());
  }

  private static Method getMethod(final Class<?> cls, final String propertyName, final Class<?> parameterType) {
    try {
      return cls.getMethod((parameterType == null ? "get" : "set") + JavaIdentifiers.toClassCase(propertyName), parameterType == null ? null : new Class<?> [] {parameterType});
    }
    catch (final NoSuchMethodException e) {
      throw new IllegalStateException(e);
    }
  }

  public static List<Class<?>> getDeclaredObjectTypes(final Field field) {
    final IdToElement idToElement = new IdToElement();
    final int[] elementIds = JsonxUtil.digest(field, idToElement);
    final Annotation[] annotations = idToElement.get(elementIds);
    final List<Class<?>> types = new ArrayList<>();
    getDeclaredObjectTypes(annotations, types);
    return types;
  }

  private static void getDeclaredObjectTypes(final Annotation[] annotations, final List<Class<?>> types) {
    for (final Annotation annotation : annotations) {
      if (annotation instanceof ArrayElement) {
        final ArrayElement element = (ArrayElement)annotation;
        if (element.type() != ArrayType.class)
          getDeclaredObjectTypes(element.type().getAnnotations(), types);
      }
      else if (annotation instanceof ObjectElement) {
        types.add(((ObjectElement)annotation).type());
      }
    }
  }

  public static int[] digest(final Field field, final IdToElement idToElement) {
    final ArrayProperty property = field.getAnnotation(ArrayProperty.class);
    if (property == null)
      throw new IllegalArgumentException("@" + ArrayProperty.class.getSimpleName() + " not found on: " + field.getDeclaringClass().getName() + "." + field.getName());

    if (property.type() != ArrayType.class)
      return digest(property.type().getAnnotations(), property.type().getName(), idToElement);

    return digest(field.getAnnotations(), field.getDeclaringClass().getName() + "." + field.getName(), idToElement);
  }

  public static int[] digest(Annotation[] annotations, final String declarerName, final IdToElement idToElement) {
    annotations = JsonxUtil.flatten(annotations);
    JsonxUtil.fillIdToElement(idToElement, annotations);
    Annotation arrayAnnotation = null;
    int[] elementIds = null;
    for (final Annotation annotation : annotations) {
      if (annotation instanceof ArrayType) {
        arrayAnnotation = annotation;
        elementIds = ((ArrayType)annotation).elementIds();
        break;
      }

      if (annotation instanceof ArrayProperty) {
        arrayAnnotation = annotation;
        elementIds = ((ArrayProperty)annotation).elementIds();
        break;
      }
    }

    if (arrayAnnotation == null)
      throw new ValidationException(declarerName + " does not declare @" + ArrayType.class.getSimpleName() + " or @" + ArrayProperty.class.getSimpleName());

    if (elementIds.length == 0)
      throw new ValidationException("elementIds property cannot be empty: " + declarerName + ": " + Annotations.toSortedString(arrayAnnotation, AttributeComparator.instance));

    return elementIds;
  }

  public static boolean isNullable(final Annotation annotation) {
    if (annotation instanceof ArrayElement)
      return ((ArrayElement)annotation).nullable();

    if (annotation instanceof BooleanElement)
      return ((BooleanElement)annotation).nullable();

    if (annotation instanceof NumberElement)
      return ((NumberElement)annotation).nullable();

    if (annotation instanceof ObjectElement)
      return ((ObjectElement)annotation).nullable();

    if (annotation instanceof StringElement)
      return ((StringElement)annotation).nullable();

    throw new UnsupportedOperationException("Unsupported annotation type " + annotation.annotationType().getName());
  }

  public static String getName(final String name, final Field field) {
    return name.length() > 0 ? name : field.getName();
  }

  public static void fillIdToElement(final IdToElement idToElement, Annotation[] annotations) {
    annotations = JsonxUtil.flatten(annotations);
    for (final Annotation annotation : annotations) {
      if (annotation instanceof ArrayElement)
        idToElement.put(((ArrayElement)annotation).id(), annotation);
      else if (annotation instanceof BooleanElement)
        idToElement.put(((BooleanElement)annotation).id(), annotation);
      else if (annotation instanceof NumberElement)
        idToElement.put(((NumberElement)annotation).id(), annotation);
      else if (annotation instanceof ObjectElement)
        idToElement.put(((ObjectElement)annotation).id(), annotation);
      else if (annotation instanceof StringElement)
        idToElement.put(((StringElement)annotation).id(), annotation);
    }
  }

  public static Annotation[] flatten(final Annotation[] annotations) {
    return flatten(annotations, 0, 0);
  }

  private static Annotation[] flatten(final Annotation[] annotations, final int index, final int depth) {
    if (index == annotations.length)
      return new Annotation[depth];

    final Annotation annotation = annotations[index];
    final Annotation[] repeatable;
    if (ArrayElements.class.equals(annotation.annotationType()))
      repeatable = ((ArrayElements)annotation).value();
    else if (BooleanElements.class.equals(annotation.annotationType()))
      repeatable = ((BooleanElements)annotation).value();
    else if (NumberElements.class.equals(annotation.annotationType()))
      repeatable = ((NumberElements)annotation).value();
    else if (ObjectElements.class.equals(annotation.annotationType()))
      repeatable = ((ObjectElements)annotation).value();
    else if (StringElements.class.equals(annotation.annotationType()))
      repeatable = ((StringElements)annotation).value();
    else
      repeatable = null;

    if (repeatable == null) {
      final Annotation[] flattened = flatten(annotations, index + 1, depth + 1);
      flattened[depth] = annotation;
      return flattened;
    }

    final Annotation[] flattened = flatten(annotations, index + 1, depth + repeatable.length);
    for (int i = 0; i < repeatable.length; i++)
      flattened[depth + i] = repeatable[i];

    return flattened;
  }
}