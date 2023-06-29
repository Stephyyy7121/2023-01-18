package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.AsSubgraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	private NYCDao dao;
	private Graph<Location, DefaultWeightedEdge> grafo ;
	private List<Location> vertici;
	private Map<Location, Integer> vicini;
	
	//ricorsione
	private List<Location> percorso;
	private int numMaxNodi;
	private Location target;
	private Location source;
    //private ConnectivityInspector<Location, DefaultWeightedEdge> inspector;
	private TreeSet<Object> utilizzabili;
	private ArrayList<Location> filteredDominio;
	
	
	
	
	public Model() {
		this.dao = new NYCDao();
		this.grafo = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		//this.inspector = null;
		
	}
	
	public void loadNode(String provider) {
		if(this.vertici.isEmpty()) {
			this.vertici = this.dao.getVertici(provider);
		}
	}
	
	public void clearGraph() {
		
		this.grafo = new SimpleWeightedGraph<Location, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.vertici = new ArrayList<>();
	}
	
	public void creaGrafo(double distanza, String provider) {
		
		clearGraph();
		loadNode(provider);
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		//archi
		for (Location l1 : this.vertici) {
			for (Location l2 : this.vertici) {
				if (l1!=l2) {
					LatLng c1 = l1.getCoordinate();
					LatLng c2 = l2.getCoordinate();
					double dis = LatLngTool.distance(c1, c2, LengthUnit.KILOMETER);
					if (dis <= distanza) {
						Graphs.addEdgeWithVertices(this.grafo, l1, l2, dis);
					}
				}
				
				
			
				
			}
		}
		  //this.inspector = new ConnectivityInspector<>(grafo);
		
		
		
	}
	
	
	public List<String> getProvider() {
		return dao.getProvider();
	}
	
	public Map<Location, Integer> maxNumVicini() {
		
		vicini  = new HashMap<Location, Integer>();
		int sizeMax = 0;
		for (Location l : this.vertici) {
			List<Location> viciniCurrent = new ArrayList<>(Graphs.neighborListOf(this.grafo, l));
			if (sizeMax <= viciniCurrent.size()) {
				sizeMax = viciniCurrent.size();
				vicini.put(l, sizeMax); //il valore di sixe si aggiorna ogni volta quindi ci saranno nella mappa dei dati che non rispettano condizione di sizeMax
			}
			
		}
		
		//creare lista di location da togliere 
		List<Location> daRimuovere = new ArrayList<>();
		for (Location l : vicini.keySet()) {
			if (vicini.get(l) < sizeMax) {
				daRimuovere.add(l);
			}
 		}
		for (Location l : daRimuovere )  {
			vicini.remove(l);
		}
		
		return vicini;
	}
	
	public int getNumMaxVicini() {
		return this.vicini.size();
	}
	
	public int getNumVartici() {
		return this.vertici.size();
	}
	
	public int getNumArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public List<Location> getVertici() {
		return this.vertici;
	}
	
	
	
	//ricorsione
	public List<Location> calcolaPercorso(Location target, String s) {
		
		this.percorso = new ArrayList<>();
		this.numMaxNodi = 0;
		this.target = target;
		
		//source random
		
		this.source = getSource(s);
		
		//dominio --> solo location che non contengono s
	    this.filteredDominio =new ArrayList<>();
		for (Location l : this.grafo.vertexSet()) {
			String nome = l.getName().trim().toLowerCase();
			if (!nome.contains(s.toLowerCase()) && !l.equals(source)) {
				filteredDominio.add(l);
			}
		}
		List<Location> viciniSource = getFilteredList(source);
		
		List<Location> parziale = new ArrayList<>();
		parziale.add(source);
		
		ricorsione(parziale, viciniSource);
		return this.percorso;
		
	}

	private List<Location> getFilteredList(Location loc) {
		// TODO Auto-generated method stub
		
		List<Location> vicini = new ArrayList<>();
		for (Location l : Graphs.neighborListOf(this.grafo, loc)) {
			if (this.filteredDominio.contains(l)) {
				vicini.add(l);
			}
		}
		return vicini;
	}

	private Location getSource(String input) {
		// TODO Auto-generated method stub
		String s = input.trim().toLowerCase();
		List<Location> vicini = new ArrayList<>();
		for (Location l : this.vicini.keySet()) {
			String nome = l.getName().trim().toLowerCase();
			if (!nome.contains(s)) {
				vicini.add(l);
			}
		}
		Random random = new Random();
		int posizione = random.nextInt(vicini.size());
		Location source = vicini.get(posizione);
		
		return source;
	}

	private void ricorsione(List<Location> parziale, List<Location> vicini) {
		// TODO Auto-generated method stub
		
		Location current = parziale.get(parziale.size()-1);
		
		if (current.equals(this.target)) {
			int lenghtCurrent = parziale.size();
			if (lenghtCurrent > this.numMaxNodi) {
				this.percorso = new ArrayList<>(parziale);
				this.numMaxNodi = lenghtCurrent;
			}
			return ;
		}
		
		for (Location l : vicini) {
			if (!parziale.contains(l)) {
				parziale.add(l);
				
				List<Location> viciniL = getFilteredList(l);
				viciniL.remove(l);
				viciniL.removeAll(vicini);
				ricorsione(parziale, viciniL);
				parziale.remove(parziale.size()-1);
			}
		}
		
	}
	public Location getSource() {
		return this.source;
	}
	 
	/*public List<Location> calcolaPercorso(Location target, String input) {
		
		this.target = target;
		this.numMaxNodi = -1;
		List<Location> randomSet = new ArrayList<>(this.vicini.keySet());
		
		// estraggo un valore casuale tra zero e randomSet.size()-1
		Random random = new Random();
		int valore = random.nextInt(randomSet.size());
		source = randomSet.get(valore);
		
		// preparo per la ricorsione
		List<Location> parziale = new ArrayList<>();
		parziale.add(source);
		
		// prima di elaborare tutto, rimuovo i valori non ammissimibili
		this.utilizzabili = new TreeSet<>();
		
		for(Location l : this.grafo.vertexSet()) {
			if(!l.getName().toLowerCase().contains(input.toLowerCase()) && !l.equals(source))
				utilizzabili.add(l);
		}
		
		// avvio la ricorsione
		this.cerca(parziale, null, this.source, 0);
		
		
		return this.percorso;
	}



	private void cerca(List<Location> parziale, Location nodoPrecedente, Location nodoAttuale, int lvl ) {
		
		// condizione di terminazione
		if(parziale.get(parziale.size()-1).equals(target)) {
			this.percorso = new ArrayList<>(parziale);
			return;
		}
		
		
		// condizione di aggiornamento
		if(parziale.size()> this.numMaxNodi) {
			 this.numMaxNodi = parziale.size();
			this.percorso = new ArrayList<>(parziale);		
		}
		
		Set<Location> viciniValidi;
		if(lvl>0) {
			viciniValidi = this.prendiViciniValidi(nodoAttuale, nodoPrecedente);
		} else {
			viciniValidi = this.prendiViciniValidi(nodoAttuale, null);
		}
		
		// altrimenti				
		for(Location l : viciniValidi) {
			
			if(!parziale.contains(l)) {
				parziale.add(l);
				this.cerca(parziale, nodoAttuale, l, lvl++);
				parziale.remove(l);
				lvl--;
			}		
		}
	}

	
	// serve ad impedire che si formino dei cicli rimuovendo ogni volta i vertici che erano presenti anche nel set prima
	// essendo essi una possibile causa di ciclicità
	
	private Set<Location> prendiViciniValidi(Location nodo, Location predecessore) {
		
		Set<Location> temp = new TreeSet<>(Graphs.neighborSetOf(this.grafo, nodo));
		
		if (predecessore != null)
			temp.removeAll(Graphs.neighborSetOf(this.grafo, predecessore));
		
		Set<Location> daRimuovere = new TreeSet<>();
		
		for(Location loc : temp) {
			if(!this.utilizzabili.contains(loc))
				daRimuovere.add(nodo);
		}
		temp.removeAll(daRimuovere);
		return temp;
	}
	*/
	
	/* public List<Location> findLongestPathWithRandomOrigin(Location target, String s) {
    
    List<Location> partial = new ArrayList<>();
    partial.add(target);
    
    this.target = target;
    this.percorso = new ArrayList<>(partial);
    this.numMaxNodi = 0;
    
    //per individuare il percorso e' necessario andare a prendere i nodi che sono connessi al target 
    Set<Location> candidates = inspector.connectedSetOf(target); //insieme dei nodi che sono connessi al target 
    candidates.remove(target);
    
    // da questi andare a togliere tutti i nodi che contengono la stringa s
    Set<Location> filteredCandidates = new TreeSet<>();
    for(Location l : candidates) {
        if (!l.getName().toLowerCase().contains(s.toLowerCase())) {
            filteredCandidates.add(l);
        }
    }
    
  //source random
	List<Location> vicini = new ArrayList<>(this.vicini.keySet());
	Random random = new Random();
	int posizione = random.nextInt(vicini.size());
	this.source = vicini.get(posizione);
    
	              //lunghezza max 
    recursive(partial, filteredCandidates);
    
    Collections.reverse(this.percorso); //poiche' si aggiunge dal target al source
    return this.percorso;
}

// Recursive algorithm
public void recursive(List<Location> partial, Set<Location> candidates) {
    
    // Define current position
    Location current = partial.get(partial.size()-1);
    
    // condizione di terminazione --> raggiunta la destinazione 
    if (current.equals(source)) {
        //double partialLenght = calulateLenghtOfPath(partial);
        if (partial.size() > this.numMaxNodi) {
            percorso = new ArrayList<>(partial);
            //bestDistance = partialLenght;
            this.numMaxNodi = partial.size();
        }
    }
    
    // Define and clean data
    
    //TUTTI I NODI CHE SONO CONNESSI AL NODO CORRENTE OVVERO ALL'ULTIMO NODO INSEIRTO IN PARZIALE
    Set<Location> possibilites = Graphs.neighborSetOf(this.grafo, current); //tutti i nodi vicini 
    possibilites.removeAll(partial); //togliere i nodi che sono gia' in parziale 
    possibilites.retainAll(candidates); //tenere solo i nodi che sono presenti in candidates
    
    
    //TUTTI I NODI CHE FORMANO CON I NODO CORRENTE UN CONESSIONE MASSIMALE OVVERO FANNO EFFETTIVAMENTE PARTE IN UN SOTTOGRAFO
    Set<Location> avaibleLocations = inspector.connectedSetOf(current);
    avaibleLocations.removeAll(partial); //rimuovere i nodi che sono gia' stati inseriti per eviatre di avere dei cicli 
    avaibleLocations.add(current); //ma riaggiungere il nodo corrente 
    avaibleLocations.retainAll(candidates); // intersezione con l'insieme dei candidati --> rimagono i nodi che sono sia connessi al nodo corrente che al nodo target
    
    
    //andare a creare un SOTTOGRAFO del grafo di partenza 
    Graph<Location, DefaultWeightedEdge> subGraphOfAvaibleLocations = new AsSubgraph<>(this.grafo, avaibleLocations);
    
    //creare un connettore specifico per questo sottografo
    ConnectivityInspector<Location, DefaultWeightedEdge> auxInspector = 
            new ConnectivityInspector<>(subGraphOfAvaibleLocations);
    
    //capire se due nodi possono essere connessi da un percorso
    if (!auxInspector.pathExists(current, source)) {
    	return;
    }
    
    //NO!!!! if (!possibilites.contains(source)) return; 
    /*
     * --> NON SI SCRIVE QUESTO GENERE DI CONTROLLO poiche' anche se il soirce non e' nel insieme dei vicini di 
     * current, non vuol dire che non si possa comunque trovare un percorso, 
     *magari e' possibile che dai vicini di current e' possibile raggiungere source, indirettamente
     *
     */
    
    
    // Iterate over data
   /* for (Location l : possibilites) {
        
        //TODO se la Location l non può raggiungere source non faccio nulla
        
        partial.add(l);
        recursive(partial, candidates);
        partial.remove(partial.size()-1);
    }
    
    
}*/




	
	
}
