package PreProcess;

import java.util.ArrayList;
import java.util.List;





import PreProcess.CreateCRDInfo.CRDMatchLevel;
import CRDInformation.CloneSourceInfo;
import CloneRepresentation.CRD;
import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.EvolutionPattern;
import CloneRepresentation.FragmentMapping;
import CloneRepresentation.GroupMapping;
import Global.VariationInformation;

abstract class Mapping{
    public String ID; //映射关系编号
}

class  CGInfo {  //CG信息结构体，包含CG的ID和大小（包含克隆片段的数量）
    public String id;
    public int size;
}

class MappingList extends ArrayList { }

class CloneFragmentMapping extends Mapping{
    public String SrcCFID;
    public String DestCFID;

    //保存映射的两个CF的CRD，是为了在识别进化模式时，计算textSim（因为在MapCF中不计算）
/*  public CloneRegionDescriptor srcCrd;
    public CloneRegionDescriptor destCrd;*/

    public CRDMatchLevel CrdMatchLevel; //新增属性，用来保存两个CF的CRD匹配级别

    public float textSim;  //两段源代码的文本相似度，从diff信息中获得
    //保存两段克隆代码的位置信息，用于计算diff时读取。sourceInfos[0]为源信息，sourceInfos[1]为目标信息
    public CloneSourceInfo[] sourceInfos = new CloneSourceInfo[2];
}


class CloneGroupMapping extends Mapping{

    public CGInfo srcCGInfo;    //源克隆群信息
    public CGInfo destCGInfo;   //目标克隆群信息
    public EvolutionPattern EvoPattern;    //保存进化模式信息
    public MappingList CFMapList;  //保存CF映射的列表
    
    // 为以确定匹配的两个CG创建一个映射关系对象
    public int CreateCGMapping(CloneGroup srcGroupEle, CloneGroup destGroupEle,int mapCount){
    	this.srcCGInfo = new CGInfo();
    	this.srcCGInfo.id = String.valueOf(srcGroupEle.getCGID());
    	this.srcCGInfo.size = srcGroupEle.getNumberofCF();
    	
    	this.destCGInfo = new CGInfo();
    	this.destCGInfo.id = String.valueOf(destGroupEle.getCGID());
    	this.destCGInfo.size = destGroupEle.getNumberofCF();

        //映射克隆群内的克隆片段映射，结果保存到CFMapList成员中
        this.CFMapList = MapCF(srcGroupEle, destGroupEle);
        this.ID = String.valueOf(++mapCount);
        return mapCount;
    }
    
