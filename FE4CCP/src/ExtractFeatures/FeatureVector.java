package ExtractFeatures;

public class FeatureVector {
	
	//代码特征
	private int sourceLine;//克隆组代码行数
	private int fragCount; //克隆组数量

	//四个heathed度量
	private int uniOPERATORCount;//唯一操作符数量，即操作符种类
    private int uniOperandCount;//唯一操作数数量，即操作数种类
    private int totalOPERATORCount;//操作符总量
    private int totalOperandCount;//操作数总量   
    
    //函数信息
  	private int totalMethodInvocCount;//函数调用
  	private int libraryMethodInvocCount;//库函数调用
  	private int localMethodInvocCount;//本地函数调用
  	private int otherMethodInvocCount;//其他调用
  	private int totalParameterCount;//总参数个数
  	
  	//目的特征
  	private boolean isLocalClone;//是否是函数克隆
  	private float simFileName;//文件名相似度
  	private float simMaskedFileName;//文件名相似度MASK
  	
  	private float simMethodName;//方法名相似度
  	private float simTotalParaName;//参数名相似度之和
  	private float simMaxParaName;//最大参数名相似度
  	private float simTotalParaType;//
  	
  	private boolean isSameBlockInfo;//..................
  	private double simCloneFragments;//..................
  	
  	//结构特征
  	private int[] struFeature;
	private int consisCount;
	
	
  	//历史变化序列统计
  	private int[] evoPattern;
  	private int age;
  	private int [] lastevoPattern;
  	//一致性维护标签
  	private int consistence;
  	
  	
  	
  	//后加的上版本代码属性
	//上一版本的各种代码属性
  	private int last_souceLines;
  	private int last_fragCount;
	private int last_totalParameterCount;
	private int last_totalMethodInvocCount;
	private int last_localMethodInvocCount;
	private int last_libraryMethodInvocCount;
	private int last_otherMethodInvocCount;
	private int last_uniOPERATORCount;
	private int last_uniOperandCount;
	private int last_totalOPERATORCount;
	private int last_totalOperandCount;
	private int[] structuralFeatureChanges_neighbor;
  	
	private int fromOrigin_souceLinesP;
	private int fromOrigin_fragCountP;
	private int fromOrigin_totalParameterCountP;
	private int fromOrigin_totalMethodInvocCountP;
	private int fromOrigin_localMethodInvocCountP;
	private int fromOrigin_libraryMethodInvocCountP;
	private int fromOrigin_otherMethodInvocCountP;
	private int fromOrigin_uniOPERATORCountP;
	private int fromOrigin_uniOperandCountP;
	private int fromOrigin_totalOPERATORCountP;
	private int fromOrigin_totalOperandCountP;
	private int[] structuralFeatureChanges_fromOriginP;
  	
	private int fromOrigin_souceLinesN;
	private int fromOrigin_fragCountN;
	private int fromOrigin_totalParameterCountN;
	private int fromOrigin_totalMethodInvocCountN;
	private int fromOrigin_localMethodInvocCountN;
	private int fromOrigin_libraryMethodInvocCountN;
	private int fromOrigin_otherMethodInvocCountN;
	private int fromOrigin_uniOPERATORCountN;
	private int fromOrigin_uniOperandCountN;
	private int fromOrigin_totalOPERATORCountN;
	private int fromOrigin_totalOperandCountN;
	private int[] structuralFeatureChanges_fromOriginN;
  	
  	public int[] getlastevoPattern() {
		return lastevoPattern;
	}
	public void setlastevoPattern(int[] evoPattern) {
		this.lastevoPattern = new int[6];
		this.lastevoPattern = evoPattern;
	}
  	
  	public int[] getEvoPattern() {
		return evoPattern;
	}
	public void setEvoPattern(int[] evoPattern) {
		this.evoPattern = new int[6];
		this.evoPattern = evoPattern;
	}
	public int getSourceLine() {
		return sourceLine;
	}
	public void setSourceLine(int sourceLine) {
		this.sourceLine = sourceLine;
	}
	
	public int getFragCount() {
		return fragCount;
	}
	public void setFragCount(int fragCount) {
		this.fragCount = fragCount;
	}
	public int getLast_totalParameterCount() {
		return last_totalParameterCount;
	}
	public int getFromOrigin_totalParameterCountP() {
		return fromOrigin_totalParameterCountP;
	}
	public void setLast_totalParameterCount(int last_totalParameterCount) {
		this.last_totalParameterCount = last_totalParameterCount;
	}
	public void setFromOrigin_totalParameterCountP(int fromOrigin_totalParameterCount) {
		this.fromOrigin_totalParameterCountP = fromOrigin_totalParameterCount;
	}
	
