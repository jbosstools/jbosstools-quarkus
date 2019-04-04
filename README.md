# quarkus-eclipse

> Quarkus Eclipse - Supersonic Subatomic Java in your Eclipse IDE

Quarkus Eclipse contains tooling to make it easier to work with the [Quarkus](https://quarkus.io) project in the [Eclipse](https://www.eclipse.org) IDE.

## Build Instructions

You will need to have Maven installed. You can build the project by issuing `mvn clean verify`. 

<img src="documentation/images/build.png" width="400"/>

When the build finishes you will find an installable Eclipse repository in the `repository/io.quarkus.eclipse.repository/target` folder. 

<img src="documentation/images/repository.png" width="400"/>

The name of the repository is `io.quarkus.eclipse.repository-x.y.z-SNAPSHOT.zip`

## Installation Instructions

Launch Eclipse to perform the installation of the freshly built repository.

<img src="documentation/images/install-new-software-menu.png" width="200"/>

Select the `Help->Install New Software...` menu to bring up the installation wizard.

<img src="documentation/images/install-wizard-add-repository.png" width="450"/>

Push the `Add...` button to bring up the `Add Repository` dialog.

<img src="documentation/images/add-repository-dialog-before.png" width="250"/>

Push the `Archive...`button and navigate to the zip file containing the repository.

<img src="documentation/images/add-repository-selection.png" width="400"/>

Push the `Open` button. The `Location` text field now contains the location of your archive. 

<img src="documentation/images/add-repository-dialog-after.png" width="250"/>

Push the `Add` button to close the dialog. 

<img src="documentation/images/install-wizard-selection.png" width="450"/>

The `Install` wizard now contains the `Quarkus` repository and feature. Push the `Select All` button and then `Next` to pass to the installation details page of the wizard.

<img src="documentation/images/install-wizard-details.png" width="450"/>

Push the `Next` button once again to pass to the license page of the wizard.

<img src="documentation/images/install-wizard-license.png" width="450"/>

Accept the license to enable the `Finish` button that you need to push subsequently. A dialog opens warning you that you are installing unsigned content.

<img src="documentation/images/security-warning.png" width="250"/>

Ignore the warning and push the `Install anyway` button. Eventually a new dialog window will open asking you to apply the changes by restarting Eclipse.

<img src="documentation/images/restart-now.png" width="250"/>

Finish the installation by selecting `Restart Now`.

