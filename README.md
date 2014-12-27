# What is Jerminal?
Jerminal is a Java library for creating embedded command-line interfaces.<br>
Most applications today require (or could benefit from) a debug console. This is what Jerminal was created for:
to remove the burden of creating debug consoles that can be easily embedded into the host application.<br>
<br>
Here are some of Jerminal's prominent features:

* Commands can be created via an annotations-based API.<br>
   ```
   public class AnnotationExample {
       @Command(description = "Does nothing, really.")
       public void testCommand(OutputPrinter outputPrinter) {
           outputPrinter.println("Hello, world!");
       }
   }
   ```
   <br>
   The above code creates a command called 'testCommand' that takes no parameters.
   When executed from the command line, the command will print "Hello, world!" to the element defined as it's output
   in the host application.<br>
   The 'outputPrinter' parameter is an optional parameter that the command should declare if it wishes to print anything.

* It is easy to integrate into your application. For example, with JavaFx:
    ```
    public class JavaFxExample extends Application {
        @Override
        public void start(Stage stage) {
            try {
                // Create a console and have it process the annotations of the above defined class.
                final ShellFileSystem fileSystem = new ShellFileSystem().processAnnotations(new AnnotationExample());
                final Parent console = new ConsoleBuilder(fileSystem).build();

                // Add a console toggler. The toggler will switch between the main scene and the console scene
                // when the default ctrl+` key combination is pressed.
                SceneToggler.register(stage, console);

                // Create a boring main scene.
                final Pane root = new Pane();
                root.getChildren().add(new Label("Nothing to see here"));
                final Scene scene = new Scene(root);

                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
    ```
    The above JavaFx code will create a main scene with the label "Nothing to see here", but will switch to the
    console scene when the key combination ctrl+` is pressed (and back to the main scene if pressed again).<br>
    The console will contain any commands that were defined in the AnnotationExample class.

* Commands can be grouped under directory hierarchies and navigated with the help of unix-style directory navigation commands. TODO: Example
* Command parameters can be mandatory, or optional with default values or flags.<br>
    ```
    @Command(description = "Parameters example")
    public void paramExample(OutputPrinter outputPrinter,
                             @IntParam(value = "mandatoryInt", description = "Mandatory int param") int intParam,
                             @StringParam(value = "optionalString", description = "Optional string param", optional = true, defaultValue = "default") String stringParam,
                             @FlagParam("flagParam") boolean flag) {
        outputPrinter.println("mandatoryInt=%d", intParam);
        outputPrinter.println("optionalString=%s", stringParam);
        outputPrinter.println("flagParam=%s", flag);
    }
    ```
    The above code declares a command called 'paramExample' that receives 3 parameters: int, string and boolean.<br>
    The int param is called 'mandatoryInt' and must be provided by the command line call.<br>
    The string param is called 'optionalString' and is optional - if it isn't provided by the command line call, it will
    have the default value 'default'.<br>
    The boolean param is called 'flagParam' and is a flag - a special type of optional parameter that will be 'true'
    if it is present on the command line call.<br>
    For example:<br>
    ```
    > paramExample 5 string flagParam
    mandatoryInt=5
    optionalString=string
    flagParam=true

    > paramExample 5
    mandatoryInt=5
    optionalString=default
    flagParam=false

    > paramExample
      	-> 	{mandatoryInt: int}	 <-
      		[optionalString: string]
      		[flagParam: flag]
      Parse Error: Mandatory parameter was not bound: 'mandatoryInt'
    ```

* Command parameters can be passed either by position or by name (scala-style parameter passing).<br>
    ```
    > paramExample 5 string flagParam
    mandatoryInt=5
    optionalString=string
    flagParam=true

    > paramExample flagParam optionalString=string mandatoryInt=5
    mandatoryInt=5
    optionalString=string
    flagParam=true

    > paramExample 5 flagParam optionalString=string
    mandatoryInt=5
    optionalString=string
    flagParam=true
    ```
    The above calls are all identical.<br>
    The 1st one uses positional parameter call - values are bound to parameters according to their declaration order.<br>
    The 2nd one uses named parameter call - values are bound to parameters according to a {name}={value} syntax.<br>
    The 3rd mixes both call types.