	public int getUniOPERATORCount() {
		return uniOPERATORCount;
	}
	public void setUniOPERATORCount(int uniOPERATORCount) {
		this.uniOPERATORCount = uniOPERATORCount;
	}
	public int getUniOperandCount() {
		return uniOperandCount;
	}
	public int getFromOrigin_fragCountP() {
		return fromOrigin_fragCountP;
	}
	public void setFromOrigin_fragCountP(int fromOrigin_fragCount) {
		this.fromOrigin_fragCountP = fromOrigin_fragCount;
	}
	public void setUniOperandCount(int uniOperandCount) {
		this.uniOperandCount = uniOperandCount;
	}
	public int getTotalOPERATORCount() {
		return totalOPERATORCount;
	}
	public void setTotalOPERATORCount(int totalOPERATORCount) {
		this.totalOPERATORCount = totalOPERATORCount;
	}
	public int getTotalOperandCount() {
		return totalOperandCount;
	}
	public void setTotalOperandCount(int totalOperandCount) {
		this.totalOperandCount = totalOperandCount;
	}
	public int getTotalMethodInvocCount() {
		return totalMethodInvocCount;
	}
	public void setTotalMethodInvocCount(int totalMethodInvocCount) {
		this.totalMethodInvocCount = totalMethodInvocCount;
	}
	public int getLibraryMethodInvocCount() {
		return libraryMethodInvocCount;
	}
	public void setLibraryMethodInvocCount(int libraryMethodInvocCount) {
		this.libraryMethodInvocCount = libraryMethodInvocCount;
	}
	public int getLocalMethodInvocCount() {
		return localMethodInvocCount;
	}
	public void setLocalMethodInvocCount(int localMethodInvocCount) {
		this.localMethodInvocCount = localMethodInvocCount;
	}
	public int getOtherMethodInvocCount() {
		return otherMethodInvocCount;
	}
	public void setOtherMethodInvocCount(int otherMethodInvocCount) {
		this.otherMethodInvocCount = otherMethodInvocCount;
	}
	public int getTotalParameterCount() {
		return totalParameterCount;
	}
	public void setTotalParameterCount(int totalParameterCount) {
		this.totalParameterCount = totalParameterCount;
	}
	public boolean isLocalClone() {
		return isLocalClone;
	}
	public void setLocalClone(boolean isLocalClone) {
		this.isLocalClone = isLocalClone;
	}
	public float getSimFileName() {
		return simFileName;
	}
	public void setSimFileName(float simFileName) {
		this.simFileName = simFileName;
	}
	public float getSimMaskedFileName() {
		return simMaskedFileName;
	}
	public void setSimMaskedFileName(float simMaskedFileName) {
		this.simMaskedFileName = simMaskedFileName;
	}
	public float getSimMethodName() {
		return simMethodName;
	}
	public void setSimMethodName(float simMethodName) {
		this.simMethodName = simMethodName;
	}
	public float getSimTotalParaName() {
		return simTotalParaName;
	}
	public float getSimMaxParaName() {
		return simMaxParaName;
	}
	public float getSimTotalParaType() {
		return simTotalParaType;
	}
	public void setSimTotalParaName(float simTotalParaName) {
		this.simTotalParaName = simTotalParaName;
	}
	public void setSimMaxParaName(float simMaxParaName) {
		this.simMaxParaName = simMaxParaName;
	}
	public void setSimTotalParaType(float simTotalParaType) {
		this.simTotalParaType = simTotalParaType;
	}
	public boolean getIsSameBlockInfo() {
		return isSameBlockInfo;
	}
	public double getSimCloneFragment() {
		return simCloneFragments;
	}
	public void setIsSameBlockInfo(boolean isSameBlockInfo) {
		this.isSameBlockInfo = isSameBlockInfo;
	}
	public void setSimCloneFragment(double simCloneFragment) {
		this.simCloneFragments = simCloneFragment;
	}
	
	public int[] getStruFeature() {
		return struFeature;
	}
	public void setStruFeature(int[] struFeature) {
		this.struFeature = new int[RelatedNodes.relevantNode.values().length];
		this.struFeature = struFeature;
	}
	public int getConsistence() {
		return consistence;
	}
	public void setConsistence(int consistence) {
		this.consistence = consistence;
	}

	public int[] getStructuralFeatureChanges_neighbor() {
		return structuralFeatureChanges_neighbor;
	}
	public void setStructuralFeatureChanges_neighbor(
			int[] structuralFeatureChanges_neighbor) {
		this.structuralFeatureChanges_neighbor = new int[RelatedNodes.relevantNode.values().length];
		this.structuralFeatureChanges_neighbor = structuralFeatureChanges_neighbor;
	}
	public int[] getStructuralFeatureChanges_fromOriginP() {
		return structuralFeatureChanges_fromOriginP;
	}
	
