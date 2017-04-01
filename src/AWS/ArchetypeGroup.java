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
	private Map<Matchup,WinRate> matchups;

	public ArchetypeGroup(ItemCollection<ScanOutcome> archetypeItems, Table match){
		
		init(archetypeItems);
	}
	
	private void addCollection(ItemCollection<ScanOutcome> archetypeItems){

		Map<String,ArchetypeData> champs = new HashMap<String,ArchetypeData>();

        for(Item o:archetypeItems){
        	String champ=o.getString("Name");
        	if(champs.get(champ)==null){
        		ArchetypeData a=new ArchetypeData(champ);
        		a.addEntry(o);
        		champs.put(champ,a);
        	}else{
        		ArchetypeData a=champs.get(champ);
        		a.addEntry(o);
        		champs.put(champ,a);
        	}
        }
        

        ArchetypeData[] champList = champs.values().toArray(new ArchetypeData[0]);
        Arrays.sort(champList);
        archetypes = new ArrayList<Archetype>();
        for(ArchetypeData a : champList){
        	archetypes.add(a.getArchetype());
        }
        
	}

	
	public Matchup getMatchup(int a, int b){
		return new Matchup(archetypes.get(a), archetypes.get(b));
	}
	
	public ArchetypeData getChamp(int i){
		return archetypes.get(i);
	}
	

	public ArrayNode matchupTable(int size){
		
		ObjectMapper mapper = new ObjectMapper();
		ArrayNode arrayNode = mapper.createArrayNode();
		
        for(int i=0;i<size; i++){
        	for(int j=0;j<i;j++){
        		Matchup m=getMatchup(i, j);
        		ObjectNode node = arrayNode.addObject();
        		node.put("AName", m.geta().getName());
        		node.put("BName", m.getb().getName());
        		node.put("ARank", archetypes.indexOf(m.geta()));
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
        
       
		return arrayNode;
	}
}
