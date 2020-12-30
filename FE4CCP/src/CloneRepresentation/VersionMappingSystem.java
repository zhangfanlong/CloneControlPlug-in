package CloneRepresentation;

import java.util.HashMap;
//import java.util.TreeMap;


/**
 * @author ZFL
 *实现版本和版本号的对应
 *1.版本号与系统版本号的对应（0，1，2，3对应系统的版本号）
 *2.版本号查询
 *3.系统版本号查询
 *
 */

public class VersionMappingSystem {
	
	//构建数据结构
	HashMap<Integer, String> NummapVersion; //数据结构:版本数字--系统版本号
	//Integer StartVersion;
	//Integer EndVersion;
	Integer VersionNumer;
	
	//初始化
	public VersionMappingSystem() {
		// TODO Auto-generated constructor stub
		 NummapVersion = new HashMap<>();//创建对象
		 //添加元素
		 NummapVersion.put(0, "V1");	 
		 
	}
	
	//查询系统版本号
		String LookupSystemVersion(Integer Version){
			
			return NummapVersion.get(Version);
		}	
	
	//查询版本号
	//Integer lookupVersion(String SystemVersion){
		
	//	return NummapVersion.get(SystemVersion);
	//}
	
		//总版本号
		public void setVersionNumer() {
			VersionNumer = NummapVersion.size();
		}
	
		public Integer getVersionNumer() {
			this.setVersionNumer();
			return VersionNumer;
		}
}
