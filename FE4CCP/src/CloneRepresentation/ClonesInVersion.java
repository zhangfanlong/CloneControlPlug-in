package CloneRepresentation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZFL
 *某一个版本的克隆代码
 *1.文件读取功能
 *2.处理后存取
 *3.
 */
public class ClonesInVersion {

	private int NumberofCG;//克隆组个数
	private int NumberofCF;//克隆片段个数
	private int VersionID;//版本号
	private String SystemVersion;//系统版本名
	private List<CloneGroup> cloneGroup;
	
	
	public void setNumberofCG(int NumberofCG){
		this.NumberofCG=NumberofCG;
	}
	
	public int getNumberofCG(){
		return NumberofCG;
	}
	
	public void setNumberofCF(int NumberofCF){
		this.NumberofCF=NumberofCF;
	}
	
	public int getNumberofCF(){
		return NumberofCF;
	}
	
	public void setVersionID(int VersionID){
		this.VersionID=VersionID;
	}
	
	public int getVersionID(){
		return VersionID;
	}
	
	public void setSystemVersion(String SystemVersion){
		this.SystemVersion=SystemVersion;
	}
	
	public String getSystemVersion(){
		return SystemVersion;
	}
	
	public List<CloneGroup> getCloneGroup() {
		return cloneGroup;
	}

	public void setCloneGroup(List<CloneGroup> cloneGroup) {
		this.cloneGroup = new ArrayList<CloneGroup>();
		this.cloneGroup = cloneGroup;
	}

	
	
	
}
