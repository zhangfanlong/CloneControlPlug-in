package ExtractFeatures;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.GroupMapping;
import Global.Path;
import Global.VariationInformation;
import PreProcess.CreateCRDInfo;
import PreProcess.Diff;
import PreProcess.LevenshteinDistance;

public class CreateTrainedFeatureVector {
	public FeatureVector featureVector;
	//private int consistCount;
	
	public void ExtractFeature(){
		VariationInformation.featureVectorList = new ArrayList<FeatureVector>();
		HalsteadMetric.InitHalsteadParam();
		List<CloneFragment> tarFragment;	
		
		//针对第一个版本
		for(CloneGroup group : VariationInformation.cloneGroup){
			if(group.getVersionID() == 0 /*&& !isSingleGenealogy(0,group.getCGID())*/){
				int consistency = ExtractConsistencyLabel(0,group.getCGID());
				int p=0;
				//for(int p=0;p<group.getClonefragment().size()-1;p++){	
					this.featureVector = new FeatureVector();
					ExtractCodeAndStrucFeature(group.getClonefragment().get(p));//代码和结构特征
					
					for(int q=p+1;q<group.getClonefragment().size();q++){
						ExtractDestinationFeature(group.getClonefragment().get(p),group.getClonefragment().get(q));//上下文特征
						
						if(group.getClonefragment().size() == 2)//计算片段间相似度
							this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
						else{
							//计算文本相似度 若小于Similarity，取Similarity
							float similarity = FragmentSimlarity(group.getClonefragment().get(p),group.getClonefragment().get(q));
							if(similarity*100 >= group.getSimilarity())	
								this.featureVector.setSimCloneFragment(similarity);
							else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
						}
						//this.featureVector.setConsisCount(this.consistCount);
						this.featureVector.setConsistence(consistency);
						VariationInformation.featureVectorList.add(featureVector);
					}
				//}
			}
		}
	
		//针对其他版本中新产生的克隆组
		if(VariationInformation.unMappedDestInfo.size()!=0){
			for(GroupMapping unMappedGroup:VariationInformation.unMappedDestInfo){
				//if(!isSingleGenealogy(unMappedGroup.getDestVersionID(),unMappedGroup.getDestCGID())){
					int consistency = ExtractConsistencyLabel(unMappedGroup.getDestVersionID(),unMappedGroup.getDestCGID());
					for(CloneGroup group : VariationInformation.cloneGroup){
						if(group.getVersionID() == unMappedGroup.getDestVersionID() && group.getCGID() == unMappedGroup.getDestCGID()){
							File subSysDir = new File(Global.Path._subSysDirectory);
							String preVersion = subSysDir.list()[group.getVersionID()-1];//上一版本的系统名
							float [] Sim = new float[group.getClonefragment().size()+1];
							//该group中找原始片段
							for(CloneFragment clonefragment : group.getClonefragment()){	

								String preVerPath = Global.Path._subSysDirectory + "\\" + preVersion + "\\"  + clonefragment.getPath().substring(clonefragment.getPath().indexOf("/")+1);//上衣版本的同类的类名
								String clonePath =  Global.Path._subSysDirectory + "\\" + clonefragment.getPath();//克隆代码所在类路径
								
								List<String> cloneInSource = new ArrayList<String>();
								cloneInSource = CreateCRDInfo.GetFileContent(clonePath);
								if(new File(preVerPath).exists()){//上一版本存在
									//文本比较  查找上一版本的克隆代码
									Sim[clonefragment.getCFID()]=this.FindFragmentInPreVersion(CreateCRDInfo.GetCFSourceFromCRDInfo(cloneInSource, clonefragment.getStartLine(), clonefragment.getEndLine()),
											CreateCRDInfo.GetFileContent(preVerPath));
									if( Sim[clonefragment.getCFID()] < 0.7)	//使用克隆检测工具默认相似度
										{ Sim[clonefragment.getCFID()] = 0; }
								}
								else Sim[clonefragment.getCFID()] = 0;
							}
							//找出sim值最大的克隆片段认为是原始复制的代码片段
							float SimMax = 0; 
							int copiedID = 0;
							for(int s=1;s<Sim.length;s++){//clonefragment ID从1开始
								if(Sim[s]>SimMax){
									SimMax = s;
									copiedID = s;
								}
							}
							if(copiedID == 0){ //没有找到原始的复制片段
								//for(int p=0;p<group.getClonefragment().size()-1;p++){
									int p=0;
									this.featureVector = new FeatureVector();
									ExtractCodeAndStrucFeature(group.getClonefragment().get(p));//代码和结构特征			
									for(int q=p+1;q<group.getClonefragment().size();q++){
										ExtractDestinationFeature(group.getClonefragment().get(p),group.getClonefragment().get(q));//上下文特征
										
										if(group.getClonefragment().size() == 2)//计算片段间相似度
											this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
										else{
											//计算文本相似度 若小于Similarity，取Similarity
											float similarity = FragmentSimlarity(group.getClonefragment().get(p),group.getClonefragment().get(q));
											if(similarity*100 >= group.getSimilarity())	
												this.featureVector.setSimCloneFragment(similarity);
											else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
										}
										//this.featureVector.setConsisCount(this.consistCount);
										this.featureVector.setConsistence(consistency);
										VariationInformation.featureVectorList.add(featureVector);
									}
								//}
							}
							else{
								CloneFragment copiedFrag=null;
								tarFragment = new ArrayList<CloneFragment>();
								for(CloneFragment frag : group.getClonefragment()){
									if(frag.getCFID() == copiedID){
										copiedFrag = frag;
									}	
									else tarFragment.add(frag);
								}
								
								this.featureVector = new FeatureVector();
								ExtractCodeAndStrucFeature(copiedFrag);//代码和结构特征
								
								for(CloneFragment frag2 : tarFragment){
									ExtractDestinationFeature(copiedFrag,frag2);//上下文特征
									
									if(group.getClonefragment().size() == 2)//计算片段间相似度
										this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
									else{
										//计算文本相似度 若小于Similarity，取Similarity
										float similarity = FragmentSimlarity(copiedFrag,frag2);
										if(similarity*100 >= group.getSimilarity())	
											this.featureVector.setSimCloneFragment(similarity);
										else	this.featureVector.setSimCloneFragment(group.getSimilarity()/100.0);
									}
									//this.featureVector.setConsisCount(this.consistCount);
									this.featureVector.setConsistence(consistency);
									VariationInformation.featureVectorList.add(featureVector);
								}
								
							}//else			
						}
					}
				//}
			}
		}//if
		
		WekaOperations.WriteFeaturesToArff(Path._clonesFolderPath + "FeatureVector.arff",1);
	}

