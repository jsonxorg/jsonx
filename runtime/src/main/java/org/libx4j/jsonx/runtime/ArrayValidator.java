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

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.lib4j.util.Annotations;
import org.lib4j.util.WrappedList;

public class ArrayValidator {
  static class Relation {
    final Object member;
    final Annotation annotation;

    Relation(final Object member, final Annotation annotation) {
      this.member = member;
      this.annotation = annotation;
    }
  }

  static class Relations extends WrappedList<Relation> {
    public Relations() {
      super(new ArrayList<Relation>());
    }

    @Override
    public Relation set(final int index, final Relation element) {
      if (index == size())
        super.add(element);
      else
        super.set(index, element);

      return element;
    }

    public List<Object> toList() {
      final List<Object> list = new ArrayList<>();
      for (final Relation relation : this)
        list.add(relation.member);

      return list;
    }
  }

  private static int getNextRequiredElement(final Annotation[] annotations, final int fromIndex, int count) {
    for (int i = fromIndex; i < annotations.length; ++i) {
      final Annotation annotation = annotations[i];
      if (annotation instanceof ArrayElement) {
        final ArrayElement element = (ArrayElement)annotation;
        if (count < element.minOccurs())
          return i;
      }
      else if (annotation instanceof BooleanElement) {
        final BooleanElement element = (BooleanElement)annotation;
        if (count < element.minOccurs())
          return i;
      }
      else if (annotation instanceof NumberElement) {
        final NumberElement element = (NumberElement)annotation;
        if (count < element.minOccurs())
          return i;
      }
      else if (annotation instanceof ObjectElement) {
        final ObjectElement element = (ObjectElement)annotation;
        if (count < element.minOccurs())
          return i;
      }
      else if (annotation instanceof StringElement) {
        final StringElement element = (StringElement)annotation;
        if (count < element.minOccurs())
          return i;
      }
      else {
        throw new UnsupportedOperationException("Unsupported annotation type " + annotation.annotationType().getName());
      }

      count = 0;
    }

    return -1;
  }

  static String validate(final ArrayIterator<? extends Object> iterator, final int count, final Annotation[] annotations, final int a, final IdToElement idToElement, final Relations relations) throws DecodeException, IOException {
    final int i = iterator.nextIndex();
    if (!iterator.hasNext()) {
      final int nextRequiredIndex = getNextRequiredElement(annotations, a, count);
      return nextRequiredIndex == -1 ? null : "Invalid content was found " + (i == 0 ? "in empty array" : "starting with member index=" + (i - 1)) + ": " + Annotations.toSortedString(annotations[nextRequiredIndex], AttributeComparator.instance) + ": Content is not complete";
    }

    if (a == annotations.length) {
      if (!iterator.hasNext())
        return null;

      iterator.next();
      final String preview = iterator.currentPreview();
      iterator.previous();
      return "Invalid content was found starting with member index=" + i + ": " + Annotations.toSortedString(annotations[a - 1], AttributeComparator.instance) + ": No members are expected at this point: " + preview;
    }

    final int minOccurs;
    final int maxOccurs;
    final boolean nullable;
    final Class<?> type;
    final Annotation annotation = annotations[a];
    if (annotation instanceof ArrayElement) {
      final ArrayElement element = (ArrayElement)annotation;
      minOccurs = element.minOccurs();
      maxOccurs = element.maxOccurs();
      nullable = element.nullable();
      type = List.class;
    }
    else if (annotation instanceof BooleanElement) {
      final BooleanElement element = (BooleanElement)annotation;
      minOccurs = element.minOccurs();
      maxOccurs = element.maxOccurs();
      nullable = element.nullable();
      type = Boolean.class;
    }
    else if (annotation instanceof NumberElement) {
      final NumberElement element = (NumberElement)annotation;
      minOccurs = element.minOccurs();
      maxOccurs = element.maxOccurs();
      nullable = element.nullable();
      type = Number.class;
    }
    else if (annotation instanceof ObjectElement) {
      final ObjectElement element = (ObjectElement)annotation;
      minOccurs = element.minOccurs();
      maxOccurs = element.maxOccurs();
      nullable = element.nullable();
      type = Object.class;
    }
    else if (annotation instanceof StringElement) {
      final StringElement element = (StringElement)annotation;
      minOccurs = element.minOccurs();
      maxOccurs = element.maxOccurs();
      nullable = element.nullable();
      type = String.class;
    }
    else {
      throw new UnsupportedOperationException("Unsupported annotation type " + annotation.annotationType().getName());
    }

    if (minOccurs < 0)
      throw new ValidationException("minOccurs must be a non-negative integer: " + Annotations.toSortedString(annotation, AttributeComparator.instance));

    if (maxOccurs < minOccurs)
      throw new ValidationException("minOccurs must be less than or equal to maxOccurs: " + Annotations.toSortedString(annotation, AttributeComparator.instance));

//    System.err.println("Matching " + j + ", count " + count + " against " + i);
    final String error;
    if (iterator.nextIsNull()) {
      if (nullable) {
        error = null;
        relations.set(i, iterator.currentRelate(annotation));
      }
      else {
        error = "Illegal value: null";
      }
    }
    else {
      if (iterator.currentMatchesType(type, annotation, idToElement)) {
        error = iterator.currentIsValid(i, annotation, idToElement, relations);
      }
      else if (count < minOccurs) {
        error = "Content is not expected: " + iterator.currentPreview();
      }
      else {
        iterator.previous();
        return validate(iterator, 0, annotations, a + 1, idToElement, relations);
      }
    }

    String result = error == null ? null : "Invalid content was found starting with member index=" + i + ": " + Annotations.toSortedString(annotation, AttributeComparator.instance) + ": " + error;

    do {
      if (result != null) {
        iterator.previous();
        return count >= minOccurs && maxOccurs >= count && validate(iterator, 0, annotations, a + 1, idToElement, relations) == null ? null : result;
      }

      if (count < maxOccurs - 1)
        result = validate(iterator, count + 1, annotations, a, idToElement, relations);
      else
        result = validate(iterator, 0, annotations, a + 1, idToElement, relations);
    }
    while (result != null);

    return null;
  }

  static String validate(final List<?> members, final IdToElement idToElement, final int[] elementIds, final Relations relations) {
    final Annotation[] annotations = idToElement.get(elementIds);
    try {
      return validate(new ValidatingIterator((ListIterator<Object>)members.listIterator()), 0, annotations, 0, idToElement, relations);
    }
    catch (final DecodeException | IOException e) {
      throw new RuntimeException("Should not happen, as this method is only called for encode", e);
    }
  }

  static String validate(final Field field, final List<Object> members, final Relations relations) {
    final IdToElement idToElement = new IdToElement();
    final int[] elementIds = JsonxUtil.digest(field, idToElement);
    return validate(members, idToElement, elementIds, relations);
  }

  static String validate(final Class<? extends Annotation> annotationType, final List<?> members, final Relations relations) {
    final IdToElement idToElement = new IdToElement();
    final int[] elementIds = JsonxUtil.digest(annotationType.getAnnotations(), annotationType.getName(), idToElement);
    return validate(members, idToElement, elementIds, relations);
  }

  private ArrayValidator() {
  }
}