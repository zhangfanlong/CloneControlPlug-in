package Myplugin.CloneControl;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.CreationReference;
import org.eclipse.jdt.core.dom.Dimension;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EmptyStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ExpressionMethodReference;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.Initializer;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.IntersectionType;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LabeledStatement;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberRef;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodRef;
import org.eclipse.jdt.core.dom.MethodRefParameter;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.NameQualifiedType;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SuperMethodReference;
import org.eclipse.jdt.core.dom.SwitchCase;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclarationStatement;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.TypeMethodReference;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.UnionType;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.core.dom.WildcardType;
import org.eclipse.jface.text.Position;


public class CloneBoundaryVisitor extends ASTVisitor {

	private int startIntRange;
	private int endIntRange;
	private int boundaryStart;
	private int boundaryEnd;
	private boolean startSet;
	private boolean endSet;

	public Position getBoundary() {
		if ((this.startSet) && (this.endSet)) {
			return new Position(this.boundaryStart, this.boundaryEnd - this.boundaryStart + 1);
		}
		return null;
	}
	
	public CloneBoundaryVisitor(int startRange, int endRange) {
		this.boundaryStart = (this.startIntRange = startRange);
		this.boundaryEnd = (this.endIntRange = endRange);
		this.startSet = (this.endSet = false);
	}

	private boolean overlapping(ASTNode node) {
		int nodeStart = node.getStartPosition();
		int nodeEnd = node.getStartPosition() + node.getLength() - 1;

		if ((nodeStart > this.endIntRange) || (nodeEnd < this.startIntRange)) {
			return false;
		}
		if ((nodeStart >= this.startIntRange) && (nodeEnd <= this.endIntRange)) {
			if (!this.startSet) {
				this.boundaryStart = nodeStart;
				this.startSet = true;
			}
			if (!this.endSet) {
				this.boundaryEnd = nodeEnd;
				this.endSet = true;
			} else if (this.boundaryEnd < nodeEnd) {
				this.boundaryEnd = nodeEnd;
			}
		}
		return true;
	}

