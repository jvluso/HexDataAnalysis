package AWS;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
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
				System.out.println("string?");
				continue;
			}
			Map<String,String> cardMap = (Map<String,String>) obj;
			if(CardList.getInstance().getCardIdHash().containsKey(cardMap.get("Template"))){
				JsonNode card = CardList.getInstance().getCardIdHash().get(cardMap.get("Template"));
				System.out.println(card.get("threshold").textValue());
				if(!Diamond){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Diamond")){
						System.out.print(",");
						Diamond=true;
					}
				}
				if(!Sapphire){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Sapphire")){
						System.out.print(",");
						Sapphire=true;
					}
				}
				if(!Blood){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Blood")){
						System.out.print(",");
						Blood=true;
					}
				}
				if(!Ruby){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Ruby")){
						System.out.print(",");
						Ruby=true;
					}
				}
				if(!Wild){
					System.out.print(".");
					if(card.get("threshold").textValue().contains("Wild")){
						System.out.print(",");
						Wild=true;
					}
				}
				System.out.println(";");
			}else{
				System.out.print("no match for ");
				System.out.println(cardMap.get("Template"));
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
		
		
		
		
		

		Map<String,Object> versionAttributeValues = new HashMap<String,Object>();
		versionAttributeValues.put(":ver",
				new HashSet<String>(Arrays.asList("0")));

		Map<String,String> versionAttributeNames = new HashMap<String,String>();
		versionAttributeNames.put("#p", "Version");
		
		t.putItem(newItem.withInt("Version", 0),"#p <> :ver",versionAttributeNames,versionAttributeValues);
		

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
