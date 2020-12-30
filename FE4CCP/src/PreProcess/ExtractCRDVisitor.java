package PreProcess;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import CRDInformation.BlockInfo;
import CRDInformation.MethodInfo;

public class ExtractCRDVisitor extends ASTVisitor{

	private boolean getCloneInfo;	//标记是否进入克隆代码区域
	private boolean getMethod;	//标记是否进入克隆代码所在 函数区域
	
	private int startPos;
	private int endPos;
	
	public String className;
	public  MethodInfo methodInfo;
	public  List<BlockInfo> blockInfos;
	//只记开始行就ok~
	public String relStartLine; //克隆代码是方法，相对位置为1
	public int pOffset; //非方法，包含克隆代码片段的方法，的offset
	
	public ExtractCRDVisitor(int start,int end){
		
		startPos=start;
		endPos=end;
		getCloneInfo = false;
		getMethod = false;
		
		relStartLine = null; 
		pOffset = -1; 
	}
	
	public void preVisit(ASTNode node) {
		int nodeStart = node.getStartPosition();
		int nodeEnd = node.getStartPosition() + node.getLength() - 1;

		if (((nodeStart >= this.startPos) && (nodeEnd <= this.endPos)) && !getMethod) {
			getCloneInfo = true;
		} else {
			getCloneInfo = false;
		}
		super.preVisit(node);
	}
	
	private void setMethodInfo(String methodName,List methodPara){		
		methodInfo = new MethodInfo();
		methodInfo.mName = methodName;
		methodInfo.mParaNum = methodPara.size();
		if(methodInfo.mParaNum == 0){
			methodInfo.mParaTypeList = null;
			methodInfo.mParaNameList = null;
		}
		else{
			for(int i=0;i<methodPara.size();i++){
				String[] para = methodPara.get(i).toString().split(" ");
				int length = para.length;
				//类型可能是两个关键字组成
				if(length == 2){
					methodInfo.mParaTypeList.add(para[0]);
					methodInfo.mParaNameList.add(para[1]);
				}
				else{
					String mParaType="";
					for(int j=0;j<length-1;j++){
						if(mParaType == "")
							mParaType += para[j];
						else
							mParaType = mParaType + " " + para[j];
					}	
					methodInfo.mParaTypeList.add(mParaType);
					methodInfo.mParaNameList.add(para[length-1]);
				}
				
			}		
		}
	}
	
	private void setBlockInfos(String blockType,String bockAnchor){
		if(this.blockInfos == null)
			blockInfos = new ArrayList<BlockInfo>();
		
		BlockInfo block = new BlockInfo();
		block.bType = blockType;
		block.anchor = bockAnchor;
		this.blockInfos.add(block);
	}
	
	public boolean visit(FieldDeclaration node) {
		if(getCloneInfo && node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION){
			 methodInfo = null; 
			 blockInfos = null;
			 relStartLine = null;
			 this.className = ((TypeDeclaration)node.getParent()).getName().toString();
			 getMethod = true;
			 return false;
		}
		return super.visit(node);
	}

	public boolean visit(MethodDeclaration node) {  //直接就是方法，提取完就结束
		if(getCloneInfo){
			if(node.getParent().getNodeType() == ASTNode.TYPE_DECLARATION){//克隆方法在正常类里面
				setMethodInfo(node.getName().toString(),node.parameters());
				relStartLine = "1";
				blockInfos = null;
				this.className = ((TypeDeclaration)node.getParent()).getName().toString();
				getMethod = true;
				return false;
			}else if(node.getParent().getNodeType()==ASTNode.ANONYMOUS_CLASS_DECLARATION){//克隆方法在匿名类里面-----------
				GetRoughCloneInfo(node);
			}
		}	
		return super.visit(node);
	}
	
	public boolean visit(SimpleName node) {	
		if(getCloneInfo){
			if(node.getParent().getNodeType() == ASTNode.METHOD_DECLARATION){
				MethodDeclaration pnode = (MethodDeclaration)node.getParent();
				if(pnode.getParent().getNodeType() == ASTNode.TYPE_DECLARATION){//克隆方法在正常类里面
					setMethodInfo(pnode.getName().toString(),pnode.parameters());
					relStartLine = "1";
					blockInfos = null;	
					this.className = ((TypeDeclaration)pnode.getParent()).getName().toString();
					getMethod = true;
					return false;
				}else if(pnode.getParent().getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION){//克隆方法在匿名类里面-------------
					GetRoughCloneInfo(pnode);	
				}	
			}else {
				GetRoughCloneInfo(node);
				return false;
			}
		}
		return super.visit(node);
	}
	
