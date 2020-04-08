package Myplugin.CloneControl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import Global.ConstantVariation;
import Global.Path;
import Global.VariationInformation;
import CloneRepresentation.CloneGroup;
import ExtractFeatures.CreateTrainedFeatureVector;
import PreProcess.CreateCRDInfo;
import PreProcess.CreateGenealogyInfo;
import PreProcess.CreateMappingInfo;
import PreProcess.GetJavaFiles;
import PreProcess.ReadCloneResults;

public class SelectDirectoryForTrainPre extends Dialog {

	protected Shell shell;
	private Text sourceCodes;
	private Text cloneResults;
	private DirectoryDialog dlg;
	
	private String arffPath;//Ĭ�ϵĴ��AFRR�ļ���·��
	private String modelPath;//Ĭ�ϵ�Ԥ��ģ��·��
	
	public static String granularity = "blocks";//��¡����
	
	public SelectDirectoryForTrainPre(Shell parentShell) {
		super(parentShell);	
		shell = parentShell;
	}
	
	private void CreateAfrrFile(){
		CreateCRDInfo createCRD = new CreateCRDInfo();
		createCRD.CreateCRDForSys();		
		for(int i=0;i<VariationInformation.clonesInVersion.size()-1;i++){
			List<CloneGroup> srcGroupList = new ArrayList<CloneGroup>();
			List<CloneGroup> destGroupList = new ArrayList<CloneGroup>();		
			// ��ȡԴ�汾 Ŀ�İ汾��group
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == i)
					srcGroupList.add(group);
				else if(group.getVersionID() == i+1)
					destGroupList.add(group);
			}
			CreateMappingInfo mapInfo = new CreateMappingInfo();
			mapInfo.MapBetweenVersions(srcGroupList, destGroupList);
			mapInfo.RecognizeEvolutionPattern();
			mapInfo.SaveMappingForSys(i,i+1);//��������ϵͳ��Mapping
		}
		
		CreateGenealogyInfo genealogyInfo = new CreateGenealogyInfo();
		genealogyInfo.CreateGenealogyForAll();
		
		
		CreateTrainedFeatureVector createFeature = new CreateTrainedFeatureVector();
		createFeature.ExtractFeature(arffPath);
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Selection Dialog"); // ����Dialog�ı�ͷ
		getShell().setSize(450, 206);
		parent.setBounds(10, 10, 424, 158);
		Label lblResultsDirectory = new Label(parent, SWT.NONE);
		lblResultsDirectory.setBounds(10, 10, 137, 17);
		lblResultsDirectory.setText("SourceCodes Directory:");

		sourceCodes = new Text(parent, SWT.BORDER);
		sourceCodes.setBounds(149, 7, 179, 23);

		Button btnBrowse = new Button(parent, SWT.NONE);
		btnBrowse.setBounds(334, 5, 80, 27);
		btnBrowse.setText("Browse");
		btnBrowse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dlg = new DirectoryDialog(shell);
				dlg.setText("Directory");
				dlg.setMessage("Please select source codes directory!");
				dlg.setFilterPath("E:/Work/Work-����/experiment/Source/SubjectSys_dnsjava");
				sourceCodes.setText(dlg.open());
			}
		});

		Label lblTargetDirectory = new Label(parent, SWT.NONE);
		lblTargetDirectory.setBounds(10, 70, 137, 17);
		lblTargetDirectory.setText("CloneResults Directory:");

		cloneResults = new Text(parent, SWT.BORDER);
		cloneResults.setBounds(149, 70, 179, 23);

		Button btnBrowse_1 = new Button(parent, SWT.NONE);
		btnBrowse_1.setBounds(334, 70, 80, 27);
		btnBrowse_1.setText("Browse");
		btnBrowse_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				dlg = new DirectoryDialog(shell);
				dlg.setText("Directory");
				dlg.setMessage("Please select clone results directory!");
				dlg.setFilterPath("E:/Work/Work-����/experiment/DetectionResults/dnsjava-results");
				cloneResults.setText(dlg.open());
			}

		});

		Button btnOK = new Button(parent, SWT.NONE);
		btnOK.setBounds(176, 121, 80, 27);
		btnOK.setText("OK");
		btnOK.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if(cloneResults.getText()=="" || sourceCodes.getText()==""){
					MessageDialog.openError(shell, "ERROR", "Please Select All Path!");
					return;
				}
				//��¼ȫ��·����Ϣ
				Path._subSysDirectory = sourceCodes.getText();
				Path._clonesFolderPath = cloneResults.getText();
				arffPath =  cloneResults.getText() + "_FeatureVector.arff";
				modelPath =  cloneResults.getText() + "_BaysNet.model";
				
				VariationInformation.init();//Ϊȫ�ֱ�����ʼ��
				//System.out.println(Path._clonesFolderPath);
				
				File sysFolder = new File(Path._subSysDirectory);
				if(sysFolder.isDirectory()){
					File[] childSysFolder = sysFolder.listFiles();
					for(File f : childSysFolder){
						if (f.isDirectory()){
							GetJavaFiles getJavaFile = new GetJavaFiles();
							getJavaFile.GetJavaFilePath(f.getAbsolutePath());
							//�������ϵͳ�ĸ����汾������java�ļ���
							VariationInformation.allVersionJavaFiles.put(f.getName(), getJavaFile.getAllJavaFiles());
						}
					}
				}
				
				
				File clonesFolder = new File(Path._clonesFolderPath);
				boolean flag = false;
				int cloneIndex = -1;
				if(clonesFolder.isDirectory()){
					File[] subCloneFolder = clonesFolder.listFiles();//blocksͬ�����
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
						//��ȡ��¡������ ������ȫ�����ݽṹ��
						ReadCloneResults readCloneResults = new ReadCloneResults(absoluteFilename,ConstantVariation.NICAD);
						VariationInformation.clonesInVersion.add(readCloneResults.inVersion);
						for(CloneGroup group :readCloneResults.groupList){
							VariationInformation.cloneGroup.add(group);
						}
						//break;
					}
					CreateAfrrFile();
					MessageDialog.openInformation(shell, null, "ARFF File Created! Will Create Predictor Model ");
				}
				else {
					MessageDialog.openError(shell, null, "Please take clone results into a directory,and select the directory.");
					return;
				}		
				//��ȡarff ������ģ��
				try {
					Instances insTrain = RelatedWekaOperation.GetInstances(arffPath);
					RelatedWekaOperation.clf = (Classifier)Class.forName("weka.classifiers.bayes.BayesNet").newInstance();//��ʼ��������
					RelatedWekaOperation.clf.buildClassifier(insTrain);//ʹ������ѵ��������
					SerializationHelper.write(modelPath, RelatedWekaOperation.clf);
				} catch (Exception e1) {
					e1.printStackTrace();
				}		
				
				MessageDialog.openInformation(shell, null, "Model has been Created Succeed!" + "\nPath: " + modelPath);
				getShell().dispose();
			}
		});
        
		return super.createDialogArea(parent);
	}

}
