<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# JUnit Jupiter Resources Extension (resources-extension)

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![Build Status](https://cloud.drone.io/api/badges/khmarbaise/resources-extension/status.svg)](https://cloud.drone.io/khmarbaise/resources-extension)
[![Issues](https://img.shields.io/github/issues/khmarbaise/resources-extension)](https://github.com/khmarbaise/resources-extension/issues)
[![Issues Closed](https://img.shields.io/github/issues-closed/khmarbaise/resources-extension)](https://github.com/khmarbaise/resources-extension/issues?q=is%3Aissue+is%3Aclosed)

# State

Prototype

# General Overview

If you are writing test often it happens that you need to read a file
from the `src/test/resources` directory (or to be more accurate from `target/test-classes`).
The result is simply always the same code:

```java
class XYZTest {

  @Test
  void first_test() {
    InputStream resourceAsStream = this.getClass()
        .getClassLoader().getResourceAsStream("anton.txt");
    InputStreamReader streamReader =
        new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);
    BufferedReader reader = new BufferedReader(streamReader);

    List<String> result = reader.lines().collect(Collectors.toList());
    ....do what needed...
  }
}
```
or something similar.

If you do that multiple times you will come up with a utility class and use that.
Of course, it's easy to write but every time in each project? A bit cumbersome isn't it?

There is an easier way just use this extension like the following:

```java
import com.soebes.junit.jupiter.resources.TestResources;

@TestResources
class XYZTest {

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
```
# Quick Start

## The General Requirements

* JDK8+
* Apache Maven 3.1.0 or above. (I recommend to use Maven 3.8.4)

## The Maven Configuration

```xml
<project..>
   ...
  <dependencies>
    ..
    <dependency>
      <groupId>com.soebes.junit.jupiter.extensions.resources</groupId>
      <artifactId>resources-extension</artifactId>
      <version>0.2.0</version>
      <scope>test</scope>
    </dependency>
  </dependencies>
  ..
</project..>
```
You have to have added junit jupiter dependencies as well.

### Availability

The dependency is available via Central Repository.

### Building 

If you like to build the project from source yourself you need the following:

* JDK 8+
* Apache Maven 3.6.3

The whole project can be built via:
```bash
mvn clean verify
```

[license]: https://www.apache.org/licenses/LICENSE-2.0