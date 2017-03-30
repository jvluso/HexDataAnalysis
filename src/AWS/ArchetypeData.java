package AWS;

import java.util.LinkedList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.document.Item;

public class ArchetypeData implements Comparable<ArchetypeData> {

	private Archetype archetype;
	private List<String> matches;
	private List<Integer> deckListHashes;
	
	
	public ArchetypeData(String n){
		archetype=new Archetype(n);
		matches = new LinkedList<String>();
		deckListHashes = new LinkedList<Integer>();
	}

	public ArchetypeData(Item item){
		archetype=new Archetype(item.getString("Name"));
		matches = new LinkedList<String>();
		deckListHashes = new LinkedList<Integer>();
		matches.addAll(item.getList("Match"));
		for(Object o:item.getList("Decks")){
			deckListHashes.add(Integer.parseInt((String) o));
		}
	}
	
	public Archetype getArchetype(){
		return archetype;
	}
	
	
	public void addEntry(Item item){
		matches.addAll(item.getList("Match"));
		for(Object o:item.getList("Decks")){
			deckListHashes.add(Integer.parseInt((String) o));
		}
	}
	
	public void addArchetype(ArchetypeData a){
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
	public int compareTo(ArchetypeData o) {
		int a=matches.size();
		int b=o.getMatches().size();
		return (a > b ? -1 :
               (a == b ? 0 : 1));
	}
	
	
	
}
