package io.quarkus.eclipse.cheatsheet;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;

public class ShowQuarkusPerspectiveAction extends Action implements ICheatSheetAction {

	@Override
	public void run(String[] params, ICheatSheetManager manager) {
		try {
			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
			workbench.showPerspective("io.quarkus.eclipse.ui.perspective", window);
		} catch (WorkbenchException e) {
			// should not happen
			e.printStackTrace();
		}
	}

}
