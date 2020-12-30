package CloneRepresentation;


/**
 * @author ZFL
 *定义克隆片段
 */

public class CloneFragment {
	
	private int CFID;//克隆片段的ID（在克隆组的ID+GroupID+VersionID）
	private int CGID;//克隆片段所在克隆组ID
	private int VersionID;//版本号
	private String Path;//相对路径名
	private String FileName;//文件名
	private int StartLine;//起始行
	private int EndLine;//结束行
	
	private CRD crd;//CRD信息
	private FragmentMapping srcFragmentMapping;//克隆片段映射信息
	private FragmentMapping destFragmentMapping;//克隆片段映射信息
	
	//设置与获取CFID
	public void setCFID(int cFID) {
		CFID = cFID;
	}
	public int getCFID() {
		return CFID;
	}
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
    //克隆文件
	public String getPath() {
		return Path;
	}
	public void setPath(String Path) {
		this.Path = Path;
	}
	//文件名
	public String getFileName() {
		return FileName;
	}
	public void setFileName(String fileName) {
		FileName = fileName;
	}
    //起始行号
	public int getStartLine() {
		return StartLine;
	}
	public void setStartLine(int start_line) {
		this.StartLine = start_line;
	}
    //结束行号
	public int getEndLine() {
		return EndLine;
	}
	public void setEndLine(int end_line) {
		this.EndLine = end_line;
	}
	public CRD getCRD() {
		return crd;
	}
	public void setCRD(CRD crd) {
		this.crd = new CRD();
		this.crd = crd;
	}
	public FragmentMapping getSrcFragmentMapping() {
		return srcFragmentMapping;
	}
	public void setSrcFragmentMapping(FragmentMapping srcFragmentMapping) {
		this.srcFragmentMapping = new FragmentMapping();
		this.srcFragmentMapping = srcFragmentMapping;
	}
	public FragmentMapping getDestFragmentMapping() {
		return destFragmentMapping;
	}
	public void setDestFragmentMapping(FragmentMapping destFragmentMapping) {
		this.destFragmentMapping = new FragmentMapping();
		this.destFragmentMapping = destFragmentMapping;
	}

	

}
