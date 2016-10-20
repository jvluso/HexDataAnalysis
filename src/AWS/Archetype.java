package AWS;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

public class Archetype implements Comparable<Archetype> {

	private String name;
	private List<String> matches;
	private List<Integer> deckListHashes;
	
	
	public Archetype(String n){
		name=n;
		matches = new LinkedList<String>();
		deckListHashes = new LinkedList<Integer>();
	}

	public Archetype(Item item){
		name=item.getString("Name");
		matches = new LinkedList<String>();
		deckListHashes = new LinkedList<Integer>();
		matches.addAll(item.getList("Match"));
		for(Object o:item.getList("Decks")){
			deckListHashes.add(Integer.parseInt((String) o));
		}
	}
	
	public String getName(){
		return name;
	}
	
	
	public void addEntry(Item item){
		matches.addAll(item.getList("Match"));
		for(Object o:item.getList("Decks")){
			deckListHashes.add(Integer.parseInt((String) o));
		}
	}
	
	public void addArchetype(Archetype a){
		matches.addAll(a.getMatches());
		deckListHashes.addAll(a.getDeckListHashes());
	}
	
	public List<String> getMatches(){
		return matches;
	}
	
	public List<Integer> getDeckListHashes(){
		return deckListHashes;
	}

	@Override
	public int compareTo(Archetype o) {
		int a=matches.size();
		int b=o.getMatches().size();
		return (a > b ? -1 :
               (a == b ? 0 : 1));
	}
	
	
	
}