	public void setStructuralFeatureChanges_fromOriginP(
			int[] structuralFeatureChanges_fromOrigin) {
		this.structuralFeatureChanges_fromOriginP = new int[RelatedNodes.relevantNode.values().length];
		this.structuralFeatureChanges_fromOriginP = structuralFeatureChanges_fromOrigin;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public int getConsisCount() {
		return consisCount;
	}
	public void setConsisCount(int consisCount) {
		this.consisCount = consisCount;
	}
	public int getLast_souceLines() {
		return last_souceLines;
	}
	public int getLast_totalMethodInvocCount() {
		return last_totalMethodInvocCount;
	}
	public int getLast_localMethodInvocCount() {
		return last_localMethodInvocCount;
	}
	public int getLast_libraryMethodInvocCount() {
		return last_libraryMethodInvocCount;
	}
	public int getLast_otherMethodInvocCount() {
		return last_otherMethodInvocCount;
	}
	public int getLast_uniOPERATORCount() {
		return last_uniOPERATORCount;
	}
	public int getLast_uniOperandCount() {
		return last_uniOperandCount;
	}
	public int getLast_totalOPERATORCount() {
		return last_totalOPERATORCount;
	}
	public int getLast_totalOperandCount() {
		return last_totalOperandCount;
	}
	public int getFromOrigin_souceLinesP() {
		return fromOrigin_souceLinesP;
	}

	public int getFromOrigin_totalMethodInvocCountP() {
		return fromOrigin_totalMethodInvocCountP;
	}
	public int getFromOrigin_localMethodInvocCountP() {
		return fromOrigin_localMethodInvocCountP;
	}
	public int getFromOrigin_libraryMethodInvocCountP() {
		return fromOrigin_libraryMethodInvocCountP;
	}
	public int getFromOrigin_otherMethodInvocCountP() {
		return fromOrigin_otherMethodInvocCountP;
	}
	public int getFromOrigin_uniOPERATORCountP() {
		return fromOrigin_uniOPERATORCountP;
	}
	public int getFromOrigin_uniOperandCountP() {
		return fromOrigin_uniOperandCountP;
	}
	public int getFromOrigin_totalOPERATORCountP() {
		return fromOrigin_totalOPERATORCountP;
	}
	public int getFromOrigin_totalOperandCountP() {
		return fromOrigin_totalOperandCountP;
	}
	public void setLast_souceLines(int last_souceLines) {
		this.last_souceLines = last_souceLines;
	}
	public int getLast_fragCount() {
		return last_fragCount;
	}
	public void setLast_fragCount(int last_fragCount) {
		this.last_fragCount = last_fragCount;
	}
	public void setLast_totalMethodInvocCount(int last_totalMethodInvocCount) {
		this.last_totalMethodInvocCount = last_totalMethodInvocCount;
	}
	public void setLast_localMethodInvocCount(int last_localMethodInvocCount) {
		this.last_localMethodInvocCount = last_localMethodInvocCount;
	}
	public void setLast_libraryMethodInvocCount(int last_libraryMethodInvocCount) {
		this.last_libraryMethodInvocCount = last_libraryMethodInvocCount;
	}
	public void setLast_otherMethodInvocCount(int last_otherMethodInvocCount) {
		this.last_otherMethodInvocCount = last_otherMethodInvocCount;
	}
	public void setLast_uniOPERATORCount(int last_uniOPERATORCount) {
		this.last_uniOPERATORCount = last_uniOPERATORCount;
	}
	public void setLast_uniOperandCount(int last_uniOperandCount) {
		this.last_uniOperandCount = last_uniOperandCount;
	}
	public void setLast_totalOPERATORCount(int last_totalOPERATORCount) {
		this.last_totalOPERATORCount = last_totalOPERATORCount;
	}
	public void setLast_totalOperandCount(int last_totalOperandCount) {
		this.last_totalOperandCount = last_totalOperandCount;
	}
	public void setFromOrigin_souceLinesP(int fromOrigin_souceLines) {
		this.fromOrigin_souceLinesP = fromOrigin_souceLines;
	}
	public void setFromOrigin_totalMethodInvocCountP(
			int fromOrigin_totalMethodInvocCount) {
		this.fromOrigin_totalMethodInvocCountP = fromOrigin_totalMethodInvocCount;
	}
	public void setFromOrigin_localMethodInvocCountP(
			int fromOrigin_localMethodInvocCount) {
		this.fromOrigin_localMethodInvocCountP = fromOrigin_localMethodInvocCount;
	}
	public void setFromOrigin_libraryMethodInvocCountP(
			int fromOrigin_libraryMethodInvocCount) {
		this.fromOrigin_libraryMethodInvocCountP = fromOrigin_libraryMethodInvocCount;
	}
	public void setFromOrigin_otherMethodInvocCountP(
			int fromOrigin_otherMethodInvocCount) {
		this.fromOrigin_otherMethodInvocCountP = fromOrigin_otherMethodInvocCount;
	}
	public void setFromOrigin_uniOPERATORCountP(int fromOrigin_uniOPERATORCount) {
		this.fromOrigin_uniOPERATORCountP = fromOrigin_uniOPERATORCount;
	}
	public void setFromOrigin_uniOperandCountP(int fromOrigin_uniOperandCount) {
		this.fromOrigin_uniOperandCountP = fromOrigin_uniOperandCount;
	}
	public void setFromOrigin_totalOPERATORCountP(int fromOrigin_totalOPERATORCount) {
		this.fromOrigin_totalOPERATORCountP = fromOrigin_totalOPERATORCount;
	}
	public void setFromOrigin_totalOperandCountP(int fromOrigin_totalOperandCount) {
		this.fromOrigin_totalOperandCountP = fromOrigin_totalOperandCount;
	}
	public int getFromOrigin_souceLinesN() {
		return fromOrigin_souceLinesN;
	}
	public void setFromOrigin_souceLinesN(int fromOrigin_souceLinesN) {
		this.fromOrigin_souceLinesN = fromOrigin_souceLinesN;
	}
	public int getFromOrigin_fragCountN() {
		return fromOrigin_fragCountN;
	}
	public void setFromOrigin_fragCountN(int fromOrigin_fragCountN) {
		this.fromOrigin_fragCountN = fromOrigin_fragCountN;
	}
	public int getFromOrigin_totalParameterCountN() {
		return fromOrigin_totalParameterCountN;
	}
	public void setFromOrigin_totalParameterCountN(int fromOrigin_totalParameterCountN) {
		this.fromOrigin_totalParameterCountN = fromOrigin_totalParameterCountN;
	}
	public int getFromOrigin_totalMethodInvocCountN() {
		return fromOrigin_totalMethodInvocCountN;
	}
	public void setFromOrigin_totalMethodInvocCountN(int fromOrigin_totalMethodInvocCountN) {
		this.fromOrigin_totalMethodInvocCountN = fromOrigin_totalMethodInvocCountN;
	}
	public int getFromOrigin_localMethodInvocCountN() {
		return fromOrigin_localMethodInvocCountN;
	}
	public void setFromOrigin_localMethodInvocCountN(int fromOrigin_localMethodInvocCountN) {
		this.fromOrigin_localMethodInvocCountN = fromOrigin_localMethodInvocCountN;
	}
	public int getFromOrigin_libraryMethodInvocCountN() {
		return fromOrigin_libraryMethodInvocCountN;
	}
	public void setFromOrigin_libraryMethodInvocCountN(int fromOrigin_libraryMethodInvocCountN) {
		this.fromOrigin_libraryMethodInvocCountN = fromOrigin_libraryMethodInvocCountN;
	}
	public int getFromOrigin_otherMethodInvocCountN() {
		return fromOrigin_otherMethodInvocCountN;
	}
	public void setFromOrigin_otherMethodInvocCountN(int fromOrigin_otherMethodInvocCountN) {
		this.fromOrigin_otherMethodInvocCountN = fromOrigin_otherMethodInvocCountN;
	}
	public int getFromOrigin_uniOPERATORCountN() {
		return fromOrigin_uniOPERATORCountN;
	}
	public void setFromOrigin_uniOPERATORCountN(int fromOrigin_uniOPERATORCountN) {
		this.fromOrigin_uniOPERATORCountN = fromOrigin_uniOPERATORCountN;
	}
	public int getFromOrigin_uniOperandCountN() {
		return fromOrigin_uniOperandCountN;
	}
	public void setFromOrigin_uniOperandCountN(int fromOrigin_uniOperandCountN) {
		this.fromOrigin_uniOperandCountN = fromOrigin_uniOperandCountN;
	}
	public int getFromOrigin_totalOPERATORCountN() {
		return fromOrigin_totalOPERATORCountN;
	}
	public void setFromOrigin_totalOPERATORCountN(int fromOrigin_totalOPERATORCountN) {
		this.fromOrigin_totalOPERATORCountN = fromOrigin_totalOPERATORCountN;
	}
	public int getFromOrigin_totalOperandCountN() {
		return fromOrigin_totalOperandCountN;
	}
	public void setFromOrigin_totalOperandCountN(int fromOrigin_totalOperandCountN) {
		this.fromOrigin_totalOperandCountN = fromOrigin_totalOperandCountN;
	}
	public int[] getStructuralFeatureChanges_fromOriginN() {
		return structuralFeatureChanges_fromOriginN;
	}

	public void setStructuralFeatureChanges_fromOriginN(
			int[] structuralFeatureChanges_fromOrigin) {
		this.structuralFeatureChanges_fromOriginN = new int[RelatedNodes.relevantNode.values().length];
		this.structuralFeatureChanges_fromOriginN = structuralFeatureChanges_fromOrigin;
	}

}
