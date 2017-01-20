package AWS;

import com.amazonaws.services.lambda.runtime.Context; 
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Lambda implements RequestStreamHandler {
	
	MatchStream matchStream;
	
	public Lambda() throws JsonParseException, IOException{
		matchStream = new MatchStream();
	}
	
    public String myHandler(JsonNode node, Context context) throws IOException {

    	
        matchStream.uploadJSON(node);
    
        return "Success";

    	
    }
    
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context)
            throws IOException {

    	System.out.println("here");
        matchStream.uploadHexTournamentData(new numFilteredStream(inputStream));
    }
}