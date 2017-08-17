package Command;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;

import Myplugin.CloneControl.SelectDirectoryForTrainPre;

public class TrainPredictor extends AbstractHandler{
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SelectDirectoryForTrainPre selDir=new SelectDirectoryForTrainPre(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		selDir.open();
		
		return null;
	}
}
