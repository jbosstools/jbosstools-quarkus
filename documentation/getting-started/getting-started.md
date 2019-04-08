# Getting Started Guide

To follow the getting started guide you will need to have the Quarkus plugins installed in your Eclipse workbench. You can build an installable repository by following the [build instructions](../building/build.md). After building, use the [installation guide](../installation/install.md) to perform the installation.

## The Quarkus Perspective

There is a `Quarkus` perpective that brings the currently available functionality together.

<img src="images/open-perspective-menu.png" width="350"/>

Select the `Window->Perspective->Open Perspective->Other...` menu item to bring up the `Open Perspective` dialog.

<img src="open-perspective-dialog.png" width="200"/>

Select `Quarkus` and push the `Open` button to open the perspective.

<img src="images/quarkus-perspective.png" width="600"/>

As you can see there is a `Quarkus Extensions` view in this perspective, containing a list of all the available Quarkus extensions.

## Create Quarkus Project

The next step would be to create a Quarkus project in your workspace. 

<img src="images/file-new-other-menu.png" width="250"/>

Select the `File->New->Other...` menu item to bring up the `New` dialog.

<img src="images/file-new-other-dialog.png" width="250"/> 

In this dialog, select `Quarkus->Create New Quarkus Project` and push the `Next` button to launch the `New Quarkus Project` wizard.

<img src="images/new-quarkus-project-wizard.png" width="300"/>

Fill out the details for your project and push the `Finish` button to close the wizard and create your new Quarkus project.

<img src="images/new-quarkus-project-result.png" width="600"/>

Observe that in the `Quarkus Extensions` view, the checkboxes next to the already installed extensions are selected.
 
## Install New Extension

You can add a new Quarkus extension to your project from teh `Quarkus Extensions` view.

<img src="images/install-quarkus-extension-popup.png" width="450"/>

Simply select the extension you want to add and bring up the context menu by right-clicking. Then push the `Install Extension` menu item.

<img src="images/install-quarkus-extension-result.png" width="600"/>

Observe that the newly installed extension is now checked in the `Quarkus Extensions` view and that a new dependency has been added to the `pom.xml` file.