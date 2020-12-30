package ExtractFeatures;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import CloneRepresentation.CRD;
import Global.Path;
import Global.VariationInformation;
import PreProcess.CreateCRDInfo;
import PreProcess.ExtractCRDVisitor;
import PreProcess.GetJavaFiles;


/**
 * @author YueYuan
 *
 */

public class FeatureOperations {
	
	//private static CompilationUnit cu;
	//private static List<String> copiedCodes;
	private static CRD copiedCrd,pastedCrd;
	private static String path;
	private static List<String> currentVersionJavaFiles;
	
	/**获得整个项目内的所有java文件，即所有class名
	 * @param sysPath 当前项目的绝对路径
	 */
	public static void init(String sysPath){
		currentVersionJavaFiles = new ArrayList<String>();
		File sysFolder = new File(sysPath);
		if(sysFolder.isDirectory()){
			File[] childSysFolder = sysFolder.listFiles();
			for(File f : childSysFolder){
				if (f.isDirectory()){
					GetJavaFiles getJavaFile = new GetJavaFiles();
					getJavaFile.GetJavaFilePath(f.getAbsolutePath());
					//存放整个系统的各个版本的所有java文件名
					currentVersionJavaFiles = getJavaFile.getAllJavaFiles();
				}
			}
		}
	}
	
	/**从文件中读取指定行号的代码
	 * @param path 
	 * @param startLine
	 * @param endLine
	 * @return
	 */
	public static List<String> GetCodesFromFile(String path,int startLine,int endLine){
		List<String> sourceCode = CreateCRDInfo.GetFileContent(path);
		List<String> copiedCodes = CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCode, startLine, endLine);
		return copiedCodes;
	}
	
	/**创建一个文件的 AST 
	 * @param path 一个文件的绝对路径
	 * @return 编译单元
	 */
	public static CompilationUnit CreateAST(String path){
		FeatureOperations.path = path;
		byte[] input =null;
        try {
        	BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
			input = new byte[bufferedInputStream.available()];  
	        bufferedInputStream.read(input);  
	        bufferedInputStream.close(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ASTParser astParser = ASTParser.newParser(AST.JLS8);  
		astParser.setSource(new String(input).toCharArray());
		astParser.setKind(ASTParser.K_COMPILATION_UNIT);
		//cu = (CompilationUnit)(astParser.createAST(null));
		return (CompilationUnit)(astParser.createAST(null));
	}
	
	/**为插件复制的 或者 粘贴的片段提取CRD信息
	 * @param path
	 * @param startLine
	 * @param endLine
	 * @param flag
	 */
	public static void CreateCRDForFrag(CompilationUnit cu,String path,int startLine,int endLine,String flag){
		//CompilationUnit cu = FeatureOperations.CreateAST(path);
	    int startPos = cu.getPosition(startLine, 0);
		int endPos = cu.getPosition(endLine + 1, 0); 
		ExtractCRDVisitor extraCrdVisitor = new ExtractCRDVisitor(startPos,endPos);
		cu.accept(extraCrdVisitor);	
		
		CRD crd = new CRD();
		crd.setFileName(path); //......................................现在的是绝对路径
		crd.setClassName(extraCrdVisitor.className);
		crd.setMethodInfo(extraCrdVisitor.methodInfo);
		crd.setBlockInfos(extraCrdVisitor.blockInfos);
		crd.setStartLine(String.valueOf(startLine));//绝对的startline
		crd.setEndLine(String.valueOf(endLine));
		if(extraCrdVisitor.relStartLine != null){
			crd.setRelStartLine(extraCrdVisitor.relStartLine);
			crd.setRelEndLine(String.valueOf(endLine - startLine + Integer.parseInt(crd.getRelStartLine())));
		}
		else if(extraCrdVisitor.pOffset != -1){
			crd.setRelStartLine(String.valueOf(startLine - cu.getLineNumber(extraCrdVisitor.pOffset) + 1));//起始为1
			crd.setRelEndLine(String.valueOf(endLine - startLine + Integer.parseInt(crd.getRelStartLine())));
		}
		else 
			crd.setRelStartLine(null);

		//添加CRD信息
		if(flag.equals("copy")){
			copiedCrd = new CRD();
			copiedCrd = crd;
		} else if(flag.equals("paste")){
			pastedCrd = new CRD();
			pastedCrd = crd;
		}
		
	}
	
	/**获得方法调用信息
	 * @param cu
	 * @param startLine 
	 * @param endLine
	 * @return
	 */
	public static MethodInvocCountVisitor GetMethodInvocInfo(CompilationUnit cu,/*String path,*/int startLine,int endLine){
		//CompilationUnit cu = FeatureOperations.CreateAST(path);
		String className = copiedCrd.getClassName();
		int startPos = cu.getPosition(startLine, 0);
  		int endPos = cu.getPosition(endLine + 1,0 ); 

  		MethodInvocCountVisitor invocFeatureVisitor = new MethodInvocCountVisitor(startPos,endPos,className,FeatureOperations.currentVersionJavaFiles);
  		cu.accept(invocFeatureVisitor);
  		return invocFeatureVisitor;
	}

	/**获得是个  Halstead 基本度量
	 * @param copiedCodes
	 * @return 
	 */
	public static HalsteadMetric GetHalsteadInfo(List<String> copiedCodes){
		copiedCodes = PreProcess.clearComment(copiedCodes);
		copiedCodes = PreProcess.clearString(copiedCodes);
		copiedCodes = PreProcess.clearImport(copiedCodes);
		return new HalsteadMetric(copiedCodes);
	}
	
	/**获得复制片段结构信息
	 * @param cu
	 * @param start
	 * @param end
	 * @param isLine
	 * @return
	 */
	public static StructuralFeatureVisitor GetStructuralInfo(CompilationUnit cu,int start,int end,boolean isLine){
		int startPos,endPos; //全是偏移量
		if(isLine){
			startPos = cu.getPosition(start, 0);
	  		endPos = cu.getPosition(end + 1,0 ); 
		} else {
			startPos = start;
			endPos = end; 
		}

  		StructuralFeatureVisitor strucFeatureVisitor = new StructuralFeatureVisitor(startPos,endPos);
        cu.accept(strucFeatureVisitor);
        return strucFeatureVisitor;
	}
	
}
