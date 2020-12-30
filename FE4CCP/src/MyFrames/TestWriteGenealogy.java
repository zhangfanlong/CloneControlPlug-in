package MyFrames;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Global.VariationInformation;
import PreProcess.XML;
import CloneRepresentation.CloneGenealogy;
import CloneRepresentation.GenealogyEvolution;

public class TestWriteGenealogy {
	public static void printGenealogyInfo(String path){
		Document xml = XML.Create();
		Element Genealogy = xml.createElement("CloneGenealogy");
		xml.appendChild(Genealogy);
		
		for(int i=0;i<VariationInformation.clonesInVersion.size();i++){
			for(CloneGenealogy cloneGene : VariationInformation.cloneGenealogy){
				if(cloneGene.getStartVersion() == i){
					Element gene = xml.createElement("GenealogyInfo");
					gene.setAttribute("age", String.valueOf(cloneGene.getAge()));
					gene.setAttribute("startVersion", String.valueOf(cloneGene.getStartVersion()));
					gene.setAttribute("endVersion", String.valueOf(cloneGene.getEndVersion()));
					Element evolution = xml.createElement("EvolutionPatternCount");
					evolution.setAttribute("Static", String.valueOf(cloneGene.getEvoPatternCount()[0]));
					evolution.setAttribute("Same", String.valueOf(cloneGene.getEvoPatternCount()[1]));
					evolution.setAttribute("Add", String.valueOf(cloneGene.getEvoPatternCount()[2]));
					evolution.setAttribute("Delete", String.valueOf(cloneGene.getEvoPatternCount()[3]));
					evolution.setAttribute("ConsistentChange", String.valueOf(cloneGene.getEvoPatternCount()[4]));
					evolution.setAttribute("InConsistentChange", String.valueOf(cloneGene.getEvoPatternCount()[5]));
					evolution.setAttribute("Split", String.valueOf(cloneGene.getEvoPatternCount()[6]));
					gene.appendChild(evolution);
				
					for(GenealogyEvolution evo :cloneGene.getEvolutionList()){
						Element evoEle = xml.createElement("Evolution");
						evoEle.setAttribute("id", String.valueOf(evo.getID()));
						evoEle.setAttribute("parentID", String.valueOf(evo.getParentID()));
						evoEle.setAttribute("chileID", String.valueOf(evo.getChildID()));

						Element srcInfo = xml.createElement("srcInfo");
						srcInfo.setAttribute("srcVersion", String.valueOf(evo.getSrcVersion()));
						srcInfo.setAttribute("cgid", String.valueOf(evo.getSrcCGID()));
						srcInfo.setAttribute("size", String.valueOf(evo.getSrcSize()));
						Element destInfo = xml.createElement("destInfo");
						destInfo.setAttribute("destVersion", String.valueOf(evo.getDestVersion()));
						destInfo.setAttribute("cgid", String.valueOf(evo.getDestCGID()));
						destInfo.setAttribute("size", String.valueOf(evo.getDestSize()));
						
						Element pattern = xml.createElement("CGMappingInfp");
						pattern.setAttribute("pattern", evo.getCgPattern());
						evoEle.appendChild(srcInfo);
						evoEle.appendChild(destInfo);
						evoEle.appendChild(pattern);
						gene.appendChild(evoEle);
					}
					
					Genealogy.appendChild(gene);
				}
			}
		}
		if(VariationInformation.singleCgGenealogyList.size()>0){
			for(CloneGenealogy singleGnen : VariationInformation.singleCgGenealogyList){
				Element single = xml.createElement("SingleGenealogy");
				single.setAttribute("startVersion", String.valueOf(singleGnen.getStartVersion()));
				single.setAttribute("cgid", String.valueOf(singleGnen.getRootCGid()));
				
				Genealogy.appendChild(single);
			}
		}
		
		
		XML.Save(xml, path);
		
	}
}
