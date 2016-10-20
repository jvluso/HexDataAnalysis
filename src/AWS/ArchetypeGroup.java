package AWS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ArchetypeGroup {
	
	private List<Archetype> archetypes;
	private ItemCollection<ScanOutcome> archetypeResult;
	private Table matchTable;

	public ArchetypeGroup(ItemCollection<ScanOutcome> archetypeItems, Table match){
		
		archetypeResult = archetypeItems;
		matchTable = match;
		
		init();
	}
	
	private void init(){

		Map<String,Archetype> champs = new HashMap<String,Archetype>();

        for(Item o:archetypeResult){
        	String champ=o.getString("Name");
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
        archetypes = Arrays.asList(champList);
        
	}

	
	public Matchup getMatchup(int a, int b){
		return new Matchup(archetypes.get(a), archetypes.get(b), matchTable);
	}
	
	public Archetype getChamp(int i){
		return archetypes.get(i);
	}
}
