package Myplugin.CloneControl;

import java.util.List;

import org.eclipse.jdt.core.dom.CompilationUnit;

import CloneRepresentation.CloneFragment;
import ExtractFeatures.FeatureOperations;
import ExtractFeatures.FeatureVector;
import ExtractFeatures.HalsteadMetric;
import ExtractFeatures.MethodInvocCountVisitor;
import ExtractFeatures.StructuralFeatureVisitor;
import Global.VariationInformation;
import MyEditors.CopyAction;
import PreProcess.CreateCRDInfo;
import PreProcess.LevenshteinDistance;

public class CreateTestingFeatureVector {
	
	public FeatureVector featureVector;
	
	public String copiedPath;//����·��
	public String copiedFileName;
	public int copiedStartLine;
	public int copiedEndLine;
	
	public String pastedPath;//����·��
	public String pastedFileName;
	public int pastedStartLine;
	public int pastedEndLine;
	
	private CompilationUnit copiedCu;
	private CompilationUnit pastedCu;
	
	public CreateTestingFeatureVector(String cPath,String cName,int cSLine,int cELine,String pPath,String pName,int pSLine,int pELine){
		
		this.copiedPath = cPath;
		this.copiedFileName = cName;
		this.copiedStartLine = cSLine;
		this.copiedEndLine = cELine;
		
		this.pastedPath = pPath;
		this.pastedFileName = pName;
		this.pastedStartLine = pSLine;
		this.pastedEndLine = pELine;
		
		copiedCu = null;
		pastedCu = null;
	}
	
	public void ExtractFeatureForTesting(){
		featureVector = new FeatureVector();
		copiedCu = FeatureOperations.CreateAST(copiedPath);
		pastedCu = FeatureOperations.CreateAST(pastedPath);
		//�������Ƽ�ճ��Ƭ�ε�CRD
		FeatureOperations.CreateCRDForFrag(copiedCu, copiedPath, copiedStartLine, copiedEndLine,"copy");
		FeatureOperations.CreateCRDForFrag(pastedCu, pastedPath, pastedStartLine, pastedEndLine,"paste");
		//��ȡ����������
		ExtractCodeAndStrucFeature();
		ExtractDestinationFeature();
	}
	
	private void ExtractCodeAndStrucFeature(){
		this.featureVector.setSourceLine(copiedEndLine - copiedStartLine + 1);//��������
		List<String> cloneCode = FeatureOperations.GetCodesFromFile(copiedPath, copiedStartLine, copiedEndLine);
		
		//halstead����  
		HalsteadMetric.InitHalsteadParam();
        HalsteadMetric halMetric = FeatureOperations.GetHalsteadInfo(cloneCode);
        this.featureVector.setUniOPERATORCount(halMetric.getUniOPERATORCount());
        this.featureVector.setUniOperandCount(halMetric.getUniOperandCount());
        this.featureVector.setTotalOPERATORCount(halMetric.getTotalOPERATORCount());
        this.featureVector.setTotalOperandCount(halMetric.getTotalOperandCount());
        
        //����������Ϣ
        int startPos = copiedCu.getPosition(copiedStartLine, 0);
  		int endPos = copiedCu.getPosition(copiedEndLine + 1,0 );
        String localClassName = FeatureOperations.getCopiedCrd().getClassName();//��ÿ�¡������������,��λ����Ϣ
  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,localClassName,Activator.currentVersionJavaFiles.get(CopyAction.getProjectName()));
  		copiedCu.accept(invocFeatureVisitor);
  		this.featureVector.setTotalMethodInvocCount(invocFeatureVisitor.getTotalMethodInvocCount());
  		this.featureVector.setLocalMethodInvocCount(invocFeatureVisitor.getLocalMethodInvocCount());
  		this.featureVector.setLibraryMethodInvocCount(invocFeatureVisitor.getLibraryMethodInvocCount());
  		this.featureVector.setOtherMethodInvocCount(invocFeatureVisitor.getOtherMethodInvocCount());
  		this.featureVector.setTotalParameterCount(invocFeatureVisitor.getTotalParameterCount());
  		