	public boolean visit(AnonymousClassDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(AnnotationTypeDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(EnumDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(TypeDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(AnnotationTypeMemberDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(EnumConstantDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(FieldDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(Initializer node) {
		return overlapping(node);
	}

	public boolean visit(MethodDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(CatchClause node) {
		return overlapping(node);
	}

	public boolean visit(BlockComment node) {
		return overlapping(node);
	}

	public boolean visit(Javadoc node) {
		return overlapping(node);
	}

	public boolean visit(LineComment node) {
		return overlapping(node);
	}

	public boolean visit(CompilationUnit node) {
		return overlapping(node);
	}

	public boolean visit(MarkerAnnotation node) {
		return overlapping(node);
	}

	public boolean visit(NormalAnnotation node) {
		return overlapping(node);
	}

	public boolean visit(SingleMemberAnnotation node) {
		return overlapping(node);
	}

	public boolean visit(ArrayAccess node) {
		return overlapping(node);
	}

	public boolean visit(ArrayCreation node) {
		return overlapping(node);
	}

	public boolean visit(ArrayInitializer node) {
		return overlapping(node);
	}

	public boolean visit(Assignment node) {
		return overlapping(node);
	}

	public boolean visit(BooleanLiteral node) {
		return overlapping(node);
	}

	public boolean visit(CastExpression node) {
		return overlapping(node);
	}

	public boolean visit(CharacterLiteral node) {
		return overlapping(node);
	}

	public boolean visit(ClassInstanceCreation node) {
		return overlapping(node);
	}

	public boolean visit(ConditionalExpression node) {
		return overlapping(node);
	}

	public boolean visit(FieldAccess node) {
		return overlapping(node);
	}

	public boolean visit(InfixExpression node) {
		return overlapping(node);
	}

	public boolean visit(InstanceofExpression node) {
		return overlapping(node);
	}

	public boolean visit(MethodInvocation node) {
		return overlapping(node);
	}

	public boolean visit(QualifiedName node) {
		return overlapping(node);
	}

	public boolean visit(SimpleName node) {
		return overlapping(node);
	}

	public boolean visit(NullLiteral node) {
		return overlapping(node);
	}

	public boolean visit(NumberLiteral node) {
		return overlapping(node);
	}

	public boolean visit(ParenthesizedExpression node) {
		return overlapping(node);
	}

	public boolean visit(PostfixExpression node) {
		return overlapping(node);
	}

	public boolean visit(PrefixExpression node) {
		return overlapping(node);
	}

	public boolean visit(StringLiteral node) {
		return overlapping(node);
	}

	public boolean visit(SuperFieldAccess node) {
		return overlapping(node);
	}

	public boolean visit(SuperMethodInvocation node) {
		return overlapping(node);
	}

	public boolean visit(ThisExpression node) {
		return overlapping(node);
	}

	public boolean visit(TypeLiteral node) {
		return overlapping(node);
	}

	public boolean visit(VariableDeclarationExpression node) {
		return overlapping(node);
	}

	public boolean visit(ImportDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(MemberRef node) {
		return overlapping(node);
	}

	public boolean visit(MemberValuePair node) {
		return overlapping(node);
	}

	public boolean visit(MethodRef node) {
		return overlapping(node);
	}

	public boolean visit(MethodRefParameter node) {
		return overlapping(node);
	}

	public boolean visit(Modifier node) {
		return overlapping(node);
	}

	public boolean visit(PackageDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(AssertStatement node) {
		return overlapping(node);
	}

	public boolean visit(Block node) {
		return overlapping(node);
	}

	public boolean visit(BreakStatement node) {
		return overlapping(node);
	}

	public boolean visit(ConstructorInvocation node) {
		return overlapping(node);
	}

	public boolean visit(ContinueStatement node) {
		return overlapping(node);
	}

	public boolean visit(DoStatement node) {
		return overlapping(node);
	}

	public boolean visit(EmptyStatement node) {
		return overlapping(node);
	}

	public boolean visit(EnhancedForStatement node) {
		return overlapping(node);
	}

	public boolean visit(ExpressionStatement node) {
		return overlapping(node);
	}

	public boolean visit(ForStatement node) {
		return overlapping(node);
	}

	public boolean visit(IfStatement node) {
		return overlapping(node);
	}

	public boolean visit(LabeledStatement node) {
		return overlapping(node);
	}

	public boolean visit(ReturnStatement node) {
		return overlapping(node);
	}

	public boolean visit(SuperConstructorInvocation node) {
		return overlapping(node);
	}

	public boolean visit(SwitchCase node) {
		return overlapping(node);
	}

	public boolean visit(SwitchStatement node) {
		return overlapping(node);
	}

	public boolean visit(SynchronizedStatement node) {
		return overlapping(node);
	}

	public boolean visit(ThrowStatement node) {
		return overlapping(node);
	}

	public boolean visit(TryStatement node) {
		return overlapping(node);
	}

	public boolean visit(TypeDeclarationStatement node) {
		return overlapping(node);
	}

	public boolean visit(VariableDeclarationStatement node) {
		return overlapping(node);
	}

	public boolean visit(WhileStatement node) {
		return overlapping(node);
	}

	public boolean visit(ArrayType node) {
		return overlapping(node);
	}

	public boolean visit(ParameterizedType node) {
		return overlapping(node);
	}

	public boolean visit(PrimitiveType node) {
		return overlapping(node);
	}

	public boolean visit(QualifiedType node) {
		return overlapping(node);
	}

	public boolean visit(SimpleType node) {
		return overlapping(node);
	}

	public boolean visit(WildcardType node) {
		return overlapping(node);
	}

	public boolean visit(TypeParameter node) {
		return overlapping(node);
	}

	public boolean visit(SingleVariableDeclaration node) {
		return overlapping(node);
	}

	public boolean visit(VariableDeclarationFragment node) {
		return overlapping(node);
	}

	@Override
	public boolean visit(CreationReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(Dimension node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(ExpressionMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(IntersectionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(LambdaExpression node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(NameQualifiedType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(SuperMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TagElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TextElement node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeMethodReference node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}

	@Override
	public boolean visit(UnionType node) {
		// TODO Auto-generated method stub
		return super.visit(node);
	}
	
	
}