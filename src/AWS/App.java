package AWS;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;

public class App {
	
    public static void main(String[] args) throws Exception {
    	

/*
 * 
 * for uploading new days of data
    	InputStream fileStr = new FileInputStream(new File("/home/jeremy/hex/2016-09-29.gz"));
    	InputStream gzipStr = new GZIPInputStream(fileStr);
    	
        new MatchStream(new numFilteredStream(gzipStr)).uploadHexTournamentData();
    */

    	
    	
    	
/*
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        //Table matchTable = dynamoDB.getTable("Matches");
        Table deckTable = dynamoDB.getTable("Decklists");
        Table archetypeTable = dynamoDB.getTable("Archetype");

        ScanSpec spec = new ScanSpec();

        ItemCollection<ScanOutcome> items = deckTable.scan(spec);

        int i=0;
        for(Item item: items) {
        	i++;
        	ArchetypeStream.addItem(item, archetypeTable);
            System.out.println(i);
        }
		*/
   
    	

    	
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table archetypeTable = dynamoDB.getTable("Archetype");
        Table matchTable = dynamoDB.getTable("Matches");

        ScanSpec aspec = new ScanSpec();

        ItemCollection<ScanOutcome> archetypeItems = archetypeTable.scan(aspec);
        List<List<Matchup>> matchups = new ArrayList<List<Matchup>>();
        
        ArchetypeGroup group = new ArchetypeGroup(archetypeItems,matchTable);
        for(int i=0;i<10; i++){
        	List<Matchup>list = new ArrayList<Matchup>();
        	for(int j=0;j<i;j++){
        		Matchup m=group.getMatchup(i, j);
        		list.add(m);
        		System.out.print(CardList.getInstance().getCardIdHash().get(group.getChamp(i).getName()).get("name").textValue());
        		System.out.print(" vs. ");
        		System.out.println(CardList.getInstance().getCardIdHash().get(group.getChamp(j).getName()).get("name").textValue());

        		System.out.print(m.getWins());
        		System.out.print(" to ");
        		System.out.println(m.getLosses());
        		
        	}
        	matchups.add(list);
        }
        for(int i=0;i<10; i++){
    		System.out.print(CardList.getInstance().getCardIdHash().get(group.getChamp(i).getName()).get("name").textValue());
        	System.out.print("\t");
        }
    	System.out.print("\n");
        for(int i=0;i<10; i++){
    		System.out.print(CardList.getInstance().getCardIdHash().get(group.getChamp(i).getName()).get("name").textValue());
        	for(int j=0;j<10;j++){
        		if(j<i){
        			System.out.print(matchups.get(i).get(j).getWins()/(matchups.get(i).get(j).getWins()+matchups.get(i).get(j).getLosses()));
        		}
        		if(i==j){
        			System.out.print(".5");
        		}
        		if(j>i){
        			System.out.print(matchups.get(j).get(i).getWins()+matchups.get(j).get(i).getLosses());
            	}
        		System.out.print("\t");
        	}
        	System.out.print("\n");
        }
        
        
        
    	System.out.print(CardList.getInstance().getCardIdHash().get("95e3096e-15fa-4bff-a3af-a44df6dc7c2c").toString());
    	
        
        
    	
/*
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table archetypeTable = dynamoDB.getTable("Archetypes");
        
        
    	Map<String,Archetype> champs = new HashMap<String,Archetype>();

        ScanSpec spec = new ScanSpec();

        ItemCollection<ScanOutcome> archetypeItems = archetypeTable.scan(spec);

        Iterator<Item> iterator = archetypeItems.iterator();
        Item it = null;
        
        while (iterator.hasNext()) {
        	it = iterator.next();
        	String champ=it.getString("Champion");
        	if(champs.get(champ)==null){
        		Archetype a=new Archetype(champ);
        		a.addEntry(it);
        		champs.put(champ,a);
        	}else{
        		Archetype a=champs.get(champ);
        		a.addEntry(it);
        		champs.put(champ,a);
        	}
        }
        
        
        
        
        List<Archetype> topChamps = new ArrayList<Archetype>(5);
    	for(String o:champs.keySet()){
        	if(champs.get(o)!=null){
        		if(topChamps.size() < 5){
    				topChamps.add(champs.get(o));
        		}else{
	        		for(int i=0;i<5;i++){
	        			if(champs.get(o).getMatches().size() > topChamps.get(i).getMatches().size()){
	        				topChamps.set(i, champs.get(o));
	        				break;
	        			}
	        		}
        		}
        	}
    	}
    	for(int i=0;i<5;i++){
    		System.out.println(topChamps.get(i).getName());
    		System.out.println(topChamps.get(i).getMatches().size());
    		System.out.println(topChamps.get(i).getMatches().get(0));
    	}
    	

		System.out.println("done");
		
		
        TableKeysAndAttributes forumTableKeysAndAttributes = new TableKeysAndAttributes("Matches");
		int i=0;
		for(String s:topChamps.get(0).getMatches()){
			if(i<100 && topChamps.get(1).getMatches().contains(s)){
				forumTableKeysAndAttributes.addHashOnlyPrimaryKey("TimePlayerKey",s);
				i++;
			}
		}
		
		
		BatchGetItemOutcome outcome = dynamoDB.batchGetItem(forumTableKeysAndAttributes);
		
        List<Item> items = outcome.getTableItems().get("Matches");
        int wins=0;
        int losses=0;
        for (Item item : items) {
            if(topChamps.get(0).getDeckListHashes().contains(item.getInt("PlayerOneDeck"))){
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
        
        System.out.println(items.size());
        System.out.print("Wins: ");
        System.out.println(wins);
        System.out.print("Losses: ");
        System.out.println(losses);
        
        */
    }
    
    
}