    //计算textSim的MapCF方法
    public MappingList MapCF(CloneGroup srcCG, CloneGroup destCG){
    	
        MappingList cfMappingList = new MappingList();
        //分别获取两个克隆群中克隆片段的CRD元素列表
        List<CRD> srcCGCrdList = new ArrayList<CRD>();
        List<CRD> destCGCrdList = new ArrayList<CRD>();
        for(CloneFragment frag : srcCG.getClonefragment()){
        	srcCGCrdList.add(frag.getCRD());
        }
        for(CloneFragment frag : destCG.getClonefragment()){
        	destCGCrdList.add(frag.getCRD());
        }
 
        int i, j;
        //建立两个标记数组保存srcCF和destCF的映射情况
        boolean[] srcCFMapped = new boolean[srcCGCrdList.size()];
        for (i = 0; i < srcCGCrdList.size(); i++)
        	srcCFMapped[i] = false; 
        boolean[] destCFMapped = new boolean[destCGCrdList.size()];
        for (j = 0; j < destCGCrdList.size(); j++)
        	destCFMapped[j] = false; 

        //开始映射
        if (srcCGCrdList != null && destCGCrdList != null && srcCGCrdList.size() != 0 && destCGCrdList.size() != 0){
            //建立矩阵保存CRDMatch结果
            CRDMatchLevel[][] crdMatchMatrix = new CRDMatchLevel[srcCGCrdList.size()][destCGCrdList.size()];
            //建立矩阵保存textSim结果
            float[][] textSimMatrix = new float[srcCGCrdList.size()][destCGCrdList.size()];

            i = -1;
            int mapCount = 0;

            //第一步，为每对srcCF与destCF计算textSim及CRDMatchLevel
            for (CRD srcCRD : srcCGCrdList){
                i++;
                j = -1;
                for (CRD destCRD : destCGCrdList){
                    j++;
                    if (!destCFMapped[j]){
                        CRDMatchLevel matchLevel = CreateCRDInfo.GetCRDMatchLevel(srcCRD, destCRD);
                        crdMatchMatrix[i][j] = matchLevel;
                        textSimMatrix[i][j] = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true); //最后一个参数指定是否忽略空行
                    }
                }
            }
 
            // 第二步，根据textSim及CRDMatchLevel，共同确定CF映射
            for (i = 0; i < srcCGCrdList.size(); i++){
                //跳过已映射的srcCF
                if (!srcCFMapped[i]){ 
                    int maxTextSimIndex = -1;   //用于记录最大的textSim对应的destCF索引
                    int maxMatchLevelIndex = -1;    //用于记录最高的CRDMatchLevel对应的destCF的索引
                    float maxTextSim = CreateCRDInfo.defaultTextSimTh;  //用于记录最大的textSim值，下限为阈值
                    CRDMatchLevel maxMatchLevel = CRDMatchLevel.DIFFERENT;
                    for (j = 0; j < destCGCrdList.size(); j++){                 
                        if (!destCFMapped[j]){             
                            if (textSimMatrix[i][j] >= maxTextSim)   //获取最大的textSim及索引
                            { maxTextSim = textSimMatrix[i][j]; maxTextSimIndex = j; }
                            if (crdMatchMatrix[i][j].ordinal() > maxMatchLevel.ordinal())   //获取最高的CRDMatchLevel及索引
                            { maxMatchLevel = crdMatchMatrix[i][j]; maxMatchLevelIndex = j; }
                        }
                        else continue; 
                    }
                    if (maxTextSimIndex > -1 || maxMatchLevelIndex > -1){  //如果找到
                        int finalIndex;
                        if(maxTextSimIndex == -1){
                        	finalIndex = maxMatchLevelIndex;
                        }else if(maxMatchLevelIndex == -1){
                        	finalIndex = maxTextSimIndex;
                        }else if (maxTextSimIndex == maxMatchLevelIndex){  //两个最大在同一个destCF上，则创建映射       
                            finalIndex = maxTextSimIndex;
                        }else if (crdMatchMatrix[i][maxTextSimIndex].ordinal() < maxMatchLevel.ordinal()){    
                            finalIndex = maxMatchLevelIndex;
                        }else if (textSimMatrix[i][maxMatchLevelIndex] < maxTextSim){  
                            finalIndex = maxTextSimIndex;
                        }else{//当根据textSim和CRDMatchLevel仍无法确定时，选index最接近i的一个
                            if (Math.abs(maxTextSimIndex - i) < Math.abs(maxMatchLevelIndex - i))
                            { finalIndex = maxTextSimIndex; }
                            else
                            { finalIndex = maxMatchLevelIndex; }
                        }
                        CloneFragmentMapping cfMapping = new CloneFragmentMapping();
                        cfMapping.ID = String.valueOf(++mapCount);
                        cfMapping.SrcCFID = String.valueOf(i + 1);
                        cfMapping.DestCFID = String.valueOf(finalIndex + 1);
                        cfMapping.textSim = textSimMatrix[i][finalIndex];
                        cfMapping.CrdMatchLevel = crdMatchMatrix[i][finalIndex];
                        cfMappingList.add(cfMapping);
                        srcCFMapped[i] = true;
                        destCFMapped[finalIndex] = true;
                    }
                }
            }
        }
        return cfMappingList;
    }
}

public class CreateMappingInfo extends Mapping{
	private String srcFileName;  //用于映射的源文件（-withCRD.xml文件）名
	private String destFileName;

