/* Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package com.xclinical.mdr.server.gwt;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.xclinical.mdr.client.reflect.Instantiable;
import com.xclinical.mdr.client.reflect.Reflection;
import com.xclinical.mdr.client.reflect.Singleton;

/**
 * Generates an implementation of an {@code ObjectFactory}. 
 *
 * For every class annotated with {@link Instantiable}, the generated
 * implementation will add a mapping from the target name of the
 * tagged class to the constructor.
 * 
 * See {@link Reflection} to see how this class can be used.
 *
 * @author michael@mictale.com
 *
 */
public class FactoryGenerator extends Generator {

	private static final String IMPL_POSTFIX = "Impl";
	
	public String generate(TreeLogger logger, GeneratorContext context, String typeName)
			throws UnableToCompleteException {

		TypeOracle typeOracle = context.getTypeOracle();

		JClassType clazz = typeOracle.findType(typeName);

		if (clazz == null) {
			logger.log(TreeLogger.ERROR, "The type '" + typeName + "' was not found");
			throw new UnableToCompleteException();
		}

		logger.log(TreeLogger.DEBUG, "Generating source for " + clazz.getQualifiedSourceName());

		String packageName = clazz.getPackage().getName();
		String simpleName = clazz.getSimpleSourceName() + IMPL_POSTFIX;
		PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);

		// If the printWriter is null here, this may indicate the class is already existing.
		// Don't know if it is a good idea to imply WE have generated it though.
		if (printWriter != null) {
			ClassSourceFileComposerFactory composer = new ClassSourceFileComposerFactory(packageName, simpleName);
			composer.addImport("java.util.HashMap");
			composer.addImplementedInterface("ObjectFactory");
			SourceWriter sourceWriter = composer.createSourceWriter(context, printWriter);

			sourceWriter.println("private static HashMap<String, ObjectCreator> factories;");
			sourceWriter.println("private static HashMap<String, Object> singletons;");
			sourceWriter.println("static {");
			sourceWriter.println("  factories = new HashMap<String, ObjectCreator>();");
			sourceWriter.println("  singletons = new HashMap<String, Object>();");

			// Add an implementation of ObjectCreator for every Instantiable.
			for (JClassType type : typeOracle.getTypes()) {
				Instantiable instantiable;
				if ((instantiable = type.getAnnotation(Instantiable.class)) != null) {
					String targetName = instantiable.value();
					if (targetName.length() == 0) targetName = type.getQualifiedSourceName();
					sourceWriter.println("factories.put(\"" + targetName + "\", new ObjectCreator() {");
					sourceWriter.println("	public Object newInstance() {");
					sourceWriter.println("		return new " + type.getQualifiedSourceName() + "();");
					sourceWriter.println("	}});");
					
					if (type.getAnnotation(Singleton.class) != null) {
						sourceWriter.println("singletons.put(\"" + targetName + "\", new " + type.getQualifiedSourceName() + "());");
					}				
				}				
			}

			// End static {
			sourceWriter.println("}");
			
			// Add an implementation for ObjectFactory.newInstance(String) that forwards
			// to the mappings.
			sourceWriter.println("public Object newInstance(String className) throws ReflectException {");
			sourceWriter.println("	ObjectCreator c = factories.get(className);");
			sourceWriter.println("  if (c == null) throw new ReflectException(className);");
			sourceWriter.println("  return c.newInstance();");
			sourceWriter.println("}");
			
			// Add an implementation for ObjectFactory.getInstance(String)
			sourceWriter.println("public Object getInstance(String className) throws ReflectException {");
			sourceWriter.println("	Object obj = singletons.get(className);");
			sourceWriter.println("	if (obj == null) {");			
			sourceWriter.println("	  ObjectCreator c = factories.get(className);");
			sourceWriter.println("    if (c == null) throw new ReflectException(className);");
			sourceWriter.println("    obj = c.newInstance();");
			sourceWriter.println("    singletons.put(className, obj);");
			sourceWriter.println("  }");
			sourceWriter.println("  return obj;");
			sourceWriter.println("}");
			
			sourceWriter.commit(logger);

			logger.log(TreeLogger.DEBUG, "Done generating source for " + clazz.getQualifiedSourceName());
		}

		return clazz.getQualifiedSourceName() + IMPL_POSTFIX;
	}
}
