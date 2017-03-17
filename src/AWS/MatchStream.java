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
	public MatchStream() throws JsonParseException, IOException {
		

        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        dynamoDB = new DynamoDB(client);

        matchTable = dynamoDB.getTable("Matches");
        deckTable = dynamoDB.getTable("Decklists");
        archetypeTable = dynamoDB.getTable("Archetype");
		
		
		

	}
	
	private void uploadNode(JsonNode rootNode, Table matchTable, Table deckTable, Table archetypeTable) throws JsonParseException, IOException{
		

    	System.out.print("TournamentTime: ");
    	System.out.println(rootNode.path("TournamentTime").asText());
    	if(rootNode.has("TournamentType") && rootNode.path("TournamentType").asText().equals("Ladder")){
	        JsonNode game = rootNode.path("Games").path(0).path("Matches").path(0);
	        
	        String matchKey = rootNode.path("TournamentTime").asText()+game.path("PlayerOne").asText();
	
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
	        
	        
	        try {
		        deckTable.putItem(p1,"attribute_not_exists(Deck)", null, null);
	        } catch (ConditionalCheckFailedException e){}
	        try{
		        deckTable.putItem(p2,"attribute_not_exists(Deck)", null, null);
	        } catch (ConditionalCheckFailedException e){}
	
			Map<String,String> expressionAttributeNames = new HashMap<String,String>();
			expressionAttributeNames.put("#p", "Match");
			
			Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
			expressionAttributeValues.put(":val",
	        new HashSet<String>(Arrays.asList(matchKey)));
	        
			
			deckTable.updateItem("HashCode", game.path("PlayerOneDeck").hashCode(),
	        "ADD #p :val",
	        expressionAttributeNames,
	        expressionAttributeValues);
	        deckTable.updateItem("HashCode", game.path("PlayerTwoDeck").hashCode(),
	        "ADD #p :val",
	        expressionAttributeNames,
	        expressionAttributeValues);
	        
	        ArchetypeStream.addItem(p1.withStringSet("Match", matchKey), archetypeTable);
	        ArchetypeStream.addItem(p2.withStringSet("Match", matchKey), archetypeTable);
	        
	        
	        System.out.println("PutItem succeeded: " + rootNode.path("TournamentTime").toString());


    	}
		
	}

	private void uploadArchetype(JsonNode rootNode, Table matchTable, Table deckTable, Table archetypeTable) throws JsonParseException, IOException{
		

    	System.out.print("TournamentTime: ");
    	System.out.println(rootNode.path("TournamentTime").asText());
    	if(rootNode.has("TournamentType") && rootNode.path("TournamentType").asText().equals("Ladder")){
	        JsonNode game = rootNode.path("Games").path(0).path("Matches").path(0);
	        
	        String matchKey = rootNode.path("TournamentTime").asText()+game.path("PlayerOne").asText();
	
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
	        
	        
	
			Map<String,String> expressionAttributeNames = new HashMap<String,String>();
			expressionAttributeNames.put("#p", "Match");
			
			Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
			expressionAttributeValues.put(":val",
	        new HashSet<String>(Arrays.asList(matchKey)));
	        
	        ArchetypeStream.addItem(p1.withStringSet("Match", matchKey), archetypeTable);
	        ArchetypeStream.addItem(p2.withStringSet("Match", matchKey), archetypeTable);
	        
	        
	        System.out.println("PutItem succeeded: " + rootNode.path("TournamentTime").toString());


    	}
		
	}

	public void uploadHexTournamentData(InputStream stream) throws JsonParseException, IOException{


        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);

        
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        if(rootNode.has("TournamentId")){
            uploadNode(rootNode, matchTable, deckTable, archetypeTable);
        }else{
	        for(JsonNode node: rootNode){
	            uploadNode(node, matchTable, deckTable, archetypeTable);
	        	
	        }
        }
        parser.close();
	}
	
	
	
	public void uploadArchetypeUpdate(InputStream stream) throws JsonParseException, IOException{

        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);

        
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        if(rootNode.has("TournamentId")){
            uploadArchetype(rootNode, matchTable, deckTable, archetypeTable);
        }else{
	        for(JsonNode node: rootNode){
	            uploadArchetype(node, matchTable, deckTable, archetypeTable);
	        	
	        }
        }
        parser.close();
	}
        

	
	
	public void uploadJSON(JsonNode node) throws JsonParseException, IOException{

    	System.out.println(node);
    	uploadNode(node, matchTable, deckTable, archetypeTable);

	}
}
