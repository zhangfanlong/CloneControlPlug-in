package CloneRepresentation;

import java.util.ArrayList;
import java.util.List;

import CRDInformation.BlockInfo;
import CRDInformation.MethodInfo;

public class CRD {
	private String fileName;//带路径（系统路径下面的整个路径）
	private String className;
	private MethodInfo methodInfo;
	private List<BlockInfo> blockInfos;

	private String relStartLine;
	private String relEndLine;

	private String startLine;
	private String endLine;

	public void setFileName(String name) {
		this.fileName = name;
	}

	public String getFileName() {
		return this.fileName;
	}

	public void setClassName(String name) {
		this.className = name;
	}

	public String getClassName() {
		return this.className;
	}

	public void setMethodInfo(MethodInfo method) {
		this.methodInfo = new MethodInfo();
		this.methodInfo = method;
	}

	public MethodInfo getMethodInfo() {
		return this.methodInfo;
	}

	public void setBlockInfos(List<BlockInfo> block) {
		this.blockInfos = new ArrayList<BlockInfo>();
		this.blockInfos = block;
	}

	public List<BlockInfo> getBlockInfos() {
		return this.blockInfos;
	}

	public void setRelStartLine(String start) {
		this.relStartLine = start;
	}

	public String getRelStartLine() {
		return this.relStartLine;
	}

	public void setRelEndLine(String end) {
		this.relEndLine = end;
	}

	public String getRelEndLine() {
		return this.relEndLine;
	}

	public String getStartLine() {
		return startLine;
	}

	public void setStartLine(String startLine) {
		this.startLine = startLine;
	}

	public String getEndLine() {
		return endLine;
	}

	public void setEndLine(String endLine) {
		this.endLine = endLine;
	}
}
