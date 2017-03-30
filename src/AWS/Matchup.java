package AWS;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class Matchup {

	
	private Archetype a;
	private Archetype b;
	private int wins;
	private int losses;

	public Matchup(ArchetypeData archetypeA, ArchetypeData archetypeB){
		initArchetypeData(archetypeA, archetypeB);
	}

	public Matchup(Archetype archetypeA, Archetype archetypeB){
		initArchetypes(archetypeA, archetypeB);
	}
	
	public Matchup(ArchetypeData archetypeA, ArchetypeData archetypeB,ItemCollection<ScanOutcome> result){
		initArchetypeData(archetypeA, archetypeB);
		if(!a.equals(b)){
			addCollection(archetypeA,archetypeB,result);
		}
	}
	

	public Matchup(ArchetypeData archetypeA, ArchetypeData archetypeB,Table matchTable){
		initArchetypeData(archetypeA, archetypeB);
		if(!a.equals(b)){
	        for (String m: matcha(archetypeA,archetypeB).getMatches()) {
	            if(matchb(archetypeA,archetypeB).getMatches().contains(m)){
	            	QuerySpec spec = new QuerySpec()
	            	.withKeyConditionExpression("TimePlayerKey  = :m")
	                .withValueMap(new ValueMap()
	                .withString(":m", m));

	            	ItemCollection<QueryOutcome> items = matchTable.query(spec);
	            	
	            	addCollection(archetypeA,archetypeB,items);
	            }
	        }
		}
	}
	

	private void initArchetypeData(ArchetypeData archetypeA, ArchetypeData archetypeB){
		initArchetypes(archetypeA.getArchetype(),archetypeB.getArchetype());
	}
	
	private void initArchetypes(Archetype archetypeA, Archetype archetypeB){
		wins=0;
		losses=0;
		int compare = archetypeA.getName().compareTo(archetypeB.getName());
		if(compare > 0){
			a=archetypeA;
			b=archetypeB;
		}else{
			a=archetypeB;
			b=archetypeA;
		}
	}
	
	private void addCollection(ArchetypeData archetypeA, ArchetypeData archetypeB,ItemCollection result){
        for (Object o : result) {
        	Item item = (Item) o;
            if(matcha(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
            		matchb(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
            	if(item.getInt("PlayerOneWins")==2){
            		wins++;
            	}else{
            		losses++;
            	}
            }else if(matchb(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
            		matcha(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
            	if(item.getInt("PlayerOneWins")!=2){
            		wins++;
            	}else{
            		losses++;
            	}
            }
        }
	}

	public ArchetypeData matcha(ArchetypeData archetypeA, ArchetypeData archetypeB){
		return match(a, archetypeA, archetypeB);
	}
	public ArchetypeData matchb(ArchetypeData archetypeA, ArchetypeData archetypeB){
		return match(b, archetypeA, archetypeB);
	}
	
	private ArchetypeData match(Archetype arch,ArchetypeData archetypeA, ArchetypeData archetypeB){
		if(arch.equals(archetypeA)){
			return archetypeA;
		}else{
			return archetypeB;
		}
	}
	
	public Archetype geta(){
		return a;
	}
	public Archetype getb(){
		return b;
	}
	
	public int getWins(Archetype archetype){
		if(archetype.equals(a)){
			return wins;
		}else if(archetype.equals(b)){
			return losses;
		}
		return 0;
	}
	
}