	public boolean GetRoughCloneInfo(ASTNode node){
		int nOffset = node.getStartPosition();
		ASTNode nParent = node.getParent(); //获取父节点
		if(node.getParent() == null){
			System.out.println("node\n" + node);
			System.out.println("parent\n" + node.getParent());
			return false;
		}
		if(nParent.getNodeType() == ASTNode.TYPE_DECLARATION){
			 methodInfo = null; 
			 blockInfos = null;
			 relStartLine = null;
			 this.className = ((TypeDeclaration)nParent).getName().toString();
			 getMethod = true;
			 return false;
		}
		if(nParent.getNodeType() == ASTNode.METHOD_DECLARATION){
			if(nParent.getParent().getNodeType() == ASTNode.TYPE_DECLARATION){
				setMethodInfo(((MethodDeclaration)nParent).getName().toString(),((MethodDeclaration)nParent).parameters());
				this.pOffset = ((MethodDeclaration)nParent).getName().getStartPosition();//用名字确定，防止有注释
				relStartLine = null;
				this.className = ((TypeDeclaration)nParent.getParent()).getName().toString();
				getMethod = true;
				return false;
			}else if(nParent.getParent().getNodeType() == ASTNode.ANONYMOUS_CLASS_DECLARATION){//克隆方法在匿名类里面-------------
				//System.out.println("匿名");
				GetRoughCloneInfo(nParent);	
				return false;
			}
		}
		if(nParent.getNodeType() == ASTNode.FOR_STATEMENT){
			setBlockInfos("for",((ForStatement)nParent).getExpression().toString());
			GetRoughCloneInfo(nParent);//递归提取父节点信息
			return false;
		}
		if(nParent.getNodeType() == ASTNode.WHILE_STATEMENT){
			setBlockInfos("while",((WhileStatement)nParent).getExpression().toString());
			GetRoughCloneInfo(nParent);//递归提取父节点信息
			return false;
		}
		if(nParent.getNodeType() == ASTNode.DO_STATEMENT){
			setBlockInfos("do",((DoStatement)nParent).getExpression().toString());
			GetRoughCloneInfo(nParent);//递归提取父节点信息
			return false;
		}
		if(nParent.getNodeType() == ASTNode.SWITCH_STATEMENT){
			setBlockInfos("switch",((SwitchStatement)nParent).getExpression().toString());
			GetRoughCloneInfo(nParent);//递归提取父节点信息
			return false;
		}
		if(nParent.getNodeType() == ASTNode.SYNCHRONIZED_STATEMENT){
			setBlockInfos("synchronized",((SynchronizedStatement)nParent).getExpression().toString());
			GetRoughCloneInfo(nParent);
			return false;
		}
		if(nParent.getNodeType() == ASTNode.IF_STATEMENT){ //else if判断
			if(((IfStatement)nParent).getElseStatement()!=null && ((IfStatement)nParent).getElseStatement().getStartPosition() == nOffset){
				setBlockInfos("else",null);
			}
			else {
				setBlockInfos("if",((IfStatement)nParent).getExpression().toString());
			}
			GetRoughCloneInfo(nParent);
			return false;
		}
		if(nParent.getNodeType() == ASTNode.TRY_STATEMENT) { // try finally判断
			if(((TryStatement)nParent).getBody()!=null && ((TryStatement)nParent).getBody().getStartPosition() == nOffset){
				setBlockInfos("try",null);
			}
			else if(((TryStatement)nParent).getFinally()!=null && ((TryStatement)nParent).getFinally().getStartPosition() == nOffset){
				setBlockInfos("finally",null);
			}
			GetRoughCloneInfo(nParent);
			return false;
		}
		if(nParent.getNodeType() == ASTNode.CATCH_CLAUSE){
			setBlockInfos("catch",((CatchClause)nParent).getException().toString());
			GetRoughCloneInfo(nParent);
			return false;
		}
		if(nParent.getNodeType() == ASTNode.FIELD_DECLARATION){	
			methodInfo = null; 
			blockInfos = null;
			relStartLine = null;
			this.className = ((TypeDeclaration)nParent.getParent()).getName().toString();
			getMethod = true;
			return false;
		}
		else{
			//System.out.println(nParent.getNodeType());
			GetRoughCloneInfo(nParent);	//递归提取父节点信息
			return false;
		}
	}

	
	public boolean visit(AnonymousClassDeclaration node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(ExpressionStatement node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(BreakStatement node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(ReturnStatement node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(Block node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(ForStatement node) {
		if(getCloneInfo){
			//setBlockInfos("for",node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(WhileStatement node) {
		if(getCloneInfo){
			//setBlockInfos("while", node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(DoStatement node) {
		if(getCloneInfo){
			//setBlockInfos("do", node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(SwitchStatement node) {
		if(getCloneInfo){
			//setBlockInfos("switch",node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(SwitchCase node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(SynchronizedStatement node) {
		if(getCloneInfo){
			//setBlockInfos("synchronized",node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(IfStatement node) {
		if(getCloneInfo){
			//setBlockInfos("if",node.getExpression().toString());
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(InfixExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(BooleanLiteral node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(TryStatement node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	
	public boolean visit(CatchClause node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(ParenthesizedExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(PostfixExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(PrefixExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(VariableDeclarationExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}
	/////////////////////////////////////////////////////////////////////

	public boolean visit(ArrayAccess node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(ConditionalExpression node) {
		if(getCloneInfo){
			GetRoughCloneInfo(node);
		}
		return super.visit(node);
	}

	public boolean visit(ImportDeclaration node) {
		if(getCloneInfo){
			 methodInfo = null; 
			 blockInfos = null;
			 relStartLine = null;
			 this.className = null;
			 getMethod = true;
			 return false;
		}
		return super.visit(node);
	}

	public boolean visit(PackageDeclaration node) {
		if(getCloneInfo){
			 methodInfo = null; 
			 blockInfos = null;
			 relStartLine = null;
			 this.className = null;
			 getMethod = true;
			 return false;
		}
		return super.visit(node);
	}
	
	
}