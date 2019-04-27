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

package org.openjax.jsonx;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.openjax.ext.jci.CompilationException;
import org.openjax.ext.jci.InMemoryCompiler;
import org.openjax.ext.json.JSON;
import org.openjax.ext.json.JsonReader;
import org.openjax.jsonx.schema_0_2_2.xL4gluGCXYYJc;
import org.openjax.ext.lang.PackageNotFoundException;
import org.openjax.ext.test.AssertXml;
import org.openjax.ext.util.Classes;
import org.openjax.ext.xml.api.ValidationException;
import org.openjax.ext.xml.api.XmlElement;
import org.openjax.ext.xml.sax.Validator;
import org.openjax.xsb.runtime.Bindings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class SchemaTest {
  private static final Logger logger = LoggerFactory.getLogger(SchemaTest.class);
  private static final URL schemaXsd = ClassLoader.getSystemClassLoader().getResource("schema.xsd");
  private static final File generatedSourcesDir = new File("target/generated-test-sources/jsonx");
  private static final File generatedResourcesDir = new File("target/generated-test-resources");
  private static final File compiledClassesDir = new File("target/test-classes");
  private static final List<Settings> settings = new ArrayList<>();

  static {
    generatedSourcesDir.mkdirs();
    generatedResourcesDir.mkdirs();
    try {
      Files.walk(generatedSourcesDir.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).filter(f -> !f.equals(generatedSourcesDir)).forEach(File::delete);
    }
    catch (final IOException e) {
      throw new ExceptionInInitializerError(e);
    }

    for (int i = 0; i < 10; ++i)
      settings.add(new Settings(i));

    settings.add(new Settings(Integer.MAX_VALUE));
  }

  private static xL4gluGCXYYJc.Schema newControlBinding(final String fileName) throws IOException, ValidationException {
    try (final InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(fileName)) {
      return (xL4gluGCXYYJc.Schema)Bindings.parse(in);
    }
  }

  private static XmlElement toXml(final SchemaElement schema, final Settings settings) {
    final XmlElement xml = schema.toXml(settings);
    xml.getAttributes().put("xsi:schemaLocation", "http://jsonx.openjax.org/schema-0.2.2.xsd " + schemaXsd);
    return xml;
  }

  private static SchemaElement testParseSchema(final xL4gluGCXYYJc.Schema controlBinding, final String prefix) throws IOException, SAXException {
    logger.info("  Parse XML...");
    logger.info("    a) XML(1) -> Schema");
    final SchemaElement controlSchema = new SchemaElement(controlBinding, prefix);
    logger.info("    b) Schema -> XML(2)");
    final String xml = toXml(controlSchema, Settings.DEFAULT).toString();
    final xL4gluGCXYYJc.Schema testBinding = (xL4gluGCXYYJc.Schema)Bindings.parse(xml);
    logger.info("    c) XML(1) == XML(2)");
    AssertXml.compare(controlBinding.toDOM(), testBinding.toDOM()).assertEqual(true);
    return controlSchema;
  }

  private static void writeFile(final String fileName, final String data) throws IOException {
    try (final OutputStreamWriter out = new FileWriter(new File(generatedResourcesDir, fileName))) {
      out.write("<!--\n");
      out.write("  Copyright (c) 2017 OpenJAX\n\n");
      out.write("  Permission is hereby granted, free of charge, to any person obtaining a copy\n");
      out.write("  of this software and associated documentation files (the \"Software\"), to deal\n");
      out.write("  in the Software without restriction, including without limitation the rights\n");
      out.write("  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n");
      out.write("  copies of the Software, and to permit persons to whom the Software is\n");
      out.write("  furnished to do so, subject to the following conditions:\n\n");
      out.write("  The above copyright notice and this permission notice shall be included in\n");
      out.write("  all copies or substantial portions of the Software.\n\n");
      out.write("  You should have received a copy of The MIT License (MIT) along with this\n");
      out.write("  program. If not, see <http://opensource.org/licenses/MIT/>.\n");
      out.write("-->\n");
      out.write(data);
    }
  }

  // This is necessary for jdk1.8, and is replaced with ClassLoader#getDeclaredPackage() in jdk9
  private static Package getPackage(final ClassLoader classLoader, final String packageName) {
    try {
      final Method method = Classes.getDeclaredMethodDeep(classLoader.getClass(), "getPackage", String.class);
      method.setAccessible(true);
      return (Package)method.invoke(classLoader, packageName);
    }
    catch (final IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  private static SchemaElement newSchema(final ClassLoader classLoader, final String packageName) throws IOException, PackageNotFoundException {
    return new SchemaElement(getPackage(classLoader, packageName), classLoader, c -> c.getClassLoader() == classLoader);
  }

  private static void assertSources(final Map<String,String> expected, final Map<String,String> actual) {
    for (final Map.Entry<String,String> entry : expected.entrySet())
      assertEquals(entry.getValue(), actual.get(entry.getKey()));

    try {
      assertEquals(expected.size(), actual.size());
    }
    catch (final AssertionError e) {
      if (expected.size() < actual.size()) {
        actual.keySet().removeAll(expected.keySet());
        logger.error(actual.toString());
      }
      else {
        expected.keySet().removeAll(actual.keySet());
        logger.error(expected.toString());
      }

      throw e;
    }
  }

  public static void test(final String fileName, final String packageName) throws ClassNotFoundException, CompilationException, DecodeException, IOException, PackageNotFoundException, SAXException {
    final String prefix = packageName + ".";

    logger.info(fileName + "...");
    final xL4gluGCXYYJc.Schema controlBinding = newControlBinding(fileName);
    testJson(controlBinding, prefix);

    logger.info("  4) Schema -> Java(1)");
    final SchemaElement controlSchema = testParseSchema(controlBinding, prefix);
    final Map<String,String> test1Sources = controlSchema.toSource(generatedSourcesDir);
    final InMemoryCompiler compiler = new InMemoryCompiler();
    for (final Map.Entry<String,String> entry : test1Sources.entrySet())
      compiler.addSource(entry.getValue());

    logger.info("  5) -- Java(1) Compile --");
    final ClassLoader classLoader = compiler.compile(compiledClassesDir, "-g");

    logger.info("  6) Java(1) -> Schema");
    final SchemaElement test1Schema = newSchema(classLoader, packageName);
    final String xml = toXml(test1Schema, Settings.DEFAULT).toString();
    logger.info("  7) Validate XML");
    writeFile("out-" + fileName, xml);
    try {
      Validator.validate(xml, false);
    }
    catch (final SAXException e) {
      logger.error(xml);
      throw e;
    }

    final SchemaElement test2Schema = testParseSchema((xL4gluGCXYYJc.Schema)Bindings.parse(xml), prefix);
    logger.info("  8) Schema -> Java(2)");
    final Map<String,String> test2Sources = test2Schema.toSource();
    logger.info("  9) Java(1) == Java(2)");
    assertSources(test1Sources, test2Sources);

    testSettings(fileName, packageName, test1Sources);
  }

  private static void testSettings(final String fileName, final String packageName, final Map<String,String> originalSources) throws ClassNotFoundException, CompilationException, DecodeException, IOException, PackageNotFoundException, ValidationException {
    for (final Settings settings : SchemaTest.settings) {
      final String prefix = packageName + ".";

      logger.info("   testSettings(\"" + fileName + "\", new Settings(" + settings.getTemplateThreshold() + "))");
      final xL4gluGCXYYJc.Schema controlBinding = newControlBinding(fileName);
      final SchemaElement controlSchema = new SchemaElement(controlBinding, prefix);
      writeFile("a" + settings.getTemplateThreshold() + fileName, toXml(controlSchema, settings).toString());
      final Map<String,String> test1Sources = controlSchema.toSource(generatedSourcesDir);
      final InMemoryCompiler compiler = new InMemoryCompiler();
      for (final Map.Entry<String,String> entry : test1Sources.entrySet())
        compiler.addSource(entry.getValue());

      assertSources(originalSources, test1Sources);

      final ClassLoader classLoader = compiler.compile();
      final SchemaElement test1Schema = newSchema(classLoader, packageName);
      final String schema = toXml(test1Schema, settings).toString();
      writeFile("b" + settings.getTemplateThreshold() + fileName, schema);
      final SchemaElement test2Schema = new SchemaElement((xL4gluGCXYYJc.Schema)Bindings.parse(schema), prefix);
      final Map<String,String> test2Sources = test2Schema.toSource();
      assertSources(test1Sources, test2Sources);

      testJson(controlBinding, prefix);
    }
  }

  private static void testJson(final xL4gluGCXYYJc.Schema controlBinding, final String prefix) throws DecodeException, IOException, ValidationException {
    final SchemaElement controlSchema = new SchemaElement(controlBinding, prefix);
    logger.info("     testJson...");
    logger.info("       a) Schema -> JSON");
    final String json = JSON.toString(controlSchema.toJson());
    logger.info("       b) JSON -> Schema");
    final SchemaElement schema;
    try (final JsonReader reader = new JsonReader(new StringReader(json))) {
      schema = new SchemaElement(JxDecoder.parseObject(schema.Schema.class, reader), prefix);
    }

    logger.info("       c) Schema -> XML(3)");
    final String jsonXml = toXml(schema, Settings.DEFAULT).toString();
    final xL4gluGCXYYJc.Schema jsonBinding = (xL4gluGCXYYJc.Schema)Bindings.parse(jsonXml);
    AssertXml.compare(controlBinding.toDOM(), jsonBinding.toDOM()).assertEqual(true);
  }

  @Test
  public void testArray() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("array.jsdx", "org.openjax.jsonx");
  }

  @Test
  public void testDataType() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("datatype.jsdx", "org.openjax.jsonx.datatype");
  }

  @Test
  public void testStructure() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("structure.jsdx", "org.openjax.jsonx.structure");
  }

  @Test
  public void testTemplate() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("template.jsdx", "org.openjax.jsonx");
  }

  @Test
  public void testReference() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("reference.jsdx", "org.openjax.jsonx.reference");
  }

  @Test
  public void testReserved() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("reserved.jsdx", "org.openjax.jsonx.reserved");
  }

  @Test
  public void testComplete() throws ClassNotFoundException, CompilationException, DecodeException, IOException, MalformedURLException, PackageNotFoundException, SAXException {
    test("complete.jsdx", "org.openjax.jsonx.complete");
  }
}