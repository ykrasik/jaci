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
        output.message("str1=%s, str2=%s, i=%d, d=%s, flag=%s", str1, str2, i, d, flag);
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
* The command then just sends the parameter values to the output. The string sent to the output will be formatted with String.format().

Here is how this looks on a LibGdx CLI implementation:  
[[images/fullExample.PNG]]

1. `ls -r` - Print the contents of our hierarchy, recursing into sub-directories.
2. `cd example/simple` - Change working directory to to `example/simple`.
3. `simpleCommand a d 3 4.5 true` - Call _simpleCommand_ with these values.
4. Some invocations of _simpleCommand_ with invalid parameter values.
5. `simpleCommand a d -int 3 -double 4.5 -flag` - Call _simpleCommand_ with a mix of passing values by-position and by-name. `-int 3` passes the value 3 for the parameter named `int`. `-flag` is a shortcut only applicable to optional boolean parameters where just specifying the parameter name without a value afterwards will bind the boolean parameter to the inverse of it's default value.
6. `simpleCommand a -int 3 -double 4.5 -flag` - Call _simpleCommand_ without specifying the value of `str2`, it receives the default value of "default".
7. `simpleCommand a -int 3 -flag` - Call _simpleCommand_ without specifying the value of `str2` or `double`, they receive the default values of "default" and 3.5, respectively.
8. `simpleCommand a -int 3` - Call _simpleCommand_ with only values bound to mandatory parameters, all optional parameters receive default values.
9. `simpleCommand a 3` - A parse error, since the next parameter to parse a value according to it's position is `str2`.


# Full Documentation
See the [Wiki](https://github.com/ykrasik/jaci/wiki) for full documentation.

# Binaries
The binaries depend on your choice of UI-platform:
* LibGdx CLI:
```
<dependency>
    <groupId>com.github.ykrasik</groupId>
    <artifactId>jaci-libgdx-cli</artifactId>
    <version>0.1.0</version>
</dependency>
```

# Change log
See [Change Log](https://github.com/ykrasik/jaci/blob/master/CHANGELOG.md)

# License
Copyright 2014 Yevgeny Krasik.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.