package AWS;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

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
	

	public Matchup(Archetype a, Archetype b,Table matchTable){
		if(a.equals(b)){
			wins=1;
			losses=1;
		}else{
	        wins=0;
	        losses=0;
	        for (String m: a.getMatches()) {
	            if(b.getMatches().contains(m)){
	            	QuerySpec spec = new QuerySpec()
	            	.withKeyConditionExpression("TimePlayerKey  = :m")
	                .withValueMap(new ValueMap()
	                .withString(":m", m));

	            	ItemCollection<QueryOutcome> items = matchTable.query(spec);
	            	
	            	for(Item item : items){
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
	        }
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
