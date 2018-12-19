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

import static org.junit.Assert.*;

class MaxOccursCase extends FailureCase<ArrayTrial<Object>> {
  static final MaxOccursCase CASE = new MaxOccursCase();

  @Override
  void onEncode(final ArrayTrial<Object> trial, final EncodeException e) throws Exception {
    assertTrue(trial.name, e.getMessage().endsWith("Content is not complete"));
  }

  @Override
  void onDecode(final ArrayTrial<Object> trial, final DecodeException e) throws Exception {
    assertTrue(trial.name, e.getMessage().contains("Content is not complete"));
  }

  private MaxOccursCase() {
  }
}