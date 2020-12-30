package ExtractFeatures;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.WhileStatement;

public class StructuralFeatureVisitor extends ASTVisitor {

	private int startPos;
	private int endPos;
	private static boolean getCloneInfo;	//标记是否进入克隆代码区域

	private int[] structuralFeature = new int[RelatedNodes.relevantNode.values().length];
	
	
	public int[] getStructuralFeature() {
		return structuralFeature;
	}

	public StructuralFeatureVisitor(int start,int end){
		startPos=start;
		endPos=end;
	}

	public void preVisit(ASTNode node) {
		int nodeStart = node.getStartPosition();
		int nodeEnd = node.getStartPosition() + node.getLength() - 1;

		if ((nodeStart >= this.startPos) && (nodeEnd <= this.endPos)) {
			getCloneInfo = true;
		} else {
			getCloneInfo = false;
		}
		super.preVisit(node);
	}
	
	//this_or_super计数
	public boolean visit(ThisExpression node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.this_or_super.ordinal()]++;
		}
		return super.visit(node);
	}
	
	public boolean visit(SuperFieldAccess node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.this_or_super.ordinal()]++;
		}
		return super.visit(node);
	}

	public boolean visit(SuperMethodInvocation node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.this_or_super.ordinal()]++;
		}
		return super.visit(node);
	}

	public boolean visit(SuperMethodReference node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.this_or_super.ordinal()]++;
		}
		return super.visit(node);
	}
	
	public boolean visit(SuperConstructorInvocation node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.this_or_super.ordinal()]++;
		}
		return super.visit(node);
	}
	
	
	
	//为所有赋值语句计数
	public boolean visit(Assignment node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.assignment.ordinal()]++;
		}
		return super.visit(node);
	}
	
	
/*
	//为所有变量计数
	public boolean visit(SimpleName node) {
		if(getCloneInfo){
			if(node.resolveBinding().getKind() == IBinding.VARIABLE)
				structuralFeature[RelatedNodes.relevantNode.identifier.ordinal()]++;
		}
		return super.visit(node);
	}
	*/
	
	//为所有常量计数
	public boolean visit(BooleanLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}
	
	public boolean visit(CharacterLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}
	
	public boolean visit(NullLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}

	public boolean visit(NumberLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}

	public boolean visit(StringLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}
	
	public boolean visit(TypeLiteral node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.literal.ordinal()]++;
		}
		return super.visit(node);
	}
	

	
	//if语句计数
	public boolean visit(IfStatement node) {
		if(getCloneInfo){
			if(node.getElseStatement()!=null)
				structuralFeature[RelatedNodes.relevantNode.if_then_else_statement.ordinal()]++;
			else structuralFeature[RelatedNodes.relevantNode.if_then_statement.ordinal()]++;
		}
		return super.visit(node);
	}

	
	
	//Switch语句计数
	public boolean visit(SwitchStatement node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.switch_statement.ordinal()]++;
		}
		return super.visit(node);
	}

	
	
	//while语句计数
	public boolean visit(WhileStatement node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.while_statement.ordinal()]++;
		}
		return super.visit(node);
	}
	
	

	//do_statement计数
	public boolean visit(DoStatement node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.do_statement.ordinal()]++;
		}
		return super.visit(node);
	}
	


	//for语句计算
	public boolean visit(ForStatement node) {
		if(getCloneInfo){
			structuralFeature[RelatedNodes.relevantNode.for_statement.ordinal()]++;
		}
		return super.visit(node);
	}



}
