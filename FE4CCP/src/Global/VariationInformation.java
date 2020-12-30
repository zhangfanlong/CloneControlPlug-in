package Global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.CloneGroup;
import CloneRepresentation.ClonesInVersion;
import CloneRepresentation.GroupMapping;
import ExtractFeatures.FeatureVector;


public class VariationInformation {
	
	public static Map<String,List<String>> allVersionJavaFiles;//系统所有类名<"dnsjava-1.2.0",List>
	public static List<ClonesInVersion> clonesInVersion;
	public static List<CloneGroup> cloneGroup;
	
	public static List<GroupMapping> mappingInfo; //以克隆群为最小单位存储map 
	public static List<GroupMapping> unMappedSrcInfo;//没映射上的 只有Src的   版本号,克隆群ID,克隆群大小
	public static List<GroupMapping> unMappedDestInfo;//只有Dest的   版本号,克隆群ID,克隆群大小
	
	public static List<CloneGenealogy> cloneGenealogy;//整个系统的克隆直系
	public static List<CloneGenealogy> singleCgGenealogyList;  //单克隆群家系列表       //只有开始版本（即所在版本）及单版本克隆群ID
	
	public static List<FeatureVector> featureVectorList;
	
	public static void init (){	//为全局变量初始化		
		VariationInformation.allVersionJavaFiles = new HashMap<String,List<String>>();
		VariationInformation.clonesInVersion = new ArrayList<ClonesInVersion>();
		VariationInformation.cloneGroup = new ArrayList<CloneGroup>();
		VariationInformation.mappingInfo = new ArrayList<GroupMapping>();
		VariationInformation.unMappedDestInfo = new ArrayList<GroupMapping>();
		VariationInformation.unMappedSrcInfo = new ArrayList<GroupMapping>();
		VariationInformation.cloneGenealogy = new ArrayList<CloneGenealogy>();
		VariationInformation.singleCgGenealogyList = new ArrayList<CloneGenealogy>(); 
		//VariationInformation.featureVectorList = new ArrayList<FeatureVector>();
		
	}
	
}
