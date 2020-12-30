package CloneRepresentation;

public class FragmentMapping {
	
	private int srcCFid;
	private int destCFid;
	private float textSim; 
	private String CRDMatchLevel;
	
	
	public int getSrcCFid() {
		return srcCFid;
	}
	public void setSrcCFid(int srcCFid) {
		this.srcCFid = srcCFid;
	}
	public int getDestCFid() {
		return destCFid;
	}
	public void setDestCFid(int destCFid) {
		this.destCFid = destCFid;
	}
	public float getTextSim() {
		return textSim;
	}
	public void setTextSim(float textSim) {
		this.textSim = textSim;
	}
	public String getCRDMatchLevel() {
		return CRDMatchLevel;
	}
	public void setCRDMatchLevel(String cRDMatchLevel) {
		CRDMatchLevel = cRDMatchLevel;
	}
	
}
