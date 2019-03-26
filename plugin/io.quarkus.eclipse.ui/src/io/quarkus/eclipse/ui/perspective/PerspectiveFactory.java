package io.quarkus.eclipse.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import io.quarkus.eclipse.ui.view.ExtensionsView;

public class PerspectiveFactory implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		IFolderLayout explorerFolder= layout.createFolder("left", IPageLayout.LEFT, (float)0.25, editorArea); 
		explorerFolder.addView(IPageLayout.ID_PROJECT_EXPLORER);	
		IFolderLayout outputfolder= layout.createFolder("bottom", IPageLayout.BOTTOM, (float)0.75, editorArea); 
		outputfolder.addView(ExtensionsView.ID);			
		IFolderLayout outlineFolder = layout.createFolder("outlines", IPageLayout.RIGHT, 0.75f, editorArea);
		outlineFolder.addView(IPageLayout.ID_OUTLINE);		
		layout.setEditorAreaVisible(true);	
	}
	
}
