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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karl Heinz Marbaise
 */
@TestResources
class ExtensionResourcesTest {

  @Test
  @ResourceRead("sub/anton.txt")
  void resource_from_file_as_line_content(ResourceContentLines resource) {
    assertThat(resource.getContent()).containsExactly("Anton.txt in sub. Line 1", "Anton.txt in sub. Line 2");
  }

  @Test
  @ResourceRead("sub/anton.txt")
  void resource_from_file_as_string(ResourceContentString resource) {
    assertThat(resource.getContent()).isEqualTo("Anton.txt in sub. Line 1" + "\n" + "Anton.txt in sub. Line 2");
  }

  @Test
  void resource_with_annotated_parameters(@ResourceRead("sub/anton.txt") ResourceContentLines resource) {
    assertThat(resource.getContent()).containsExactly("Anton.txt in sub. Line 1", "Anton.txt in sub. Line 2");
  }

  @Test
  void resource_with_annotated_parameters(@ResourceRead("sub/anton.txt") String resource) {
    assertThat(resource).isEqualTo("Anton.txt in sub. Line 1" + "\n" + "Anton.txt in sub. Line 2");
  }

  @Test
  void resource_with_annotated_parameters(@ResourceRead("sub/anton.txt") List<String> resource) {
    assertThat(resource).containsExactly("Anton.txt in sub. Line 1", "Anton.txt in sub. Line 2");
  }

  @Test
  void resource_with_annotated_parameters(@ResourceRead("sub/anton.txt") Stream<String> resource) {
    assertThat(resource).containsExactly("Anton.txt in sub. Line 1", "Anton.txt in sub. Line 2");
  }

}
