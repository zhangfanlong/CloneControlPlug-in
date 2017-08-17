package Myplugin.CloneControl;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.NumericToNominal;
import MyEditors.PasteAction;
import MyEditors.CopyAction;

public class DocumentPasteListener implements IDocumentListener {
	
	public static int cloneID; //���ƺ�ճ����IDһ�����������
	
	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public void documentChanged(DocumentEvent event) {
		if (PasteAction.pasted != 1 || !CopyAction.isClone) {
			return;
		}
		PasteAction.pasted += 1;
		if (event.fOffset != 0) {
			try {
				if(RelatedWekaOperation.clf == null){
					MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), null, "No Classifiers!" + "\n"
							+ "Prediction function needs to load a classifier,please load one!");
					System.out.println("û�з����������ȼ��أ�");
					return;
				}
				/*ճ���ĵ����к� ��event.getDocument().getLineOfOffset(event.fOffset)+1
				 * ��������event.getDocument().getNumberOfLines(event.getOffset(), event.getText().length())
				 * ճ�����룺event.fText
				 */
				/*�������ĵ����к� ��sourceDoc.getLineOfOffset(copyStart)+1
				 * �����кţ�sourceDoc.getLineOfOffset(copyEnd)+1
				 * ճ�����룺sourceDoc.get(copyStart, PositionTracking.getCopyLength())
				 */
				
				IDocument sourceDoc = CopyAction.getCopiedDoc();
				int copyStartOffset = PositionTracking.getCopyOffset();
				int copyEndOffset = copyStartOffset + PositionTracking.getCopyLength() - 1;

				ASTParser astParser = ASTParser.newParser(8);
				astParser.setSource(sourceDoc.get().toCharArray());
				CompilationUnit cuSource = (CompilationUnit) astParser.createAST(null);
				CloneBoundaryVisitor sourceBoundaryVisitor = new CloneBoundaryVisitor(copyStartOffset, copyEndOffset);
				cuSource.accept(sourceBoundaryVisitor);
				Position sourceRange = sourceBoundaryVisitor.getBoundary();//ʵ�ʵ�copy fragment��Χ
			
				/*System.out.println("ʵ�� : Clone in copy: " + "\n " 
									+ sourceDoc.get(copyStartOffset, PositionTracking.getCopyLength()));
				System.out.println("AST: Clone in copy: " + "\n "
									+ sourceDoc.get(sourceRange.getOffset(), sourceRange.getLength()));*/
		
				/*
				 * 
				//   D:/Test/src/Test.java
				//   E:/JavaWork/workspace/CloneAnalyzer/src/com/hit/fileconverter/FileConverterFactory.java
				System.out.println(CopyAction.getCopiedPath());
				//   /Test/src/Test.java
				//   /CloneAnalyzer/src/com/hit/fileconverter/FileConverterFactory.java
				System.out.println(CopyAction.getProjectName());
				// Test
				// FileConverterFactory
				System.out.println("file name  " + CopyAction.getFileName());
				//E:/JavaWork/workspace/CloneAnalyzer
				System.out.println("abstract path  " + CopyAction.getProjectAbsPath());
				
				*/

				ITextEditor editor = Activator.getActiveEditor();
				IDocumentProvider documentProvider = editor.getDocumentProvider();
				IDocument targetDoc = documentProvider.getDocument(editor.getEditorInput());
				
			 	astParser.setSource(targetDoc.get().toCharArray());
				CompilationUnit cuTarget = (CompilationUnit) astParser.createAST(null);
				CloneBoundaryVisitor targetBoundaryVisitor = new CloneBoundaryVisitor(event.fOffset, event.fOffset + event.getText().length()- 1);
				cuTarget.accept(targetBoundaryVisitor);
				Position targetRange = targetBoundaryVisitor.getBoundary();
				
				/*System.out.println("Clone in pasted : " + "\n"
									+ targetDoc.get(targetRange.getOffset(), targetRange.getLength()));*/
				
				//���ƶ�  ճ����  ������ȡ
				String pastedPath = ((IFileEditorInput) editor.getEditorInput()).getFile().getLocation().toString();
				CreateTestingFeatureVector creTestFeaVector = new CreateTestingFeatureVector(CopyAction.getCopiedPath().toString(),//����·��
						CopyAction.getFileName(),//ֱ�����ļ�����û��extension
						sourceDoc.getLineOfOffset(sourceRange.getOffset()) + 1,// getLineOfOffset()��0��ʼ
						sourceDoc.getLineOfOffset(sourceRange.getOffset()+sourceRange.getLength()-1) + 1,
						pastedPath,
						pastedPath.substring(pastedPath.lastIndexOf("/")+1, pastedPath.indexOf(".java")),
						targetDoc.getLineOfOffset(targetRange.getOffset()) + 1,
						targetDoc.getLineOfOffset(targetRange.getOffset()+targetRange.getLength()-1) + 1
						);
				creTestFeaVector.ExtractFeatureForTesting();
				
				//��Ԥ�⡣����������������ֻ��ͨ������arff�ļ��𣿣�������  
				//Arff�ݴ�����Ŀ�ļ���  �磺  E:/JavaWork/workspace/CloneAnalyzer/temp.arff
				String tempArffPath = CopyAction.getProjectAbsPath() + "/" + "temp.arff";
				RelatedWekaOperation.WriteFeaturesToArff(creTestFeaVector.featureVector, tempArffPath, 1);
				double pre = -1;
				try {
					Instances insTests = RelatedWekaOperation.GetInstances(tempArffPath);
					insTests.setClassIndex(insTests.numAttributes()-1);
					pre = RelatedWekaOperation.getPredictionResult(RelatedWekaOperation.clf, insTests.instance(0));
				} catch (Exception e1) {
					e1.printStackTrace();
				}	

				if(pre > 0.5){
					System.out.println("��Ҫһ����ά���Ŀ�����:  " + pre);
					MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(), null, "Warning! The operation maybe lead extra maintance!");
				}
				
				//���ٿ�¡,�����渴��ճ���Ŀ�¡���뵽���ݽṹ(ֻ���¡�Ļ�����Ϣ)
				IFile sourceFile = CopyAction.javaFile;
				IFile targetFile = ((IFileEditorInput) editor.getEditorInput()).getFile();
				cloneID++;
				CloneStore.Clone sourceClone = new CloneStore.Clone(sourceFile,sourceRange,sourceDoc,cloneID);
				CloneStore.Clone targetClone = new CloneStore.Clone(targetFile,targetRange,targetDoc,cloneID);
				
				CloneStore.Clone result = CloneStore.queryClone(sourceClone);
				boolean sourceCloneExisting = result != null;
				CloneStore.addClonePair(sourceClone, targetClone);
				if (sourceCloneExisting)
			          sourceClone = result;
				
				if (!sourceCloneExisting) {
					PositionTracking.trackingClonePosition(sourceDoc, sourceRange, String.valueOf(cloneID));
					PositionTracking.trackingClonePosition(targetDoc, targetRange, String.valueOf(cloneID));
				} else {
					PositionTracking.trackingClonePosition(targetDoc, targetRange, String.valueOf(result.cloneID));		
				}
			} catch (Exception exception) {
			}
		}
	}

}
