package io.quarkus.eclipse.cheatsheet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

import io.quarkus.eclipse.ui.wizard.CreateProjectWizard;

public class OpenCreateProjectWizardAction extends Action implements ICheatSheetAction {

	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		CreateProjectWizard createProjectWizard = new CreateProjectWizard();
		createProjectWizard.init(workbench, null);
		WizardDialog dialog = new WizardDialog(window.getShell(), createProjectWizard);
		dialog.create();
		dialog.open();
	}

}
