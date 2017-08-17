package MyEditors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Event;

public class PasteAction extends Action{
	private IAction oldAction;
	public static int pasted = 0;
	
	public PasteAction(IAction action){
		this.oldAction=action;
		setText(oldAction.getText());
	}

	public void runWithEvent(Event event) {
		System.out.println("paste action is run.");
		
		pasted = 1;
	    this.oldAction.runWithEvent(event);
	    pasted = 0;
		
	}
	
	
}
