/* Copyright (c) 2018 JSONx
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

package org.jsonx;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.libj.lang.Strings;

/**
 * A JAX-RS {@link Provider} that implements an {@link ExceptionMapper} to
 * present a JSON error body in case of a {@link BadRequestException}.
 */
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
  private final boolean verbose;

  public BadRequestExceptionMapper(final boolean verbose) {
    this.verbose = verbose;
  }

  public BadRequestExceptionMapper() {
    this(true);
  }

  @Override
  public Response toResponse(final BadRequestException exception) {
    try (final Response response = exception.getResponse()) {
      final StringBuilder builder = new StringBuilder("{\"status\":").append(response.getStatus());
      final String message = exception.getMessage();
      if (message != null) {
        final String prefix = "HTTP " + response.getStatus() + " ";
        builder.append(",\"message\":\"").append(message.startsWith(prefix) ? message.substring(prefix.length()) : message).append('"');
        if (exception.getCause() instanceof DecodeException) {
          builder.append(",\"cause\":\"").append(Strings.escapeForJava(exception.getCause().getMessage())).append('"');
          if (verbose) {
            final DecodeException decodeException = (DecodeException)exception.getCause();
            if (decodeException.getContent() != null)
              builder.append(",\"json\":\"").append(Strings.escapeForJava(decodeException.getContent())).append('"');
          }
        }
      }

      return Response.fromResponse(response).entity(builder.append('}').toString()).build();
    }
  }
}