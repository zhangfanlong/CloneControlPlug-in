package Command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import Myplugin.CloneControl.SelectDirectoryForLoadModel;

public class LoadModel extends AbstractHandler{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		SelectDirectoryForLoadModel loadModel=new SelectDirectoryForLoadModel(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		loadModel.open();

		return null;
	}

	
}
