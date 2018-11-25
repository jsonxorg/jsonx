/* Copyright (c) 2018 OpenJAX
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

package org.openjax.jsonx.runtime;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;

import org.fastjax.util.Annotations;

class NumberSpec extends PrimitiveSpec<Number> {
  private final Form form;
  private final Range range;

  NumberSpec(final NumberProperty property, final Field field) {
    super(field, property.name(), property.use());
    this.form = property.form();
    if (property.range().length() == 0) {
      this.range = null;
    }
    else {
      try {
        this.range = new Range(property.range());
      }
      catch (final ParseException e) {
        throw new ValidationException("Invalid range attribute: " + Annotations.toSortedString(property, AttributeComparator.instance));
      }
    }
  }

  @Override
  boolean test(final char firstChar) {
    return firstChar == '-' || '0' <= firstChar && firstChar <= '9';
  }

  @Override
  String validate(final String json) {
    if (form == Form.REAL)
      return null;

    final int dot = json.indexOf('.');
    if (dot != -1)
      return "Illegal non-INTEGER value";

    // FIXME: decode() is done here and in the caller's scope
    return range != null && !range.isValid(decode(json)) ? "Range (" + range + ") is not matched: " + json : null;
  }

  @Override
  Number decode(final String json) {
    return form == Form.INTEGER ? new BigInteger(json) : new BigDecimal(json);
  }

  @Override
  String elementName() {
    return "number";
  }
}