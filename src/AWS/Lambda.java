package AWS;

import com.amazonaws.services.lambda.runtime.Context; 
import com.fasterxml.jackson.core.JsonParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class Lambda {
	
	
	MatchStream matchStream;
	public Lambda() throws JsonParseException, IOException{
		matchStream = new MatchStream();
	}
    public String myHandler(InputStream myStream, Context context) throws IOException {

    	
        matchStream.uploadHexTournamentData(new numFilteredStream(myStream));
    
        return "Success";

    	
    }
}