package MyFrames;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import CloneRepresentation.CloneGroup;
import CloneRepresentation.ClonesInVersion;
import CloneRepresentation.GroupMapping;
import Global.ConstantVariation;
import Global.Path;
import Global.VariationInformation;
import PreProcess.GetJavaFiles;
import PreProcess.ReadCloneResults;

public class SelectDirectory extends Dialog {

	protected Object result;
	protected Shell shell;
	private Text sourceCodes;
	private Text cloneResults;
	private DirectoryDialog dlg;
	

	public static String granularity = "blocks";//克隆粒度
	private Text text;
	
	public SelectDirectory(Shell parent, int style) {
		super(parent, style);
		setText("Import Dialog");		
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setSize(450, 206);
		shell.setText(getText());
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 10, 424, 158);

		Label lblResultsDirectory=new Label(composite, SWT.NONE);
		lblResultsDirectory.setBounds(10, 10, 137, 17);
		lblResultsDirectory.setText("SourceCodes Directory:");
		
		sourceCodes = new Text(composite, SWT.BORDER);
		sourceCodes.setBounds(149, 7, 179, 23);
		
		Button btnBrowse = new Button(composite, SWT.NONE);
		btnBrowse.setBounds(334, 5, 80, 27);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				dlg=new DirectoryDialog(shell);
				dlg.setText("Directory");
				dlg.setMessage("Please select source codes directory!");
				//dlg.setFilterPath("C:/Users/YueYuan/Desktop/SubjectSys_dnsjava");//去掉后默认同赵文
				dlg.setFilterPath("D:\\Work\\Research\\博士课题(Code Clones）\\1Paper\\7基于变化的克隆一致性维护需求预测\\experiment\\0源代码和克隆检测结果\\SubjectSys_jEdit");
				sourceCodes.setText(dlg.open());
			}
		});
		
		Label lblTargetDirectory = new Label(composite, SWT.NONE);
		lblTargetDirectory.setBounds(10, 83, 137, 17);
		lblTargetDirectory.setText("CloneResults Directory:");
		
		cloneResults = new Text(composite, SWT.BORDER);
		cloneResults.setBounds(149, 83, 179, 23);
		
		Button btnBrowse_1 = new Button(composite, SWT.NONE);
		btnBrowse_1.setBounds(334, 83, 80, 27);
		btnBrowse_1.setText("Browse");
		btnBrowse_1.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				dlg=new DirectoryDialog(shell);
				dlg.setText("Directory");
				dlg.setMessage("Please select clone results directory!");
				//dlg.setFilterPath("C:/Users/YueYuan/Desktop/dnsjava-results");
				dlg.setFilterPath("D:\\Work\\Research\\博士课题(Code Clones）\\1Paper\\7基于变化的克隆一致性维护需求预测\\experiment\\0源代码和克隆检测结果\\jEdit-_results");
				cloneResults.setText(dlg.open());
			}
			
		});
		
		/*Label label = new Label(composite, SWT.NONE);
		label.setText("ClassFiles Directory:");
		label.setBounds(10, 47, 137, 17);
		
		text = new Text(composite, SWT.BORDER);
		text.setBounds(149, 47, 179, 23);
		
		Button button = new Button(composite, SWT.NONE);
		button.setText("Browse");
		button.setBounds(334, 47, 80, 27);
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				dlg=new DirectoryDialog(shell);
				dlg.setText("Directory");
				dlg.setMessage("Please select system bytecode files directory!");
				dlg.setFilterPath("C:/Users/YueYuan/Desktop/ClassSys_dnsjava");
				text.setText(dlg.open());
			}
		});
		*/
		Button btnOK = new Button(composite, SWT.NONE);
		btnOK.setBounds(176, 121, 80, 27);
		btnOK.setText("OK");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(cloneResults.getText()=="" || sourceCodes.getText()==""/* || text.getText()==""*/){
					MessageDialog.openError(shell, "ERROR", "Please Select All Path!");
					return;
				}
				//记录全局路径信息
				Path._subSysDirectory = sourceCodes.getText();
				Path._clonesFolderPath = cloneResults.getText();
				//Path._subClassFilesDirectory = text.getText();
				
				VariationInformation.init();//为全局变量初始化
				
				File sysFolder = new File(Path._subSysDirectory);
				if(sysFolder.isDirectory()){
					File[] childSysFolder = sysFolder.listFiles();
					for(File f : childSysFolder){
						if (f.isDirectory()){
							GetJavaFiles getJavaFile = new GetJavaFiles();
							getJavaFile.GetJavaFilePath(f.getAbsolutePath());
							//存放整个系统的各个版本的所有java文件名
							VariationInformation.allVersionJavaFiles.put(f.getName(), getJavaFile.getAllJavaFiles());
						}
					}
				}
				
				
				File clonesFolder = new File(Path._clonesFolderPath);
				boolean flag = false;
				int cloneIndex = -1;
				if(clonesFolder.isDirectory()){
					File[] subCloneFolder = clonesFolder.listFiles();//blocks同级别的
					for(int i=0;i<subCloneFolder.length;i++){
						if(subCloneFolder[i].isDirectory()){
							String[] cloneFiles = subCloneFolder[i].list();
							if(cloneFiles.length !=0 && cloneFiles[0].endsWith("-classes.xml")){
								cloneIndex = i;
								break;
							}	
						}
						else{
							MessageDialog.openError(shell, null, "Please take clone results into a directory,and select the directory.");
							return;
						}
					}
					if(cloneIndex == -1){
						MessageDialog.openInformation(shell, null, "Clone Result Files Don't Exist!");
						return;
					}
					
					
					for(int i=0;i<subCloneFolder[cloneIndex].list().length;i++){
						String [] tempFilesname = subCloneFolder[cloneIndex].list();
						if(!flag){
							if(tempFilesname[i].contains("_blocks-")){
								ConstantVariation.granularity = "blocks";
							}
							else if(tempFilesname[i].contains("_functions-")){
								ConstantVariation.granularity = "functions";
							}
							flag = true;
						}
						String absoluteFilename = Path._clonesFolderPath + '\\'+ subCloneFolder[cloneIndex].getName() 
								+ '\\'+ tempFilesname[i];
						//System.out.println(absoluteFilename);
						//读取克隆代码结果 并加入全局数据结构中
						ReadCloneResults readCloneResults = new ReadCloneResults(absoluteFilename,ConstantVariation.NICAD);
						VariationInformation.clonesInVersion.add(readCloneResults.inVersion);
						for(CloneGroup group :readCloneResults.groupList){
							VariationInformation.cloneGroup.add(group);
						}
						//break;
					}
					MessageDialog.openInformation(shell, null, "Succeed!");
				}
				else {
					MessageDialog.openError(shell, null, "Please take clone results into a directory,and select the directory.");
					return;
				}		
				shell.dispose();
			}
		});
		
	}
}
