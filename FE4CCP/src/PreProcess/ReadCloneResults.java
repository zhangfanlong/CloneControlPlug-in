package PreProcess;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;

import CloneRepresentation.CloneFragment;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.ClonesInVersion;


public class ReadCloneResults {

	public ClonesInVersion inVersion = new ClonesInVersion();	
	public List<CloneGroup> groupList = new ArrayList<CloneGroup>();

	
	private Document cloneResult;
	private static int versionID;

	public ReadCloneResults(String fileName,int Nicad){
		cloneResult = XML.Load(fileName);
		List<CloneFragment> fragmentList;
		int fragmentTotal=0;
		
		inVersion.setSystemVersion(XML.GetAttriValue(cloneResult, "systeminfo", 0, "system"));
		inVersion.setVersionID(versionID);
		inVersion.setNumberofCG(Integer.parseInt(XML.GetAttriValue(cloneResult, "classinfo", 0, "nclasses")));

		CloneGroup group;
		CloneFragment fragment;
		for(int i=0;i<inVersion.getNumberofCG();i++){//每个class
			fragmentTotal += Integer.parseInt(XML.GetAttriValue(cloneResult, "class", i, "nclones"));
			group = new CloneGroup();
			group.setCGID(i+1);
			group.setVersionID(versionID);
			group.setNumberofCF(Integer.parseInt(XML.GetAttriValue(cloneResult, "class", i, "nclones")));
			group.setSimilarity(Integer.parseInt(XML.GetAttriValue(cloneResult, "class", i, "similarity")));
			
			fragmentList = new ArrayList<CloneFragment>();
			for(int j=0;j<group.getNumberofCF();j++){//每个节点下的source
				fragment = new CloneFragment();
				String file = XML.GetChildAttriValue(cloneResult, "class", i, j, "file");
				fragment.setCFID(j+1);
				fragment.setCGID(group.getCGID());
				fragment.setVersionID(versionID);
				fragment.setPath(file);
				fragment.setFileName(file.substring(file.indexOf("/"), file.length()));
				fragment.setStartLine(Integer.parseInt(XML.GetChildAttriValue(cloneResult, "class", i, j, "startline")));
				fragment.setEndLine(Integer.parseInt(XML.GetChildAttriValue(cloneResult, "class", i, j, "endline")));
				fragmentList.add(fragment);
			}	
			group.setClonefragment(fragmentList);
			groupList.add(group);
		}
		
		inVersion.setNumberofCF(fragmentTotal);
		inVersion.setCloneGroup(groupList);
		
		++versionID;
	}
}
