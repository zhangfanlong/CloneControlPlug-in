package Myplugin.CloneControl;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
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

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;


public class SelectDirectoryForLoadARFF extends Dialog{

	protected Shell shell;
	private Text arffFile;
	private FileDialog fDlg;
	
	public SelectDirectoryForLoadARFF (Shell parentShell) {
		super(parentShell);	
		shell = parentShell;
	}
	
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Load AFRR File"); // 设置Dialog的标头
		getShell().setSize(450, 206);
		parent.setBounds(10, 10, 424, 158);
		
		Label lblResultsDirectory = new Label(parent, SWT.NONE);
		lblResultsDirectory.setBounds(10, 10, 160, 17);
		lblResultsDirectory.setText("ARFF File Path:");

		arffFile = new Text(parent, SWT.BORDER);
		arffFile.setBounds(10, 63, 318, 23);

		Button btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setBounds(334, 63, 80, 27);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fDlg = new FileDialog(shell);
				fDlg.setText("Please Select ARFF File Path!");
				String [] extensions = {"*.arff"};
				fDlg.setFilterExtensions(extensions);
				arffFile.setText(fDlg.open());
			}
		});

		Button btnLoad = new Button(parent, SWT.NONE);
		btnLoad.setBounds(176, 121, 80, 27);
		btnLoad.setText("Load");
		btnLoad.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(arffFile.getText()==""){
					MessageDialog.openError(shell, "ERROR", "Please Select ARFF File Path!");
					return;
				}
				String modelPath="";
				try {
					Instances insTrain = RelatedWekaOperation.GetInstances(arffFile.getText());
					RelatedWekaOperation.clf = (Classifier)Class.forName("weka.classifiers.bayes.BayesNet").newInstance();//初始化分类器
					RelatedWekaOperation.clf.buildClassifier(insTrain);//使用样本训练分类器
					
					//模型路径同arff路径
					//arff文件名是规范的。。。模型名为系统名加 "_BaysNet.model" ，否则模型名为arff文件名加 "_BaysNet.model"
					if(arffFile.getText().indexOf("_FeatureVector.arff")!=-1){
						modelPath = arffFile.getText().substring(0, arffFile.getText().indexOf("_FeatureVector.arff")) + "_BaysNet.model";
					} else {
						modelPath = arffFile.getText().substring(0, arffFile.getText().indexOf(".arff")) + "_BaysNet.model";
					}
					
					SerializationHelper.write(modelPath, RelatedWekaOperation.clf);
				} catch (Exception e1) {
					e1.printStackTrace();
				}		
				
				MessageDialog.openInformation(shell, null, "Model has been Created Succeed! Path: " + modelPath);
				getShell().dispose();
				
			}
		});
		
		
		return super.createDialogArea(parent);
	}
}
