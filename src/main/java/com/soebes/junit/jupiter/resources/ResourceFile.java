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

import org.apiguardian.api.API;

import java.io.File;
import java.net.URL;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.1.0")
public class ResourceFile {

  private final ClassLoader classLoader;

  ResourceFile(ClassLoader classLoader) {
    this.classLoader = classLoader;
  }

  ClassLoader getClassLoader() {
    return classLoader;
  }

  /**
   * @param name The name of the resource you would like to get.
   * @return The {@link File} of the requested resource.
   * @throws ResourceNotFoundException in case of trying to access a resource which does not exist.
   */
  public File get(String name) {
    URL resource = this.classLoader.getResource(name);
    if (resource == null) {
      throw new ResourceNotFoundException(String.format("The resource '%s' couldn't being found.", name));
    }
    return new File(resource.getFile());
  }

}
