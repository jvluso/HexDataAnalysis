package AWS;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;

public class Matchup {

	
	private List<Item> matchResults;
	
	private int wins;
	private int losses;
	
	
	public Matchup(Archetype a, Archetype b,DynamoDB dynamoDB){
		if(a.equals(b)){
			wins=1;
			losses=1;
			matchResults=new LinkedList<Item>();
		}else{
			setMatchResults(matches(a,b),dynamoDB);
			init(a,b,dynamoDB);
		}
	}
	
	private void setMatchResults(List<String> matches,DynamoDB dynamoDB){

        TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes("Matches");
		
		for(String s:matches){
			forumTableKeysAndAttributes.addHashOnlyPrimaryKey("TimePlayerKey",s);
		}
		
		
		BatchGetItemOutcome outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes);
		
		matchResults = outcome.getTableItems().get("Matches");
		
	}
	
	private void init(Archetype a, Archetype b,DynamoDB dynamoDB){
        wins=0;
        losses=0;
        for (Item item : matchResults) {
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
	
	private List<String> matches(Archetype a, Archetype b){
		List<String> matches=new LinkedList<String>();

		for(String s:a.getMatches()){
			if(b.getMatches().contains(s)){
				matches.add(s);
			}
		}
		matches = matches.subList(0, Math.min(100, matches.size()));
		return matches;
	}
	
	public int getWins(){
		return wins;
	}
	
	public int getLosses(){
		return losses;
	}
}
