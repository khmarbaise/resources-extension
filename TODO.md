
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
    void name(Stream<String> lines) {
    //?? 
    }

    @Test
    void name(@ResourceRead("sub/anton.txt") byte[] resource) {
    assertThat(resource).isEqualTo(new byte[]{0x10, 0x20});
    }
    

    @Test
    @ResourceFile("sub/anton.txt")
    void name(byte[] content) {

    }

    @ParameterizedTest
    @ResourcesSource("first", "second", "third")
    void check_for_content(ResourceContentLines lines) {
      ....
    }

    @ParameterizedTest
    @ResourcesSource(directory = "src/test/resources/content") // All files from
    void check_for_content(ResourceContentLines lines) {
      ....
    }
    
    
  }
```