  		//�ṹ����
        StructuralFeatureVisitor strucFeatureVisitor = FeatureOperations.GetStructuralInfo(copiedCu, startPos, endPos, false);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
	}

	private void ExtractDestinationFeature() {
		// �Ƿ��Ǿֲ���¡
		this.featureVector.setLocalClone(copiedPath.equals(pastedPath));
		// �ļ��������ƶ�
		this.featureVector.setSimFileName(LevenshteinDistance.sim(copiedFileName, pastedFileName));
		// Masked �ļ������ƶ�
		if (featureVector.isLocalClone())
			this.featureVector.setSimMaskedFileName(0);
		else
			this.featureVector.setSimMaskedFileName(1);

		float maxSimParaName = -1;//�����������ƶ�
		float sumSimParaName = 0;//������
		float sumSimParaType = 0;//��������
		//���������ƶ�
		//��¡���벻��һ����������,�����ڷ��������ƶ�Ϊ1������������������������Ӧ�ÿ���?????
		if(FeatureOperations.getCopiedCrd().getMethodInfo()==null  && FeatureOperations.getPastedCrd().getMethodInfo()==null){
			this.featureVector.setSimMethodName(1);
			
			this.featureVector.setSimTotalParaName(1);
			this.featureVector.setSimTotalParaType(1);
			this.featureVector.setSimMaxParaName(1);
		} else if((FeatureOperations.getCopiedCrd().getMethodInfo()==null  && FeatureOperations.getPastedCrd().getMethodInfo()!=null) ||
				(FeatureOperations.getCopiedCrd().getMethodInfo()!=null  && FeatureOperations.getPastedCrd().getMethodInfo()==null)){
			this.featureVector.setSimMethodName(0);
			
			this.featureVector.setSimTotalParaName(0);
			this.featureVector.setSimTotalParaType(0);
			this.featureVector.setSimMaxParaName(0);
		} else { 
			this.featureVector.setSimMethodName(LevenshteinDistance.sim(FeatureOperations.getCopiedCrd().getMethodInfo().mName, FeatureOperations.getPastedCrd().getMethodInfo().mName));
			//����������ƶ�
			if(FeatureOperations.getCopiedCrd().getMethodInfo().mParaNum == 0 && FeatureOperations.getPastedCrd().getMethodInfo().mParaNum == 0){
				this.featureVector.setSimTotalParaName(1);
				this.featureVector.setSimTotalParaType(1);
				this.featureVector.setSimMaxParaName(1);
			}
			else if((FeatureOperations.getCopiedCrd().getMethodInfo().mParaNum == 0 && FeatureOperations.getPastedCrd().getMethodInfo().mParaNum != 0) ||
					(FeatureOperations.getCopiedCrd().getMethodInfo().mParaNum != 0 && FeatureOperations.getPastedCrd().getMethodInfo().mParaNum == 0)){
				this.featureVector.setSimTotalParaName(0);
				this.featureVector.setSimTotalParaType(0);
				this.featureVector.setSimMaxParaName(0);
			}
			else{  
				for(int m=0;m<FeatureOperations.getCopiedCrd().getMethodInfo().mParaTypeList.size();m++){
					String strName1 = FeatureOperations.getCopiedCrd().getMethodInfo().mParaNameList.get(m);
					String strType1 = FeatureOperations.getCopiedCrd().getMethodInfo().mParaTypeList.get(m);
					for(int n=0;n<FeatureOperations.getPastedCrd().getMethodInfo().mParaTypeList.size();n++){
						String strName2 = FeatureOperations.getPastedCrd().getMethodInfo().mParaNameList.get(n);
						String strType2 = FeatureOperations.getPastedCrd().getMethodInfo().mParaTypeList.get(n);
						
						sumSimParaType += LevenshteinDistance.sim(strType1, strType2);
						sumSimParaName += LevenshteinDistance.sim(strName1, strName2);
						if(LevenshteinDistance.sim(strName1, strName2) > maxSimParaName) 
							maxSimParaName = LevenshteinDistance.sim(strName1, strName2);
					}
				}
				//�ܵĲ������ƶ�
				this.featureVector.setSimTotalParaName(sumSimParaName);
				this.featureVector.setSimTotalParaType(sumSimParaType);
				this.featureVector.setSimMaxParaName(maxSimParaName);
			}
			
		}
			
		
		//����Ϣ��
		if(FeatureOperations.getCopiedCrd().getBlockInfos() == null && FeatureOperations.getPastedCrd().getBlockInfos() == null){
			featureVector.setIsSameBlockInfo(true);
		} else if((FeatureOperations.getCopiedCrd().getBlockInfos() == null && FeatureOperations.getPastedCrd().getBlockInfos() != null) ||
				(FeatureOperations.getCopiedCrd().getBlockInfos() != null && FeatureOperations.getPastedCrd().getBlockInfos() == null)){
			featureVector.setIsSameBlockInfo(false);
		} else if(CreateCRDInfo.compareWithBlockList(FeatureOperations.getCopiedCrd().getBlockInfos(),FeatureOperations.getPastedCrd().getBlockInfos())){
			featureVector.setIsSameBlockInfo(true);
		} else{featureVector.setIsSameBlockInfo(false);}
		
		//ճ��˲��Ԥ�⣬����Ƭ�����ƶ�Ϊ1
		this.featureVector.setSimCloneFragment(1.0);
	}
}