* Auto-complete suggestions are supported for everything (directory names, command names, parameter names, parameter values).
    ```
    > p {Auto-complete}
    > paramExample {Auto-complete}
    paramExample
    	-> 	{mandatoryInt: int}	 <-
    		[optionalString: string]
    		[flagParam: flag]
    Suggestions:
    	Parameter names: [flagParam, mandatoryInt, optionalString]

    > paramExample o {Auto-complete}
    > paramExample optionalString= {Auto-complete}
    paramExample
    		{mandatoryInt: int}
    	-> 	[optionalString: string]	 <-
    		[flagParam: flag]

    > paramExample optionalString=string m {Auto-complete}
    > paramExample optionalString=string mandatoryInt= {Auto-complete}
    paramExample
    	-> 	{mandatoryInt: int}	 <-
    		[optionalString: string] = string
    		[flagParam: flag]
    Parse Error: Cannot autoComplete int parameters 'mandatoryInt'!'

    > paramExample optionalString=string mandatoryInt=5 f {Auto-complete}
    > paramExample optionalString=string mandatoryInt=5 flagParam
    ```

## Terminology
This terminology may not be accurately used (with real world counterparts), but this is the terminology adopted by Jerminal.

* **Shell** - The software that parses and processes the command line and sends results to be displayed. The logic.
* **Command** - A piece of code that can be executed by the Shell.
* **File System** - A hierarchy of Commands. Commands can be grouped under directories, starting from a *root directory*.
* **Global Command** - A command that doesn't belong to any directory and can be executed from any directory.
* **Display Driver** - A component that can display the events generated by the Shell, the screen.
* **Terminal** - A Display Driver that displays everything as text.
* **Command Line Driver** - A component that can read from and write to the command line.
* **Console** - The complete package - a Shell, a Terminal and a Command Line Driver.

## More In Depth
Jerminal is separated into 2 basic components: backend and frontend. The backend is the core logic and the frontend is the
integration layer of the backend with a specific platform.

All that is required to create a console is:

1. Provide the debug command hierarchy that will act as the Shell's File System.
2. Hooks calls to Jerminal's API.

### 1. The Command Hierarchy
The basic unit of Jerminal is a command. In a file system structure, a command can be thought of as a file.

Commands can be grouped under directories. The Shell's File System contains a *root directory* which can contain
child commands or directories. Directories can be nested to unlimited depth.

The Shell operates as would be expected from a standard command line interface - it maintains a *current working directory*
and supports calling commands both relatively from the *current working directory* and with an absolute path from the *root*.

Jerminal comes with built-in control commands - *cd*, *ls* and *man*. These are implemented as Global Commands.<br>
Global Commands are commands that aren't bound to any parent directory. They can be executed from any *current working directory*.<br>
Adding custom global commands is supported for commands whose names don't clash with the built-in commands.

#### 1.1 Command Parameters
Commands may take 0 or more parameters of the types string, boolean, int, double, flag.<br>
Any parameter may be declared as optional, in which case a default value will be provided if it isn't supplied.<br>

* String parameters may either be constrained to a set of possible values, or be allowed any value.
  Furthermore, if constrained, the values may either be pre-determined or calculated at runtime through a value supplier.
  Values consisting of more then 1 word (separated by whitespace) can be surrounded with either single '' or double "" quotes.
* Boolean parameters only accept the values *true* or *false*.
* Numerical parameters (int and double) cannot be auto completed and don't (currently) support value constraints.
* Flags are special boolean parameters that are always optional and default to *false* if not provided.
  They can be set to *true* only by specifying their name. For example, the control command *ls* has a flag *-r* which makes
  it recurse into sub-directories. 'ls -r' is valid, but 'ls true' is not.

### 2. Display Driver and Command Line Driver
The DisplayDriver and CommandLineDriver are the 2 Service Provider Interfaces that must be implemented in order to
provide Jerminal integration with a platform.

The DisplayDriver is a higher-level interface which can display the different events generated by the Shell.
This can be implemented if a more graphically sophisticated UI is desired.<br>
There is also a lower-level interface, Terminal, which displays all the events generated by the Shell as text.
This should be enough for most cases.

The CommandLineDriver is a component that can read from and write to the command line. The command line is the text input
area.
TODO: Mention that this is not the only way to implement a console.

Jerminal currently supports libGdx and JavaFX as frontends.<br>
Any contributions will be certainly welcome :)

### 3. Hook calls to Jerminal
TBD.

# TODO
* Auto complete
* Full Annotations spec
* Programmatic creation