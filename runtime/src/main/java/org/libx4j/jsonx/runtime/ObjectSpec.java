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

import java.lang.reflect.Field;

public class ObjectSpec extends Spec {
  private final Class<?> type;

  public ObjectSpec(final Field field, final ObjectProperty property) {
    super(field, property.name(), property.use());
    this.type = field.getType();
  }

  public Class<?> getType() {
    return type;
  }

  @Override
  public String elementName() {
    return "object";
  }
}