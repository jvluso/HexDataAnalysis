package AWS;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

public class App {
	
    public static void main(String[] args) throws Exception {
    	


 
    	//for uploading new days of data
    	InputStream fileStr = new FileInputStream(new File("/home/jeremy/hex/2016-10-18.gz"));
    	InputStream gzipStr = new GZIPInputStream(fileStr);
    	
        new MatchStream().uploadHexTournamentData(new numFilteredStream(gzipStr));
    

    	
   
    	
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
        
        int size = 15;
        
        ArchetypeGroup group = new ArchetypeGroup(archetypeItems,matchTable);
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
        				System.out.print(10000*(long)matchups.get(i).get(j).getWins()/(long)(matchups.get(i).get(j).getWins()+matchups.get(i).get(j).getLosses()));
        		
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
        
    }
    
    
}