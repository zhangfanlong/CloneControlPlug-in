package Command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import Myplugin.CloneControl.SelectDirectoryForExistedClones;

public class LoadExistedClones extends AbstractHandler{

	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		//SelectDirectoryForExistedClones loadClones=new SelectDirectoryForExistedClones(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		//loadClones.open();

		return null;
	}

}
