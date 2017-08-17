package Command;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.PlatformUI;

import Myplugin.CloneControl.CloneStore;

public class ShowClones extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CloneStore.OutputClones();

		//MessageDialog.openInformation (PlatformUI.getWorkbench().getDisplay().getActiveShell(), "CloneReuse", "Clone Resue Test");
		
		return null;
	}

	
}