	private List<CloneGroupMapping> CGMapList;
	private List UnMappedSrcCGList;
	private List UnMappedDestCGList;

	public void MapBetweenVersions(List<CloneGroup> srcGroupList, List<CloneGroup> destGroupList){//参数为一个版本的CloneGroup

		//置初始状态
		CGMapList = new ArrayList<CloneGroupMapping>();
		UnMappedSrcCGList = null;
        UnMappedDestCGList = null;
        int mapCount = 0; //用于统计CG映射的数量
      
        int[][] cgMatchLevelMatrix = new int[srcGroupList.size()][destGroupList.size()];
        float[][] cgLocationOverlapMatrix = new float[srcGroupList.size()][destGroupList.size()];
        for(int i=0;i<srcGroupList.size();i++){
        	for(int j=0;j<destGroupList.size();j++){
        		cgMatchLevelMatrix[i][j] = -1; //-1表示未计算matchLevel
        		cgLocationOverlapMatrix[i][j] = -1;
        	}
        }
        
        boolean[] isSrcCGMapped = new boolean[srcGroupList.size()];   //源克隆群映射标记数组
        boolean[] isDestCGMapped = new boolean[destGroupList.size()];   //目标克隆群映射标记数组
        for (int srcIndex = 0; srcIndex < srcGroupList.size(); srcIndex++)
        	isSrcCGMapped[srcIndex] = false; 
        for (int destIndex = 0; destIndex < destGroupList.size(); destIndex++)
        	isDestCGMapped[destIndex] = false; 
        
        //第一轮映射（在METHODINFOMATCH级别上映射）
        for(int i=0;i<srcGroupList.size();i++){
        	for(int j=0;j<destGroupList.size();j++){
        		if(!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j),CRDMatchLevel.METHODINFOMATCH)){
        			//如果两个CG匹配，则构造一个CG映射关系对象，加入映射列表中
                    CloneGroupMapping cgMapping = new CloneGroupMapping();
                    mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                    //将此CG映射关系加入CGMapList中
                    this.CGMapList.add(cgMapping);
                    isSrcCGMapped[i] = true;
                    isDestCGMapped[j] = true; //置已映射标记为真
                    break;  //源克隆群找到对应的（一个）CG后，不再继续寻找
                }
                else continue;
        	}
        }
		
        //第二轮映射（在METHODNAMEMATCH级别上映射，与第一轮基本相似）
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		for(int j=0;j<destGroupList.size();j++){
        			 if (!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.METHODNAMEMATCH)){
        				 CloneGroupMapping cgMapping = new CloneGroupMapping();
        				 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                         this.CGMapList.add(cgMapping);
                         isSrcCGMapped[i] = true;
                         isDestCGMapped[j] = true;
                         break;
                     }
        			 else continue;
        		}
        	}
        	else continue;
        }
        
        //第三轮映射（在FILECLASSMATCH级别上映射，与第一轮基本相似）
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		for(int j=0;j<destGroupList.size();j++){
        			 if (!isDestCGMapped[j] && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.FILECLASSMATCH)){
        				 CloneGroupMapping cgMapping = new CloneGroupMapping();
        				 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                         this.CGMapList.add(cgMapping);
                         isSrcCGMapped[i] = true;
                         isDestCGMapped[j] = true;
                         break;
                     }
        			 else continue;
        		}
        	}
        	else continue;
        }
        
        //将前三轮后未被映射的源克隆群加入UnMappedSrcCGList
        for(int i=0;i<srcGroupList.size();i++){
        	if(!isSrcCGMapped[i]){
        		if (this.UnMappedSrcCGList == null)
        			this.UnMappedSrcCGList = new MappingList(); 
                CGInfo info = new CGInfo();
                info.id = String.valueOf(srcGroupList.get(i).getCGID());
                info.size = srcGroupList.get(i).getNumberofCF();
                
                this.UnMappedSrcCGList.add(info);
        	}
        }
        
        //第四轮映射（处理一对多的情况，在METHODNAMEMATCH级别上考查，与前几轮类似，只是从dest到src方向）
        for(int j=0;j<destGroupList.size();j++){
        	if(!isDestCGMapped[j]){ //只考察未被映射的destCG
        		for(int i=0;i<srcGroupList.size();i++){ 
        			//只考察已映射的源克隆群（若不满足，则不进行后面比较）
        			 if (isSrcCGMapped[i]  && IsCGMatch(srcGroupList.get(i),destGroupList.get(j), CRDMatchLevel.METHODNAMEMATCH)){
        				 //在现存的映射列表中查找srcClassEle所在的映射，创建一个映射插入它后面的位置，使具有统一源的两个映射位置上相邻
        				 int mapIndex = -1;
                         for (CloneGroupMapping cgMap : this.CGMapList){
                             mapIndex++;
                             String classId = String.valueOf(srcGroupList.get(i).getCGID());
                             if (classId != null && cgMap.srcCGInfo.id.equals(classId)){
                                 CloneGroupMapping cgMapping = new CloneGroupMapping();
                                 mapCount = cgMapping.CreateCGMapping(srcGroupList.get(i), destGroupList.get(j),mapCount);
                                 //将此CG映射关系加入CGMapList中mapIndex+1的位置
                                 this.CGMapList.add(mapIndex + 1, cgMapping);
                                 isDestCGMapped[j] = true; //置已映射标记为真                                       
                                 break;
                             }
                         }
                         break;
                     }
        			 else continue;
        		}
        	}
        }
        
        //将未找到源克隆群的目标克隆群加入UnMappedDestCGList
        for(int j=0;j<destGroupList.size();j++){
        	if (!isDestCGMapped[j]){
                if (this.UnMappedDestCGList == null)
                	this.UnMappedDestCGList = new MappingList();
                CGInfo info = new CGInfo();
                info.id = String.valueOf(destGroupList.get(j).getCGID());
                info.size = destGroupList.get(j).getNumberofCF();

            	this.UnMappedDestCGList.add(info);
            }
        }
	}
	

	private boolean IsCGMatch(CloneGroup srcCG, CloneGroup destCG, CRDMatchLevel matchLevelThres){ // 根据CRD匹配级别及位置覆盖率，辅以文本相似度，判断两个CG是否构成映射
		
        float overlapTh = -1;  //指定覆盖率阈值   
        for(CloneFragment srcCF :  srcCG.getClonefragment()){
        	CRD srcCRD = srcCF.getCRD();
        	for(CloneFragment destCF : destCG.getClonefragment()){
        		CRD destCRD = destCF.getCRD();
        		CRDMatchLevel matchLevel = CreateCRDInfo.GetCRDMatchLevel(srcCRD, destCRD);
        		if ( matchLevel.ordinal() >= matchLevelThres.ordinal()){
                    if (matchLevelThres.ordinal() >= CRDMatchLevel.METHODNAMEMATCH.ordinal()){
                   	 
                        if (matchLevelThres.ordinal() == CRDMatchLevel.METHODINFOMATCH.ordinal()){ 
                        	overlapTh = CreateCRDInfo.locationOverlap1; 
                        }
                        else if (matchLevelThres.ordinal() == CRDMatchLevel.METHODNAMEMATCH.ordinal()){ 
                        	overlapTh = CreateCRDInfo.locationOverlap2; 
                        }

                        float overlap = CreateCRDInfo.GetLocationOverlap(srcCRD, destCRD);  //计算overlap
                        if (overlap >= overlapTh)return true; 
                        else{//当位置覆盖率不能判断时，使用文本相似度             	 
                            float textSim = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true);
                            if (textSim >= CreateCRDInfo.defaultTextSimTh)	return true; 
                        }
                    }
                    if (matchLevelThres.ordinal() == CRDMatchLevel.FILECLASSMATCH.ordinal()){    //在FILECLASSMATCH级别上直接使用文本相似度
                    
                        float textSim = CreateCRDInfo.GetTextSimilarity(srcCRD, destCRD, true);
                        if (textSim >= CreateCRDInfo.defaultTextSimTh)	return true;
                    }
                }
        	}
        }
        
        return false;
	}
	
	//识别克隆群的进化模式 
	// 新的进化模式识别方法
	
	//模式定义为1,2,3
	//1克隆组内全部克隆代码片段发生相同的变化为一致性变化，最小的定义,定义1目前有问题
	//2克隆组内至少有两个克隆代码片段发生相同的变化为一致性变化，中间的定义
	//3克隆组内只要同时发生变化就是一致性变化，最大的定义
	public void RecognizeEvolutionPattern(){
    	boolean[] atGroupFlag;
        if (this.CGMapList.size() > 0){
        	atGroupFlag = new boolean[this.CGMapList.size()];		
        	for (CloneGroupMapping cgMap : this.CGMapList) {

    			//模式定义识别根据克隆组大小和映射数量进行对比
    			//根据已经映射上的进行判断一致性变化或者不一致变化
    	
				if (cgMap.EvoPattern == null)
					cgMap.EvoPattern = new EvolutionPattern();

				//数量不变，并且全部映射上

				if ((cgMap.srcCGInfo.size == cgMap.destCGInfo.size) && (cgMap.srcCGInfo.size == cgMap.CFMapList.size())){	
					
					cgMap.EvoPattern.setSAME(true);// 识别SAME模式
					
					/*
					 //定义1 克隆组内全部克隆代码片段发生相同的变化为一致性变化，最小的定义			
					

					
					//克隆组内全部克隆代码片段发生相同的变化为一致性变化
					//有问题，数量变化
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.007) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
						else cgMap.EvoPattern.setSTATIC(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//定义2 克隆组内至少有两个克隆代码片段发生相同的变化为一致性变化，中间的定义						
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//设相同变化的阈值,原来默认为0.0000001，现在调整为0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.0000001 && Math.abs(textSim_n - 1) <= 0.0000001){
								if (Static)	Static = true; 
							} else {
								if (Static)	Static = false; 
								}
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true);
						} 
					}
					
					/*
					//3克隆组内只要同时发生变化就是一致性变化，最大的定义
					int chanFraCount = 0;
					int unChagFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}else if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim == 1) {
							unChagFraCount ++;
						}
					}
					if(unChagFraCount == cgMap.CFMapList.size()) {
						cgMap.EvoPattern.setSTATIC(true);
					} else if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
						 */
				}
				
				//映射上的小于目的克隆组
				if ((cgMap.CFMapList.size() < cgMap.destCGInfo.size)){
									
					cgMap.EvoPattern.setADD(true);// 识别ADD模式
					
					/*
					 //定义1 克隆组内全部克隆代码片段发生相同的变化为一致性变化，最小的定义	
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.0000001) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//定义2 克隆组内至少有两个克隆代码片段发生相同的变化为一致性变化，中间的定义	
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//设相同变化的阈值,原来默认为0.0000001，现在调整为0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.007 && Math.abs(textSim_n - 1) <= 0.007){
								if (Static)	Static = true; 
							} else {if (Static)	Static = false; }
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true);
						} 
					}
					
					/*
					//3克隆组内只要同时发生变化就是一致性变化，最大的定义
					int chanFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}
					}
					if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}*/
				}
	            
				//映射上小于源克隆组
				if ((cgMap.CFMapList.size() < cgMap.srcCGInfo.size)) {
					
					cgMap.EvoPattern.setSUBSTRACT(true);// 识别SUBSTRACT模式
					
					/*
					 //定义1 克隆组内全部克隆代码片段发生相同的变化为一致性变化，最小的定义	
					boolean equal = true;
					float textSim = ((CloneFragmentMapping)cgMap.CFMapList.get(0)).textSim;
					for (int k = 1; k < cgMap.CFMapList.size(); k++) {
						if (Math.abs(((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim - textSim) > 0.0000001) {
							equal = false;
							break;
						}
					}
					if(equal){
						if(textSim < 1) cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					}else { 
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}
					*/
					
					//定义2 克隆组内至少有两个克隆代码片段发生相同的变化为一致性变化，中间的定义	
					boolean Static = true;
					for(int m=0;m<cgMap.CFMapList.size()-1; m++){
						float textSim_m = ((CloneFragmentMapping) cgMap.CFMapList.get(m)).textSim;
						for(int n=m+1;n<cgMap.CFMapList.size(); n++){
							float textSim_n = ((CloneFragmentMapping) cgMap.CFMapList.get(n)).textSim;
							if (textSim_m < 1 && textSim_n < 1) {
								//设相同变化的阈值,原来默认为0.0000001，现在调整为0.007
								if(Math.abs(textSim_m - textSim_n) <= 0.007 ) {
									cgMap.EvoPattern.setCONSISTENTCHANGE(true);
								}
								Static = false;
							} else if(Math.abs(textSim_m - 1) <= 0.007 && Math.abs(textSim_n - 1) <= 0.007){
								if (Static)	Static = true; 
							} else {if (Static)	Static = false; }
						}
					}
					if(Static) {
						cgMap.EvoPattern.setSTATIC(true);
					} else {
						if(!cgMap.EvoPattern.isCONSISTENTCHANGE()) {cgMap.EvoPattern.setINCONSISTENTCHANGE(true);} 
					}
					
					/*
					//3克隆组内只要同时发生变化就是一致性变化，最大的定义
					int chanFraCount = 0;
					for (int k = 0; k < cgMap.CFMapList.size(); k++) {
						if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
							chanFraCount ++;
						}
					}
					if(chanFraCount >= 2){
						cgMap.EvoPattern.setCONSISTENTCHANGE(true);
					} else {
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true); 
					}*/
				}

				
			/*	
				// 识别STATIC模式  --- 新版本旧版本中都存在的克隆没有改变
				boolean textSameFlag = true; // 标记各个CFMap的文本是否完全相同
				for (int k = 0; k < cgMap.CFMapList.size(); k++) {
					if (((CloneFragmentMapping) cgMap.CFMapList.get(k)).textSim < 1) {
						textSameFlag = false;
						break;
					}
				}
				if (textSameFlag)
					cgMap.EvoPattern.setSTATIC(true);
			*/

				// 识别SPLIT模式
				// 在进行克隆群映射时，已将具有相同源的映射放在相邻的位置，因此只需要检查紧邻当前映射的几个映射即可
				int i = this.CGMapList.indexOf(cgMap);
				if (!atGroupFlag[i] && i < this.CGMapList.size() - 1) {
					// 当后面CGMap与当前cgMap同源的时候，继续检查
					while (i < this.CGMapList.size() - 1
							&& ((CloneGroupMapping) this.CGMapList.get(i + 1)).srcCGInfo.id == cgMap.srcCGInfo.id) {
						if (cgMap.EvoPattern.getMapGroupIDs() == null) { // 默认值（未赋值）为null
							cgMap.EvoPattern.setMapGroupIDs(cgMap.ID);
							cgMap.EvoPattern.setSPLIT(true);
							cgMap.EvoPattern.setINCONSISTENTCHANGE(true); // 若发生SPLIT模式，则必发生INCONSISTENTCHANGE模式
							atGroupFlag[i] = true;
						}
						cgMap.EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs() + ",");
						cgMap.EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs() + ((CloneGroupMapping) this.CGMapList.get(i + 1)).ID);
						i++;
					}
					// 将与当前cgMap同源的几个CGMap置为SPLIT状态，并置MapGroupIDs的值
					for (int j = this.CGMapList.indexOf(cgMap) + 1; j <= i; j++) {
						((CloneGroupMapping) this.CGMapList.get(j)).EvoPattern.setSPLIT(true);
						cgMap.EvoPattern.setINCONSISTENTCHANGE(true);// 若发生SPLIT模式，则必发生INCONSISTENTCHANGE模式
						((CloneGroupMapping) this.CGMapList.get(j)).EvoPattern.setMapGroupIDs(cgMap.EvoPattern.getMapGroupIDs());
						atGroupFlag[j] = true;
					}
				}
			}//for
			
        }
       
    }
	
	/*public void SaveMappingForGroup(int srcVersion,int destVersion){
		GroupMapping gSrcMap;
		GroupMapping gDestMap;

		for (CloneGroupMapping cgMap : this.CGMapList){
			gSrcMap = new GroupMapping(); //存放于SrcVersion,存Dest
			gDestMap = new GroupMapping();//存放于DestVersion,存Src
			//对应克隆组映射信息
			gSrcMap.setDestVersionID(destVersion);
			gSrcMap.setDestCGID(Integer.parseInt(cgMap.destCGInfo.id));
			gSrcMap.setDestCGSize(cgMap.destCGInfo.size);
			
			gDestMap.setSrcVersionID(srcVersion);
			gDestMap.setSrcCGID(Integer.parseInt(cgMap.srcCGInfo.id));
			gDestMap.setSrcCGSize(cgMap.srcCGInfo.size);
			
			//对应克隆片段映射信息
			List<FragmentMapping> fMapList = new ArrayList<FragmentMapping>();
			FragmentMapping fMap = new FragmentMapping();
			for(int k=0;k<cgMap.CFMapList.size();k++){
				fMap.setSrcCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).SrcCFID));
				fMap.setDestCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).DestCFID));
				fMap.setCRDMatchLevel((((CloneFragmentMapping)cgMap.CFMapList.get(k)).CrdMatchLevel).toString());
				fMap.setTextSim(((CloneFragmentMapping)cgMap.CFMapList.get(k)).textSim);
				
				fMapList.add(fMap);
			}
			
			//克隆组模式信息
			EvolutionPattern evoPattern = new EvolutionPattern();
			evoPattern.setSTATIC(cgMap.EvoPattern.isSTATIC());
			evoPattern.setSAME(cgMap.EvoPattern.isSAME());
			evoPattern.setADD(cgMap.EvoPattern.isADD());
			evoPattern.setSUBSTRACT(cgMap.EvoPattern.isSUBSTRACT());
			evoPattern.setCONSISTENTCHANGE(cgMap.EvoPattern.isCONSISTENTCHANGE());
			evoPattern.setINCONSISTENTCHANGE(cgMap.EvoPattern.isINCONSISTENTCHANGE());
			evoPattern.setSPLIT(cgMap.EvoPattern.isSPLIT());
			if(cgMap.EvoPattern.getMapGroupIDs() != null){
				evoPattern.setMapGroupIDs(String.valueOf(cgMap.EvoPattern.getMapGroupIDs()));
			}

			//加入到对应的Group中
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == srcVersion && group.getCGID() == Integer.parseInt(cgMap.srcCGInfo.id)){
					group.setDestGroupMapping(gSrcMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : fMapList){
							if(fragment.getCFID() == fraMap.getSrcCFid()){
								fragment.setDestFragmentMapping(fMap);//fMap.getSrcCFid 是他自 fragment.getCFID()
							}
						}
					}
				}
				else if(group.getVersionID() == destVersion && group.getCGID() == Integer.parseInt(cgMap.destCGInfo.id)){
					group.setSrcGroupMapping(gDestMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : fMapList){
							if(fragment.getCFID() == fraMap.getDestCFid()){
								fragment.setSrcFragmentMapping(fMap);
							}
						}
					}
				}
			}			
		}
	}*/
	
	public void SaveMappingForSys(int srcVersion,int destVersion){
		GroupMapping gMap=null;
		for (CloneGroupMapping cgMap : this.CGMapList){
			gMap = new GroupMapping();
			gMap.setSrcVersionID(srcVersion);
			gMap.setDestVersionID(destVersion);
			gMap.setSrcCGID(Integer.parseInt(cgMap.srcCGInfo.id));
			gMap.setDestCGID(Integer.parseInt(cgMap.destCGInfo.id));
			gMap.setSrcCGSize(cgMap.srcCGInfo.size);
			gMap.setDestCGSize(cgMap.destCGInfo.size);
			
			List<FragmentMapping> fMapList = new ArrayList<FragmentMapping>();
			FragmentMapping fMap=null;
			for(int k=0;k<cgMap.CFMapList.size();k++){
				fMap = new FragmentMapping();
				fMap.setSrcCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).SrcCFID));
				fMap.setDestCFid(Integer.parseInt(((CloneFragmentMapping)cgMap.CFMapList.get(k)).DestCFID));
				fMap.setCRDMatchLevel((((CloneFragmentMapping)cgMap.CFMapList.get(k)).CrdMatchLevel).toString());
				fMap.setTextSim(((CloneFragmentMapping)cgMap.CFMapList.get(k)).textSim);
				
				fMapList.add(fMap);
			}
			gMap.setFragMapList(fMapList);
			
			EvolutionPattern evoPattern = new EvolutionPattern();
			evoPattern.setSTATIC(cgMap.EvoPattern.isSTATIC());
			evoPattern.setSAME(cgMap.EvoPattern.isSAME());
			evoPattern.setADD(cgMap.EvoPattern.isADD());
			evoPattern.setSUBSTRACT(cgMap.EvoPattern.isSUBSTRACT());
			evoPattern.setCONSISTENTCHANGE(cgMap.EvoPattern.isCONSISTENTCHANGE());
			evoPattern.setINCONSISTENTCHANGE(cgMap.EvoPattern.isINCONSISTENTCHANGE());
			evoPattern.setSPLIT(cgMap.EvoPattern.isSPLIT());
			if(cgMap.EvoPattern.getMapGroupIDs() != null){
				evoPattern.setMapGroupIDs(String.valueOf(cgMap.EvoPattern.getMapGroupIDs()));
			}
			gMap.setEvolutionPattern(evoPattern);
			
			VariationInformation.mappingInfo.add(gMap);//加入到全局变量
			
			//将map加入到对应的Group中
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == srcVersion && group.getCGID() == Integer.parseInt(cgMap.srcCGInfo.id)){
					group.setDestGroupMapping(gMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : gMap.getFragMapList()){
							if(fragment.getCFID() == fraMap.getSrcCFid()){
								fragment.setDestFragmentMapping(fraMap);//fMap.getSrcCFid 是他自 fragment.getCFID()
							}
						}
					}
				}
				else if(group.getVersionID() == destVersion && group.getCGID() == Integer.parseInt(cgMap.destCGInfo.id)){
					group.setSrcGroupMapping(gMap);
					for(CloneFragment fragment : group.getClonefragment()){
						for(FragmentMapping fraMap : gMap.getFragMapList()){
							if(fragment.getCFID() == fraMap.getDestCFid()){
								fragment.setSrcFragmentMapping(fraMap);
							}
						}
					}
				}
			}
		}
		
		if(this.UnMappedSrcCGList != null){
			for(int k=0;k<this.UnMappedSrcCGList.size();k++){
				gMap = new GroupMapping();
				gMap.setSrcVersionID(srcVersion);
				gMap.setSrcCGID(Integer.parseInt(((CGInfo)this.UnMappedSrcCGList.get(k)).id));
				gMap.setSrcCGSize(((CGInfo)this.UnMappedSrcCGList.get(k)).size);
				
				VariationInformation.unMappedSrcInfo.add(gMap); 
				//System.out.println("unMappedSrcCGVersion " + srcVersion + " CGID " + gMap.getSrcCGID());
			}
		}

		
		if(this.UnMappedDestCGList != null){
			for(int k=0;k<this.UnMappedDestCGList.size();k++){
				gMap = new GroupMapping();
				gMap.setDestVersionID(destVersion);
				gMap.setDestCGID(Integer.parseInt(((CGInfo)this.UnMappedDestCGList.get(k)).id));
				gMap.setDestCGSize(((CGInfo)this.UnMappedDestCGList.get(k)).size);
				
				VariationInformation.unMappedDestInfo.add(gMap);
				//System.out.println("unMappeddestCGVersion" + gMap.getDestVersionID() + " CGID " + gMap.getDestCGID());
			}
		}
				
	}
	
}
