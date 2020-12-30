package ExtractFeatures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class GetSourceLines {
	private int codeLines = 0; // 代码行
	private int commentLines = 0; // 注释行,可能用。。。。。
	private int whiteLines = 0; // 空行,可能用。。。。。
	
	public int getCodeLines() {
		return codeLines;
	}

	public int getCommentLines() {
		return commentLines;
	}

	public int getWhiteLines() {
		return whiteLines;
	}
	
	public void SumLinesFromStringList(List<String> tarSource){
		//boolean comment = false;
		PreProcess.clearComment(tarSource);//清除注释
		for(String line : tarSource){
			line = line.trim();
			if (line.matches("^[//s&&[^//n]]*$")) {
				whiteLines++;
			}else {
				codeLines++;
			} 
//			else if (line.startsWith("/*") && !line.endsWith("*/")) {
//				commentLines++;
//				comment = true;
//			} else if (comment) {
//				commentLines++;
//				if (line.endsWith("*/")) {
//					comment = false;
//				}
//			} else if (line.startsWith("//")) {
//				commentLines++;
//			} 
			
		}
	}
	
	public void SumLinesFromFile(File file,int startLine,int endLine) {
		BufferedReader br = null;
		boolean comment = false;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = "";
			int lineCount = 0;
			try {
				while ((line = br.readLine()) != null) {
					++lineCount;
					if(lineCount>=startLine && lineCount<=endLine){//统计相应代码行数
						line = line.trim();
						if (line.matches("^[//s&&[^//n]]*$")) {
							whiteLines++;
						} else if (line.startsWith("/*") && !line.endsWith("*/")) {
							commentLines++;
							comment = true;
						} else if (comment) {
							commentLines++;
							if (line.endsWith("*/")) {
								comment = false;
							}
						} else if (line.startsWith("//")) {
							commentLines++;
						} else {
							codeLines++;
						}
					}			
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
					br = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
