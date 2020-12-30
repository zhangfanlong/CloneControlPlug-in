package PreProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class LCSItem{		//LCS中项的结构
    public boolean isExactSame;    //标记此项是否是完全相同或者有修改
    public String lineContent; //相同的行的内容
    public int lineOfFileA;    //该行在FileA中的行号（索引号+1）
    public int lintOfFileB;    //该行在FileB中的行号（索引号+1）
}

class ListStringLCS{   // ArrayListLCS类，完成两个ArrayList的LCS的计算

    public List<String> strA;
    public List<String> strB;
    public int length;
    //pointerArray是存放标记的二维数组，U表示向上，L表示向左，Y表示向左上，\0表示空（未赋值）
    private char[][] pointerArray;
    //LengthArray是存放lcs长度的二维数组
    private int[][] lengthArray;
    public ArrayList<LCSItem> lcs;

    public ListStringLCS(List<String> a, List<String> b){
    
        strA = new ArrayList<String>(a);
        strB = new ArrayList<String>(b);
    }

    // 获得strA与strB的LCS，保存在lcs成员中，并保存LCS长度在length成员中
    public void GetLCS() {
        this.length = LCSLength(this.strA, this.strB);
        this.lcs = new ArrayList<LCSItem>();
        PrintLCS(strA.size(), strB.size(), this.length);
    }


    // 打印（记录）LCS的项，采用递归方法。使用LCS的MarkArray成员，更新lcs成员
    // <param name="indexLCS">这个参数提供当前打印的LCS项的索引号</param>
    private void PrintLCS(int indexA, int indexB, int indexLCS){

        if (indexA == 0 || indexB == 0)	return; 
        if (this.pointerArray[indexA][indexB] == 'Y'){
        
        	PrintLCS(indexA - 1, indexB - 1, indexLCS - 1);
            LCSItem newItem = new LCSItem();
            //如果是完全相同的项，存入其中一个即可
            if ((strA.get(indexA - 1)).equals(strB.get(indexB - 1))){
                newItem.isExactSame = true;
                newItem.lineContent = strA.get(indexA - 1);
            }
            else{	//修改的项，将两个连接到一起，存入。用$DIVIDER$串分隔
                newItem.isExactSame = false;
                newItem.lineContent = strA.get(indexA - 1) + "$DIVIDER$" + strB.get(indexB - 1);
            }
            newItem.lineOfFileA = indexA;   //index也是从1开始，因此不需要+1
            newItem.lintOfFileB = indexB;
            //this.lcs[indexLCS] = newItem;
            this.lcs.add(newItem);
        }
        else if (this.pointerArray[indexA][indexB] == 'U'){
        	PrintLCS(indexA - 1, indexB, indexLCS); 
        }
        else{
         PrintLCS(indexA, indexB - 1, indexLCS); 
        }
    }

    public static String TrimChars(String s){// 处理字符串中多余的空格符，修改回车(\r\n)为换行(\n)，及删除多余的\t
        
    	//注：Unix 系统里，每行结尾只有“<换行>”，即“\n”；Windows系统里面，每行结尾是“<回车><换行>”，即“ \r\n”
        s = s.replace("\r", "");    //修改回车(\r\n)为换行(\n)
        
        Pattern pattern = Pattern.compile("\\s+");
        Matcher matcher = pattern.matcher(s);
        s = matcher.replaceAll(" ");//删除多余的空格
 
        pattern = Pattern.compile("\\t+");
        matcher = pattern.matcher(s);
        s = matcher.replaceAll(" ");  //替换一个或多个制表符为空格

        s.trim();   //删除开头和结尾的空格
        return s;
    }
    
  
    // 计算两个字符串的相似度。完全相同时为1，完全不同（LCS长度为0）时为0
    public static float GetStringSimilarity(String strA, String strB){
        return LevenshteinDistance.ld(strA, strB);
    }
    
    // 计算strA和strB的LCS长度，动态规划方法，非递归。使用并更新LCS的MarkArray和LenghthArray成员
    // <returns>返回LCS长度</returns>
	private int LCSLength(List<String> strA, List<String> strB){ // 注：这个方法还存在缺陷，会导致误差！
		if (strA.size() == 0 || strB.size() == 0) {
			return 0;
		}
		// 初始化两个二维数组的结构
		this.pointerArray = new char[strA.size() + 1][strB.size() + 1];
		this.lengthArray = new int[strA.size() + 1][strB.size() + 1];
		int i, j;
		// 置第一行和第一列值
		for (j = 0; j < strB.size() + 1; j++) {
			this.lengthArray[0][j] = 0;
			this.pointerArray[0][j] = '\0';
		}
		for (i = 0; i < strA.size() + 1; i++) {
			this.lengthArray[i][0] = 0;
			this.pointerArray[i][0] = '\0';
		}
		// 开始扫描
		for (i = 1; i < strA.size() + 1; i++) {
			String a = strA.get(i - 1);
			a = TrimChars(a); // 处理空格，回车，制表符等字符
			for (j = 1; j < strB.size() + 1; j++) {
				String b = strB.get(j - 1);
				b = TrimChars(b);
				// 两行完全相同，或不完全相同，但相似度高于阈值
				if (a.equals(b)
						|| GetStringSimilarity(a, b) >= Diff.StrSimThreshold) {
					this.pointerArray[i][j] = 'Y';
					this.lengthArray[i][j] = this.lengthArray[i - 1][j - 1] + 1;
				} else {
					if (this.lengthArray[i - 1][j] >= this.lengthArray[i][j - 1]) {
						this.pointerArray[i][j] = 'U';
						this.lengthArray[i][j] = this.lengthArray[i - 1][j];
					} else {
						this.pointerArray[i][j] = 'L';
						this.lengthArray[i][j] = this.lengthArray[i][j - 1];
					}
				}
			}
		}
		return this.lengthArray[strA.size()][strB.size()];
	}
}



