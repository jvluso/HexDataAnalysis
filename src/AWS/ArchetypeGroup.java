package AWS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
	

	public String matchupTable(int size){
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		
        for(int i=0;i<size; i++){
        	for(int j=0;j<i;j++){
        		Matchup m=getMatchup(i, j);
        		ObjectNode node = arrayNode.addObject();
        		node.put("AName", getChamp(i).getName());
        		node.put("BName", getChamp(j).getName());
        		node.put("ARank", i);
        		node.put("BRank", j);
        		node.put("AWins", m.getWins());
        		node.put("BWins", m.getLosses());
        		System.out.print(getChamp(i).getName());
        		System.out.print(" vs. ");
        		System.out.println(getChamp(j).getName());

        		System.out.print(m.getWins());
        		System.out.print(" to ");
        		System.out.println(m.getLosses());
        		
        	}
        }
        
       
        
        try {
			return mapper.writeValueAsString(arrayNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "{}";
	}
}
