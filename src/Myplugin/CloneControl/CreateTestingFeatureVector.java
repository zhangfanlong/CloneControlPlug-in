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
	
	public String copiedPath;//绝对路径
	public String copiedFileName;
	public int copiedStartLine;
	public int copiedEndLine;
	
	public String pastedPath;//绝对路径
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
		//创建复制及粘贴片段的CRD
		FeatureOperations.CreateCRDForFrag(copiedCu, copiedPath, copiedStartLine, copiedEndLine,"copy");
		FeatureOperations.CreateCRDForFrag(pastedCu, pastedPath, pastedStartLine, pastedEndLine,"paste");
		//提取并设置特征
		ExtractCodeAndStrucFeature();
		ExtractDestinationFeature();
	}
	
	private void ExtractCodeAndStrucFeature(){
		this.featureVector.setSourceLine(copiedEndLine - copiedStartLine + 1);//代码行数
		List<String> cloneCode = FeatureOperations.GetCodesFromFile(copiedPath, copiedStartLine, copiedEndLine);
		
		//halstead度量  
		HalsteadMetric.InitHalsteadParam();
        HalsteadMetric halMetric = FeatureOperations.GetHalsteadInfo(cloneCode);
        this.featureVector.setUniOPERATORCount(halMetric.getUniOPERATORCount());
        this.featureVector.setUniOperandCount(halMetric.getUniOperandCount());
        this.featureVector.setTotalOPERATORCount(halMetric.getTotalOPERATORCount());
        this.featureVector.setTotalOperandCount(halMetric.getTotalOperandCount());
        
        //方法调用信息
        int startPos = copiedCu.getPosition(copiedStartLine, 0);
  		int endPos = copiedCu.getPosition(copiedEndLine + 1,0 );
        String localClassName = FeatureOperations.getCopiedCrd().getClassName();//获得克隆代码所在类名,及位置信息
  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,localClassName,Activator.currentVersionJavaFiles.get(CopyAction.getProjectName()));
  		copiedCu.accept(invocFeatureVisitor);
  		this.featureVector.setTotalMethodInvocCount(invocFeatureVisitor.getTotalMethodInvocCount());
  		this.featureVector.setLocalMethodInvocCount(invocFeatureVisitor.getLocalMethodInvocCount());
  		this.featureVector.setLibraryMethodInvocCount(invocFeatureVisitor.getLibraryMethodInvocCount());
  		this.featureVector.setOtherMethodInvocCount(invocFeatureVisitor.getOtherMethodInvocCount());
  		this.featureVector.setTotalParameterCount(invocFeatureVisitor.getTotalParameterCount());
  		
  		//结构特征
        StructuralFeatureVisitor strucFeatureVisitor = FeatureOperations.GetStructuralInfo(copiedCu, startPos, endPos, false);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
	}

	private void ExtractDestinationFeature() {
		// 是否是局部克隆
		this.featureVector.setLocalClone(copiedPath.equals(pastedPath));
		// 文件名的相似度
		this.featureVector.setSimFileName(LevenshteinDistance.sim(copiedFileName, pastedFileName));
		// Masked 文件名相似度
		if (featureVector.isLocalClone())
			this.featureVector.setSimMaskedFileName(0);
		else
			this.featureVector.setSimMaskedFileName(1);

		float maxSimParaName = -1;//最大参数名相似度
		float sumSimParaName = 0;//参数名
		float sumSimParaType = 0;//参数类型
		//方法名相似度
		//克隆代码不在一个方法里面,都不在方法名相似度为1。。。。。。。。。。。。应该可以?????
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
			//计算参数相似度
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
				//总的参数相似度
				this.featureVector.setSimTotalParaName(sumSimParaName);
				this.featureVector.setSimTotalParaType(sumSimParaType);
				this.featureVector.setSimMaxParaName(maxSimParaName);
			}
			
		}
			
		
		//块信息度
		if(FeatureOperations.getCopiedCrd().getBlockInfos() == null && FeatureOperations.getPastedCrd().getBlockInfos() == null){
			featureVector.setIsSameBlockInfo(true);
		} else if((FeatureOperations.getCopiedCrd().getBlockInfos() == null && FeatureOperations.getPastedCrd().getBlockInfos() != null) ||
				(FeatureOperations.getCopiedCrd().getBlockInfos() != null && FeatureOperations.getPastedCrd().getBlockInfos() == null)){
			featureVector.setIsSameBlockInfo(false);
		} else if(CreateCRDInfo.compareWithBlockList(FeatureOperations.getCopiedCrd().getBlockInfos(),FeatureOperations.getPastedCrd().getBlockInfos())){
			featureVector.setIsSameBlockInfo(true);
		} else{featureVector.setIsSameBlockInfo(false);}
		
		//粘贴瞬间预测，所以片段相似度为1
		this.featureVector.setSimCloneFragment(1.0);
	}
}
