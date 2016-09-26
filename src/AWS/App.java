package AWS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class App {
	
	static String[] Sets = {"ShardsOfFate.json",
			                "ShatteredDestiny.json",
			                "ArmiesOfMyth.json",
			                "PrimalDawn.json",
			                "Herofall.json"};
	
	
    	

    public static void main(String[] args) throws Exception {

    	/*

    	Hashtable<String,JsonNode> cardHash = new Hashtable<String,JsonNode>(5*1024);
    	
    	for(String s:Sets){

			JsonParser parser = new JsonFactory()
			    .createParser(new File("/home/jeremy/hexSets/" + s));
			JsonNode rootNode = new ObjectMapper().readTree(parser);

	        JsonNode cards = rootNode.path("cards");
	        for(int i=0;!cards.path(i).isMissingNode();i++){
	        	cardHash.put(cards.path(i).path("uuid").toString(), cards.path(i));
	        }
	        parser.close();
		        
    	}
        */
        //new MatchStream(new numFilteredStream(new FileInputStream(new File("/home/jeremy/API.log")))).uploadStream();
    
    	

        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        //Table matchTable = dynamoDB.getTable("Matches");
        //Table deckTable = dynamoDB.getTable("Decklists");
        Table archetypeTable = dynamoDB.getTable("Archetypes");
        ItemCollection<ScanOutcome> result;
        
       
    	
    	/*for(JsonNode c: CardList.getInstance().getChampions()){
    		result= deckTable.scan("Champion = :c",
            		"listAttr, #m",
            		new NameMap().with("#m", "Match"),
            		new ValueMap().with(":c", c.path("uuid").asText())
            		);*/

		result= archetypeTable.scan("Version = :v",
        		null,
        		new ValueMap().with(":v", 0)
        		);
		
		ArchetypeGroup topGroup = new ArchetypeGroup(result);
		
		
		System.out.println(topGroup.getTopChamps().get(1).getMatches().get(1));
		
		/*
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
        
        
        
        
        List<Archetype> topChamps = new ArrayList<Archetype>(5);
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
    	for(int i=0;i<5;i++){
    		System.out.println(topChamps.get(i).getName());
    		System.out.println(topChamps.get(i).getMatches().size());
    		System.out.println(topChamps.get(i).getMatches().get(0));
    	}
    	

		System.out.println("done");
		
		
        TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes("Matches");
		
		for(String s:topChamps.get(0).getMatches()){
			if(topChamps.get(1).getMatches().contains(s)){
				forumTableKeysAndAttributes.addHashOnlyPrimaryKey("TimePlayerKey",s);
			}
		}
		
		
		BatchGetItemOutcome outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes);
		
        List<Item> items = outcome.getTableItems().get("Matches");
        int wins=0;
        int losses=0;
        for (Item item : items) {
            if(topChamps.get(0).getDeckListHashes().contains(item.getInt("PlayerOneDeck"))){
            	if(item.getInt("PlayerOneWins")==2){
            		wins++;
            	}else{
            		losses++;
            	}
            }else{
            	if(item.getInt("PlayerOneWins")!=2){
            		wins++;
            	}else{
            		losses++;
            	}
            }
        }
        
        System.out.println(items.size());
        System.out.print("Wins: ");
        System.out.println(wins);
        System.out.print("Losses: ");
        System.out.println(losses);
        
        */
    }
    
    
}