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

	InputStream stream;
	public MatchStream(InputStream s) {
		stream=s;
	}
	
	private void uploadNode(JsonNode rootNode, Table matchTable, Table deckTable, Table archetypeTable) throws JsonParseException, IOException{
		

    	System.out.println(rootNode.path(0).path("TournamentTime").asText());
    	if(rootNode.path("TournamentId").asInt()==0){
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
	        
	        
	        System.out.println("PutItem succeeded: " + rootNode.path("TournamentTime").toString());


    	}
		
	}
	

	public void uploadHexTournamentData() throws JsonParseException, IOException{

        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table matchTable = dynamoDB.getTable("Matches");
        Table deckTable = dynamoDB.getTable("Decklists");
        Table archetypeTable = dynamoDB.getTable("Archetype");
        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);

        
        JsonNode rootNode = new ObjectMapper().readTree(parser);
        for(JsonNode node: rootNode){
            uploadNode(node, matchTable, deckTable, archetypeTable);
        	
        }
        
	}
	
	
	
	public void uploadStream() throws JsonParseException, IOException{

        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table matchTable = dynamoDB.getTable("Matches");
        Table deckTable = dynamoDB.getTable("Decklists");
        Table archetypeTable = dynamoDB.getTable("Archetype");
        JsonParser parser;
		parser = new JsonFactory()
		    .createParser(stream);
		
		
		

        
        for(JsonNode rootNode = new ObjectMapper().readTree(parser);
        		rootNode!=null;
        		rootNode = new ObjectMapper().readTree(parser)){
        
        	uploadNode(rootNode, matchTable, deckTable, archetypeTable);
        }
	        
	        

	}
        
}