	/*private boolean isSingleGenealogy(int version,int cgID){
		for(CloneGenealogy single : VariationInformation.singleCgGenealogyList){
			if(single.getStartVersion() == version && single.getRootCGid() == cgID)
				return true;
		}
		return false;
	} */
	
	private void ExtractCodeAndStrucFeature(CloneFragment frag){
		
		this.featureVector.setSourceLine(frag.getEndLine() - frag.getStartLine()+1);//代码行数
		String subSysPath = Path._subSysDirectory + "\\" + frag.getPath();
		List<String> cloneCode = FeatureOperations.GetCodesFromFile(subSysPath, frag.getStartLine(), frag.getEndLine());
    
		
		//函数调用次数及总数，从AST提取
  		CompilationUnit cu = FeatureOperations.CreateAST(subSysPath);	
  		int startPos = cu.getPosition(frag.getStartLine(), 0);
  		int endPos = cu.getPosition(frag.getEndLine() + 1,0 ); //endLine + 1 or startPos + 1
  		
  		String className = frag.getCRD().getClassName();//获得克隆代码所在类名,及位置信息
  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,className,VariationInformation.allVersionJavaFiles.get(frag.getPath().substring(0, frag.getPath().indexOf("/"))));
  		cu.accept(invocFeatureVisitor);
  		this.featureVector.setTotalParameterCount(invocFeatureVisitor.getTotalParameterCount());
  		this.featureVector.setTotalMethodInvocCount(invocFeatureVisitor.getTotalMethodInvocCount());
  		this.featureVector.setLocalMethodInvocCount(invocFeatureVisitor.getLocalMethodInvocCount());
  		this.featureVector.setLibraryMethodInvocCount(invocFeatureVisitor.getLibraryMethodInvocCount());
  		this.featureVector.setOtherMethodInvocCount(invocFeatureVisitor.getOtherMethodInvocCount());
  	
  		
  		//halstead度量  
        HalsteadMetric halMetric = FeatureOperations.GetHalsteadInfo(cloneCode);
        this.featureVector.setUniOPERATORCount(halMetric.getUniOPERATORCount());
        this.featureVector.setUniOperandCount(halMetric.getUniOperandCount());
        this.featureVector.setTotalOPERATORCount(halMetric.getTotalOPERATORCount());
        this.featureVector.setTotalOperandCount(halMetric.getTotalOperandCount());         
  		
        
        //结构特征
        StructuralFeatureVisitor strucFeatureVisitor = FeatureOperations.GetStructuralInfo(cu, startPos, endPos, false);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
        //ExtractStructuralFeature(cu,startPos,endPos);
	}
	
