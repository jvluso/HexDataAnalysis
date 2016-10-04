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
	private ItemCollection<ScanOutcome> archetypeResult;
	private ItemCollection<ScanOutcome> matchResult;

	public ArchetypeGroup(ItemCollection<ScanOutcome> archetypes, ItemCollection<ScanOutcome> matches){
		
		Map<String,Archetype> champs = new HashMap<String,Archetype>();

		archetypeResult = archetypes;
		matchResult = matches;
		
		
        for(Item o:archetypeResult){
        	String champ=o.getString("Champion");
        	if(champs.get(champ)==null){
        		Archetype a=new Archetype(champ);
        		System.out.println(o.toJSONPretty());
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
        		topChamps.get(5).addArchetype(champs.get(o));
        	}
        }
	}
	

	
	public Matchup getMatchup(int a, int b){
		return new Matchup(topChamps.get(a), topChamps.get(b), matchResult);
	}
	
	public Archetype getChamp(int i){
		return topChamps.get(i);
	}
}
