package Myplugin.CloneControl;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;

public class EditorTracking {

	private static final DocumentPasteListener pasteListener = new DocumentPasteListener();
	private static boolean isWritedResults;
	
	public static void InitEditorTracking() {
		CollectActiveJavaEditors();
		RegisterJavaEditorListeners();
	}

	private static void CollectActiveJavaEditors() {
		IEditorPart[] editors = Activator.getInstanciatedEditors();
		
		System.out.println("Number of editors " + editors.length);
		for (int i = 0; i < editors.length; i++) {
			ITextEditor editor = (ITextEditor) editors[i].getAdapter(ITextEditor.class);
			if (editor != null) {
				ListeningToEditorDocument(editor);
			}
		}
	}

	private static void ListeningToEditorDocument(ITextEditor editor) {
		System.out.println("listening to Document!");
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());

		doc.removeDocumentListener(pasteListener);
		doc.addDocumentListener(pasteListener);

	}

	
	private static void RegisterJavaEditorListeners() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		workbench.addWindowListener(new WindowListener());
		IWorkbenchWindow[] windows = workbench.getWorkbenchWindows();
		System.out.println("Number of windows " + windows.length);

		for (int i = 0; i < windows.length; i++) {
			IWorkbenchWindow window = windows[i];
			window.addPageListener(new PageListener());
			IWorkbenchPage[] pages = window.getPages();
			System.out.println("Number of pages " + pages.length);
			for (int j = 0; j < pages.length; j++) {
				IWorkbenchPage page = pages[j];
				IEditorPart editorPart = page.getActiveEditor();
				if (editorPart != null) {
					ITextEditor textEditor = (ITextEditor) editorPart.getAdapter(ITextEditor.class);
					if (textEditor != null) {
						doEditor(textEditor);
					}
				}
				page.addPartListener(new PartListener());
			}
		}
	}

	private static void doEditor(ITextEditor te) {
		if (!(te instanceof JavaEditor)) {
			return;
		}
		ListeningToEditorDocument(te);
	}
	
	private static class WindowListener implements IWindowListener {
		public void windowOpened(IWorkbenchWindow window) {
			window.addPageListener(new EditorTracking.PageListener());
		}

		public void windowClosed(IWorkbenchWindow window) {	//Eclipse退出，将当前编辑系统的克隆写入硬盘
			if(!isWritedResults){
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>> Window Closed");
				if(CloneStore.clone2group == null || CloneStore.clone2group.size()==0 ){
					isWritedResults=true;
					return;
				} 
				try {
					CloneStore.WriteClones();
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				
				isWritedResults=true;
			}
			
		}

		public void windowActivated(IWorkbenchWindow window) {
			window.addPageListener(new EditorTracking.PageListener());
		}

		public void windowDeactivated(IWorkbenchWindow window) {
		}
	}

	private static class PageListener implements IPageListener {
		public void pageOpened(IWorkbenchPage page) {
			page.addPartListener(new EditorTracking.PartListener());
		}

		public void pageClosed(IWorkbenchPage page) {
		}

		public void pageActivated(IWorkbenchPage page) {
			page.addPartListener(new EditorTracking.PartListener());
		}
	}

	private static class PartListener implements IPartListener {
		public void partOpened(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
		}

		public void partActivated(IWorkbenchPart part) {
			ITextEditor te = (ITextEditor) part.getAdapter(ITextEditor.class);
			if (te != null) {
				EditorTracking.doEditor(te);
			}
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partDeactivated(IWorkbenchPart part) {
		}
	}
	
}
