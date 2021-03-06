package org.harmony_analyser.application;

import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ResourceBundle;

import org.harmony_analyser.application.visualizations.DataChartFactory;
import org.harmony_analyser.jharmonyanalyser.services.*;

/**
 * Controller for Audio Analysis Tool Events
 */

public class AudioAnalysisToolController implements Initializable {
	@FXML
	private StackPane browsePane;

	@FXML
	private TreeView<File> browse;

	@FXML
	private TextArea console;

	@FXML
	private ListView<String> vampAvailable;

	@FXML
	private Label vampTitle;

	@FXML
	private Label vampDescription;

	@FXML
	private Button vampSettings;

	@FXML
	private Button vampAnalyse;

	@FXML
	private ListView<String> caAvailable;

	@FXML
	private Label caTitle;

	@FXML
	private Label caDescription;

	@FXML
	private Button caSettings;

	@FXML
	private Button caAnalyse;

	@FXML
	private ListView<String> chrAvailable;

	@FXML
	private Label chrTitle;

	@FXML
	private Label chrDescription;

	@FXML
	private Button chrSettings;

	@FXML
	private Button chrAnalyse;

	@FXML
	private ListView<String> ppAvailable;

	@FXML
	private Label ppTitle;

	@FXML
	private Label ppDescription;

	@FXML
	private Button ppSettings;

	@FXML
	private Button ppAnalyse;

	@FXML
	private TextField ppExtension;

	private AudioAnalyser audioAnalyser;

	@Override // This method is called by the FXMLLoader when initialization is complete
	public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
		//initialize AudioAnalyser
		AnalysisFactory analysisFactory = new AnalysisFactory();
		DataChartFactory dataChartFactory = new DataChartFactory();
		audioAnalyser = new AudioAnalyser(analysisFactory, dataChartFactory);

		// create the tree view
		// TODO: Check unchecked assignments
		browse = TreeViewBuilder.buildFileSystemBrowser();
		browsePane.getChildren().add(browse);

		//load plugins
		ObservableList<String> vampPlugins = FXCollections.observableArrayList(audioAnalyser.getAllWrappedVampPlugins());
		vampAvailable.setItems(vampPlugins);
		ObservableList<String> chordAnalyserPlugins = FXCollections.observableArrayList(audioAnalyser.getAllChordAnalyserPlugins());
		caAvailable.setItems(chordAnalyserPlugins);
		ObservableList<String> chromaAnalyserPlugins = FXCollections.observableArrayList(audioAnalyser.getAllChromaAnalyserPlugins());
		chrAvailable.setItems(chromaAnalyserPlugins);
		ObservableList<String> postProcessingFilters = FXCollections.observableArrayList(audioAnalyser.getAllPostProcessingFilters());
		ppAvailable.setItems(postProcessingFilters);

		//init UI
		vampAvailable.getSelectionModel().select(0);
		caAvailable.getSelectionModel().select(0);
		chrAvailable.getSelectionModel().select(0);
		ppAvailable.getSelectionModel().select(0);
		try {
			vampTitle.setText(audioAnalyser.getPluginName(vampAvailable.getSelectionModel().getSelectedItem()));
			vampDescription.setText(audioAnalyser.getPluginDescription(vampAvailable.getSelectionModel().getSelectedItem()));
			caTitle.setText(audioAnalyser.getPluginName(caAvailable.getSelectionModel().getSelectedItem()));
			caDescription.setText(audioAnalyser.getPluginDescription(caAvailable.getSelectionModel().getSelectedItem()));
			chrTitle.setText(audioAnalyser.getPluginName(chrAvailable.getSelectionModel().getSelectedItem()));
			chrDescription.setText(audioAnalyser.getPluginDescription(chrAvailable.getSelectionModel().getSelectedItem()));
			ppTitle.setText(audioAnalyser.getPluginName(ppAvailable.getSelectionModel().getSelectedItem()));
			ppDescription.setText(audioAnalyser.getPluginDescription(ppAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}

		vampAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				vampTitle.setText(audioAnalyser.getPluginName(vampAvailable.getSelectionModel().getSelectedItem()));
				vampDescription.setText(audioAnalyser.getPluginDescription(vampAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		caAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				caTitle.setText(audioAnalyser.getPluginName(caAvailable.getSelectionModel().getSelectedItem()));
				caDescription.setText(audioAnalyser.getPluginDescription(caAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		chrAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				chrTitle.setText(audioAnalyser.getPluginName(chrAvailable.getSelectionModel().getSelectedItem()));
				chrDescription.setText(audioAnalyser.getPluginDescription(chrAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});

		ppAvailable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			try {
				ppTitle.setText(audioAnalyser.getPluginName(ppAvailable.getSelectionModel().getSelectedItem()));
				ppDescription.setText(audioAnalyser.getPluginDescription(ppAvailable.getSelectionModel().getSelectedItem()));
			} catch (AudioAnalyser.LoadFailedException e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	void printVampSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(vampAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printChordAnalyserSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(caAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printChromaAnalyserSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(chrAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void printPostProcessingSettings(ActionEvent event) {
		try {
			console.setText(audioAnalyser.printParameters(ppAvailable.getSelectionModel().getSelectedItem()));
		} catch (AudioAnalyser.LoadFailedException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void runVampAnalysis(ActionEvent event) {
		analyseFolder(browse.getSelectionModel().getSelectedItem().getValue(), vampAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runChordAnalyserAnalysis(ActionEvent event) {
		analyseFolder(browse.getSelectionModel().getSelectedItem().getValue(), caAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runChromaAnalyserAnalysis(ActionEvent event) {
		analyseFolder(browse.getSelectionModel().getSelectedItem().getValue(), chrAvailable.getSelectionModel().getSelectedItem(), ".wav");
	}

	@FXML
	void runPostProcessingAnalysis(ActionEvent event) {
		String extension = ppExtension.getText();
		if (extension.equals("")) {
			console.setText("\n> Extension for filtering not specified. Please enter extension.");
		} else {
			analyseFolder(browse.getSelectionModel().getSelectedItem().getValue(), ppAvailable.getSelectionModel().getSelectedItem(), extension);
		}
	}

	private void analyseFolder(File inputFolder, String analysisKey, String suffixAndExtension) {
		console.setText(audioAnalyser.analyseFolder(inputFolder, analysisKey, suffixAndExtension, 0.07f));
	}
}