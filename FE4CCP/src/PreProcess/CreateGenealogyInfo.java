package PreProcess;

import java.util.ArrayList;
import java.util.List;

import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.GenealogyEvolution;
import CloneRepresentation.GroupMapping;
import Global.VariationInformation;

class CGMapInfo{//定义克隆家系中保存的CGMap信息
    public String id;   //id其实是访问map文件中CGMap元素的“指针”
    public String pattern;
}

public class CreateGenealogyInfo {
	
	public List<GenealogyEvolution> evoGeneaList;
	public int[] evoPatternCount;
	private int id = 0;
	private int endIndex = -1;
	
	/*private boolean IsMappingVersionSuccessive() {
		for (GroupMapping mapping : VariationInformation.mappingInfo) {
			if (mapping.getDestVersionID() - mapping.getSrcVersionID() != 1)
				return false;
		}
		return true;
	}*/
	
	public void CreateGenealogyForAll(){
		/*if(!this.IsMappingVersionSuccessive()){
			System.out.println("版本不连续。。。...");
			return;
		}*/
        //寻找每个版本中新产生的CG，以其为根建立克隆家系
		int prev = 0; // 记录前一个被构造的CG的id，避免重复构造（针对分裂的情况）	
		for (GroupMapping mapping : VariationInformation.mappingInfo) {
			if (mapping.getSrcVersionID() == 0) {// 第一个，为源版本中所有克隆群构建以其为根的克隆家系
				
				if (mapping.getSrcCGID() != prev) {

					CloneGenealogy cloneGenealogy = new CloneGenealogy();
					cloneGenealogy.setStartVersion(0);// 家系起始版本
					cloneGenealogy.setRootCGid(mapping.getSrcCGID());// 根克隆群id

					endIndex = -1;
					id = 0;
					evoGeneaList = new ArrayList<GenealogyEvolution>();// 保存进化关系列表
					this.BuildGenealogyEvolution(mapping);

					cloneGenealogy.setEndVersion(cloneGenealogy.getStartVersion() + endIndex);// 终止版本
					cloneGenealogy.setAge(endIndex + 1);// 寿命

					// 统计各种进化模式出现的次数，保存在evoPatternCount数组中
					this.evoPatternCount = new int[7];
					for (GenealogyEvolution evolution : this.evoGeneaList) {
						if (evolution.getCgPattern().contains("STATIC")) {
							this.evoPatternCount[0]++;
						}
						if (evolution.getCgPattern().contains("SAME")) {
							this.evoPatternCount[1]++;
						}
						if (evolution.getCgPattern().contains("ADD")) {
							this.evoPatternCount[2]++;
						}
						if (evolution.getCgPattern().contains("DELETE")) {
							this.evoPatternCount[3]++;
						}
						if (!evolution.getCgPattern().contains("INCONSISTENTCHANGE") && evolution.getCgPattern().contains("CONSISTENTCHANGE")) {
							this.evoPatternCount[4]++;
						}
						if (evolution.getCgPattern().contains("INCONSISTENTCHANGE")) {
							this.evoPatternCount[5]++;
						}
						if (evolution.getCgPattern().contains("SPLIT")) {
							this.evoPatternCount[6]++;
						}
					}
					cloneGenealogy.setEvoPatternCount(evoPatternCount);// 报错进化模式出现次数
					cloneGenealogy.setEvolutionList(evoGeneaList);// 保存进化关系列表
					prev = mapping.getSrcCGID();
					VariationInformation.cloneGenealogy.add(cloneGenealogy);
				}
			}
		}
		
        //第一个版本中没映射上的Src克隆组
        if(VariationInformation.unMappedSrcInfo.size() != 0){
            for(GroupMapping singleMapping : VariationInformation.unMappedSrcInfo){
            	if(singleMapping.getSrcVersionID() == 0){
            		CloneGenealogy singleCG = new CloneGenealogy();
            		singleCG.setStartVersion(singleMapping.getSrcVersionID());
            		singleCG.setRootCGid(singleMapping.getSrcCGID());
            		System.out.println("第一个版本没映射的Single" + singleMapping.getSrcVersionID());
            		VariationInformation.singleCgGenealogyList.add(singleCG);
            	}
            }
        }
      
        //所有版本中没映射上的dest克隆组
        if(VariationInformation.unMappedDestInfo.size() != 0){
        	/*	for(GroupMapping destMapping : VariationInformation.unMappedDestInfo){
        		System.out.println("版本   " + destMapping.getDestVersionID() + "   组ID " + destMapping.getDestCGID() + "   组Size " + destMapping.getDestCGSize());
        	}
        	*/
        	for(GroupMapping destMapping : VariationInformation.unMappedDestInfo){
        		CloneGenealogy cloneGenealogy = new CloneGenealogy();
            	cloneGenealogy.setStartVersion(destMapping.getDestVersionID());//家系起始版本
            	cloneGenealogy.setRootCGid(destMapping.getDestCGID());//根克隆群id
            	
            	endIndex = -1;
            	id = 0;
            	evoGeneaList = new ArrayList<GenealogyEvolution>();//保存进化关系列表
            	this.BuildGenealogyEvolution(destMapping);
            	
            	if(this.endIndex > 0){
            		cloneGenealogy.setEndVersion(cloneGenealogy.getStartVersion() + endIndex);//终止版本
                	cloneGenealogy.setAge(endIndex+1);//寿命           	
                	
                	//统计各种进化模式出现的次数，保存在evoPatternCount数组中
                    this.evoPatternCount = new int[7];
                    for (GenealogyEvolution evolution : this.evoGeneaList){
                        if (evolution.getCgPattern().contains("STATIC") )
                        { this.evoPatternCount[0]++; }
                        if (evolution.getCgPattern().contains("SAME"))
                        { this.evoPatternCount[1]++; }
                        if (evolution.getCgPattern().contains("ADD"))
                        { this.evoPatternCount[2]++; }
                        if (evolution.getCgPattern().contains("DELETE"))
                        { this.evoPatternCount[3]++; }
                        if (!evolution.getCgPattern().contains("INCONSISTENTCHANGE") && evolution.getCgPattern().contains("CONSISTENTCHANGE"))
                        { this.evoPatternCount[4]++; }
                        if (evolution.getCgPattern().contains("INCONSISTENTCHANGE"))
                        { this.evoPatternCount[5]++; }
                        if (evolution.getCgPattern().contains("SPLIT"))
                        { this.evoPatternCount[6]++; }
                    }
                    cloneGenealogy.setEvoPatternCount(evoPatternCount);//报错进化模式出现次数
                	cloneGenealogy.setEvolutionList(evoGeneaList);//保存进化关系列表     	
                	VariationInformation.cloneGenealogy.add(cloneGenealogy);
            	} 	
            	else{
            		//System.out.println("I'm 其他版本没映射的 SingleGenealogy  " + cloneGenealogy.getStartVersion() + "  " + cloneGenealogy.getRootCGid());
            		VariationInformation.singleCgGenealogyList.add(cloneGenealogy);
            	}
        	}
        }

	}
        	
    // 生成以指定CG为根的克隆家系
    public void BuildGenealogyEvolution(GroupMapping mapping){
    	GenealogyEvolution gEvolu = new GenealogyEvolution ();
    	id = gEvolu.BuildFromCGMap(mapping, id, evoGeneaList);
    	endIndex++;
    	evoGeneaList.add(gEvolu);//加入进化关系列表
    	List<GroupMapping> targetMapping = new ArrayList<GroupMapping>();
    	for(GroupMapping tar : VariationInformation.mappingInfo){
    		if((mapping.getDestVersionID() == tar.getSrcVersionID()) && 
    				(mapping.getDestCGID() == tar.getSrcCGID())){
    			targetMapping.add(tar);
    		}
    	}
    	if(targetMapping.size() > 0){
    		for(GroupMapping tar : targetMapping){
    			this.BuildGenealogyEvolution(tar);
    		}	
    	}
    	return;
    }
}
