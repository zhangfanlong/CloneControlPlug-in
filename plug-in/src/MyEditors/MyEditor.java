package MyEditors;

import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;


public class MyEditor extends CompilationUnitEditor {

	
	protected void createActions() {
		super.createActions();
		
		IAction copy = new CopyAction(getAction(ITextEditorActionConstants.COPY));
		copy.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_COPY);//"org.eclipse.ui.edit.copy" IJavaEditorActionDefinitionIds.COPY 
		setAction(ITextEditorActionConstants.COPY, copy);//�ڸ����Ķ�����ʶ�°�װ�����Ķ���

	    IAction paste = new PasteAction(getAction(ITextEditorActionConstants.PASTE));
		paste.setActionDefinitionId(IWorkbenchCommandConstants.EDIT_PASTE);//"org.eclipse.ui.edit.paste"
		setAction(ITextEditorActionConstants.PASTE, paste);

	}
	
	
}
