package CloneRepresentation;

import java.util.List;

class InfoStruct{  //保存进化关系中源和目标信息的结构

    public String version; //CG所在版本（crd文件的文件名，不含路径）
    public String cgid;    //CG的ID
    public int size;    //CG含有的CF数量
}

public class GenealogyEvolution {
	private String ID;
    //保存继承关系
    private String parentID;    //保存本进化的父亲的id

    private String childID;   //保存本进化的后代的id
   
    //进化关系的源信息
    private int srcVersion;
    private int srcCGID;
    private int srcSize;

    //进化关系的目标信息
    private int destVersion;
    private int destCGID;
    private int destSize;
    
    private String cgPattern;
    
    //根据CGMap对象构造Evolution对象。源和目标文件的信息要传入。使用并更新当前的evolutionList（确定父子关系）
    public int BuildFromCGMap(GroupMapping cgMap,int id, List<GenealogyEvolution> evolutionList){
    	this.ID = String.valueOf(++id);
    	
        this.srcVersion = cgMap.getSrcVersionID();
        this.srcCGID = cgMap.getSrcCGID();
        this.srcSize = cgMap.getSrcCGSize();
        this.destVersion = cgMap.getDestVersionID();
        this.destCGID = cgMap.getDestCGID();
        this.destSize = cgMap.getDestCGSize();

        //构造pattern成员，多个进化模式之间以空格分隔
        this.cgPattern = "";
        if (cgMap.getEvolutionPattern().isSTATIC())
        { this.cgPattern += "STATIC"; }
        if (cgMap.getEvolutionPattern().isSAME())
        { this.cgPattern += "+SAME"; }
        if (cgMap.getEvolutionPattern().isADD())
        { this.cgPattern += "+ADD"; }
        if (cgMap.getEvolutionPattern().isSUBSTRACT())
        { this.cgPattern += "+DELETE"; }
        if (cgMap.getEvolutionPattern().isCONSISTENTCHANGE())
        { this.cgPattern += "+CONSISTENTCHANGE"; }
        if (cgMap.getEvolutionPattern().isINCONSISTENTCHANGE())
        { this.cgPattern += "+INCONSISTENTCHANGE"; }
        if (cgMap.getEvolutionPattern().isSPLIT())
        { this.cgPattern += "+SPLIT"; }
        //构造父子成员
        this.parentID = null;   //若有parent，则在下面被置值，否则此项为null
        this.childID = null;
        
        //在当前的evolutionList中查找destInfo与当前对象的srcInfo相同的对象，确定父子继承关系
        for (int i = 0; i < evolutionList.size(); i++){
            if ((evolutionList.get(i).destVersion == this.srcVersion) && (evolutionList.get(i).destCGID == this.srcCGID)){
                if (evolutionList.get(i).childID == null){
                    evolutionList.get(i).childID = this.ID;
                }
                else
                { evolutionList.get(i).childID += "+" + this.ID; }    //多个ID之间以+分隔
                this.parentID = evolutionList.get(i).ID;
                //break;
            }
        }
        
        return Integer.parseInt(this.ID);
   }

	public String getCgPattern() {
		return cgPattern;
	}

	public void setCgPattern(String cgPattern) {
		this.cgPattern = cgPattern;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getChildID() {
		return childID;
	}

	public void setChildID(String childID) {
		this.childID = childID;
	}

	public int getSrcVersion() {
		return srcVersion;
	}

	public void setSrcVersion(int srcVersion) {
		this.srcVersion = srcVersion;
	}

	public int getSrcCGID() {
		return srcCGID;
	}

	public void setSrcCGID(int srcCGID) {
		this.srcCGID = srcCGID;
	}

	public int getSrcSize() {
		return srcSize;
	}

	public void setSrcSize(int srcSize) {
		this.srcSize = srcSize;
	}

	public int getDestVersion() {
		return destVersion;
	}

	public void setDestVersion(int destVersion) {
		this.destVersion = destVersion;
	}

	public int getDestCGID() {
		return destCGID;
	}

	public void setDestCGID(int destCGID) {
		this.destCGID = destCGID;
	}

	public int getDestSize() {
		return destSize;
	}

	public void setDestSize(int destSize) {
		this.destSize = destSize;
	}
    
    
    
}
