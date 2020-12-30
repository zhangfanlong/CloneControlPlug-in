package PreProcess;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import CRDInformation.BlockInfo;
import CRDInformation.CloneSourceInfo;
import CRDInformation.MethodInfo;
import CloneRepresentation.CRD;
import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import Global.Path;
import Global.VariationInformation;

public class CreateCRDInfo {
	private CRD crd;
	
	//文本相似度阈值默认值0.8
    public static float defaultTextSimTh = (float)0.7;  //此处0.7的来源是nicad允许近似克隆的阈值为0.7
    //位置覆盖率阈值
    public static float locationOverlap1 = (float)0.5;  //在METHODINFOMATCH情况下使用
    public static float locationOverlap2 = (float)0.7;  //在METHODNAMEMATCH情况下使用
	
    //衡量两个CRD匹配程度的枚举类型
    public enum CRDMatchLevel{
        //文本匹配的意思是相似度高于阈值（Diff类默认值为0.8）
        DIFFERENT,    //完全不同（各层级信息不同，文本不匹配）
        FILECLASSMATCH,     //文件名和类信息都相同，无方法或方法信息不同，文本匹配
        METHODNAMEMATCH,    //文件名，类名，方法名相同，参数不同，块信息不同，文本匹配
        METHODINFOMATCH,    //文件名，类名，方法名，参数都相同，块信息不同，文本匹配
        BLOCKMATCH;     //文件名，类名，方法信息，块信息相同，文本匹配
    }
    
