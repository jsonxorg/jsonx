/* Copyright (c) 2017 lib4j
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

import java.lang.annotation.Annotation;

import org.libx4j.jsonx.jsonx_0_9_8.xL2gluGCXYYJc.$ArrayMember;
import org.libx4j.jsonx.jsonx_0_9_8.xL2gluGCXYYJc.$Template;

class Template extends Element {
  private final Model model;

  public Template(final $ArrayMember.Template binding, final Model model) {
    super(binding, null, null, binding.getMinOccurs$(), binding.getMaxOccurs$());
    this.model = model;
  }

  public Template(final $Template binding, final Model model) {
    super(binding.getName$().text(), null, binding.getRequired$().text(), binding.getDoc$() == null ? null : binding.getDoc$().text());
    this.model = model;
  }

  public Template(final String name, final boolean nullable, final boolean required, final Model model, final String doc) {
    super(name, nullable, required, doc);
    this.model = model;
  }

  public Template(final Integer minOccurs, final Integer maxOccurs, final Model model, final String doc) {
    super(null, null, minOccurs, maxOccurs, doc);
    this.model = model;
  }

  @Override
  public final Boolean nullable() {
    return model.nullable();
  }

  public final Model reference() {
    return this.model;
  }

  @Override
  protected final Type type() {
    return model.type();
  }

  @Override
  protected final Class<? extends Annotation> propertyAnnotation() {
    return model.propertyAnnotation();
  }

  @Override
  protected final Class<? extends Annotation> elementAnnotation() {
    return model.elementAnnotation();
  }

  @Override
  protected final Element normalize(final Registry registry) {
    return registry.getNumReferrers(reference()) != 1 || reference() instanceof ObjectModel && ((ObjectModel)reference()).isAbstract() ? this : reference().merge(this);
  }

  @Override
  protected final String toJSON(final String packageName) {
    final StringBuilder builder = new StringBuilder(super.toJSON(packageName));
    if (builder.length() > 0)
      builder.insert(0, ",\n");

    if (model != null)
      builder.append(",\n  reference: \"").append(Type.getSubName(model.reference(), packageName)).append('"');

    return "{\n" + (builder.length() > 0 ? builder.substring(2) : builder.toString()) + "\n}";
  }

  @Override
  protected final String toJSONX(final Member owner, final String packageName) {
    final StringBuilder builder = new StringBuilder(owner instanceof ObjectModel ? "<property xsi:type=\"template\"" : "<template");
    if (model != null)
      builder.append(" reference=\"").append(Type.getSubName(model.reference(), packageName)).append('"');

    return builder.append(super.toJSONX(owner, packageName)).append("/>").toString();
  }

  @Override
  protected final String toAnnotation(final String packageName, final boolean full) {
    final StringBuilder builder = new StringBuilder(super.toAnnotation(packageName, true));
    final String annotation = model.toAnnotation(packageName, false);
    if (builder.length() > 0 && annotation.length() > 0)
      builder.append(", ");

    return builder.append(annotation).toString();
  }

  @Override
  protected String toElementAnnotations(final String packageName) {
    return model.toElementAnnotations(packageName);
  }
}