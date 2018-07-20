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

package org.libx4j.jsonx.generator;

import java.util.TreeMap;

import org.lib4j.util.ObservableMap;

public class AttributeMap extends ObservableMap<String,String> {
  public AttributeMap() {
    super(new TreeMap<String,String>());
  }

  @Override
  protected boolean beforePut(final String key, final String oldValue, final String newValue) {
    if (oldValue == null || oldValue.equals(newValue) || "xsi:schemaLocation".equals(key))
      return true;

    throw new IllegalStateException("Attempted overwrite of attribute: " + key + " from " + oldValue + " to " + newValue);
  }

  public String put(final String key, final Object value) {
    return super.put(key, value == null ? null : String.valueOf(value));
  }
}