# What is Jaci?
Java Annotation Command Interface.  
Jaci generates User Interfaces (CLI/GUI) from annotated methods, which can then be embedded into your application.

# What is Jaci for?
Jaci is geared towards 2 things:

1. Creating debug consoles - A debug console doesn't need to look pretty, it needs to be convenient to work with (at runtime) and convenient to add commands to (at dev time).
2. Creating quick, throw-away prototype code - When you just need to quickly test some logic (or learn a new API), just write the logic, add annotations and let Jaci generate the UI.

# Example
```
@CommandPath("example/simple")
public class Example {
    private CommandOutput output;

    @Command(description = "Does nothing, really.")
    public void simpleCommand(@StringParam(value = "str1", accepts = {"a", "b", "c"}) String str1,
                              @StringParam(value = "str2", acceptsSupplier = "strSupplier", optional = true, defaultValue = "default") String str2,
                              @IntParam("int") int i,
                              @DoubleParam(value = "double", optional = true, defaultValueSupplier = "doubleSupplier") double d,
                              @BoolParam(value = "flag", optional = true) boolean flag) {
        output.message("str1=" + str1 + ", str2=" + str2 + ", i=" + i + ", d=" + d + ", flag=" + flag);
    }
}

private String[] strSupplier() {
    return new String[] {"d", "e", "f"};
}
    
private double doubleSupplier() {
    return 3.5;
}
```

Let's explain what's going on here:
* Example defines a directory that has 1 command and is mounted at: 'example/simple'.
* There is a private, non-initialized CommandOutput parameter. CommandOutput is the way by which commands send output to the host application. When processing a class, Jaci will inject any fields of the type CommandOutput with an implementing instance.
* The single command that is defined in the class, 'simpleCommand' has 5 parameters:
  1. A mandatory string called `str1` that only accepts the values {`a`, `b`, `c`}.
  2. An optional string called `str2` that only accepts the values {`d`, `e`, `f`} (supplied by a supplier method). If the parameter doesn't receive a value, it will have a value of `default`.
  3. A mandatory int called `int`.
  4. An optional double called `double`. If the parameter doesn't receive a value, it will have a value of `3.5` (supplied by a supplier method).
  5. An optional boolean called `flag`.
* The command then just sends the parameter values to the output (The output instance itself will be injected by the library
  when it processes the class: [Wiki](https://github.com/ykrasik/jaci/wiki/Annotation-API))  

Here is how this looks on a LibGdx CLI implementation:  
![alt text](https://github.com/ykrasik/jaci/wiki/images/fullExample.PNG)

# Full Documentation
See the [Wiki](https://github.com/ykrasik/jaci/wiki) for full documentation.

# Binaries
The binaries depend on your choice of UI-platform:

### LibGdx CLI:
Unfortunately, at present (libGdx 1.8), the libGdx reflection API does not support parameter annotations. 
Unsupported parameter annotations mean that it is impossible to give names to command parameters, mark them as optional with default values (all parameters will be considered mandatory) or constrain their accepted values. 
Since parameter annotations are a big part of the library, and this only potentially affects GWT, it was decided to create 2 jaci-libgdx distributions:

#### jaci-libgdx-cli-java
Uses the Java reflection API. Supports full reflection capabilities (including parameter annotations), but does not compile with GWT. 
Use this if you want full support for Jaci's capabilities and aren't using GWT backend.
```
<dependency>
    <groupId>com.github.ykrasik</groupId>
    <artifactId>jaci-libgdx-cli-java</artifactId>
    <version>0.3.2</version>
</dependency>
```

#### jaci-libgdx-cli-gwt
Uses the libGdx reflection API. Does not support parameter annotations (they will be ignored), but compiles with GWT. 
Use this if you use GWT as a backend.
```
<dependency>
    <groupId>com.github.ykrasik</groupId>
    <artifactId>jaci-libgdx-cli-gwt</artifactId>
    <version>0.3.2</version>
</dependency>
```

In your .gwt.xml file, add the following:
```
<inherits name='com.github.ykrasik.jaci' />
```

All jars come with source code packaged in the jar, so no other dependencies are required.

# Change log
See [Change Log](https://github.com/ykrasik/jaci/blob/master/CHANGELOG.md)

# License
Copyright 2014 Yevgeny Krasik.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.