package AWS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

public class ArchetypeGroup {
	
	private List<Archetype> topChamps;

	public ArchetypeGroup(ItemCollection<ScanOutcome> result){
		
		Map<String,Archetype> champs = new HashMap<String,Archetype>();
    	
        for(Item o:result){
        	String champ=o.getString("Champion");
        	if(champs.get(champ)==null){
        		Archetype a=new Archetype(champ);
        		a.addEntry(o);
        		champs.put(champ,a);
        	}else{
        		Archetype a=champs.get(champ);
        		a.addEntry(o);
        		champs.put(champ,a);
        	}
        }
        

        
        topChamps = new ArrayList<Archetype>(6);
    	for(String o:champs.keySet()){
        	if(champs.get(o)!=null){
        		if(topChamps.size() < 5){
    				topChamps.add(champs.get(o));
        		}else{
	        		for(int i=0;i<5;i++){
	        			if(champs.get(o).getMatches().size() > topChamps.get(i).getMatches().size()){
	        				topChamps.set(i, champs.get(o));
	        				break;
	        			}
	        		}
        		}
        	}
    	}
        

		topChamps.add(new Archetype("other"));
		

    	for(String o:champs.keySet()){
        	if(!topChamps.contains(o)){
        		topChamps.get(6).addArchetype(champs.get(o));
        	}
        }
	}
	
	
	
	public List<Archetype> getTopChamps(){
		return topChamps;
	}
}
