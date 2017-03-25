package AWS;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MatchStream {

    AmazonDynamoDBClient client;
    DynamoDB dynamoDB;
	Table matchTable;
	Table deckTable;
	Table archetypeTable;
	Table immortalArchetypeTable;
	public MatchStream() throws JsonParseException, IOException {
		

        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        dynamoDB = new DynamoDB(client);

        matchTable = dynamoDB.getTable("Matches");
        deckTable = dynamoDB.getTable("Decklists");
        archetypeTable = dynamoDB.getTable("Archetype");
        immortalArchetypeTable = dynamoDB.getTable("ImmortalArchetype");
		
		
		

	}
	
	private void uploadNode(JsonNode rootNode) throws JsonParseException, IOException{

		uploadNodeOptions(rootNode, false, false);
	}
		
	private void uploadNodeOptions(JsonNode rootNode, boolean archetypeOnly, boolean immortalOnly) throws JsonParseException, IOException{
			

    	if(rootNode.has("TournamentType") && 
    			((rootNode.path("TournamentType").asText().equals("Ladder") && !immortalOnly) ||
    			 rootNode.path("TournamentType").asText().equals("Immortal"))){
	        JsonNode game = rootNode.path("Games").path(0).path("Matches").path(0);

	    	System.out.print("TournamentTime: ");
	    	System.out.println(rootNode.path("TournamentTime").asText());
	        String matchKey = rootNode.path("TournamentTime").asText()+game.path("PlayerOne").asText();
	
	        boolean immortal = rootNode.path("TournamentType").asText().equals("Immortal");
	        
	        
	        try {
		        matchTable.putItem(new Item()
		        .withPrimaryKey("TimePlayerKey", matchKey)
		        .withString("TournamentTime", rootNode.path("TournamentTime").asText())
		        .withString("PlayerOne", game.path("PlayerOne").asText())
		        .withString("PlayerTwo", game.path("PlayerTwo").asText())
		        .withString("PlayerOneWins", game.path("PlayerOneWins").asText())
		        .withString("PlayerTwoWins", game.path("PlayerTwoWins").asText())
		        .withNumber("PlayerOneDeck", game.path("PlayerOneDeck").hashCode())
		        .withNumber("PlayerTwoDeck", game.path("PlayerTwoDeck").hashCode())
		        .withBoolean("Immortal", immortal)
		        ,"attribute_not_exists(TournamentTime)", null, null);
	        } catch (ConditionalCheckFailedException e){}
	        Item p1 = new Item()
	        	.withPrimaryKey("HashCode", game.path("PlayerOneDeck").hashCode())
	        	.withString("Champion", game.path("PlayerOneDeck").path("Champion").asText())
	        	.withJSON("Deck", game.path("PlayerOneDeck").path("Deck").toString())
	        	.withJSON("Sideboard", game.path("PlayerOneDeck").path("Sideboard").toString());
	        Item p2 = new Item()
	        	.withPrimaryKey("HashCode", game.path("PlayerTwoDeck").hashCode())
	        	.withString("Champion", game.path("PlayerTwoDeck").path("Champion").asText())
	        	.withJSON("Deck", game.path("PlayerTwoDeck").path("Deck").toString())
	        	.withJSON("Sideboard", game.path("PlayerTwoDeck").path("Sideboard").toString());
	        
	        if(!archetypeOnly){
		        try {
			        deckTable.putItem(p1,"attribute_not_exists(Deck)", null, null);
		        } catch (ConditionalCheckFailedException e){}
		        try{
			        deckTable.putItem(p2,"attribute_not_exists(Deck)", null, null);
		        } catch (ConditionalCheckFailedException e){}
	
	        }
	        
			Map<String,String> expressionAttributeNames = new HashMap<String,String>();
			
			Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
			

	        if(!archetypeOnly){
				expressionAttributeNames.put("#p", "Match");
				expressionAttributeValues.put(":val",
		        new HashSet<String>(Arrays.asList(matchKey)));
	        }
			
			deckTable.updateItem("HashCode", game.path("PlayerOneDeck").hashCode(),
	        "ADD #p :val",
	        expressionAttributeNames,
	        expressionAttributeValues);
	        deckTable.updateItem("HashCode", game.path("PlayerTwoDeck").hashCode(),
	        "ADD #p :val",
	        expressionAttributeNames,
	        expressionAttributeValues);

			if(rootNode.path("TournamentType").asText().equals("Ladder")){
				ArchetypeStream.addItem(p1.withStringSet("Match", matchKey), archetypeTable);
				ArchetypeStream.addItem(p2.withStringSet("Match", matchKey), archetypeTable);
			}else{
				ArchetypeStream.addItem(p1.withStringSet("Match", matchKey), immortalArchetypeTable);
				ArchetypeStream.addItem(p2.withStringSet("Match", matchKey), immortalArchetypeTable);
			}
	        
	        System.out.println("PutItem succeeded: " + rootNode.path("TournamentTime").toString());


    	}
		
	}

	private void uploadArchetype(JsonNode rootNode) throws JsonParseException, IOException{
		

		uploadNodeOptions(rootNode, true, false);
		
	}

	public void uploadHexTournamentData(InputStream stream) throws JsonParseException, IOException{


        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);

        
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        if(rootNode.has("TournamentId")){
            uploadNode(rootNode);
        }else{
	        for(JsonNode node: rootNode){
	            uploadNode(node);
	        	
	        }
        }
        parser.close();
	}
	
	
	
	public void uploadDataOptions(InputStream stream, boolean archetypeOnly, boolean immortalOnly) throws JsonParseException, IOException{

        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);

        
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        if(rootNode.has("TournamentId")){
            uploadNodeOptions(rootNode, archetypeOnly, immortalOnly);
        }else{
	        for(JsonNode node: rootNode){
	            uploadNodeOptions(node, archetypeOnly, immortalOnly);
	        	
	        }
        }
        parser.close();
	}
        

	
	
	public void uploadJSON(JsonNode node) throws JsonParseException, IOException{

    	System.out.println(node);
    	uploadNode(node);

	}
}
