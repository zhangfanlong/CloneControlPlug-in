package Myplugin.CloneControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.IdentityHashMap;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import CloneRepresentation.CloneGroup;
import CloneRepresentation.CloneFragment;
import Global.VariationInformation;
import MyEditors.CopyAction;
import ExtractFeatures.CreateTrainedFeatureVector;
import PreProcess.CreateCRDInfo;
import PreProcess.Diff;
import PreProcess.XML;
 
public class CloneStore {
	
	public static List<CloneGroup> CloneGroupList_currentVersion;
	//public static HashMap<IFile, Vector<Position>> clonesPerFile = new HashMap();  //ÿ���ļ��е����п�¡Ƭ�ε�λ�ã������ļ�
	public static HashMap<Clone, Vector<Clone>> clone2group = new HashMap();//��ʾÿ����¡Ⱥ��Ŀ�¡Ƭ�Σ�������¡Ⱥ
	public static HashMap<Clone, Clone> cloneMap = new HashMap(); //ԴԴ��Ӧ��Ŀ��Ŀ�Ķ�Ӧ��������ҵ���Ƭ��
	

	public static void addClonePair(Clone source, Clone target) { //��������������ݽṹ������

		IFile sourceFile = source.file;
		Position sourceRange = source.range;
		IFile targetFile = target.file;
		Position targetRange = target.range;

		
		Vector group = (Vector) clone2group.get(source);
		if (group == null) {
			group = new Vector();
			group.add(source);
			group.add(target);
			clone2group.put(source, group);
			clone2group.put(target, group);

		} else {
			Vector tempGroup=group;
			tempGroup.add(target);
			for(int i=0;i<group.size();i++){			
				clone2group.put((Clone)group.get(i), tempGroup);
			}
			clone2group.put(target, tempGroup);

			//clonesInPaste.add(targetRange);
		}

		if (!cloneMap.containsKey(source)) {
			cloneMap.put(source, source);
		}
		if (!cloneMap.containsKey(target))
			cloneMap.put(target, target);
	
	}	//addClonePair

	public static Clone queryClone(Clone source) {
		Clone original = (Clone) cloneMap.get(source);
		return original;
	}
	
	public static void OutputClones(){
		HashMap<Clone,Clone> cloneFrags = new HashMap<Clone,Clone>();   //�����ã�key��valueһ���ģ�������ҵ���Ƭ��
		int groupID = 1;
		Iterator iter = clone2group.entrySet().iterator();
	
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Clone key = (Clone) entry.getKey();
			Vector<Clone> val = (Vector<Clone>) entry.getValue();
			
			if(cloneFrags.get(key) == null){
				System.out.println("Group ID:" + groupID++);
				for(Clone valClone : val){	
					try {
						System.out.println("FileName :" + valClone.file.getName() 
								+ "   Path :" + valClone.file.getFullPath() + "\n"//����·��
								+ "   StartLine :" + (valClone.doc.getLineOfOffset(valClone.range.getOffset())+1)
								+ "   EndLine :" + (valClone.doc.getLineOfOffset(valClone.range.getOffset() + valClone.range.getLength()-1)+1)
								);
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
					cloneFrags.put(valClone, valClone);		
				}
			}
		}
	}
	
	public static void WriteClones() throws BadLocationException{
		HashMap<Clone,Clone> cloneFrags = new HashMap<Clone,Clone>();   //�����ã�key��valueһ���ģ�������ҵ���Ƭ��
		int groupID = 1;
		int versionID = 1;
		if(VariationInformation.clonesInVersion == null){ versionID = 1; }
		else if(VariationInformation.clonesInVersion.size()!=0) {
			versionID = VariationInformation.clonesInVersion.size();
		}
		Iterator iter = clone2group.entrySet().iterator();
	
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Clone key = (Clone) entry.getKey();
			Vector<Clone> val = (Vector<Clone>) entry.getValue();
			
			if(cloneFrags.get(key) == null){
				CloneGroup group = new CloneGroup();
				group.setCGID(groupID++);//��¡��ID
				group.setVersionID(versionID);//�汾����ô��ӣ�����������������Ҫѵ���Ŀ���+1��ûѵ������ô�㣬�����˵
				int fragCount = 0; //���ڿ�¡Ƭ������  Vector<Clone> val  �����У����Գ�ʼֵΪ0
				int fragID = 1;
				List<CloneFragment> fragList = new ArrayList<CloneFragment>();
				for(Clone valClone : val){	
					fragCount ++;
					CloneFragment frag = new CloneFragment();
					frag.setCFID(fragID++);
					frag.setCGID(group.getCGID());
					frag.setVersionID(group.getVersionID());
					frag.setPath(valClone.file.getFullPath().toString().substring(1));//�ͼ�⹤�߽������һ�£����Բ�Ҫ��ǰ���"/"
					frag.setFileName(valClone.file.getName());//û��extension��ֻ����
					frag.setStartLine(valClone.doc.getLineOfOffset(valClone.range.getOffset()) + 1);//getLineOfOffset()���ش�0��ʼ������������+1
					frag.setEndLine(valClone.doc.getLineOfOffset(valClone.range.getOffset()+valClone.range.getLength()-1) + 1 );
					//CRD���״治���ء��������Ȳ���
					fragList.add(frag);
					cloneFrags.put(valClone, valClone);	
				}
				group.setNumberofCF(fragCount);
				
				float similarity = (float) 1.0;//��¡�������ƶ�
				float tempSim = similarity;
				for(int m=0;m<val.size()-1;m++){
					//�õ�����Ƭ��
					List<String> codes_m = new ArrayList<String>();
					codes_m = val.elementAt(m).GetCodesOfCloneRange();
					
					for(int n=m+1;n<val.size();n++){
						List<String> codes_n = new ArrayList<String>();
						codes_n = val.elementAt(n).GetCodesOfCloneRange();
						//�������ƶ�
						Diff.UseDefaultStrSimTh();  //ʹ�������ƶ���ֵĬ��ֵ0.5
						tempSim =  Diff.FileSimilarity(new Diff().DiffFiles(codes_m, codes_n), codes_m.size(), codes_n.size(), true);
						//ѡ����С���ƶ�
						if (tempSim < similarity){
							similarity = tempSim;
						}
					}
				}
				group.setSimilarity((int)(similarity*100));//Ϊ�˺�Nicad ���һ�´�ɰٷ���,����û��С������		
				group.setClonefragment(fragList);

				
				if(CloneGroupList_currentVersion == null){ CloneGroupList_currentVersion = new ArrayList<CloneGroup>();}
				CloneGroupList_currentVersion.add(group);
				
				
			}	// if ����
		}// while ����
		
