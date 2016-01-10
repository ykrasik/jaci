# Jaci Releases

### 0.3.0
GWT compatibility:
* Removed Lombok
* Removed all code not compatible with GWT (String.format, regex)
* Abstracted over reflection code, to allow for a reflection provider (java / libGdx)
* Split jaci-libGdx into 2: jaci-libgdx-cli-java (Java reflection), jaci-libgdx-cli-gwt (libGdx reflection)

### 0.2.0
* Changed lombok & libGdx dependencies to be 'provided'

#### 0.1.0
Initial release.  

* libGdx CLI
* JavaFx CLI