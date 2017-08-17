package Myplugin.CloneControl;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultPositionUpdater;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;

import MyEditors.CopyAction;

public class PositionTracking {
	private static String copiedRange = "_copied_range";
	private static final String clonedCode = "_cloned_code";
	
	public static void trackingCopiedRange(int offset, int length) {

		IDocument copiedDoc = CopyAction.getCopiedDoc();
		try {
			copiedDoc.removePositionCategory(copiedRange);
		} catch (BadPositionCategoryException localBadPositionCategoryException1) {
		}
		copiedDoc.addPositionCategory(copiedRange);
		copiedDoc.addPositionUpdater(new DefaultPositionUpdater(copiedRange));

		try {
			copiedDoc.addPosition(copiedRange, new Position(offset, length));
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		} catch (BadPositionCategoryException e1) {
			e1.printStackTrace();
		}
		
	}

	public static int getCopyOffset() {
		IDocument copiedDoc = CopyAction.getCopiedDoc();
		try {
			Position[] pos = copiedDoc.getPositions(copiedRange);
			return pos[0].offset;
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int getCopyLength() {
		IDocument copiedDoc = CopyAction.getCopiedDoc();
		try {
			Position[] pos = copiedDoc.getPositions(copiedRange);
			return pos[0].length;
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
		return -1;
	}
	

	public static void trackingClonePosition(IDocument doc, Position clonePos, String clondID){ //clondID方便跟踪的标记
		if (!doc.containsPositionCategory(clonedCode+clondID)) {
		     doc.addPositionCategory(clonedCode+clondID);
		     doc.addPositionUpdater(new DefaultPositionUpdater(clonedCode+clondID));
		}
		try {	
			doc.addPosition(clonedCode+clondID, clonePos);
			//System.out.println("跟踪的位置 : " + (doc.getLineOfOffset(clonePos.getOffset())+1));
		} catch (BadLocationException e) {
			e.printStackTrace();
		} catch (BadPositionCategoryException e) {
			e.printStackTrace();
		}
	}

}
