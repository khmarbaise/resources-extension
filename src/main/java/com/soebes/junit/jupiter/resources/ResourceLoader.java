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

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
class ResourceLoader {

  private final ClassLoader classLoader;
  private final String resourceName;
  private final Charset encoding;

  ResourceLoader(ClassLoader classLoader, String resourceName, String encoding) {
    this.classLoader = classLoader;
    this.resourceName = resourceName;
    this.encoding = Charset.forName(encoding);
  }

  List<String> asList() {
    URL resource = getResource(this.classLoader, this.resourceName);
    try {
      return Files.readAllLines(getResourcePath(resource), this.encoding);
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' could not be found.", this.resourceName), e);
    }
  }

  Stream<String> asStream() {
    URL resource = getResource(this.classLoader, this.resourceName);
    try {
      return Files.lines(getResourcePath(resource), this.encoding);
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' could not be found.", this.resourceName), e);
    }
  }

  String asString() {
    URL resource = getResource(this.classLoader, this.resourceName);
    try {
      return new String(Files.readAllBytes(getResourcePath(resource)));
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' could not be read.", this.resourceName), e);
    }
  }

  byte[] asBytes() {
    URL resource = getResource(this.classLoader, this.resourceName);
    try {
      return Files.readAllBytes(getResourcePath(resource));
    } catch (IOException e) {
      throw new ResourceNotFoundException(String.format("The resource '%s' could not be read.", this.resourceName), e);
    }
  }

  private Path getResourcePath(URL resource) {
    return Paths.get(resource.getFile());
  }

  private URL getResource(ClassLoader classLoader, String resourceName) {
    URL resource = classLoader.getResource(resourceName);
    if (resource == null) {
      throw new ResourceNotFoundException(String.format("The resource '%s' could not be found.", resourceName));
    }
    return resource;
  }


}
