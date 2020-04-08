package Myplugin.CloneControl;

import java.io.File;
import java.io.IOException;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import ExtractFeatures.FeatureVector;
import ExtractFeatures.FeatureOperations;
import Global.VariationInformation;


public class RelatedWekaOperation {

	public static Classifier clf;
	
	
    /**从.arff文件中获取样本Instances;
     * @param arffPath   arff特征文件的绝对路径
     * @return 已经过滤完成的 Instances
     * @throws Exception
     */
    public static Instances GetInstances(String arffPath) throws Exception{
       File file= new File(arffPath);
       return GetInstances(file);
    }
 
    /**从.arff文件中获取样本Instances;
     * @param file 获得instances的File对象
     * @return 已经过滤完成的 Instances
     * @throws Exception
     */
    public static Instances GetInstances(File arffFile) throws Exception{
       Instances inst = null;
       try{
           ArffLoader loader = new ArffLoader();
           loader.setFile(arffFile);
           inst = loader.getDataSet();
       }
       catch(Exception e){
           throw new Exception(e.getMessage());
       }
       
       inst.setClassIndex(inst.numAttributes()-1);
       //预处理
       NumericToNominal numToNom = new NumericToNominal();
       numToNom.setInputFormat(inst);
       return numToNom.useFilter(inst, numToNom);
    }
    
    
    /**从已经训练好的预测模型路径中加载该分类器
     * @param modeltPath
     * @return 该预测模型分类器
     */
    public static void InitClassifierFromModel(String modeltPath){
    	try {
    		RelatedWekaOperation.clf = (Classifier) weka.core.SerializationHelper.read(modeltPath);
    		System.out.println("模型加载成功！");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
    
    
	public static void CreateClassifierModel(String featurePath){
		File file=new File(featurePath);
		ArffLoader arff =null;
		Instances insTrain=null;
		
		try {
			arff = new ArffLoader();
			arff.setFile(file);
			insTrain = arff.getDataSet(); // 读入训练文件
			insTrain.setClassIndex(insTrain.numAttributes()-1);
			
			RelatedWekaOperation.clf=(Classifier)Class.forName("weka.classifiers.bayes.BayesNet").newInstance();//初始化分类器
			RelatedWekaOperation.clf.buildClassifier(insTrain);//使用训练样本训练分类器

		}  catch (Exception e) {
			e.printStackTrace();
		}
	}
 
    
    /**用于检测分类模型
     * @param clf 已经训练好的分类模型
     * @param ins 测试样本(一个)
     * @return 一个Evaluation对象
     */
	public static double getPredictionResult(Classifier classifer, Instance ins) {
		
		System.out.println("instance: "+ins);
		
		double predicted = -1;
		try {
			predicted = classifer.distributionForInstance(ins)[1];
			System.out.println("================================");
			for (int i=0;i<classifer.distributionForInstance(ins).length;i++)
				System.out.println(classifer.distributionForInstance(ins)[i]);
			System.out.println("================================");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return predicted;
	}
    
    public static void WriteFeaturesToArff(FeatureVector vec, String path, int flag){
    	
		FastVector atts;  
		FastVector attVals; 
        Instances data;  
        double[] vals;  
         
        atts = new FastVector();   
        
        atts.addElement(new Attribute("sourceLine")); 
        
        atts.addElement(new Attribute("uniOPERATORCount")); 
        atts.addElement(new Attribute("uniOperandCount")); 
        atts.addElement(new Attribute("totalOPERATORCount")); 
        atts.addElement(new Attribute("totalOperandCount"));        

        atts.addElement(new Attribute("totalMethodInvocCount")); 
        atts.addElement(new Attribute("libraryMethodInvocCount")); 
        atts.addElement(new Attribute("localMethodInvocCount")); 
        atts.addElement(new Attribute("otherMethodInvocCount")); 
        atts.addElement(new Attribute("totalParameterCount")); 
       
        attVals = new FastVector();  
        attVals.addElement("0");
        attVals.addElement("1");
        atts.addElement(new Attribute("isLocalClone",attVals));  
        atts.addElement(new Attribute("simFileName")); 
        atts.addElement(new Attribute("simMaskedFileName")); 
        atts.addElement(new Attribute("simMethodName")); 
        atts.addElement(new Attribute("simTotalParaName")); 
        atts.addElement(new Attribute("simMaxParaName")); 
        atts.addElement(new Attribute("simTotalParaType")); 
        atts.addElement(new Attribute("isSameBlockInfo",attVals));
        atts.addElement(new Attribute("simCloneFragments")); 
        
        atts.addElement(new Attribute("this_or_super")); 
        atts.addElement(new Attribute("assignment")); 
        atts.addElement(new Attribute("identifier"));        
        atts.addElement(new Attribute("if_then_statement")); 
        atts.addElement(new Attribute("if_then_else_statement")); 
        atts.addElement(new Attribute("switch_statement")); 
        atts.addElement(new Attribute("while_statement")); 
        atts.addElement(new Attribute("do_statement"));
        atts.addElement(new Attribute("for_statement")); 

        atts.addElement(new Attribute("consistency",attVals));  //一致性维护label

        data = new Instances("FeatureVectors", atts, 0);  

		vals = new double[data.numAttributes()];  
		vals[0] = vec.getSourceLine();
		vals[1] = vec.getUniOPERATORCount();
		vals[2] = vec.getUniOperandCount();
		vals[3] = vec.getTotalOPERATORCount();
		vals[4] = vec.getTotalOperandCount();
		vals[5] = vec.getTotalMethodInvocCount();
		vals[6] = vec.getLibraryMethodInvocCount();
		vals[7] = vec.getLocalMethodInvocCount();
		vals[8] = vec.getOtherMethodInvocCount();
		vals[9] = vec.getTotalParameterCount();
		if(vec.isLocalClone()) vals[10] = attVals.indexOf("1");
		else vals[10] = attVals.indexOf("0");
		vals[11] = vec.getSimFileName();
		vals[12] = vec.getSimMaskedFileName();
		vals[13] = vec.getSimMethodName();
		vals[14] = vec.getSimTotalParaName();
		vals[15] = vec.getSimMaxParaName();
		vals[16] = vec.getSimTotalParaType();
		if(vec.getIsSameBlockInfo()) vals[17]= attVals.indexOf("1");
		else vals[17] = attVals.indexOf("0");
		vals[18] = vec.getSimCloneFragment();
		vals[19] = vec.getStruFeature()[0];
		vals[20] = vec.getStruFeature()[1];
		vals[21] = vec.getStruFeature()[2];
		vals[22] = vec.getStruFeature()[3];
		vals[23] = vec.getStruFeature()[4];
		vals[24] = vec.getStruFeature()[5];
		vals[25] = vec.getStruFeature()[6];
		vals[26] = vec.getStruFeature()[7];
		vals[27] = vec.getStruFeature()[8];	

		vals[vals.length-1] = attVals.indexOf("1");	//没有该值 先暂设为1
		
        data.add(new Instance(1.0, vals));
		 
        ArffSaver saver = new ArffSaver(); 
        saver.setInstances(data);  
        try {
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	
}
