package AWS;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

public class Archetype {

	private String name;
	private List<String> matches;
	private List<Integer> deckListHashes;
	
	
	public Archetype(String n){
		name=n;
		matches = new LinkedList<String>();
		deckListHashes = new LinkedList<Integer>();
	}
	
	public String getName(){
		return name;
	}
	
	public void addEntry(Item item){
		matches.addAll(item.getList("Match"));
		deckListHashes.add(item.getInt("HashCode"));
	}
	
	public List<String> getMatches(){
		return matches;
	}
	
	public List<Integer> getDeckListHashes(){
		return deckListHashes;
	}
	
}
