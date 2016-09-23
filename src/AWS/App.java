package AWS;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    	
    	for(JsonNode c: CardList.getInstance().getChampions()){
    		result= archetypeTable.scan("Champion = :c",
            		"listAttr, #m",
            		new NameMap().with("#m", "Match"),
            		new ValueMap().with(":c", c.path("uuid").asText())
            		);
            System.out.println(c.path("name").asText());
            int i=0;
            for(Item o:result){
            	i+=o.getList("Match").size();
            }
            System.out.println(i);
            
    	}
            
    	
    }
    
    
}