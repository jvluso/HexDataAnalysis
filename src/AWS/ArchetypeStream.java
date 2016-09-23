package AWS;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;

public class ArchetypeStream {

	
	public static void addItem(Item i, Table t){
		Item newItem = i;
		
		
		newItem.removeAttribute("Deck");
		newItem.removeAttribute("Sideboard");
		
		
		Map<String,Object> expressionAttributeValues = new HashMap<String,Object>();
		expressionAttributeValues.put(":val",i.get("Match"));
		Map<String,String> expressionAttributeNames = new HashMap<String,String>();
		expressionAttributeNames.put("#p", "Match");
		
		
		newItem.removeAttribute("Matches");
		

		Map<String,Object> versionAttributeValues = new HashMap<String,Object>();
		versionAttributeValues.put(":ver",
				new HashSet<String>(Arrays.asList("0")));

		Map<String,String> versionAttributeNames = new HashMap<String,String>();
		versionAttributeNames.put("#p", "Version");
		
		t.putItem(newItem.withInt("Version", 0),"#p <> :ver",versionAttributeNames,versionAttributeValues);
		
		

		t.updateItem("HashCode", newItem.getInt("HashCode"),
        "ADD #p :val",
        expressionAttributeNames,
        expressionAttributeValues);
		
	}
}
