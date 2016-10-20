package AWS;

import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;

public class RecentDates {
	private static RecentDates instance = null;
	   protected RecentDates() {
	      // Exists only to defeat instantiation.
	   }
	   public static RecentDates getInstance() {
	      if(instance == null) {
	         instance = new RecentDates();
	      }
	      return instance;
	   }
	   
	   

	   private List<String> dateList;
	   public List<String> getDateList(){

		   
		   if(dateList != null){
			   return dateList;
		   }
		List<String> list=new LinkedList<String>();
		
	    Calendar rightNow = Calendar.getInstance();
	    
	    
	    
	    for(int i=0;i<14;i++){
		    list.add(String.format("%02d", rightNow.get(Calendar.MONTH)+1) + "/"
		    		+String.format("%02d", rightNow.get(Calendar.DATE)) + "/"
		    		+String.format("%04d", rightNow.get(Calendar.YEAR)));
	    	rightNow.add(Calendar.DATE, -1);
	    }
		
		System.out.println(list);
		dateList=list;
		return list;
	}
}
