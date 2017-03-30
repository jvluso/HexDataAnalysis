package AWS;

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
}
