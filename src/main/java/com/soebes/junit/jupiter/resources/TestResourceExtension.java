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

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Karl Heinz Marbaise
 */
class TestResourceExtension implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    Class<?> type = parameterContext.getParameter().getType();

    //Need to check existence of annotation @ResourceFileRead
    if (type.equals(ResourceContentLines.class) || type.equals(ResourceContentString.class)) {
      return true;
    }

    if (type.equals(ResourceFile.class)) {
      return true;
    }
    if (type.equals(ResourcePath.class)) {
      return true;
    }

    return false;
  }

  private List<String> readResource(ClassLoader classLoader, String resourceName) {
    URL resource = classLoader.getResource(resourceName);
    if (resource == null) {
      throw new ResourceNotFoundException(String.format("The resource '%s' couldn't being found.", resourceName));
    }
    try {
      return Files.readAllLines(Paths.get(resource.getFile()));
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' cound not being read.", resourceName), e);
    }

  }

  private String readResourceAsString(ClassLoader classLoader, String resourceName) {
    URL resource = classLoader.getResource(resourceName);
    if (resource == null) {
      throw new ResourceNotFoundException(String.format("The resource '%s' couldn't being found.", resourceName));
    }
    try {
      return new String(Files.readAllBytes(Paths.get(resource.getFile())));
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' cound not being read.", resourceName), e);
    }

  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    ClassLoader classLoader = extensionContext.getRequiredTestInstance().getClass().getClassLoader();

    Class<?> type = parameterContext.getParameter().getType();
    if (type.equals(ResourceContentLines.class) || type.equals(ResourceContentString.class)) {
      Method requiredTestMethod = extensionContext.getRequiredTestMethod();
      // @ResourceFileRead("sub/anton.txt") must be present! either as annotation on the method
      // or as annotation on the method parameter.
      if (!requiredTestMethod.isAnnotationPresent(ResourceRead.class) && !parameterContext.getParameter().isAnnotationPresent(ResourceRead.class)) {
        // Fail!!!
        throw new IllegalStateException("@ResourceRead not given on method nor on method parameter.");
      }

      ResourceRead annotation;
      if (parameterContext.getParameter().isAnnotationPresent(ResourceRead.class)) {
        annotation = parameterContext.getParameter().getAnnotation(ResourceRead.class);
      } else {
        annotation = requiredTestMethod.getAnnotation(ResourceRead.class);
      }

      if (type.equals(ResourceContentLines.class) || type.isAnnotationPresent(ResourceRead.class)) {
        List<String> strings = readResource(classLoader, annotation.value());
        return new ResourceContentLines(strings);
      } else {
        String strings = readResourceAsString(classLoader, annotation.value());
        return new ResourceContentString(strings);
      }
    }

    if (type.equals(ResourceFile.class)) {
      return new ResourceFile(classLoader);
    }
    if (type.equals(ResourcePath.class)) {
      return new ResourcePath(classLoader);
    }
    return null;
  }
}