		//����xml�ڵ�
		Document xml = XML.Create();
		Element clonesRoot = xml.createElement("clones");
		xml.appendChild(clonesRoot);
		// ��ӿ�¡����Ϣ
		Element classinfo = xml.createElement("classinfo");
		classinfo.setAttribute("nclasses", String.valueOf(CloneGroupList_currentVersion.size()));
		classinfo.setAttribute("project", CopyAction.getProjectName());
		clonesRoot.appendChild(classinfo);

		for(CloneGroup g : CloneGroupList_currentVersion){
			Element group = xml.createElement("class");
			group.setAttribute("versionid", String.valueOf(g.getVersionID()));
			group.setAttribute("classid", String.valueOf(g.getCGID()));
			group.setAttribute("nclones", String.valueOf(g.getNumberofCF()));
			group.setAttribute("similarity", String.valueOf(g.getSimilarity()));
			
			for(CloneFragment f : g.getClonefragment()){
				Element frag = xml.createElement("source");
				frag.setAttribute("versionid", String.valueOf(f.getVersionID()));
				frag.setAttribute("classid", String.valueOf(f.getCGID()));
				frag.setAttribute("fragmentid", String.valueOf(f.getCFID()));
				frag.setAttribute("file", f.getPath());//�������Ŀ�����λ��
				frag.setAttribute("startline", String.valueOf(f.getStartLine()));
				frag.setAttribute("endline", String.valueOf(f.getEndLine()));
				
				group.appendChild(frag);
			}
			
			clonesRoot.appendChild(group);
		}
		
		//����ǰ�����д��Ӳ��(Ĭ��д�����һ�θ��Ʋ���������Ŀ���ļ�����)
		XML.Save(xml, CopyAction.getProjectAbsPath()+"/DetectiongResults_CurrentVersion.xml");
	}
	
	public static class Clone {	
		IFile file;
		Position range;
		
		IDocument doc;//�����ô�������
		int cloneID;

		public Clone(IFile file, Position range,int cloneID){
			this.file = file;
			this.range = range;
			this.cloneID = cloneID;
		}
		
		public Clone(IFile file, Position range, IDocument doc, int cloneID) {
			this.file = file;
			this.range = range;
			this.doc = doc;
			this.cloneID = cloneID;
		}

		public boolean equals(Object o) {
			return ((o instanceof Clone)) && (((Clone) o).file.equals(this.file)) && (((Clone) o).range.equals(this.range));
		}

		public int hashCode() {
			return this.file.hashCode();
		}
		
		public List<String> GetCodesOfCloneRange(){ //����Clone��Χ�ô�����Ĵ���
			String sysPath = this.file.getLocation().toString();
			List<String> sourceCodes = new ArrayList<String>();
			sourceCodes = CreateCRDInfo.GetFileContent(sysPath);
			int startLine=0,endLine=0;
			try {
				startLine = doc.getLineOfOffset(range.getOffset())+1;
				endLine = doc.getLineOfOffset(range.getOffset()+range.getLength()-1)+1;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			List<String> cloneCodes = new ArrayList<String>();
			cloneCodes = CreateCRDInfo.GetCFSourceFromCRDInfo(sourceCodes, startLine, endLine);
			return cloneCodes;
		}
	}
}