/*	private void ExtractStructuralFeature(CompilationUnit cu,int startPos,int endPos){
		StructuralFeatureVisitor strucFeatureVisitor = new StructuralFeatureVisitor(startPos,endPos);
        cu.accept(strucFeatureVisitor);
        this.featureVector.setStruFeature(strucFeatureVisitor.getStructuralFeature());
	}*/
	
	private void ExtractDestinationFeature(CloneFragment frag,CloneFragment frag2){//frag为复制的片段,frag2为粘贴的片段
		//是否是局部克隆
		this.featureVector.setLocalClone(frag.getPath().equals(frag2.getPath()));
		//文件名的相似度
		this.featureVector.setSimFileName(LevenshteinDistance.sim(frag.getFileName(), frag2.getFileName()));
		//Masked 文件名相似度
		if(featureVector.isLocalClone()) this.featureVector.setSimMaskedFileName(0);
		else this.featureVector.setSimMaskedFileName(1);		
		
		
		float maxSimParaName = -1;//最大参数名相似度
		float sumSimParaName = 0;//参数名
		float sumSimParaType = 0;//参数类型
		//方法名相似度
		//克隆代码不在一个方法里面,都不在方法名相似度为1。。。。。。。。。。。。应该可以?????
		if(frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()==null){
			this.featureVector.setSimMethodName(1);
			
			this.featureVector.setSimTotalParaName(1);
			this.featureVector.setSimTotalParaType(1);
			this.featureVector.setSimMaxParaName(1);
		} else if((frag.getCRD().getMethodInfo()==null  && frag2.getCRD().getMethodInfo()!=null) ||
				(frag.getCRD().getMethodInfo()!=null  && frag2.getCRD().getMethodInfo()==null)){
			this.featureVector.setSimMethodName(0);
			
			this.featureVector.setSimTotalParaName(0);
			this.featureVector.setSimTotalParaType(0);
			this.featureVector.setSimMaxParaName(0);
		} else { 
			this.featureVector.setSimMethodName(LevenshteinDistance.sim(frag.getCRD().getMethodInfo().mName, frag2.getCRD().getMethodInfo().mName));
			//计算参数相似度
			if(frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum == 0){
				this.featureVector.setSimTotalParaName(1);
				this.featureVector.setSimTotalParaType(1);
				this.featureVector.setSimMaxParaName(1);
			}
			else if((frag.getCRD().getMethodInfo().mParaNum == 0 && frag2.getCRD().getMethodInfo().mParaNum != 0) ||
					(frag.getCRD().getMethodInfo().mParaNum != 0 && frag2.getCRD().getMethodInfo().mParaNum == 0)){
				this.featureVector.setSimTotalParaName(0);
				this.featureVector.setSimTotalParaType(0);
				this.featureVector.setSimMaxParaName(0);
			}
			else{  
				for(int m=0;m<frag.getCRD().getMethodInfo().mParaTypeList.size();m++){
					String strName1 = frag.getCRD().getMethodInfo().mParaNameList.get(m);
					String strType1 = frag.getCRD().getMethodInfo().mParaTypeList.get(m);
					for(int n=0;n<frag2.getCRD().getMethodInfo().mParaTypeList.size();n++){
						String strName2 = frag2.getCRD().getMethodInfo().mParaNameList.get(n);
						String strType2 = frag2.getCRD().getMethodInfo().mParaTypeList.get(n);
						
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
			
		
		
		
		//.........................块信息度怎么算????????????????
		if(frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() == null){
			featureVector.setIsSameBlockInfo(true);
		} else if((frag.getCRD().getBlockInfos() == null && frag2.getCRD().getBlockInfos() != null) ||
				(frag.getCRD().getBlockInfos() != null && frag2.getCRD().getBlockInfos() == null)){
			featureVector.setIsSameBlockInfo(false);
		} else if(CreateCRDInfo.compareWithBlockList(frag.getCRD().getBlockInfos(),frag2.getCRD().getBlockInfos())){
			featureVector.setIsSameBlockInfo(true);
		} else{featureVector.setIsSameBlockInfo(false);}
	}
	
	private int ExtractConsistencyLabel(int versionId,int groupId){
	//	this.consistCount = 0;
		for(CloneGenealogy cloneGene : VariationInformation.cloneGenealogy){
			if(cloneGene.getStartVersion() == versionId && cloneGene.getRootCGid() == groupId){
				if(cloneGene.getEvoPatternCount()[4] > 0){ //发生过一致变化就认为是需要一致维护,如何改进??????????????
				//	this.consistCount += cloneGene.getEvoPatternCount()[4];
					return 1; 
				}
				else return 0;
			}
		}
		//单版本克隆家系 就认为是不需要一致性维护的
		return 0;
	}
	
	public float FindFragmentInPreVersion (List<String> tarfragment,List<String> srcfragment){
		Diff.UseDefaultStrSimTh();  //使用行相似度阈值默认值0.5
		return new Diff().FineFragment(tarfragment, srcfragment);
	}

	
	private float FragmentSimlarity(CloneFragment frag,CloneFragment frag2){//frag为复制的片段,frag2为粘贴的片段
		
		String subSysPath = Path._subSysDirectory + "\\" + frag.getPath();
		List<String> sourceCode=CreateCRDInfo.GetFileContent(subSysPath);
		List<String> cloneCode = new ArrayList<String>();
		cloneCode =	CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode, frag.getStartLine(), frag.getEndLine());
		
		String subSysPath2 = Path._subSysDirectory + "\\" + frag2.getPath();
		List<String> sourceCode2=CreateCRDInfo.GetFileContent(subSysPath2);
		List<String> cloneCode2 = new ArrayList<String>();
		cloneCode2 = CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode2, frag2.getStartLine(), frag2.getEndLine());

		Diff.UseDefaultStrSimTh();  //使用行相似度阈值默认值0.5
		return Diff.FileSimilarity(new Diff().DiffFiles(cloneCode, cloneCode2), 
				cloneCode.size(), cloneCode2.size(), true);
		
	}
	
}
