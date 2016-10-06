package AWS;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public class ArchetypeStream {

	
	public static void addItem(Item i, Table t) throws JsonParseException, IOException{
		
		Boolean Diamond=false;
		Boolean Sapphire=false;
		Boolean Blood=false;
		Boolean Ruby=false;
		Boolean Wild=false;
		
		List<HashMap<String,String>> cardList = i.getList("Deck");
		cardList.addAll(i.getList("Sideboard"));
		
		for(Object obj: cardList){
			if(!(obj instanceof Map)){
				continue;
			}
			Map<String,String> cardMap = (Map<String,String>) obj;
			if(CardList.getInstance().getCardIdHash().containsKey(cardMap.get("Template"))){
				JsonNode card = CardList.getInstance().getCardIdHash().get(cardMap.get("Template"));
				if(!Diamond){
					if(card.get("threshold").textValue().contains("Diamond")){
						Diamond=true;
					}
				}
				if(!Sapphire){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Sapphire")){
						Sapphire=true;
					}
				}
				if(!Blood){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Blood")){
						Blood=true;
					}
				}
				if(!Ruby){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Ruby")){
						Ruby=true;
					}
				}
				if(!Wild){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Wild")){
						Wild=true;
					}
				}
			}
		}
		
		
		
		String name = CardList.getInstance().getCardIdHash().get(i.getString("Champion")).get("name").textValue();
		if(Wild){
			name = "Wild " + name;
		}
		if(Ruby){
			name = "Ruby " + name;
		}
		if(Blood){
			name = "Blood " + name;
		}
		if(Sapphire){
			name = "Saphire " + name;
		}
		if(Diamond){
			name = "Diamond " + name;
		}
		
		
		Item newItem = new Item().withBoolean("Diamond", Diamond)
				                 .withBoolean("Saphire", Sapphire)
				                 .withBoolean("Blood", Blood)
				                 .withBoolean("Ruby", Ruby)
				                 .withBoolean("Wild", Wild)
				                 .withPrimaryKey("Name", name)
				                 .withString("Champion", i.getString("Champion"));
		
		
		
		
		
		
		try{
			t.putItem(newItem,"attribute_not_exists(Decks)",null,null);
		}catch (ConditionalCheckFailedException e){}

		Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
		expressionAttributeValues.put(":m",i.get("Match"));
		Map<String,String> expressionAttributeNames = new HashMap<String,String>();
		expressionAttributeNames.put("#m", "Match");
		
		t.updateItem("Name", name,
				"ADD #m :m",
				expressionAttributeNames,
				expressionAttributeValues);
		
		

		expressionAttributeValues = new HashMap<String,Object>();
		expressionAttributeValues.put(":d",
		        new HashSet<String>(Arrays.asList(i.getString("HashCode"))));
		expressionAttributeNames = new HashMap<String,String>();
		expressionAttributeNames.put("#d", "Decks");
		t.updateItem("Name", name,
		        "ADD #d :d",
		        expressionAttributeNames,
		        expressionAttributeValues);
		
	}
}
