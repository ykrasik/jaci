# Jaci Releases

### 0.4.0  
* Added support for enum parameters.
* Added support for nullable parameters and ability to pass null values.  
* Added support for commands in nested inner classes.  
* Added support for libGdx log output to be redirected to Jaci CLI.
* Solved the issue with toggle key being typed on CLI toggle.

### 0.3.0
GWT compatibility:
  
* Removed Lombok.
* Removed all code not compatible with GWT (String.format, regex).
* Abstracted over reflection code, to allow for a reflection provider (java / libGdx).
* Split jaci-libGdx into 2: jaci-libgdx-cli-java (Java reflection), jaci-libgdx-cli-gwt (libGdx reflection).

### 0.2.0
* Changed lombok & libGdx dependencies to be 'provided'.

#### 0.1.0
Initial release:  

* libGdx CLI.
* JavaFx CLI.