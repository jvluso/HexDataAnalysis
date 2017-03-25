package AWS;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MatchupLambda implements RequestStreamHandler {
	
	MatchStream matchStream;
	
	public MatchupLambda() throws JsonParseException, IOException{
		matchStream = new MatchStream();
	}
	
    
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {
    	AmazonDynamoDBClient client = new AmazonDynamoDBClient();
        client.withRegion(Regions.US_WEST_1);

        DynamoDB dynamoDB = new DynamoDB(client);

        Table archetypeTable = dynamoDB.getTable("ImmortalArchetype");
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
        
        outputStream.write(group.matchupTable(size).getBytes());
      
    }
    
}