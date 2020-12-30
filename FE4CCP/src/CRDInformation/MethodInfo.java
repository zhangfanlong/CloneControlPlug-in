package CRDInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MethodInfo{
	public String mName;//方法名  
	public int mParaNum;//参数个数
	public List<String> mParaTypeList; //参数类型列表，无参时，为null
	public List<String> mParaNameList;//参数名字列表，无参时，为null

	public MethodInfo(){
		mName = null;
		mParaNum = 0;
		mParaTypeList = new ArrayList<String>();
		mParaNameList = new ArrayList<String>();
	}
	
	public boolean equals(MethodInfo  mInfo){
	       if(mInfo == null) return false;
	       if(!this.mName.equals(mInfo.mName))	return false ;
	       if(this.mParaNum != mInfo.mParaNum) return false ;
	       if(this.mParaNum == 0 && mInfo.mParaNum == 0) return true ;
	       if(!compare(this.mParaTypeList, mInfo.mParaTypeList)) return false;
	       return true;
	}
	
	public static <T extends Comparable<T>> boolean compare(List<T> a, List<T> b) {
	    if(a.size() != b.size()) return false;
	    Collections.sort(a);
	    Collections.sort(b);
	    for(int i=0;i<a.size();i++){
	        if(!a.get(i).equals(b.get(i)))
	            return false;
	    }
	    return true;
	}
}