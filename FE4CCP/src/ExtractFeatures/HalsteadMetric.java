package ExtractFeatures;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HalsteadMetric {
	public static List<String> Keywords;  
    private static void InitKeywordCollection(){
    	Keywords = new ArrayList<String>();
    	Keywords.add("break");
    	Keywords.add("case");
    	Keywords.add("catch");
    	Keywords.add("continue");
    	Keywords.add("do");
    	Keywords.add("else");
    	Keywords.add("finally");
    	Keywords.add("for");
    	Keywords.add("goto");
    	Keywords.add("if");
    	Keywords.add("return");
    	Keywords.add("sizeof");
    	Keywords.add("switch");
    	Keywords.add("try");
    	Keywords.add("while");
    	Keywords.add("new");
    }
   
    public static List<String> Operators;
    private static void InitOperatorCollection(){
    	Operators = new ArrayList<String>();
        //包含"="的运算符(0-14)
    	Operators.add("=");
    	Operators.add("==");
    	Operators.add("+=");
        Operators.add("-=");
        Operators.add("*=");
        Operators.add("/=");
        Operators.add("%=");
        Operators.add("&=");
        Operators.add("|=");
        Operators.add("^=");
        Operators.add("!=");
        Operators.add("<<="); //与"<="的顺序不可换
        Operators.add("<=");
        Operators.add(">>="); //与">="的顺序不可换
        Operators.add(">=");
        //包含"<"(15,16)
        Operators.add("<");
        Operators.add("<<");
        //包含">"(17,18)
        Operators.add(">");
        Operators.add(">>");
        //包含"+"(19,20)
        Operators.add("+");
        Operators.add("++");
        //包含"-"(21,22)
        Operators.add("-");
        Operators.add("--");
        //包含"&"(23,24)
        Operators.add("&");
        Operators.add("&&");
        //包含"|"(15,26)
        Operators.add("|");
        Operators.add("||");
        //单个字符的运算符(27-33)
        Operators.add("*");
        Operators.add("/");
        Operators.add("%");
        Operators.add("!");
        Operators.add("^");
        Operators.add("~");
        Operators.add(".");
        //中间有字符的运算符(34-36)
        Operators.add("?:");
        Operators.add("()");
        Operators.add("[]");
        Operators.add("instanceof");	//Java特有,对象运算符
    }
 
    public static void InitHalsteadParam(){ //统一初始化上述成员
        InitKeywordCollection();
        InitOperatorCollection();
    }
   
	private int uniOPERATORCount;//唯一操作符数量，即操作符种类
    private int uniOperandCount;//唯一操作数数量，即操作数种类
    private int totalOPERATORCount;//操作符总量
    private int totalOperandCount;//操作数总量
    
    //统计操作数时候使用
   /* private List<String> operandList = new ArrayList<String>();  //用于存放操作数的表
    private List<Integer> operandCounter = new ArrayList<Integer>(); //用来保存操作数出现次数的表
    private List<String> operatorList = new ArrayList<String>();  //用于存放操作符的表
    private List<Integer> operatorCounter = new ArrayList<Integer>(); //用来保存操作符出现次数的表
*/    
    public HalsteadMetric(List<String> codeFragment){
        uniOperandCount = 0;
        uniOPERATORCount = 0;
        totalOperandCount = 0;
        totalOPERATORCount = 0;
        InitHalsteadParam();
        this.GetOPERATORCountFromCode(codeFragment);
        this.GetOperandCountFromCode(codeFragment);
    }
    
    // 计算代码段的操作符信息
    private void GetOPERATORCountFromCode(List<String> codeFragment){
        //用到的数组（统计各种关键字和运算符出现的次数）
        int[] KeyCounter = new int[Keywords.size()];
        int[] OptorCounter = new int[Operators.size()];

        for(String line : codeFragment){
            int keyIndexHead, keyIndexTail;  //关键字起止位置
            String curStr="";  //用来对行进行分割检查
            String leftStr; //用来对行进行分割检查

            //统计关键字
            for (String cKey : Keywords){
                leftStr = line;
                //分段检测，在一行中是否多次出现
                while (leftStr.indexOf(cKey) != -1){
                    keyIndexHead = leftStr.indexOf(cKey);
                    keyIndexTail = keyIndexHead + cKey.length() - 1;
                    curStr = leftStr.substring(0, keyIndexTail + 1);
                    //如果真的包含关键字
                    if ((keyIndexHead == 0 || !String.valueOf(curStr.charAt(keyIndexHead - 1)).matches("[A-Za-z0-9_]")) &&
                            (keyIndexTail == curStr.length() - 1 || !String.valueOf(curStr.charAt(keyIndexTail + 1)).matches("[A-Za-z0-9_]"))){
                            KeyCounter[Keywords.indexOf(cKey)]++;
                    }
                    if(keyIndexTail < leftStr.length()-1){
                        leftStr = leftStr.substring(keyIndexTail + 1);
                    }else break;
                }
            }
            
            int opIndex;    //记录当前处理的操作符的位置
            //检测包含"="的运算符系列
            if (line.indexOf("=") != -1){
                leftStr = line;
                boolean flag;
                while (leftStr.indexOf("=") != -1){ //以"="为边界，分段检测，是否多次出现
                    flag = false;
                    opIndex = leftStr.indexOf("=");
                    if(opIndex+1 == leftStr.length()) break;
                    if(opIndex < leftStr.length()-3){
                    	curStr = leftStr.substring(0, opIndex + 3);    
                    }else {
                    	curStr = leftStr.substring(0, opIndex + 2);      
                    }
                    for (int i = 1; i <= 14; i++){
                        if (curStr.indexOf(Operators.get(i)) != -1){
                            OptorCounter[i]++;
                            flag = true;
                            break;
                        }
                    }
                    if (!flag){ //如果只是包含"="
                        OptorCounter[Operators.indexOf("=")]++;
                    }
                    if(opIndex < leftStr.length()-3){
                    	leftStr = leftStr.substring(opIndex + 3);  
                    }else if(opIndex < leftStr.length()-2){ 
                    	leftStr = leftStr.substring(opIndex + 2);
                    }else break;   
                }
            }
            
            //检测包含"<",">"的系列
            if (line.indexOf("<") != -1){//检测"<"
                leftStr = line;
                while (leftStr.indexOf("<") != -1){
                    opIndex = leftStr.indexOf("<");
                    if(opIndex+1 == leftStr.length()) break;
                    if(opIndex < leftStr.length()-3){ 
                    	 curStr = leftStr.substring(0, opIndex + 3);     
                    }else {
                    	curStr = leftStr.substring(0, opIndex + 2);
                    }
                    if (curStr.indexOf("<<") != -1 && curStr.indexOf("<<=") == -1)  //排除"<<="
                    { OptorCounter[Operators.indexOf("<<")]++; }
                    else{
                        if (curStr.indexOf("<=") == -1 && curStr.indexOf("<<=") == -1) //排除"<="和"<<="
                        { OptorCounter[Operators.indexOf("<")]++; }
                    }
                    
                    if(opIndex < leftStr.length()-3){
                    	leftStr = leftStr.substring(opIndex + 3);  
                    }else if(opIndex < leftStr.length()-2){ 
                    	leftStr = leftStr.substring(opIndex + 2);
                    }else break; 
                }
            }   
            if (line.indexOf(">") != -1){//检测">"
                leftStr = line;
                while (leftStr.indexOf(">") != -1){
                    opIndex = leftStr.indexOf(">");
                    if(opIndex+1 == leftStr.length()) break;
                    if(opIndex < leftStr.length()-3){
                   	 	curStr = leftStr.substring(0, opIndex + 3);
                    }else {
                    	curStr = leftStr.substring(0, opIndex + 2);
                    }
 
                    if (curStr.indexOf(">>") != -1 && curStr.indexOf(">>=") == -1)
                    { OptorCounter[Operators.indexOf(">>")]++; }
                    else{
                        if (curStr.indexOf(">=") == -1 && curStr.indexOf(">>=") == -1) //排除">="和">>="，因为已经处理过
                        { OptorCounter[Operators.indexOf(">")]++; }
                    }
                    if(opIndex < leftStr.length()-3){
                    	leftStr = leftStr.substring(opIndex + 3);
                    }else if(opIndex < leftStr.length()-2){
                    	leftStr = leftStr.substring(opIndex + 2);
                    }else break;
                    
                }
            }
            
            //检测"+","-","&","|"系列          
            if (line.indexOf("+") != -1){//包含"+"的情况
                leftStr = line;
                while (leftStr.indexOf("+") != -1){
                    opIndex = leftStr.indexOf("+");
                    if (opIndex == leftStr.length() - 1){ //"+" 做加号右边必有以操作数，应该不用
                        OptorCounter[Operators.indexOf("+")]++;
                        break;
                    }
                    curStr = leftStr.substring(0, opIndex + 2);         
                    if (curStr.indexOf("++") != -1)
                    { OptorCounter[Operators.indexOf("++")]++; }
                    else{
                        if (curStr.indexOf("+=") == -1)
                        { OptorCounter[Operators.indexOf("+")]++; }
                    }
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("-") != -1){//包含"-"的情况
                leftStr = line;
                while (leftStr.indexOf("-") != -1){
                    opIndex = leftStr.indexOf("-");
                    if (leftStr.length() == opIndex + 1){
                        OptorCounter[Operators.indexOf("-")]++;
                        break;
                    }
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("--") != -1)
                    { OptorCounter[Operators.indexOf("--")]++; }
                    else{
                        if (curStr.indexOf("-=") == -1)
                        { OptorCounter[Operators.indexOf("-")]++; }
                    }
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("&") != -1){//包含"&"的情况
                leftStr = line;
                while (leftStr.indexOf("&") != -1){
                    opIndex = leftStr.indexOf("&");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("&&") != -1)
                    { OptorCounter[Operators.indexOf("&&")]++; }
                    else{
                        if (curStr.indexOf("&=") == -1)
                        { OptorCounter[Operators.indexOf("&")]++; }
                    }
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("|") != -1){ //包含"|"的情况
                leftStr = line;
                while (leftStr.indexOf("|") != -1){
                    opIndex = leftStr.indexOf("|");
                    if(opIndex+1 == leftStr.length()) break;//为什么会出现这种情况???
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("||") != -1)
                    { OptorCounter[Operators.indexOf("||")]++; }
                    else{
                        if (curStr.indexOf("|=") == -1)
                        { OptorCounter[Operators.indexOf("|")]++; }
                    }
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            
            //检测"*","/","%","!","^","~"系列
            if (line.indexOf("*") != -1){//包含"*"
                leftStr = line;
                while (leftStr.indexOf("*") != -1){
                    opIndex = leftStr.indexOf("*");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    leftStr = leftStr.substring(opIndex + 2);
                    if (curStr.indexOf("*=") == -1)
                    { OptorCounter[Operators.indexOf("*")]++; }
                    
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("/") != -1){//包含"/"
                leftStr = line;
                while (leftStr.indexOf("/") != -1){
                    opIndex = leftStr.indexOf("/");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("/=") == -1)
                    { OptorCounter[Operators.indexOf("/")]++; }
                   
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("%") != -1) {//包含"%"
                leftStr = line;
                while (leftStr.indexOf("%") != -1){
                    opIndex = leftStr.indexOf("%");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("%=") == -1)
                    { OptorCounter[Operators.indexOf("%")]++; }
                    
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("!") != -1){//包含"!"
                leftStr = line;
                while (leftStr.indexOf("!") != -1){
                    opIndex = leftStr.indexOf("!");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("!=") == -1)
                    { OptorCounter[Operators.indexOf("!")]++; }
                    
                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("^") != -1){//包含"^"
                leftStr = line;
                while (leftStr.indexOf("^") != -1){
                    opIndex = leftStr.indexOf("^");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("^=") == -1)
                    { OptorCounter[Operators.indexOf("^")]++; }

                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }
            if (line.indexOf("~") != -1){//包含"~"
                leftStr = line;
                while (leftStr.indexOf("~") != -1){
                    opIndex = leftStr.indexOf("~");
                    if(opIndex+1 == leftStr.length()) break;
                    curStr = leftStr.substring(0, opIndex + 2);
                    if (curStr.indexOf("~=") == -1)
                    { OptorCounter[Operators.indexOf("~")]++; }

                    if (opIndex >= leftStr.length() - 3) break;
                    leftStr = leftStr.substring(opIndex + 2);
                }
            }   
      
            //检测"."------------------------以下没修改
            if (line.indexOf(".") != -1){
                leftStr = line;
                while (leftStr.indexOf(".") != -1){
                    opIndex = leftStr.indexOf(".");
                    if (leftStr.length() == opIndex + 1){
                    	OptorCounter[Operators.indexOf(".")]++;
                        break;
                    }
                    curStr = leftStr.substring(0, opIndex + 2);
                    leftStr = leftStr.substring(opIndex + 1);
                    //检查"."前后是否都是数字
                    if (!curStr.substring(opIndex - 1, opIndex + 2).matches("\\d\\.\\d"))
                    { OptorCounter[Operators.indexOf(".")]++; }
                }
            }
            
            //检测"?:","()","[]"
            if (line.indexOf("?") != -1 && line.indexOf(":") != -1){//检测"?:"
                leftStr = line;
                int index1, index2;  //分别保存"?"和":"的位置
                while (leftStr.indexOf("?") != -1 && leftStr.indexOf(":") != -1){
                    index1 = leftStr.indexOf("?");
                    index2 = leftStr.indexOf(":");
                    if (leftStr.length() == index2 + 1){
                        OptorCounter[Operators.indexOf("?:")]++;
                        break;
                    }
                    else if (index1 > index2){
                        OptorCounter[Operators.indexOf("?:")]++;
                        break;
                    }
                    curStr = leftStr.substring(0, index2 + 2);  //以":"进行分割
                    leftStr = leftStr.substring(index2 + 2);
                    String str = curStr.substring(index1, index2);
                    if (str.indexOf(";") == -1) //只要"?"和":"中间没有";"（是否准确？？）
                    { OptorCounter[Operators.indexOf("?:")]++; }   // "?;"=>"?;"
                }
            }
            
            //检测"()"，考虑到()嵌套等情况
            if (line.indexOf("(") != -1 && line.indexOf(")") != -1 && line.indexOf("(") <  line.indexOf(")")){
                int indexFirstL, indexLastR;  //获得第一个"("和最后一个")"的位置
                indexFirstL = line.indexOf("(");
                indexLastR = line.lastIndexOf(")");
                /*System.out.println("==========================================");
                System.out.println("L:  " + indexFirstL + "  R:  " + indexLastR);
                System.out.println("==========================================");
                */
                String str = line.substring(indexFirstL, indexLastR+1); //截取中间的字符串
                int lCount = 0,coupleCount = 0;    //记录未配对的左括号及括号对的数量，默认值都为0
                for (int i = 0; i < str.length(); i++){
                    if (str.charAt(i) == '(')
                    { lCount++; }
                    else if (str.charAt(i) == ')')
                    { lCount--; coupleCount++; }
                    else
                    { continue; }
                }
                OptorCounter[Operators.indexOf("()")] += coupleCount;
            }

            //检测"[]"
            if (line.indexOf("[") != -1 && line.indexOf("]") != -1 && line.indexOf("[") <line.indexOf("]")){
                int indexFirstL, indexLastR;  //获得第一个"("和最后一个")"的位置
                indexFirstL = line.indexOf("[");
                indexLastR = line.lastIndexOf("]");
                String str = line.substring(indexFirstL+1, indexLastR); //截取中间的字符串
                int lCount, coupleCount;    //记录未配对的左括号及括号对的数量，默认值都为0
                lCount = 0;
                coupleCount = 0;
                for (int i = 0; i < str.length(); i++){
                    if (str.charAt(i) == '[')
                    { lCount++; }
                    else if (str.charAt(i) == ']')
                    { lCount--; coupleCount++; }
                    else
                    { continue; }
                }
                OptorCounter[Operators.indexOf("[]")] += coupleCount;
            }
            
            //检测"instanceof"
            if (line.indexOf(" instanceof ") != -1){
                leftStr = line;
                while (leftStr.indexOf(" instanceof ") != -1)
                {
                    opIndex = leftStr.indexOf(" instanceof ");
                    curStr = leftStr.substring(0, opIndex + 12);
                    leftStr = leftStr.substring(opIndex + 12);
                    OptorCounter[Operators.indexOf("instanceof")]++;
                }
            }
        }//For
        
        //统计操作符种类及数量
        for (int keyCount : KeyCounter){
            if (keyCount > 0){
                this.uniOPERATORCount++;    //统计操作符种类
                this.totalOPERATORCount += keyCount;    //统计操作符总量
            }
        }

        for (int optorCount : OptorCounter){
            if (optorCount > 0){
                this.uniOPERATORCount++;
                this.totalOPERATORCount += optorCount;
            }
        }
    }

    // 统计代码段的操作数信息
    private void GetOperandCountFromCode(List<String> codeFragment){
    	List<String> operandList = new ArrayList<String>();  //用于存放操作数的表
    	List<Integer> operandCounter = new ArrayList<Integer>(); //用来保存操作数出现次数的表
    	List<String> operatorList = new ArrayList<String>();  //用于存放操作符的表
    	List<Integer> operatorCounter = new ArrayList<Integer>(); //用来保存操作符出现次数的表
        boolean flagOperand,flagOperator;
        //逐行查找操作数
        for(int k=0;k<codeFragment.size();k++){
        	String tar = codeFragment.get(k);
        	
        	//可能有函数调用,用.和(区分是否是函数调用 , 还可能是属性
        	if(tar.indexOf(".") != -1){
        		String[] maybeFunc = tar.split("\\.");
        		for(int c=1;c<maybeFunc.length;c++){
        			if(maybeFunc[c].indexOf("(") != -1){
        				int opIndex = maybeFunc[c].indexOf("(");
        				flagOperator = false;
        				for(int j=0;j<operatorList.size();j++){
        					if(maybeFunc[c].substring(0,opIndex).equals(operatorList.get(j))){
        						flagOperator = true;
        						operatorCounter.set(j, operatorCounter.get(j)+1);
        						break;
        					}
        				}
        				if (!flagOperator){  //如果是新出现的操作符
                            operatorList.add(maybeFunc[c].substring(0,opIndex));
                            operatorCounter.add(1);
                        }
        			}
        		}
        	}
        	
        	List<String> tokens = new ArrayList<String>();
       		Pattern pattern=Pattern.compile("\\b[a-zA-Z_0-9]+[a-zA-Z_0-9]*\\b"); //匹配变量，常量，包括函数名
       		Matcher matcher=pattern.matcher(tar);
       		while(matcher.find()){
       			 tokens.add(matcher.group(0));
       		}
       		if(tokens.size() != 0){
        		for (int i = 0; i < tokens.size(); i++){
        			String tarToken = tokens.get(i);
                    //检查标识符是否是关键字
        			KeyWordsCollection keyCollection = new KeyWordsCollection();
                    if (keyCollection.IsKeyWord(tarToken))	continue;
                    else{		//如果不是关键字       
                    	//粗略的算下，忽略各种空格情况。。。。。。
                    	int opStartIndex = tar.indexOf(tarToken);//tarToken 第一个字符的index
                    	int opEndIndex = opStartIndex + tarToken.length() - 1;//tarToken 最后一个字符的index
                    	//带.前面已经包括,该情况针对本地函数直接调用
                    	if(opEndIndex <= tar.length()-3){//至少要留出()的长度
                    		if((opStartIndex == 0 && tar.charAt(opEndIndex+1) == '(') || 
                        			(opStartIndex > 0 && tar.charAt(opStartIndex-1) != '.' && tar.charAt(opEndIndex+1) == '(') ){
                        		
                        		flagOperator = false;
                				for(int j=0;j<operatorList.size();j++){
                					if(tarToken.equals(operatorList.get(j))){
                						flagOperator = true;
                						operatorCounter.set(j, operatorCounter.get(j)+1);
                						break;
                					}
                				}
                				if (!flagOperator){  //如果是新出现的操作符
                                    operatorList.add(tarToken);
                                    operatorCounter.add(1);
                                }
                        	}
                    	}
                    	
                    	//整个统计个数，可能为函数名或者操作数	
                		flagOperand = false;   //flag标记是否是已存在的操作数
                		for(int j=0;j<operandList.size();j++){
                			if(tokens.get(i).equals(operandList.get(j))){
                				flagOperand = true;
                				operandCounter.set(j, operandCounter.get(j)+1);//对应计数器+1
                				break;
                			}
                		}
                		if (!flagOperand){  //如果是新出现的操作数
                            operandList.add(tokens.get(i));
                            operandCounter.add(1);
                        }
                    }   
        		}
       		} 	       	
        }
        
        
        this.uniOperandCount = operandList.size() - operatorList.size();   //唯一操作数数量,要减去函数调用,它算操作符
        for (int count : operandCounter) {  //统计操作数总量
            this.totalOperandCount += count;
        }
        
        if(operatorList.size()!=0){         
            this.uniOPERATORCount += operatorList.size();//唯一操作符加上函数调用
            for (int count : operatorCounter) {  
            	this.totalOperandCount -= count; //统计操作数总量,要减去操作符总量
                this.totalOPERATORCount += count;//统计函数调用操作数总量
            }
        }    
    }

    public int getUniOPERATORCount() {
		return uniOPERATORCount;
	}

	public int getUniOperandCount() {
		return uniOperandCount;
	}

	public int getTotalOPERATORCount() {
		return totalOPERATORCount;
	}

	public int getTotalOperandCount() {
		return totalOperandCount;
	}
}
