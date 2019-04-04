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

## Getting Started

Now that you have succesfully installed the Quarkus plugins into your Eclipse workbench, it is time to use the available functionality.

### The Quarkus Perspective

There is a `Quarkus` perpective that brings the currently available functionality together.

<img src="documentation/images/open-perspective-menu.png" width="350"/>

Select the `Window->Perspective->Open Perspective->Other...` menu item to bring up the `Open Perspective` dialog.

<img src="documentation/images/open-perspective-dialog.png" width="200"/>

Select `Quarkus` and push the `Open` button to open the perspective.

<img src="documentation/images/quarkus-perspective.png" width="600"/>

As you can see there is a `Quarkus Extensions` view in this perspective, containing a list of all the available Quarkus extensions.

### Create Quarkus Project

The next step would be to create a Quarkus project in your workspace. 

<img src="documentation/images/file-new-other-menu.png" width="250"/>

Select the `File->New->Other...` menu item to bring up the `New` dialog.

<img src="documentation/images/file-new-other-dialog.png" width="250"/> 

In this dialog, select `Quarkus->Create New Quarkus Project` and push the `Next` button to launch the `New Quarkus Project` wizard.

<img src="documentation/images/new-quarkus-project-wizard.png" width="300"/>

Fill out the details for your project and push the `Finish` button to close the wizard and create your new Quarkus project.

<img src="documentation/images/new-quarkus-project-result.png" width="600"/>

Observe that in the `Quarkus Extensions` view, the checkboxes next to the already installed extensions are selected.
 

