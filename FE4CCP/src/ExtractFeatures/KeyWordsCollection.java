package ExtractFeatures;

import java.util.List;
import java.util.ArrayList;

public class KeyWordsCollection {

    //枚举类型定义语言共有关键字（扩大集合，包含面向对象语言的关键字）(50)
    private List<String> _commonKeyWords = new ArrayList<String>();
    private List<String> _JavaKeyWords = new ArrayList<String>();
    
    public KeyWordsCollection(){    //初始化
    	_commonKeyWords.add("abstract");
    	_commonKeyWords.add("auto");
    	_commonKeyWords.add("boolean");
    	_commonKeyWords.add("break");
    	_commonKeyWords.add("case");
    	_commonKeyWords.add("catch");
    	_commonKeyWords.add("char");
    	_commonKeyWords.add("class");
    	_commonKeyWords.add("const");
    	_commonKeyWords.add("continue");
    	_commonKeyWords.add("default");
    	_commonKeyWords.add("do");
    	_commonKeyWords.add("double");
    	_commonKeyWords.add("else");
    	_commonKeyWords.add("enum");
    	_commonKeyWords.add("extern");
    	_commonKeyWords.add("false");
    	_commonKeyWords.add("finally");
    	_commonKeyWords.add("float");   	
    	_commonKeyWords.add("for");
    	_commonKeyWords.add("goto");
    	_commonKeyWords.add("if");
    	_commonKeyWords.add("int");
    	_commonKeyWords.add("long");
    	_commonKeyWords.add("malloc"); 	
    	_commonKeyWords.add("namespace");
    	_commonKeyWords.add("new");
    	_commonKeyWords.add("null");
    	_commonKeyWords.add("private");
    	_commonKeyWords.add("protected");
    	_commonKeyWords.add("public");
    	_commonKeyWords.add("return");
    	_commonKeyWords.add("short");
    	_commonKeyWords.add("signed");
    	_commonKeyWords.add("sizeof");
    	_commonKeyWords.add("static");
    	_commonKeyWords.add("switch");
    	_commonKeyWords.add("this");
    	_commonKeyWords.add("throw");
    	_commonKeyWords.add("try");
    	_commonKeyWords.add("true");
    	_commonKeyWords.add("typedef");
    	_commonKeyWords.add("unsigned");
    	_commonKeyWords.add("using");
    	_commonKeyWords.add("virtual");
    	_commonKeyWords.add("void");
    	_commonKeyWords.add("volatile");
    	_commonKeyWords.add("while");
    	
    	_JavaKeyWords.add("boolean");
    	_JavaKeyWords.add("byte");
    	_JavaKeyWords.add("extends");
    	_JavaKeyWords.add("final");
    	_JavaKeyWords.add("interface");
    	_JavaKeyWords.add("implements");
    	_JavaKeyWords.add("import");
    	_JavaKeyWords.add("instanceof");
    	_JavaKeyWords.add("java");//不是关键字，但在引用的包中经常出现，不能视为标识符
    	_JavaKeyWords.add("native");
    	_JavaKeyWords.add("package");
    	_JavaKeyWords.add("strictfp");
    	_JavaKeyWords.add("synchronized");
    	_JavaKeyWords.add("super");
    	_JavaKeyWords.add("transient");
    	_JavaKeyWords.add("throws");
    }

    // 判断标识符是否是关键字
    public boolean IsKeyWord(String identifier){
        for (String key : _commonKeyWords){
            if (identifier == key)	return true; 
        }

        for (String key : _JavaKeyWords){
            if (identifier == key)	return true; 
        } 

        return false;
    }

}