public class Diff {

	public static float StrSimThreshold;   //定义字符串（行）相似度阈值
    public static void SetStrSimThreshode(float value){ //设定相似度阈值
    	StrSimThreshold = value; 
    }
	public static void UseDefaultStrSimTh(){     //使用默认相似度阈值
		StrSimThreshold = (float)0.5; 
	}

    public enum ConfictType{
        MODIFIED,
        ADD,
        DELETE;
    }
    
    class ConfictItem{
        //冲突的类型，如果ADD，则contentA为空；如果为DELETE，则contentB为空；如果为MODIFIED，两者都不空
        public ConfictType type;
        public List<String> contentA;
        public List<String> contentB;
    }

    class DiffInfo extends ArrayList { } //定义DiffInfo类型
    
    public float FineFragment(List<String> fileA, List<String> fileB){//A是克隆代码片段，B是克隆所在文件
    	ListStringLCS lcsObject = new ListStringLCS(fileA, fileB); // 创建一个LCS类的对象
		lcsObject.GetLCS();
		int minLineB=lcsObject.lcs.get(0).lintOfFileB,maxLineB=0;
		for (LCSItem lcsItem : lcsObject.lcs) {
			if(lcsItem.lintOfFileB < minLineB) 
				minLineB = lcsItem.lintOfFileB;
			if(lcsItem.lintOfFileB > maxLineB)
				maxLineB = lcsItem.lintOfFileB;
		}
		fileB = CreateCRDInfo.GetCFSourceFromCRDInfo(fileB, minLineB, maxLineB);
		return FileSimilarity(DiffFiles(fileA, fileB), 
				fileA.size(), fileB.size(), true);
    }
    
