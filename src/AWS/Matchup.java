package AWS;

import java.util.List;

import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;

public class Matchup {

	private BatchGetItemOutcome outcome;
	
	private int wins;
	private int losses;
	
	
	public Matchup(Archetype a, Archetype b,DynamoDB dynamoDB){
		init(a,b,dynamoDB);
	}
	
	private void init(Archetype a, Archetype b,DynamoDB dynamoDB){

        TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes("Matches");
		
		for(String s:a.getMatches()){
			if(b.getMatches().contains(s)){
				forumTableKeysAndAttributes.addHashOnlyPrimaryKey("TimePlayerKey",s);
			}
		}
		
		
		outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes);
		
        List<Item> items = outcome.getTableItems().get("Matches");
        wins=0;
        losses=0;
        for (Item item : items) {
            if(a.getDeckListHashes().contains(item.getInt("PlayerOneDeck"))){
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
        
	}
	
	public int getWins(){
		return wins;
	}
	
	public int getLosses(){
		return losses;
	}
}
