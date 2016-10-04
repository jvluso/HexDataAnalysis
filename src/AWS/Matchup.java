package AWS;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.TableKeysAndAttributes;

public class Matchup {

	
	
	private int wins;
	private int losses;
	
	
	public Matchup(Archetype a, Archetype b,ItemCollection<ScanOutcome> result){
		if(a.equals(b)){
			wins=1;
			losses=1;
		}else{
			init(a,b,result);
		}
	}
	
	private void init(Archetype a, Archetype b,ItemCollection<ScanOutcome> result){
        wins=0;
        losses=0;
        for (Item item : result) {
            if(a.getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
               b.getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
            	if(item.getInt("PlayerOneWins")==2){
            		wins++;
            	}else{
            		losses++;
            	}
            }else if(b.getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
                     a.getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
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
