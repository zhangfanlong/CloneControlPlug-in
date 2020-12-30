package CloneRepresentation;

import java.util.List;


public class CloneGenealogy {
	private int startVersion;//起始版本
	private int rootCGid;//根克隆群id
	
	private int endVersion;//终止版本
	
	private int age;//寿命（或当前年龄）
	  
	private int[] evoPatternCount;//各种进化模式出现次数统计

    private List<GenealogyEvolution> evolutionList;//保存进化关系列表
    
    

	public int getStartVersion() {
		return startVersion;
	}

	public void setStartVersion(int startVersion) {
		this.startVersion = startVersion;
	}

	public int getEndVersion() {
		return endVersion;
	}

	public void setEndVersion(int endVersion) {
		this.endVersion = endVersion;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public int[] getEvoPatternCount() {
		return evoPatternCount;
	}

	public void setEvoPatternCount(int[] evoPatternCount) {
		this.evoPatternCount = new int[7];
		this.evoPatternCount = evoPatternCount;
	}

	public int getRootCGid() {
		return rootCGid;
	}

	public void setRootCGid(int rootCGid) {
		this.rootCGid = rootCGid;
	}

	public List<GenealogyEvolution> getEvolutionList() {
		return evolutionList;
	}

	public void setEvolutionList(List<GenealogyEvolution> evolutionList) {
		this.evolutionList = evolutionList;
	}
	


}
