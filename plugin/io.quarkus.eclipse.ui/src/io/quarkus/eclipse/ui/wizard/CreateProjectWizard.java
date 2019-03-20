package io.quarkus.eclipse.ui.wizard;

import java.util.HashMap;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import io.quarkus.eclipse.ui.action.CreateProjectAction;

public class CreateProjectWizard extends Wizard implements INewWizard {
	
	private CreateProjectWizardPage createProjectWizardPage = null;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {}

	@Override
	public boolean performFinish() {
		try {
			new CreateProjectAction(
					createProjectWizardPage.getGroupId(), 
					createProjectWizardPage.getArtefactId(), 
					createProjectWizardPage.getVersion(), 
					createProjectWizardPage.getName(),
					new HashMap<String, Object>())
				.run();	
			PlatformUI.getWorkbench().showPerspective(
					"org.jboss.tools.shamrock.ui.perspective", 
					PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			return true;
		} catch (WorkbenchException e) {
			return false;
		}
	}

	public void addPages() {
		createProjectWizardPage = new CreateProjectWizardPage();
	    addPage(createProjectWizardPage);
	}
	
}
