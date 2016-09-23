package AWS;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
		
	   static String[] Sets = {"ShardsOfFate",
				               "ShatteredDestiny",
				               "ArmiesOfMyth",
				               "PrimalDawn",
				               "Herofall"};
	   static String[] SetNames = {"Shards of Fate",
                                   "Shattered Destiny",
                                   "Armies of Myth",
                                   "Primal Dawn",
               					   "Herofall"};
		
	   private List<JsonNode> cardList;
	   public List<JsonNode> getCardList(){

		   
		   if(cardList != null){
			   return cardList;
		   }
		   List<JsonNode> cardListBuilder = new LinkedList<JsonNode>();
	    	
		   for(String s:Sets){

				JsonParser parser;
				try {
					parser = new JsonFactory()
					    .createParser(new File("/home/jeremy/hexSets/" + s + ".json"));
					JsonNode rootNode = new ObjectMapper().readTree(parser);

			        JsonNode cards = rootNode.path("cards");
			        for(int i=0;!cards.path(i).isMissingNode();i++){
			        	if(Arrays.asList(SetNames).contains(cards.path(i).path("set_id").asText())){
			        		cardListBuilder.add(cards.path(i));
			        	}
			        }
			        parser.close();
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			        
	    	}
		   cardList=cardListBuilder;
		   return cardList;
	   }
	   
	   

	   private List<JsonNode> champions;
	   public List<JsonNode> getChampions(){

		   
		   if(champions != null){
			   return champions;
		   }
		   List<JsonNode> championsBuilder = new LinkedList<JsonNode>();
		   
		   
		   for(JsonNode c: getCardList()){
			   if(c.path("rarity").asText().equals("Champion")){
				   championsBuilder.add(c);
			   }
		   }
		   
		   

		   champions=championsBuilder;
		   return champions;
		   
	   }
	   
}
