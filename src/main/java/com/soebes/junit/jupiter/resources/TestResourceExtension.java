package com.soebes.junit.jupiter.resources;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
class TestResourceExtension implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    Parameter parameter = parameterContext.getParameter();
    Class<?> type = parameter.getType();

    if (type.getTypeParameters().length > 1) {
      return false;
    }

    if (type.getTypeParameters().length == 1) {
      Type parameterizedType = parameter.getParameterizedType();
      Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
      if (actualTypeArguments[0].equals(String.class) && isListOrStream(type)) {
        return true;
      }
    }

    if (isResourceContentLine(type) || isResourceContentString(type) || isResourceFile(type) || isResourcePath(type)) {
      return true;
    }

    if (isString(type) && hasResourceReadAnnotation(parameter)) {
      return true;
    }

    return false;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    ClassLoader classLoader = extensionContext.getRequiredTestInstance().getClass().getClassLoader();

    Class<?> type = parameterContext.getParameter().getType();

    ResourceRead annotation = parameterContext.getParameter().getAnnotation(ResourceRead.class);

    if (isString(type) && type.getTypeParameters().length == 0 && hasResourceReadAnnotation(parameterContext.getParameter())) {
      return new ResourceLoader(classLoader, annotation.value(), annotation.encoding()).asString();
    }

    if (type.getTypeParameters().length == 1 && hasResourceReadAnnotation(parameterContext.getParameter())) {

      Type parameterizedType = parameterContext.getParameter().getParameterizedType();
      Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();

      if (actualTypeArguments[0].equals(String.class)) {
        if (isList(type)) {
          return new ResourceLoader(classLoader, annotation.value(), annotation.encoding()).asList();
        }
        if (isStream(type)) {
          return new ResourceLoader(classLoader, annotation.value(), annotation.encoding()).asStream();
        }
      }
    }

    if (isResourceContentLine(type) || isResourceContentString(type)) {
      // @ResourceFileRead("sub/anton.txt") must be present! either as annotation on the method
      // or as annotation on the method parameter.
      Method requiredTestMethod = extensionContext.getRequiredTestMethod();
      if (!requiredTestMethod.isAnnotationPresent(ResourceRead.class) && !hasResourceReadAnnotation(parameterContext.getParameter())) {
        // Fail!!!
        throw new IllegalStateException("@ResourceRead not given on method nor on method parameter.");
      }

      ResourceRead annotationRead;
      if (hasResourceReadAnnotation(parameterContext.getParameter())) {
        annotationRead = parameterContext.getParameter().getAnnotation(ResourceRead.class);
      } else {
        annotationRead = requiredTestMethod.getAnnotation(ResourceRead.class);
      }

      if (isResourceContentLine(type) || type.isAnnotationPresent(ResourceRead.class)) {
        List<String> strings = new ResourceLoader(classLoader, annotationRead.value(), annotationRead.encoding()).asList();
        return new ResourceContentLines(strings);
      } else {
        String strings = new ResourceLoader(classLoader, annotationRead.value(), annotationRead.encoding()).asString();
        return new ResourceContentString(strings);
      }
    }

    if (isResourceFile(type)) {
      return new ResourceFile(classLoader);
    }
    if (isResourcePath(type)) {
      return new ResourcePath(classLoader);
    }
    return null;
  }

  private boolean hasResourceReadAnnotation(Parameter parameter) {
    return parameter.isAnnotationPresent(ResourceRead.class);
  }

  private boolean isList(Class<?> type) {
    return type.equals(List.class);
  }

  private boolean isStream(Class<?> type) {
    return type.equals(Stream.class);
  }

  private boolean isListOrStream(Class<?> type) {
    return isList(type) || isStream(type);
  }

  private boolean isString(Type type) {
    return type.equals(String.class);
  }

  private boolean isResourceContentString(Class<?> type) {
    return type.equals(ResourceContentString.class);
  }

  private boolean isResourceContentLine(Class<?> type) {
    return type.equals(ResourceContentLines.class);
  }

  private boolean isResourcePath(Class<?> type) {
    return type.equals(ResourcePath.class);
  }

  private boolean isResourceFile(Class<?> type) {
    return type.equals(ResourceFile.class);
  }
}
