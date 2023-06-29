/**
 * Sample Skeleton for 'Scene.fxml' Controller Class
 */

package it.polito.tdp.nyc;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.nyc.model.Location;
import it.polito.tdp.nyc.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FXMLController {

	private Model model;
	
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="btnAnalisi"
    private Button btnAnalisi; // Value injected by FXMLLoader

    @FXML // fx:id="btnCreaGrafo"
    private Button btnCreaGrafo; // Value injected by FXMLLoader

    @FXML // fx:id="btnPercorso"
    private Button btnPercorso; // Value injected by FXMLLoader

    @FXML // fx:id="cmbProvider"
    private ComboBox<String> cmbProvider; // Value injected by FXMLLoader

    @FXML // fx:id="txtDistanza"
    private TextField txtDistanza; // Value injected by FXMLLoader

    @FXML // fx:id="txtResult"
    private TextArea txtResult; // Value injected by FXMLLoader

    @FXML // fx:id="txtStringa"
    private TextField txtStringa; // Value injected by FXMLLoader
    
    @FXML // fx:id="txtTarget"
    private ComboBox<Location> txtTarget; // Value injected by FXMLLoader

    private boolean creaGrafo = false;
    
    @FXML
    void doAnalisiGrafo(ActionEvent event) {
    	
    	if (!creaGrafo) {
    		txtResult.appendText("Grafo non creato");
    	}
    	txtResult.appendText("\nVertici con più vicini: \n");
    	for (Location l : this.model.maxNumVicini().keySet()) {
    		txtResult.appendText(l.toString()+", #"+this.model.maxNumVicini().get(l)+"\n");
    	}
    }

    @FXML
    void doCalcolaPercorso(ActionEvent event) {
    	
    	txtResult.clear();
    	Location target = this.txtTarget.getValue();
    	if (target == null) {
    		txtResult.appendText("Inserire un valore\n");
    	}
    	String s  = this.txtStringa.getText();
    	if (s.compareTo("")==0) {
    		txtResult.appendText("Inserire una stringa\n");
    	}
    	if (!this.creaGrafo) {
    		txtResult.appendText("Non è stato creato un grafo\n");
    	}
    	if (target.getName().toLowerCase().contains(s.toLowerCase())) {
    		txtResult.appendText("Non e' possobile trovare un percorso");
    	}
    	else {
    		List<Location> percorso = this.model.calcolaPercorso(target, s);
        	if (percorso.isEmpty()) {
        		txtResult.appendText("Non e' stato trovato un percorso\n");
        	}
        	else {
        		txtResult.appendText("Trovato percorso tra " + this.model.getSource() + " - " + target + ": \n");
        		for (Location l : percorso) {
        		txtResult.appendText(l.toString()+"\n");
        		}
        	}
    		
    	}
    	
    }

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	txtResult.clear();
    	String input = txtDistanza.getText();
    	String provider = cmbProvider.getValue();
    	
    	if (provider == null) {
    		txtResult.setText("Inserire valore");
    	}
    	if (input=="") {
    		txtResult.appendText("Inserire distanza");
    	}
    	
    	double distanza = 0.0;
    	try {
    		distanza = Double.parseDouble(input);
    	}catch (NumberFormatException e) {
    		txtResult.appendText("Valore inserito non accettabile");
    		return;
    	}
    	
    	this.model.creaGrafo(distanza, provider);
    	this.creaGrafo = true;
    	txtResult.appendText("Grafo creato! \n#Vertici: "+this.model.getNumVartici() + "\n#Archi: "+ this.model.getNumArchi()+"\n");
    	this.txtTarget.getItems().addAll(this.model.getVertici());
    	
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert btnAnalisi != null : "fx:id=\"btnAnalisi\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnCreaGrafo != null : "fx:id=\"btnCreaGrafo\" was not injected: check your FXML file 'Scene.fxml'.";
        assert btnPercorso != null : "fx:id=\"btnPercorso\" was not injected: check your FXML file 'Scene.fxml'.";
        assert cmbProvider != null : "fx:id=\"cmbProvider\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtDistanza != null : "fx:id=\"txtDistanza\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Scene.fxml'.";
        assert txtStringa != null : "fx:id=\"txtStringa\" was not injected: check your FXML file 'Scene.fxml'.";

    }

    public void setModel(Model model) {
    	this.model = model;
    	this.cmbProvider.getItems().addAll(this.model.getProvider());
    }
    
}
