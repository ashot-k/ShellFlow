### Prerequisites
JavaFX 24


### Build
The application can be built using the following command:

`mvn clean package -P build-app-image`

An application installer (for windows) can be made by running: 

`mvn clean package -P build-windows-installer`

These will create a new build of the application inside `${project.basedir}/output`

### Dependencies
- atlantafx-base 
  - The PrimerDark and PrimerLight themes used throughout the application can be found here.
- richtextfx
  - Provides multiple classes and nodes with robust control of text styling.
- controlsfx
  - Contains a multitude of controls, in particular this project currently utilizes it for notifications and icons.
- jeditermfx-core jeditermfx-ui
  - Core components of the application which allow for terminal emulation through the Jetbrains JediTerm by adding compatibility with JavaFX.
- testfx-junit5 testfx-core
  - Testing utilities for JavaFX
