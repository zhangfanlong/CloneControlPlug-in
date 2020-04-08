package MyEditors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import Global.VariationInformation;
import Myplugin.CloneControl.Activator;
import Myplugin.CloneControl.PositionTracking;
import PreProcess.GetJavaFiles;

public class CopyAction extends Action{

	private IAction oldAction;
	private static IDocument copiedDoc;
	private static IPath copiedPath;//绝对路径
	private static String projectName;
	public static IFile javaFile = null;
	public static boolean isClone;
	
	
	public CopyAction(IAction action){
		this.oldAction = action;
		setText(this.oldAction.getText());
	}
	
	public void runWithEvent(Event event) {
		System.out.println("copy action is run.");

		ITextEditor editor = Activator.getActiveEditor() ;
		IFileEditorInput fileEditorInput = (IFileEditorInput) editor.getEditorInput();
		this.javaFile = fileEditorInput.getFile();
		
		if(javaFile.getName().endsWith(".java")){
			ISelection selection = editor.getSelectionProvider().getSelection();
			if (!(selection instanceof ITextSelection)) {  
                return;  
            } 
			ITextSelection textSelection = (ITextSelection)selection;			
			//******************Test***********************************************
			/*System.out.println("StartLine: " + (textSelection.getStartLine()+1) + "\n" +
							   "EndLine: " + (textSelection.getEndLine()+1) + "\n" +
							   "Text: " + textSelection.getText() + "\n" +
							   "FileName: " + javaFile.getName());*/
			//*************************************************************************
			/*int start = textSelection.getOffset();
			int end = start + textSelection.getLength() - 1;*/
			
			if(textSelection.getEndLine() - textSelection.getStartLine() + 1 >= 5){ //判断是否是克隆代码
				System.out.println("行数判断是克隆!");				
				isClone = true;
				copiedDoc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
				copiedPath = javaFile.getLocation();
				String fullPath = javaFile.getFullPath().toString();
				projectName = fullPath.split("/")[1];
				if(Activator.currentVersionJavaFiles == null){
					GetJavaFiles getJavaFile = new GetJavaFiles();
					getJavaFile.GetJavaFilePath(CopyAction.getProjectAbsPath());
					//存放整个系统的各个版本的所有java文件名
					Activator.currentVersionJavaFiles.put(projectName, getJavaFile.getAllJavaFiles());
				}
				PositionTracking.trackingCopiedRange(textSelection.getOffset(), textSelection.getLength());//进行复制源位置跟踪
			} else { 
				isClone = false;
				System.out.println("行数判断不是克隆!");
			}

		}
		this.oldAction.runWithEvent(event);
	}

	public static IDocument getCopiedDoc() {
		return copiedDoc;
	}
	
	public static IPath getCopiedPath() {
		return copiedPath;
	}
	
	public static String getProjectName(){
		return projectName;
	}
	
	public static String getFileName(){//没有extension
		return copiedPath.toString().substring(copiedPath.toString().lastIndexOf("/")+1, copiedPath.toString().indexOf(".java"));
	}
	
	public static String getProjectAbsPath(){ //给到项目路径      如： //E:/JavaWork/workspace/CloneAnalyzer
		int classPathIndex = copiedPath.toString().indexOf(projectName);
		String projectAbsPath = copiedPath.toString().substring(0, classPathIndex) + projectName;
		return projectAbsPath;
	}
}
