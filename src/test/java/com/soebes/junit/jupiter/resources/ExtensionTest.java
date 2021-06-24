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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Karl Heinz Marbaise
 */
@TestResources
class ExtensionTest {

  @Nested
  class PathTest {

    @Test
    void resource_path_sub(ResourcePath resource) throws IOException {
      Path rf = resource.get("sub/anton.txt");
      List<String> lines = Files.lines(rf).collect(Collectors.toList());
      assertThat(lines).containsExactly("Anton.txt in sub. Line 1", "Anton.txt in sub. Line 2");
    }

    @Test
    void resource_path_root(ResourcePath resource) throws IOException {
      Path rf = resource.get("anton.txt");
      List<String> lines = Files.lines(rf).collect(Collectors.toList());
      assertThat(lines).containsExactly("This is anton.txt");
    }

    @Test
    void resource_does_not_exist(ResourcePath resource) {
      assertThatExceptionOfType(ResourceNotFoundException.class)
          .isThrownBy(() -> resource.get("egon"))
          .withMessage("The resource 'egon' couldn't being found.");
    }

  }

  @Nested
  class FileTest {

    @Test
    void resource_file_in_sub(ResourceFile resource) throws IOException {
      File rf = resource.get("sub/file-in-sub.txt");

      try (BufferedReader reader = new BufferedReader(new FileReader(rf))) {
        String currentLine = reader.readLine();
        assertThat(currentLine).isEqualTo("File in Sub directory.");
      }
    }

    @Test
    void check_classloader(ResourceFile resource) {
      ClassLoader resourceClassLoader = resource.getClassLoader();
      ClassLoader currentClass = this.getClass().getClassLoader();

      assertThat(resourceClassLoader).isSameAs(currentClass);
    }

    @Test
    void resource_file_test_root(ResourceFile resource) throws IOException {
      File rf = resource.get("anton.txt");

      try (BufferedReader reader = new BufferedReader(new FileReader(rf))) {
        String currentLine = reader.readLine();
        assertThat(currentLine).isEqualTo("This is anton.txt");
      }
    }

    @Test
    void resource_file_test_sub(ResourceFile resource) throws IOException {
      File rf = resource.get("sub/anton.txt");

      try (BufferedReader reader = new BufferedReader(new FileReader(rf))) {
        String currentLine = reader.readLine();
        assertThat(currentLine).isEqualTo("Anton.txt in sub. Line 1");
      }
    }

    @Test
    void resource_does_not_exist(ResourceFile resource) {
      assertThatExceptionOfType(ResourceNotFoundException.class)
          .isThrownBy(() -> resource.get("egon"))
          .withMessage("The resource 'egon' couldn't being found.");
    }

  }
}