	public void CreateCRDForSys(){

		for(CloneGroup group : VariationInformation.cloneGroup){  //修改VariationInformation.cloneGroup数据结构
			for(CloneFragment fragment : group.getClonefragment()){
				crd = new CRD();
				String subSysPath = Path._subSysDirectory + "\\" + fragment.getPath();
				
				BufferedInputStream bufferedInputStream;
			    byte[] input = null;
				try {
					bufferedInputStream = new BufferedInputStream(new FileInputStream(subSysPath));
					input = new byte[bufferedInputStream.available()];  
			        bufferedInputStream.read(input);  
			        bufferedInputStream.close(); 
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {  
		            e.printStackTrace();  
		        }     
				ASTParser astParser = ASTParser.newParser(AST.JLS8);
				astParser.setSource(new String(input).toCharArray());
				astParser.setKind(ASTParser.K_COMPILATION_UNIT);
				CompilationUnit cu = (CompilationUnit) (astParser.createAST(null));
				int startPos = cu.getPosition(fragment.getStartLine(), 0);
				int endPos = cu.getPosition(fragment.getEndLine() + 1, 0); //endLine + 1 or startPos + 1
				ExtractCRDVisitor extraCrdVisitor = new ExtractCRDVisitor(startPos,endPos);
				cu.accept(extraCrdVisitor);
				
				//添加CRD信息
				crd.setFileName(fragment.getPath());
				crd.setClassName(extraCrdVisitor.className);
				crd.setMethodInfo(extraCrdVisitor.methodInfo);
				crd.setBlockInfos(extraCrdVisitor.blockInfos);
				int start = fragment.getStartLine();
				int end = fragment.getEndLine();
				crd.setStartLine(String.valueOf(start));//绝对的startline
				crd.setEndLine(String.valueOf(end));
				if(extraCrdVisitor.relStartLine != null){
					crd.setRelStartLine(extraCrdVisitor.relStartLine);
					crd.setRelEndLine(String.valueOf(end - start + Integer.parseInt(crd.getRelStartLine())));
				}
				else if(extraCrdVisitor.pOffset != -1){
					crd.setRelStartLine(String.valueOf(fragment.getStartLine() - cu.getLineNumber(extraCrdVisitor.pOffset) + 1));//起始为1
					crd.setRelEndLine(String.valueOf(end - start + Integer.parseInt(crd.getRelStartLine())));
				}
				else 
					crd.setRelStartLine(null);

				//将CRD加入到fragment属性中
				fragment.setCRD(crd);
			}
		}
	
/*		int index = 0;
		for(ClonesInVersion clones : VariationInformation.cloneInVersion){ //修改VariationInformation.cloneInVersion数据结构
			List<CloneGroup> tempCloneGroup;
			for(CloneGroup group : VariationInformation.cloneGroup){
				if(group.getVersionID() == clones.getVersionID()){
					
				}
			}
		}*/
		
	}
	
	public static CRDMatchLevel GetCRDMatchLevel(CRD src,CRD dest) {
		// 除去文件名字符串中系统名称的部分
		String srcFileName = src.getFileName().substring(src.getFileName().indexOf("/")+1);
		String destFileName = dest.getFileName().substring(dest.getFileName().indexOf("/")+1);
		
		// 对于特定输入，在如下分支中，只走一条路径
		if (!srcFileName.equals(destFileName)) {
			return CRDMatchLevel.DIFFERENT;
		} // 文件名不同的认为不匹配
		else{// 文件信息相同
			if ((src.getClassName() != null && dest.getClassName() != null)&& !src.getClassName().equals(dest.getClassName())
					|| src.getClassName() != null && dest.getClassName() == null || src.getClassName() == null && dest.getClassName() != null) {
				return CRDMatchLevel.DIFFERENT;
			} // 类信息不同的认为不匹配
			else{// 类信息相同，或两个类信息都为空
				// 如果二者方法信息都为空
				if (src.getMethodInfo() == null && dest.getMethodInfo() == null) {
					return CRDMatchLevel.FILECLASSMATCH;
				}
				// 二者中有一个方法信息为空，另一个不为空
				else if (src.getMethodInfo() != null && dest.getMethodInfo() == null || src.getMethodInfo() == null && dest.getMethodInfo() != null) {
					return CRDMatchLevel.DIFFERENT;
				}
				// 如果二者方法信息都不为空
				else {		
					// 如果方法信息相同（方法名，参数信息都相同），检查块信息
					if (src.getMethodInfo().equals(dest.getMethodInfo())) {
						if (src.getBlockInfos() != null && dest.getBlockInfos() != null) {
							if(compareWithBlockList(src.getBlockInfos(),dest.getBlockInfos()))// 块信息相同	
								return CRDMatchLevel.BLOCKMATCH;
							else// 块信息不同
								return CRDMatchLevel.METHODINFOMATCH;
						}
						// 有一个块信息为空，另一个不为空，或两个块信息都为空（包含Granularity=functions的情况）
						else 
							return CRDMatchLevel.METHODINFOMATCH;
					}
					// 方法名相同，参数信息不同
					else if ((src.getMethodInfo().mName == dest.getMethodInfo().mName)
							&& !MethodInfo.compare(src.getMethodInfo().mParaTypeList, dest.getMethodInfo().mParaTypeList)) {
						return CRDMatchLevel.METHODNAMEMATCH;
					}
					// 方法名，参数信息都不同
					else {
						return CRDMatchLevel.FILECLASSMATCH;
					}
				}
			}
		}
	}
	
	public static boolean compareWithBlockList(List<BlockInfo> a, List<BlockInfo> b) {
	    if(a.size() != b.size())
	        return false;

	    for(int i=0;i<a.size();i++){
	    	for(int j=0;j<b.size();j++)
	        if(!a.get(i).equals(b.get(i)))
	            return false;
	    }
	    return true;
	}
	
    // 计算两段克隆代码的位置覆盖率（采用在方法中的相对位置计算）
    public static float GetLocationOverlap(CRD srcCrd, CRD destCrd){
        if (srcCrd.getRelStartLine() == null || srcCrd.getRelEndLine() == null || destCrd.getRelStartLine() == null || destCrd.getRelEndLine() == null)	
        	return -1;
        
        int startLine1, startLine2, endLine1, endLine2;
        startLine1 = Integer.parseInt(srcCrd.getRelStartLine());
        endLine1 = Integer.parseInt(srcCrd.getRelEndLine());
        startLine2 = Integer.parseInt(destCrd.getRelStartLine());
        endLine2 = Integer.parseInt(destCrd.getRelEndLine());
        int startLine = startLine1 > startLine2 ? startLine1 : startLine2;  //取startLine中较大的
        int endLine = endLine1 < endLine2 ? endLine1 : endLine2;    //取endLine中较小的
        //计算overLapping
        return (float)(endLine - startLine) / (float)(endLine2 - startLine2);
    }
	
    public static float GetTextSimilarity(CRD srcCrd, CRD destCrd, boolean ignoreEmptyLines) {
	    	List<String> srcFileContent = new ArrayList<String>();
	        List<String> destFileContent = new ArrayList<String>();
	        List<String> srcFragment = new ArrayList<String>();
	        List<String> destFragment = new ArrayList<String>();
	        CloneSourceInfo info = new CloneSourceInfo();
	        
	        // 获得srcCrdNode源代码文本
	        info.sourcePath = srcCrd.getFileName();  //获得源文件名
	        String fullName = Path._subSysDirectory + "\\" + info.sourcePath;
	        //获取源代码行区间
	        info.startLine =Integer.parseInt(srcCrd.getStartLine());
	        info.endLine = Integer.parseInt(srcCrd.getEndLine());
	        
	        srcFileContent = GetFileContent(fullName);
	        srcFragment = GetCFSourceFromSourcInfo(srcFileContent, info);

	       	// 获得destCrdNode源代码文本
	        info.sourcePath = destCrd.getFileName();    //获得源文件名
	        fullName = Path._subSysDirectory + "\\" + info.sourcePath;
	        //获取源代码行区间
	        info.startLine =Integer.parseInt(destCrd.getStartLine());
	        info.endLine = Integer.parseInt(destCrd.getEndLine());
	     
	        destFileContent = GetFileContent(fullName);
	        destFragment = GetCFSourceFromSourcInfo(destFileContent, info);

	        //使用Diff类计算两段代码的相似度
	        Diff.UseDefaultStrSimTh();  //使用行相似度阈值默认值0.5
	        Diff.DiffInfo diffFile = new Diff().DiffFiles(srcFragment, destFragment);
	        float sim = Diff.FileSimilarity(diffFile, srcFragment.size(), destFragment.size(), ignoreEmptyLines);

	        return sim;
	}
	 
	public static List<String> GetFileContent(String fileName){  
		List<String> content = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(fileName)));
			String str = reader.readLine();
			while (str != null){
				content.add(str);
	            str = reader.readLine();
	        }
	        reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}

    public static List<String> GetCFSourceFromSourcInfo(List<String> source, CloneSourceInfo sourceInfo){
        List<String> codeClone = new ArrayList<String>();
        for (int i = sourceInfo.startLine - 1; i < sourceInfo.endLine; i++) { //注意，索引从0起算，而行号从1起算
        	codeClone.add(source.get(i).toString()); 
        }
        return codeClone;
    }
    public static List<String> GetCFSourceFromCRDInfo(List<String> source,int start,int end){
        List<String> codeClone = new ArrayList<String>();
        for (int i = start - 1; i < end; i++) { //注意，索引从0起算，而行号从1起算
        	codeClone.add(source.get(i).toString()); 
        }
        return codeClone;
    }

}
