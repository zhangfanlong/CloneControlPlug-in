package Myplugin.CloneControl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Position;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import PreProcess.XML;

public class SelectDirectoryForExistedClones extends Dialog {
	
	protected Shell shell;
	private Text clones;
	private FileDialog fDlg;
	
	public SelectDirectoryForExistedClones (Shell parentShell) {
		super(parentShell);	
		shell = parentShell;
	}
	
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Load Existed Clones File"); // 设置Dialog的标头
		getShell().setSize(450, 206);
		parent.setBounds(10, 10, 424, 158);
		
		Label lblResultsDirectory = new Label(parent, SWT.NONE);
		lblResultsDirectory.setBounds(10, 10, 160, 17);
		lblResultsDirectory.setText("Existed Clone File Path:");

		clones = new Text(parent, SWT.BORDER);
		clones.setBounds(10, 63, 318, 23);

		Button btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setBounds(334, 63, 80, 27);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fDlg = new FileDialog(shell);
				fDlg.setText("Please Select Clone File Path!");
				String [] extensions = {"*.xml"};
				fDlg.setFilterExtensions(extensions);
				clones.setText(fDlg.open());
			}
		});

		Button btnLoad = new Button(parent, SWT.NONE);
		btnLoad.setBounds(176, 121, 80, 27);
		btnLoad.setText("Load");
		btnLoad.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(clones.getText()==""){
					MessageDialog.openError(shell, "ERROR", "Please Select Clone File Path!");
					return;
				}
				Document doc = XML.Load(clones.getText());
				String proName = XML.GetAttriValue(doc, "classinfo", 0, "project");
				IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(proName);//获得项目句柄
				String proPath = project.getLocation().toOSString(); //项目绝对路径
				
				NodeList classNodes = doc.getElementsByTagName("class");
				for(int i=0;i<classNodes.getLength();i++){
					Node cn = classNodes.item(i);
					NodeList sourceList = cn.getChildNodes();
					for(int j=0;j<sourceList.getLength();j++){
						Node s = sourceList.item(j);
						if(s.getNodeName()=="source"){
							NamedNodeMap attributes = s.getAttributes();
							int startLine=0,endLine=0;
							String fileName="";
							for (int k = 0; k < attributes.getLength(); i++) {
								if (attributes.item(k).getNodeName() == "startline") {
									startLine = Integer.parseInt(attributes.item(k).getNodeValue());
								}
								else if (attributes.item(k).getNodeName() == "endline") {
									endLine = Integer.parseInt(attributes.item(k).getNodeValue());
								}
								else if (attributes.item(k).getNodeName() == "file") {
									fileName = attributes.item(k).getNodeValue();
								}
							}
							String absFilePath = proPath + fileName.substring(fileName.indexOf("/")); //文件绝对路径
							
							//获得 IDocument 对象idoc，然后通过IDocument将 startLine和endLine 转成 offset，通过offset 创建Position对象 pos，然后下面两行去掉注释
							
							//CloneStore.Clone frag = new CloneStore.Clone(file,pos,DocumentPasteListener.cloneID);
							//PositionTracking.trackingClonePosition(idoc, pos, String.valueOf(DocumentPasteListener.cloneID));
						}
					}
					++DocumentPasteListener.cloneID;
				}
				
				
				
				MessageDialog.openInformation(shell, null, "Load Succeed! Existed Clones will be traced");
				getShell().dispose();
				
			}
		});
		
		
		return super.createDialogArea(parent);
	}
}
