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

	public Matchup(ArchetypeData archetypeA, ArchetypeData archetypeB){
		initArchetypeData(archetypeA, archetypeB);
	}

	public Matchup(Archetype archetypeA, Archetype archetypeB){
		initArchetypes(archetypeA, archetypeB);
	}
	
	

	public WinRate addTable(ArchetypeData archetypeA, ArchetypeData archetypeB,Table matchTable) throws Exception{
		WinRate winRate = new WinRate();
		initArchetypeData(archetypeA, archetypeB);
		if(!a.equals(b)){
	        for (String m: archetypeA.getMatches()) {
	            if(archetypeB.getMatches().contains(m)){
	            	QuerySpec spec = new QuerySpec()
	            	.withKeyConditionExpression("TimePlayerKey  = :m")
	                .withValueMap(new ValueMap()
	                .withString(":m", m));

	            	ItemCollection<QueryOutcome> items = matchTable.query(spec);
	            	
	            	addCollection(archetypeA,archetypeB,items,winRate);
	            }
	        }
		}
		return winRate;
	}
	

	private void initArchetypeData(ArchetypeData archetypeA, ArchetypeData archetypeB){
		initArchetypes(archetypeA.getArchetype(),archetypeB.getArchetype());
	}
	
	private void initArchetypes(Archetype archetypeA, Archetype archetypeB){
		int compare = archetypeA.getName().compareTo(archetypeB.getName());
		if(compare > 0){
			a=archetypeA;
			b=archetypeB;
		}else{
			a=archetypeB;
			b=archetypeA;
		}
	}
	
	public void addCollection(ArchetypeData archetype1, ArchetypeData archetype2,ItemCollection result, WinRate winRate) throws Exception{
		ArchetypeData archetypeA;
		ArchetypeData archetypeB;
		if(a.equals(archetype1.getArchetype())&&b.equals(archetype2.getArchetype())){
			archetypeA=archetype1;
			archetypeB=archetype2;
		}else if(a.equals(archetype2.getArchetype())&&b.equals(archetype1.getArchetype())){
			archetypeA=archetype2;
			archetypeB=archetype1;
		}else{
			throw new Exception("Incorrect archetypes");
		}
		if(!a.equals(b)){
	        for (Object o : result) {
	        	Item item = (Item) o;
	            if(matcha(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
	            		matchb(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
	            	if(item.getInt("PlayerOneWins")==2){
	            		winRate.addWin();
	            	}else{
	            		winRate.addLoss();
	            	}
	            }else if(matchb(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerOneDeck")) &&
	            		matcha(archetypeA,archetypeB).getDeckListHashes().contains(item.getInt("PlayerTwoDeck"))){
	            	if(item.getInt("PlayerOneWins")!=2){
	            		winRate.addWin();
	            	}else{
	            		winRate.addLoss();
	            	}
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
	
	public int getWins(Archetype archetype, WinRate winRate){
		if(archetype.equals(a)){
			return winRate.getWins();
		}else if(archetype.equals(b)){
			return winRate.getLosses();
		}
		return 0;
	}
	
}
