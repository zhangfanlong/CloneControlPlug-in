package ExtractFeatures;

public class RelatedNodes { 
	public static enum relevantNode{
		//方法声明不需要，方法调用之前统计过，故也不需要
		this_or_super,
		assignment, //所有赋值语句
		//identifier, //所有变量
		literal,//所有常量(String,char,int,boolean,null)
		
		if_then_statement,
		if_then_else_statement,
		switch_statement,
		
		while_statement,
		do_statement,
		for_statement,
	}
}
