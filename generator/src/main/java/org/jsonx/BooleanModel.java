/* Copyright (c) 2017 JSONx
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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;

import org.jsonx.www.schema_0_3.xL0gluGCXAA;
import org.libj.lang.IllegalAnnotationException;

final class BooleanModel extends Model {
  private static final Id ID = Id.hashed("b");

  private static xL0gluGCXAA.Schema.Boolean type(final String name) {
    final xL0gluGCXAA.Schema.Boolean xsb = new xL0gluGCXAA.Schema.Boolean();
    if (name != null)
      xsb.setName$(new xL0gluGCXAA.Schema.Boolean.Name$(name));

    return xsb;
  }

  private static xL0gluGCXAA.$Boolean property(final schema.BooleanProperty jsd, final String name) {
    final xL0gluGCXAA.$Boolean xsb = new xL0gluGCXAA.$Boolean() {
      private static final long serialVersionUID = 650722913732574568L;

      @Override
      protected xL0gluGCXAA.$Member inherits() {
        return new xL0gluGCXAA.$ObjectMember.Property();
      }
    };

    if (name != null)
      xsb.setName$(new xL0gluGCXAA.$Boolean.Name$(name));

    if (jsd.getNullable() != null)
      xsb.setNullable$(new xL0gluGCXAA.$Boolean.Nullable$(jsd.getNullable()));

    if (jsd.getUse() != null)
      xsb.setUse$(new xL0gluGCXAA.$Boolean.Use$(xL0gluGCXAA.$Boolean.Use$.Enum.valueOf(jsd.getUse())));

    return xsb;
  }

  private static xL0gluGCXAA.$ArrayMember.Boolean element(final schema.BooleanElement jsd) {
    final xL0gluGCXAA.$ArrayMember.Boolean xsb = new xL0gluGCXAA.$ArrayMember.Boolean();
    if (jsd.getNullable() != null)
      xsb.setNullable$(new xL0gluGCXAA.$ArrayMember.Boolean.Nullable$(jsd.getNullable()));

    if (jsd.getMinOccurs() != null)
      xsb.setMinOccurs$(new xL0gluGCXAA.$ArrayMember.Boolean.MinOccurs$(new BigInteger(jsd.getMinOccurs())));

    if (jsd.getMaxOccurs() != null)
      xsb.setMaxOccurs$(new xL0gluGCXAA.$ArrayMember.Boolean.MaxOccurs$(jsd.getMaxOccurs()));

    return xsb;
  }

  static xL0gluGCXAA.$BooleanMember jsdToXsb(final schema.Boolean jsd, final String name) {
    final xL0gluGCXAA.$BooleanMember xsb;
    if (jsd instanceof schema.BooleanProperty)
      xsb = property((schema.BooleanProperty)jsd, name);
    else if (jsd instanceof schema.BooleanElement)
      xsb = element((schema.BooleanElement)jsd);
    else if (name != null)
      xsb = type(name);
    else
      throw new UnsupportedOperationException("Unsupported type: " + jsd.getClass().getName());

    if (jsd.getDoc() != null && jsd.getDoc().length() > 0)
      xsb.setDoc$(new xL0gluGCXAA.$Documented.Doc$(jsd.getDoc()));

    return xsb;
  }

  static BooleanModel declare(final Registry registry, final Declarer declarer, final xL0gluGCXAA.Schema.Boolean xsb) {
    return registry.declare(xsb).value(new BooleanModel(registry, declarer, xsb), null);
  }

  static Reference referenceOrDeclare(final Registry registry, final Referrer<?> referrer, final BooleanProperty property, final Field field) {
    final BooleanModel model = new BooleanModel(registry, referrer, property, field);
    final Id id = model.id;

    final BooleanModel registered = (BooleanModel)registry.getModel(id);
    return new Reference(registry, referrer, JsdUtil.getName(property.name(), field), property.nullable(), property.use(), registered == null ? registry.declare(id).value(model, referrer) : registry.reference(registered, referrer));
  }

  static Reference referenceOrDeclare(final Registry registry, final Referrer<?> referrer, final BooleanElement element) {
    final BooleanModel model = new BooleanModel(registry, referrer, element);
    final Id id = model.id;

    final BooleanModel registered = (BooleanModel)registry.getModel(id);
    return new Reference(registry, referrer, element.nullable(), element.minOccurs(), element.maxOccurs(), registered == null ? registry.declare(id).value(model, referrer) : registry.reference(registered, referrer));
  }

  static BooleanModel reference(final Registry registry, final Referrer<?> referrer, final xL0gluGCXAA.$Array.Boolean xsb) {
    return registry.reference(new BooleanModel(registry, referrer, xsb), referrer);
  }

  static BooleanModel reference(final Registry registry, final Referrer<?> referrer, final xL0gluGCXAA.$Boolean xsb) {
    return registry.reference(new BooleanModel(registry, referrer, xsb), referrer);
  }

  private BooleanModel(final Registry registry, final Declarer declarer, final xL0gluGCXAA.Schema.Boolean xsb) {
    super(registry, declarer, Id.named(xsb.getName$()), xsb.getDoc$());
  }

  private BooleanModel(final Registry registry, final Declarer declarer, final xL0gluGCXAA.$Boolean xsb) {
    super(registry, declarer, ID, xsb.getDoc$(), xsb.getName$(), xsb.getNullable$(), xsb.getUse$());
  }

  private BooleanModel(final Registry registry, final Declarer declarer, final xL0gluGCXAA.$Array.Boolean xsb) {
    super(registry, declarer, ID, xsb.getDoc$(), xsb.getNullable$(), xsb.getMinOccurs$(), xsb.getMaxOccurs$());
  }

  private BooleanModel(final Registry registry, final Declarer declarer, final BooleanProperty property, final Field field) {
    super(registry, declarer, ID, property.nullable(), property.use());
    if (!isAssignable(field, Boolean.class, false, property.nullable(), property.use()) || field.getType() == boolean.class && (property.use() == Use.OPTIONAL || property.nullable()))
      throw new IllegalAnnotationException(property, field.getDeclaringClass().getName() + "." + field.getName() + ": @" + BooleanProperty.class.getSimpleName() + " can only be applied to fields of String type with use=\"required\" or nullable=false, or of primitive boolean type with use=\"required\" and nullable=false, or of Optional<Boolean> type with use=\"optional\" and nullable=true");
  }

  private BooleanModel(final Registry registry, final Declarer declarer, final BooleanElement element) {
    super(registry, declarer, ID, element.nullable(), null);
  }

  @Override
  Registry.Type type() {
    return registry.getType(Boolean.class);
  }

  @Override
  String elementName() {
    return "boolean";
  }

  @Override
  Class<? extends Annotation> propertyAnnotation() {
    return BooleanProperty.class;
  }

  @Override
  Class<? extends Annotation> elementAnnotation() {
    return BooleanElement.class;
  }
}