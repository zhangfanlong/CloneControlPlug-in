package CRDInformation;


public class BlockInfo {
	public  String bType;//块的类型
	public  String anchor;//块的标记：分支的条件，循环的终止条件，try和catch块的此项为空

	public BlockInfo(){
		bType = null;
		anchor = null;
	}
	public  boolean equals(BlockInfo  bInfo){
		if(bInfo == null) return false;
		if(!this.bType.equals(bInfo.bType))	return false;
		if(this.anchor == null || bInfo.anchor == null) return false;
		if(!this.anchor.equals(bInfo.anchor))	return false;
		return true;
	}
}