	public DiffInfo DiffFiles(List<String> fileA, List<String> fileB) {
		
		DiffInfo diffFile = new DiffInfo();

		ListStringLCS lcsObject = new ListStringLCS(fileA, fileB); // 创建一个LCS类的对象
		lcsObject.GetLCS();
		int lineNoA, lineNoB, prevLineNoA, prevLineNoB;
		prevLineNoA = 0;
		prevLineNoB = 0;
		lineNoA = 0;
		lineNoB = 0;
		// 将LCS中的项及冲突项依次加入diffFile中
		for (LCSItem lcsItem : lcsObject.lcs) {
			int lcsIndex = lcsObject.lcs.indexOf(lcsItem);
			lineNoA = lcsItem.lineOfFileA;
			lineNoB = lcsItem.lintOfFileB;
			if (lineNoA - prevLineNoA == 1 && lineNoB - prevLineNoB == 1) {
			} 
			else if (lineNoA - prevLineNoA == 1 && lineNoB - prevLineNoB > 1){// 增加的情况
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.ADD;
				cItem.contentA = null;
				cItem.contentB = new ArrayList<String>();
				for (int i = prevLineNoB; i < lineNoB - 1; i++) { // 将fileB中增加的行加入此项
					cItem.contentB.add(fileB.get(i));
				}
				diffFile.add(cItem);
			}
			else if (lineNoA - prevLineNoA > 1 && lineNoB - prevLineNoB == 1){ // 删除的情况
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.DELETE;
				cItem.contentA = new ArrayList<String>();
				cItem.contentB = null;
				for (int i = prevLineNoA; i < lineNoA - 1; i++) { // 将fileA中删除的行加入此项
					cItem.contentA.add(fileA.get(i));
				}
				diffFile.add(cItem);
			}
			// lineNoA - prevLineNoA > 1 && lineNoB - prevLineNoB > 1，同时有增加和删除
			else {
				// 按先后顺序将两边的冲突项加入diffFile
				// 下面分支的作用是将行号靠前的一边构造的冲突项先加入diffFile
				if (prevLineNoA >= prevLineNoB) {
					// 先加入左边删除项
					ConfictItem cItem = new ConfictItem();
					cItem.type = ConfictType.DELETE;
					cItem.contentA = new ArrayList<String>();
					cItem.contentB = null;
					for (int i = prevLineNoA; i < lineNoA - 1; i++) // 将fileA中删除的行加入contentA
					{
						cItem.contentA.add(fileA.get(i));
					}
					diffFile.add(cItem);
					// 后加入右边增加项
					cItem = new ConfictItem();
					cItem.type = ConfictType.ADD;
					cItem.contentA = null;
					cItem.contentB = new ArrayList<String>();
					for (int i = prevLineNoB; i < lineNoB - 1; i++) {
						cItem.contentB.add(fileB.get(i));
					} // 将fileB中增加的行加入contentB
					diffFile.add(cItem);
				} else {
					// 先加入右边增加项
					ConfictItem cItem = new ConfictItem();
					cItem.type = ConfictType.ADD;
					cItem.contentA = null;
					cItem.contentB = new ArrayList<String>();
					for (int i = prevLineNoB; i < lineNoB - 1; i++) {
						cItem.contentB.add(fileB.get(i));
					}
					diffFile.add(cItem);
					// 先加入左边删除项
					cItem = new ConfictItem();
					cItem.type = ConfictType.DELETE;
					cItem.contentB = null;
					cItem.contentA = new ArrayList<String>();
					for (int i = prevLineNoA; i < lineNoA - 1; i++) {
						cItem.contentA.add(fileA.get(i));
					}
					diffFile.add(cItem);
				}

			}
			// 将LCS中的项加入diffFile
			if (lcsItem.isExactSame){ // 完全相同的项直接加入diffFile
			
				diffFile.add(lcsItem.lineContent.toString());
				prevLineNoA = lineNoA; // prev指针下移
				prevLineNoB = lineNoB;
			} else{// 有修改的项作为冲突项加入diffFile
			
				ConfictItem cItem = new ConfictItem();
				cItem.type = ConfictType.MODIFIED;
				cItem.contentA = new ArrayList<String>();
				cItem.contentB = new ArrayList<String>();
				do{	// 使用循环将连续的有修改的项合并到一个冲突项
					
					// 分隔符前面的内容加入contentA，后面的内容加入contentB
					int indexDiv = lcsItem.lineContent.toString().indexOf(
							"$DIVIDER$");
					cItem.contentA.add(lcsItem.lineContent.toString()
							.substring(0, indexDiv));
					cItem.contentB.add(lcsItem.lineContent.toString()
							.substring(indexDiv + 9));
					diffFile.add(cItem); // 将冲突项加入diffFile
					prevLineNoA = lineNoA; // prev指针下移
					prevLineNoB = lineNoB;
				} while (lineNoA - prevLineNoA == 1
						&& lineNoB - prevLineNoB == 1 && !lcsItem.isExactSame);
			}

		}
		// 处理剩余项
		if (lineNoA < fileA.size()){ // 如果还有删除项
		
			ConfictItem cItem = new ConfictItem();
			cItem.type = ConfictType.DELETE;
			cItem.contentA = new ArrayList<String>();
			cItem.contentB = null;
			for (int i = lineNoA; i < fileA.size(); i++) {
				cItem.contentA.add(fileA.get(i));
			}
			diffFile.add(cItem);
		}
		if (lineNoB < fileB.size()){ // 如果还有增加项
			ConfictItem cItem = new ConfictItem();
			cItem.type = ConfictType.ADD;
			cItem.contentA = null;
			cItem.contentB = new ArrayList<String>();
			for (int i = lineNoB; i < fileB.size(); i++) {
				cItem.contentB.add(fileB.get(i));
			}
		}

		return diffFile;
	}

	// 静态计算文件相似度方法，根据diffFile信息。因为diffFile中不提供两个文件的长度，因此用参数提供
    public static float FileSimilarity(DiffInfo diffFile, int lengthA, int lengthB, boolean ignoreEmptyLine){
        int uniLineCountA = 0;
        int uniLineCountB = 0;
        int emptyLineCount = 0;
        
        for (Object cItem : diffFile){   
            if (cItem instanceof ConfictItem){ 
                if (((ConfictItem)cItem).contentA != null){
                    for (String line : ((ConfictItem)cItem).contentA){   
                        uniLineCountA++;    //统计FileA中冲突项的行数
                        if (line.trim() == "") { emptyLineCount++; }   //统计空行的数量
                    }
                }
                if (((ConfictItem)cItem).contentB != null){
                    for (String line : ((ConfictItem)cItem).contentB){
                        uniLineCountB++;    //统计FileB中冲突项的行数 
                        if (line.trim() == "")
                        { emptyLineCount++; }
                    }
                }
            }
        }
        if (ignoreEmptyLine)
        { return (float)1 - ((float)(uniLineCountA + uniLineCountB - emptyLineCount) / (float)(lengthA + lengthB - emptyLineCount)); }
        else
        { return (float)1 - ((float)(uniLineCountA + uniLineCountB) / (float)(lengthA + lengthB)); }
    }

	
}
