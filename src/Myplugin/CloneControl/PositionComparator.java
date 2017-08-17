package Myplugin.CloneControl;

import java.util.Comparator;

import javax.swing.text.Position;

public class PositionComparator implements Comparator {
	
	public int compare(Object o1, Object o2){
	    Position pos1 = (Position)o1;
	    Position pos2 = (Position)o2;
	    return pos1.getOffset() - pos2.getOffset();
	 }
}
