package Command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import Myplugin.CloneControl.SelectDirectoryForLoadARFF;

public class LoadARFFFile extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		SelectDirectoryForLoadARFF loadARFF=new SelectDirectoryForLoadARFF(PlatformUI.getWorkbench().getDisplay().getActiveShell());
		loadARFF.open();
		
		return null;
	}

}
