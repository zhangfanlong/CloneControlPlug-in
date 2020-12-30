package CloneRepresentation;

import java.util.ArrayList;
import java.util.List;

public class CloneGroup {
	private int CGID;//克隆组ID
	private int VersionID;//版本号
	private int NumberofCF;//克隆数量	
	private int similarity;//克隆组内相似度
	private List<CloneFragment> cloneFragment;//所有的克隆片段

	private GroupMapping srcGroupMapping;//克隆组映射信息
	private GroupMapping destGroupMapping;
	
	
	public void setCGID(int cGID) {
		CGID = cGID;
	}
	
	public int getCGID() {
		return CGID;
	}
	
	public int getVersionID() {
		return VersionID;
	}

	public void setVersionID(int VersionID) {
		this.VersionID = VersionID;
	}

	public List<CloneFragment> getClonefragment() {
		return cloneFragment;
	}

	public void setClonefragment(List<CloneFragment> clonefragment) {
		this.cloneFragment = new ArrayList<CloneFragment>();
		this.cloneFragment = clonefragment;
	}

	public int getNumberofCF() {
		return NumberofCF;
	}

	public void setNumberofCF(int NumberofCF) {
		this.NumberofCF = NumberofCF;
	}

	public void setSimilarity(int similarity){
		this.similarity = similarity;
	}
	
	public int getSimilarity(){
		return similarity;
	}

	public GroupMapping getSrcGroupMapping() {
		return srcGroupMapping;
	}

	public void setSrcGroupMapping(GroupMapping srcGroupMapping) {
		this.srcGroupMapping = new GroupMapping();
		this.srcGroupMapping = srcGroupMapping;
	}

	public GroupMapping getDestGroupMapping() {
		return destGroupMapping;
	}

	public void setDestGroupMapping(GroupMapping destGroupMapping) {
		this.destGroupMapping = new GroupMapping();
		this.destGroupMapping = destGroupMapping;
	}


	
}
