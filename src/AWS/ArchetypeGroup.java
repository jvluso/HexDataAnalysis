package AWS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ArchetypeGroup {
	
	private List<Archetype> topChamps;
	private ItemCollection<ScanOutcome> archetypeResult;
	private Table matchTable;

	public ArchetypeGroup(ItemCollection<ScanOutcome> archetypes, Table match){
		
		Map<String,Archetype> champs = new HashMap<String,Archetype>();

		archetypeResult = archetypes;
		matchTable = match;
		
		
        for(Item o:archetypeResult){
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
        

        Archetype[] champList = champs.values().toArray(new Archetype[0]);
        Arrays.sort(champList);
        topChamps = Arrays.asList(champList);
        
	}
	

	
	public Matchup getMatchup(int a, int b){
		return new Matchup(topChamps.get(a), topChamps.get(b), matchTable);
	}
	
	public Archetype getChamp(int i){
		return topChamps.get(i);
	}
}
