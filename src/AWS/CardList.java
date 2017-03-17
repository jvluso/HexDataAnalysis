package AWS;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CardList {
	private static CardList instance = null;
	   protected CardList() {
	      // Exists only to defeat instantiation.
	   }
	   public static CardList getInstance() {
	      if(instance == null) {
	         instance = new CardList();
	      }
	      return instance;
	   }
	   
	   
		static String[] Sets = {"ShardsOfFate.json",
            "ShatteredDestiny.json",
            "ArmiesOfMyth.json",
            "PrimalDawn.json",
            "Herofall.json",
            "ScarsOfWar.json"};


	   static String[] SetNames = {"Shards of Fate",
                                   "Shattered Destiny",
                                   "Armies of Myth",
                                   "Primal Dawn",
               					   "Herofall",
               					   "Scars of War"};
		
	   

	   private Hashtable<String,JsonNode> cardNameHash;
	   private Hashtable<String,JsonNode> cardIdHash;
	   public Hashtable<String,JsonNode> getCardIdHash() throws JsonParseException, IOException{

		   
		   if(cardIdHash != null){
			   return cardIdHash;
		   }
		   
		   if(!new File("src/AWS/hexSets/ShardsOfFate.json").exists()){
			   setNameHashesFromDynamoDB();
			   return cardIdHash;
		   }
		   
		   cardIdHash = new Hashtable<String,JsonNode>(5*1024);
		   cardNameHash = new Hashtable<String,JsonNode>(5*1024);

		   for(String s:Sets){

			   JsonParser parser = new JsonFactory()
			   .createParser(new File("src/AWS/hexSets/" + s));
			   
			   JsonNode rootNode = new ObjectMapper().readTree(parser);

			   JsonNode cards = rootNode.path("cards");
			   for(int i=0;!cards.path(i).isMissingNode();i++){
				   cardIdHash.put(cards.path(i).path("uuid").textValue(), cards.path(i));
				   cardNameHash.put(cards.path(i).path("name").textValue(), cards.path(i));
			   }
			   parser.close();
			        
		   }
	        
		   return cardIdHash;
	   }
	   
	   public Hashtable<String,JsonNode> getCardNameHash() throws JsonParseException, IOException{

		   
		   if(cardNameHash != null){
			   return cardNameHash;
		   }

		   if(!new File("src/AWS/hexSets/ShardsOfFate.json").exists()){
			   setNameHashesFromDynamoDB();
			   return cardIdHash;
		   }
		   
		   cardIdHash = new Hashtable<String,JsonNode>(5*1024);
		   cardNameHash = new Hashtable<String,JsonNode>(5*1024);

		   for(String s:Sets){

			   JsonParser parser = new JsonFactory()
			   .createParser(new File("src/AWS/hexSets/" + s));
			   JsonNode rootNode = new ObjectMapper().readTree(parser);

			   JsonNode cards = rootNode.path("cards");
			   for(int i=0;!cards.path(i).isMissingNode();i++){
				   cardIdHash.put(cards.path(i).path("uuid").textValue(), cards.path(i));
				   cardNameHash.put(cards.path(i).path("name").textValue(), cards.path(i));
			   }
			   parser.close();
			        
		   }
	        
		   return cardNameHash;
	   }
	   
	   
	   private void setNameHashesFromDynamoDB() throws JsonParseException, IOException{

		   cardIdHash = new Hashtable<String,JsonNode>(5*1024);
		   cardNameHash = new Hashtable<String,JsonNode>(5*1024);

	       AmazonDynamoDBClient client;
	       DynamoDB dynamoDB;
	       Table cardTable;
	       client = new AmazonDynamoDBClient();
	       client.withRegion(Regions.US_WEST_1);

	       dynamoDB = new DynamoDB(client);

	       cardTable = dynamoDB.getTable("Cards");
	       

	        ItemCollection<ScanOutcome> cardItems = cardTable.scan();
		   
	        
	        for(Item i : cardItems){

			   JsonParser parser = new JsonFactory()
			   .createParser(i.toJSON());
			   JsonNode rootNode = new ObjectMapper().readTree(parser);

			   cardIdHash.put(rootNode.path("uuid").textValue(), rootNode);
			   cardNameHash.put(rootNode.path("name").textValue(), rootNode);
	        }
		   
	   }

	   private List<JsonNode> champions;
	   public List<JsonNode> getChampions() throws JsonParseException, IOException{

		   
		   if(champions != null){
			   return champions;
		   }
		   List<JsonNode> championsBuilder = new LinkedList<JsonNode>();
		   
		   
		   for(JsonNode c: getCardIdHash().values()){
			   if(c.path("rarity").asText().equals("Champion")){
				   championsBuilder.add(c);
			   }
		   }
		   
		   

		   champions=championsBuilder;
		   return champions;
		   
	   }
	   
}
