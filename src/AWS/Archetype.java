package AWS;

import java.util.List;

public class Archetype {

	private String name;
	public Archetype(String n){
		name=n;
	}
	

	public String getName(){
		return name;
	}
	
	
	public boolean equals(Archetype o){
		return name.equals(o.getName());
	}
	
	public ArchetypeData findData(List<ArchetypeData> l) throws Exception{
		for(ArchetypeData a : l){
			if(this.equals(a.getArchetype())){
				return a;
			}
		}
		throw new Exception("Archetype not found");
	}
}
