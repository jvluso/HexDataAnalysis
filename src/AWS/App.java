package AWS;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
	
    public static void main(String[] args) throws Exception {
    	


 /*
    	//for uploading new days of data
    	
    	// use /media/sf_UbuntuVM3Share/2017-03-16.gz
    	Scanner scanner = new Scanner(System.in);
    	
    	System.out.println("filename");
    	String filename = scanner.nextLine();
    	System.out.println("collected");
    	scanner.close();
    	InputStream fileStr = new FileInputStream(new File(filename));
    	InputStream gzipStr = new GZIPInputStream(fileStr);
    	
        new MatchStream().uploadDataOptions(new numFilteredStream(gzipStr), false, true);
    

/*
 		//for uploading card sets
        AmazonDynamoDBClient client;
        DynamoDB dynamoDB;
    	Table cardTable;
        client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        dynamoDB = new DynamoDB(client);

        cardTable = dynamoDB.getTable("Cards");
		
	    JsonParser parser = new JsonFactory()
	    .createParser(new File("src/AWS/hexSets/ScarsOfWar.json"));
	   
	    JsonNode rootNode = new ObjectMapper().readTree(parser);
	    JsonNode cards = rootNode.path("cards");
	    for(int i=0;!cards.path(i).isMissingNode();i++){

	        cardTable.putItem(new Item()
	        .withPrimaryKey("Set", getCardAttribute(cards.path(i),"set_id"),
	        		        "uuid", getCardAttribute(cards.path(i),"uuid"))
	        .withString("name", getCardAttribute(cards.path(i),"name"))
	        .withString("cost", getCardAttribute(cards.path(i),"cost"))
	        .withString("threshold", getCardAttribute(cards.path(i),"threshold"))
	        .withString("type", getCardAttribute(cards.path(i),"type"))
	        .withString("subtype", getCardAttribute(cards.path(i),"subtype"))
	        .withString("restriction", getCardAttribute(cards.path(i),"restriction"))
	        .withString("rarity", getCardAttribute(cards.path(i),"rarity"))
	        .withString("text", getCardAttribute(cards.path(i),"text"))
	        .withString("flavor", getCardAttribute(cards.path(i),"flavor"))
	        .withString("atk", getCardAttribute(cards.path(i),"atk"))
	        ,"attribute_not_exists(TournamentTime)", null, null);
	    }
	    parser.close();
	*/    
	    

        
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table archetypeTable = dynamoDB.getTable("ImmortalArchetype");

        
        String FilterExpression = "";
        int itt = 0;
        ValueMap valMap = new ValueMap();
        for(String d:RecentDates.getInstance().getDateList()){
        	if(FilterExpression == ""){
        		FilterExpression = "#d <> :d" + itt;
        	}else{
        		FilterExpression = FilterExpression + " and #d <> :d" + itt;
        	}
        	valMap = valMap.with(":d"+itt, d);
        	itt++;
        }
        
        
        ScanSpec aspec = new ScanSpec().withFilterExpression(FilterExpression)
        							   .withNameMap(new NameMap()
        							       .with("#d", "Date"))
        		                       .withValueMap(valMap);
        

        ItemCollection<ScanOutcome> archetypeItems = archetypeTable.scan(aspec);
        
        for(Item s : archetypeItems){
        	System.out.println(s.getString("date"));
        	archetypeTable.deleteItem("name",s.getString("name"),"date",s.getString("date"));
        }
/*
   
    
        AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table archetypeTable = dynamoDB.getTable("Archetype");
        Table matchTable = dynamoDB.getTable("Matches");

        
        String FilterExpression = "";
        int itt = 0;
        ValueMap valMap = new ValueMap();
        for(String d:RecentDates.getInstance().getDateList()){
        	if(FilterExpression == ""){
        		FilterExpression = "#d = :d" + itt;
        	}else{
        		FilterExpression = FilterExpression + " or #d = :d" + itt;
        	}
        	valMap = valMap.with(":d"+itt, d);
        	itt++;
        }
        
        
        ScanSpec aspec = new ScanSpec().withFilterExpression(FilterExpression)
        							   .withNameMap(new NameMap()
        							       .with("#d", "Date"))
        		                       .withValueMap(valMap);

        ItemCollection<ScanOutcome> archetypeItems = archetypeTable.scan(aspec);
        List<List<Matchup>> matchups = new ArrayList<List<Matchup>>();
        
        int size = 5;
        
        ArchetypeGroup group = new ArchetypeGroup(archetypeItems,matchTable);

        
        for(int i=0;i<size; i++){
    		System.out.print(group.getChamp(i).getName());
    		System.out.print(" : ");
    		System.out.println(group.getChamp(i).getMatches().size());
        }
        for(int i=0;i<size; i++){
        	List<Matchup>list = new ArrayList<Matchup>();
        	for(int j=0;j<i;j++){
        		Matchup m=group.getMatchup(i, j);
        		list.add(m);
        		System.out.print(group.getChamp(i).getName());
        		System.out.print(" vs. ");
        		System.out.println(group.getChamp(j).getName());

        		System.out.print(m.getWins());
        		System.out.print(" to ");
        		System.out.println(m.getLosses());
        		
        	}
        	matchups.add(list);
        }
    	System.out.print(".\t");
        for(int i=0;i<size; i++){
    		System.out.print(group.getChamp(i).getName());
        	System.out.print("\t");
        }
    	System.out.print("\n");
        for(int i=0;i<size; i++){
    		System.out.print(group.getChamp(i).getName());
        	System.out.print("\t");
        	for(int j=0;j<size;j++){
        		if(j<i){
        			if(matchups.get(i).get(j).getWins()+matchups.get(i).get(j).getLosses() == 0){
        				System.out.print("5000");
        			}else{
        				System.out.print(10000*(long)(10+matchups.get(i).get(j).getWins())/(long)(20+(matchups.get(i).get(j).getWins()+matchups.get(i).get(j).getLosses())));
        		
        			}
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
        
/*
        System.out.println(group.matchupTable(10));
        
        */
      
    }
    
    
    public static String getCardAttribute(JsonNode node,String att){
    	
    	if(node.path(att).textValue().isEmpty()){
    		return " ";
    	}else{
    		return node.path(att).textValue();
    	}
    
    	
    }
}