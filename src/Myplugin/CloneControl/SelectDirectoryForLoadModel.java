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

public class SelectDirectoryForLoadModel extends Dialog {
	protected Shell shell;
	private Text predctorModel;
	//private DirectoryDialog dlg;
	private FileDialog fDlg;
	
	public SelectDirectoryForLoadModel(Shell parentShell) {
		super(parentShell);	
		shell = parentShell;
	}

	protected Control createDialogArea(Composite parent) {
		getShell().setText("Load Predictor Model"); // 设置Dialog的标头
		getShell().setSize(450, 206);
		parent.setBounds(10, 10, 424, 158);
		
		Label lblResultsDirectory = new Label(parent, SWT.NONE);
		lblResultsDirectory.setBounds(10, 10, 160, 17);
		lblResultsDirectory.setText("Predictor Model Path:");

		predctorModel = new Text(parent, SWT.BORDER);
		predctorModel.setBounds(10, 63, 318, 23);

		Button btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setBounds(334, 63, 80, 27);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				fDlg = new FileDialog(shell);
				fDlg.setText("Please Select Predictor Model Path!");
				String [] extensions = {"*.model"};
				fDlg.setFilterExtensions(extensions);
				predctorModel.setText(fDlg.open());
			}
		});

		Button btnLoad = new Button(parent, SWT.NONE);
		btnLoad.setBounds(176, 121, 80, 27);
		btnLoad.setText("Load");
		btnLoad.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(predctorModel.getText()==""){
					MessageDialog.openError(shell, "ERROR", "Please Select Predictor Model Path!");
					return;
				}
				RelatedWekaOperation.InitClassifierFromModel(predctorModel.getText());
				MessageDialog.openInformation(shell, null, "Model has been Loaded Succeed!");
				getShell().dispose();
			}
		});
		
	
		return super.createDialogArea(parent);
	}

	
}
