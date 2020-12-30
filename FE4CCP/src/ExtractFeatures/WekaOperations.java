package ExtractFeatures;

import java.io.File;
import java.io.IOException;

import Global.VariationInformation;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;

public class WekaOperations {
    
	//将特征从内存中写出成ARFF文件
	public static void WriteFeaturesToArff(String path,int flag){
		FastVector atts;  
		FastVector attVals; 
        Instances data;    
        double[] vals;   
        
        // 1. set up attributes  
        atts = new FastVector();   
        
        //代码特征   10
        atts.addElement(new Attribute("sourceLine")); 
        //四个heathed度量
        atts.addElement(new Attribute("uniOPERATORCount")); 
        atts.addElement(new Attribute("uniOperandCount")); 
        atts.addElement(new Attribute("totalOPERATORCount")); 
        atts.addElement(new Attribute("totalOperandCount"));        
        //函数信息
        atts.addElement(new Attribute("totalMethodInvocCount")); 
        atts.addElement(new Attribute("libraryMethodInvocCount")); 
        atts.addElement(new Attribute("localMethodInvocCount")); 
        atts.addElement(new Attribute("otherMethodInvocCount")); 
        atts.addElement(new Attribute("totalParameterCount")); 
        
        
        //上下文特征    9
        attVals = new FastVector();  
        attVals.addElement(1);
        attVals.addElement(0);
        atts.addElement(new Attribute("isLocalClone"));  
        atts.addElement(new Attribute("simFileName")); 
        atts.addElement(new Attribute("simMaskedFileName")); 
        atts.addElement(new Attribute("simMethodName")); 
        atts.addElement(new Attribute("simTotalParaName")); 
        atts.addElement(new Attribute("simMaxParaName")); 
        atts.addElement(new Attribute("simTotalParaType")); 
        atts.addElement(new Attribute("isSameBlockInfo"));
        atts.addElement(new Attribute("simCloneFragments")); 
        
       
        //结构特征   9
        atts.addElement(new Attribute("this_or_super")); 
        atts.addElement(new Attribute("assignment")); 
        atts.addElement(new Attribute("identifier")); 
        //atts.addElement(new Attribute("literal"));        
        atts.addElement(new Attribute("if_then_statement")); 
        atts.addElement(new Attribute("if_then_else_statement")); 
        atts.addElement(new Attribute("switch_statement")); 
        atts.addElement(new Attribute("while_statement")); 
        atts.addElement(new Attribute("do_statement"));
        atts.addElement(new Attribute("for_statement")); 
      
       /* if(flag == 1){
        	atts.addElement(new Attribute("CONSISTENTCHANGE"));
        }
        */
        if(flag == 2){ //第二种特征
        	atts.addElement(new Attribute("STATIC"));
        	atts.addElement(new Attribute("SAME"));
        	atts.addElement(new Attribute("ADD"));
        	atts.addElement(new Attribute("DELETE"));
        	atts.addElement(new Attribute("SPLIT"));
        	
        	atts.addElement(new Attribute("CONSISTENTCHANGE"));
        	atts.addElement(new Attribute("INCONSISTENTCHANGE"));
        	
        	atts.addElement(new Attribute("cloneAge"));
        	atts.addElement(new Attribute("fragmentCount"));
        	
        	atts.addElement(new Attribute("lastSTATIC"));
        	atts.addElement(new Attribute("lastSAME"));
        	atts.addElement(new Attribute("lastADD"));
        	atts.addElement(new Attribute("lastDELETE"));
        	atts.addElement(new Attribute("lastSPLIT"));
        	
        	atts.addElement(new Attribute("lastCONSISTENTCHANGE"));
        	atts.addElement(new Attribute("lastINCONSISTENTCHANGE"));
        	
        	
        	
        	//上一版本的代码属性变化
        	atts.addElement(new Attribute("last_sourceLine")); 
        	atts.addElement(new Attribute("last_fragmentCount"));
            atts.addElement(new Attribute("last_uniOPERATORCount")); 
            atts.addElement(new Attribute("last_uniOperandCount")); 
            atts.addElement(new Attribute("last_totalOPERATORCount")); 
            atts.addElement(new Attribute("last_totalOperandCount"));        
            atts.addElement(new Attribute("last_totalMethodInvocCount")); 
            atts.addElement(new Attribute("last_libraryMethodInvocCount")); 
            atts.addElement(new Attribute("last_localMethodInvocCount")); 
            atts.addElement(new Attribute("last_otherMethodInvocCount")); 
            atts.addElement(new Attribute("last_totalParameterCount")); 
            
            atts.addElement(new Attribute("last_this_or_super")); 
            atts.addElement(new Attribute("last_assignment")); 
            atts.addElement(new Attribute("last_identifier"));      
            atts.addElement(new Attribute("last_if_then_statement")); 
            atts.addElement(new Attribute("last_if_then_else_statement")); 
            atts.addElement(new Attribute("last_switch_statement")); 
            atts.addElement(new Attribute("last_while_statement")); 
            atts.addElement(new Attribute("last_do_statement"));
            atts.addElement(new Attribute("last_for_statement")); 
            
            
            
            //上一直到头间的版本代码属性变化
            atts.addElement(new Attribute("fromOrigin_sourceLineP")); 
        	atts.addElement(new Attribute("fromOrigin_fragmentCountP"));
            atts.addElement(new Attribute("fromOrigin_uniOPERATORCountP")); 
            atts.addElement(new Attribute("fromOrigin_uniOperandCountP")); 
            atts.addElement(new Attribute("fromOrigin_totalOPERATORCountP")); 
            atts.addElement(new Attribute("fromOrigin_totalOperandCountP"));        
            atts.addElement(new Attribute("fromOrigin_totalMethodInvocCountP")); 
            atts.addElement(new Attribute("fromOrigin_libraryMethodInvocCountP")); 
            atts.addElement(new Attribute("fromOrigin_localMethodInvocCountP")); 
            atts.addElement(new Attribute("fromOrigin_otherMethodInvocCountP")); 
            atts.addElement(new Attribute("fromOrigin_totalParameterCountP")); 

            atts.addElement(new Attribute("fromOrigin_this_or_superP")); 
            atts.addElement(new Attribute("fromOrigin_assignmentP")); 
            atts.addElement(new Attribute("fromOrigin_identifierP"));      
            atts.addElement(new Attribute("fromOrigin_if_then_statementP")); 
            atts.addElement(new Attribute("fromOrigin_if_then_else_statementP")); 
            atts.addElement(new Attribute("fromOrigin_switch_statementP")); 
            atts.addElement(new Attribute("fromOrigin_while_statementP")); 
            atts.addElement(new Attribute("fromOrigin_do_statementP"));
            atts.addElement(new Attribute("fromOrigin_for_statementP")); 

            atts.addElement(new Attribute("fromOrigin_sourceLineN")); 
        	atts.addElement(new Attribute("fromOrigin_fragmentCountN"));
            atts.addElement(new Attribute("fromOrigin_uniOPERATORCountN")); 
            atts.addElement(new Attribute("fromOrigin_uniOperandCountN")); 
            atts.addElement(new Attribute("fromOrigin_totalOPERATORCountN")); 
            atts.addElement(new Attribute("fromOrigin_totalOperandCountN"));        
            atts.addElement(new Attribute("fromOrigin_totalMethodInvocCountN")); 
            atts.addElement(new Attribute("fromOrigin_libraryMethodInvocCountN")); 
            atts.addElement(new Attribute("fromOrigin_localMethodInvocCountN")); 
            atts.addElement(new Attribute("fromOrigin_otherMethodInvocCountN")); 
            atts.addElement(new Attribute("fromOrigin_totalParameterCountN")); 

            atts.addElement(new Attribute("fromOrigin_this_or_superN")); 
            atts.addElement(new Attribute("fromOrigin_assignmentN")); 
            atts.addElement(new Attribute("fromOrigin_identifierN"));      
            atts.addElement(new Attribute("fromOrigin_if_then_statementN")); 
            atts.addElement(new Attribute("fromOrigin_if_then_else_statementN")); 
            atts.addElement(new Attribute("fromOrigin_switch_statementN")); 
            atts.addElement(new Attribute("fromOrigin_while_statementN")); 
            atts.addElement(new Attribute("fromOrigin_do_statementN"));
            atts.addElement(new Attribute("fromOrigin_for_statementN")); 
        }
        
        //一致性维护label
        atts.addElement(new Attribute("consistency")); 
        

        // 2. create Instances object  
        data = new Instances("FeatureVectors", atts, 0);  
        // 3. fill with data  
		for(FeatureVector vec : VariationInformation.featureVectorList){
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
			if(vec.isLocalClone()) vals[10] = 1;
			else vals[10] = 0;
			vals[11] = vec.getSimFileName();
			vals[12] = vec.getSimMaskedFileName();
			vals[13] = vec.getSimMethodName();
			vals[14] = vec.getSimTotalParaName();
			vals[15] = vec.getSimMaxParaName();
			vals[16] = vec.getSimTotalParaType();
			if(vec.getIsSameBlockInfo()) vals[17] = 1;
			else vals[17] = 0;
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
			/*if(flag == 1){
				vals[28] = vec.getConsisCount();
	        }*/
			if(flag==2){
				vals[28] = vec.getEvoPattern()[0];
				vals[29] = vec.getEvoPattern()[1];
				vals[30] = vec.getEvoPattern()[2];
				vals[31] = vec.getEvoPattern()[3];
				vals[32] = vec.getEvoPattern()[4];	
				vals[33] = vec.getEvoPattern()[5];
				vals[34] = vec.getEvoPattern()[6];	
				vals[35] = vec.getAge();	
				vals[36] = vec.getFragCount();

				vals[37] = vec.getlastevoPattern()[0];
				vals[38] = vec.getlastevoPattern()[1];
				vals[39] = vec.getlastevoPattern()[2];
				vals[40] = vec.getlastevoPattern()[3];
				vals[41] = vec.getlastevoPattern()[4];	
				vals[42] = vec.getlastevoPattern()[5];
				vals[43] = vec.getlastevoPattern()[6];	
				
				
				//////////////////////////
				vals[44] = vec.getLast_souceLines();
				vals[45] = vec.getLast_fragCount();
				vals[46] = vec.getLast_uniOPERATORCount();
				vals[47] = vec.getLast_uniOperandCount();
				vals[48] = vec.getLast_totalOPERATORCount();
				vals[49] = vec.getLast_totalOperandCount();
				vals[50] = vec.getLast_totalMethodInvocCount();
				vals[51] = vec.getLast_libraryMethodInvocCount();
				vals[52] = vec.getLast_localMethodInvocCount();
				vals[53] = vec.getLast_otherMethodInvocCount();
				vals[54] = vec.getLast_totalParameterCount();
						
				vals[55] = vec.getStructuralFeatureChanges_neighbor()[0];
				vals[56] = vec.getStructuralFeatureChanges_neighbor()[1];
				vals[57] = vec.getStructuralFeatureChanges_neighbor()[2];
				vals[58] = vec.getStructuralFeatureChanges_neighbor()[3];
				vals[59] = vec.getStructuralFeatureChanges_neighbor()[4];
				vals[60] = vec.getStructuralFeatureChanges_neighbor()[5];
				vals[61] = vec.getStructuralFeatureChanges_neighbor()[6];
				vals[62] = vec.getStructuralFeatureChanges_neighbor()[7];
				vals[63] = vec.getStructuralFeatureChanges_neighbor()[8];
				
				
				vals[64] = vec.getFromOrigin_souceLinesP();
				vals[65] = vec.getFromOrigin_fragCountP();
				vals[66] = vec.getFromOrigin_uniOPERATORCountP();
				vals[67] = vec.getFromOrigin_uniOperandCountP();
				vals[68] = vec.getFromOrigin_totalOPERATORCountP();
				vals[69] = vec.getFromOrigin_totalOperandCountP();
				vals[70] = vec.getFromOrigin_totalMethodInvocCountP();
				vals[71] = vec.getFromOrigin_libraryMethodInvocCountP();
				vals[72] = vec.getFromOrigin_localMethodInvocCountP();
				vals[73] = vec.getFromOrigin_otherMethodInvocCountP();
				vals[74] = vec.getFromOrigin_totalParameterCountP();
						
				vals[75] = vec.getStructuralFeatureChanges_fromOriginP()[0];
				vals[76] = vec.getStructuralFeatureChanges_fromOriginP()[1];
				vals[77] = vec.getStructuralFeatureChanges_fromOriginP()[2];
				vals[78] = vec.getStructuralFeatureChanges_fromOriginP()[3];
				vals[79] = vec.getStructuralFeatureChanges_fromOriginP()[4];
				vals[80] = vec.getStructuralFeatureChanges_fromOriginP()[5];
				vals[81] = vec.getStructuralFeatureChanges_fromOriginP()[6];
				vals[82] = vec.getStructuralFeatureChanges_fromOriginP()[7];
				vals[83] = vec.getStructuralFeatureChanges_fromOriginP()[8];
	           
				vals[84] = vec.getFromOrigin_souceLinesN();
				vals[85] = vec.getFromOrigin_fragCountN();
				vals[86] = vec.getFromOrigin_uniOPERATORCountN();
				vals[87] = vec.getFromOrigin_uniOperandCountN();
				vals[88] = vec.getFromOrigin_totalOPERATORCountN();
				vals[89] = vec.getFromOrigin_totalOperandCountN();
				vals[90] = vec.getFromOrigin_totalMethodInvocCountN();
				vals[91] = vec.getFromOrigin_libraryMethodInvocCountN();
				vals[92] = vec.getFromOrigin_localMethodInvocCountN();
				vals[93] = vec.getFromOrigin_otherMethodInvocCountN();
				vals[94] = vec.getFromOrigin_totalParameterCountN();
						
				vals[95] = vec.getStructuralFeatureChanges_fromOriginN()[0];
				vals[96] = vec.getStructuralFeatureChanges_fromOriginN()[1];
				vals[97] = vec.getStructuralFeatureChanges_fromOriginN()[2];
				vals[98] = vec.getStructuralFeatureChanges_fromOriginN()[3];
				vals[99] = vec.getStructuralFeatureChanges_fromOriginN()[4];
				vals[100] = vec.getStructuralFeatureChanges_fromOriginN()[5];
				vals[101] = vec.getStructuralFeatureChanges_fromOriginN()[6];
				vals[102] = vec.getStructuralFeatureChanges_fromOriginN()[7];
				vals[103] = vec.getStructuralFeatureChanges_fromOriginN()[8];
			}
			
			vals[vals.length-1] = vec.getConsistence();
			
	        data.add(new Instance(1.0, vals));
		}
		
		// 4. output data  
        System.out.println(data); 
        
        // 5. save arff  
        ArffSaver saver = new ArffSaver();  
        saver.setInstances(data);  
        try {
			saver.setFile(new File(path));
			saver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}  
	}
	
	
	/*
     * 从.arff文件中获取样本Instances;
     * 1.fileName instances的文件名
     */
    public static Instances getInstances(String fileName) throws Exception{
       File file= new File(fileName);
       return getInstances(file);
    }
 
    
    /*
     * 从.arff文件中获取样本Instances;
     * 1.file 获得instances的File对象
     */
    public static Instances getInstances(File file) throws Exception{
       Instances inst = null;
       try{
           ArffLoader loader = new ArffLoader();
           loader.setFile(file);
           inst = loader.getDataSet();
       }
       catch(Exception e){
           throw new Exception(e.getMessage());
       }
       return inst;
    }
 
    
}
