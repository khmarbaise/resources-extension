
I should try to test things like:
```java
  @Nested
  class ReadAllLines {
    @Test
    @ResourceFile("sub/anton.txt")
    void name(List<String> lines) {
      //?? 
    }
    @Test
    @ResourceFile("sub/anton.txt")
    void name(byte[] content) {

    }

    @ParameterizedTest
    @FileResourcesSource("first", "second", "third")
    void check_for_content(ResourceContentLines lines) {
      ....
    }

    @ParameterizedTest
    @FileResourcesSource(directory = "src/test/resources/content") // All files from
    void check_for_content(ResourceContentLines lines) {
      ....
    }
  }
```