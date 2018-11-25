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

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLDecoder;

class StringSpec extends PrimitiveSpec<String> {
  private final String pattern;
  private final boolean urlDecode;

  StringSpec(final StringProperty property, final Field field) {
    super(field, property.name(), property.use());
    this.pattern = property.pattern().length() == 0 ? null : property.pattern();
    this.urlDecode = property.urlDecode();
  }

  @Override
  boolean test(final char firstChar) {
    return firstChar == '"';
  }

  @Override
  String validate(final String token) {
    if ((token.charAt(0) != '"' || token.charAt(token.length() - 1) != '"') && (token.charAt(0) != '\'' || token.charAt(token.length() - 1) != '\''))
      return "Is not a string";

    // FIXME: decode() is done here and in the caller's scope
    return pattern != null && !decode(token).matches(pattern) ? "Pattern (" + pattern + ") is not matched: \"" + token + "\"" : null;
  }

  @Override
  String decode(String json) {
    json = json.substring(1, json.length() - 1);
    try {
      return urlDecode ? URLDecoder.decode(json, "UTF-8") : json;
    }
    catch (final UnsupportedEncodingException e) {
      throw new UnsupportedOperationException(e);
    }
  }

  @Override
  String elementName() {
    return "string";
  }
}