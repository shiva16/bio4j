package com.bio4j.model.uniref.programs;

import com.bio4j.model.uniref.UniRefGraph;
import com.bio4j.model.uniref.nodes.UniRef100Cluster;
import com.bio4j.model.uniref.nodes.UniRef50Cluster;
import com.bio4j.model.uniref.nodes.UniRef90Cluster;
import com.ohnosequences.typedGraphs.UntypedGraph;
import com.ohnosequences.xml.api.model.XMLElement;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.jdom2.Element;

/**
 * Imports uniref(100,90,50) clusters info into Bio4j
 *
 * @author <a href="mailto:ppareja@era7.com">Pablo Pareja Tobes</a>
 */
public abstract class ImportUniRef<I extends UntypedGraph<RV,RVT,RE,RET>,RV,RVT,RE,RET> {

	public static final String ENTRY_TAG_NAME = "entry";

	private static final Logger logger = Logger.getLogger("ImportUniRef");
	private static FileHandler fh;

	protected abstract UniRefGraph<I,RV,RVT,RE,RET> config(String dbFolder);

	public void importUniRef(String[] args) {

		if (args.length != 4) {
			System.out.println("This program expects the following parameters: \n"
					+ "1. Uniref 100 xml filename \n"
					+ "2. Uniref 90 xml filename \n"
					+ "3. Uniref 50 xml filename \n"
					+ "4. Bio4j DB folder");
		} else {

			long initTime = System.nanoTime();

			File uniref100File = new File(args[0]);
			File uniref90File = new File(args[1]);
			File uniref50File = new File(args[2]);
			String dbFolder = args[3];

			BufferedWriter statsBuff = null;

			int uniref100EntryCounter =0, uniref90EntryCounter = 0, uniref50EntryCounter = 0;

			logger.log(Level.INFO, "creating manager...");

			UniRefGraph<I,RV,RVT,RE,RET> uniRefGraph = config(dbFolder);

			try {

				// This block configure the logger with handler and formatter
				fh = new FileHandler("ImportUniRefTitan.log", true);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);
				logger.addHandler(fh);
				logger.setLevel(Level.ALL);

				//---creating writer for stats file-----
				statsBuff = new BufferedWriter(new FileWriter(new File("ImportUnirefStats.txt")));

				//------------------- UNIREF 100----------------------------
				System.out.println("Reading Uniref 100 file...");
				uniref100EntryCounter = importUnirefFile(uniRefGraph, uniref100File, 100);
				System.out.println("Done! :)");
				System.out.println("Reading Uniref 90 file...");
				uniref90EntryCounter = importUnirefFile(uniRefGraph, uniref90File, 90);
				System.out.println("Done! :)");
				System.out.println("Reading Uniref 50 file...");
				uniref50EntryCounter = importUnirefFile(uniRefGraph, uniref50File, 50);
				System.out.println("Done! :)");


			} catch (Exception ex) {
				logger.log(Level.SEVERE, ex.getMessage());
				StackTraceElement[] trace = ex.getStackTrace();
				for (StackTraceElement stackTraceElement : trace) {
					logger.log(Level.SEVERE, stackTraceElement.toString());
				}
			} finally {
				try {
					//closing logger file handler
					fh.close();
					//closing neo4j managers
					uniRefGraph.raw().shutdown();

					//-----------------writing stats file---------------------
					long elapsedTime = System.nanoTime() - initTime;
					long elapsedSeconds = Math.round((elapsedTime / 1000000000.0));
					long hours = elapsedSeconds / 3600;
					long minutes = (elapsedSeconds % 3600) / 60;
					long seconds = (elapsedSeconds % 3600) % 60;

					statsBuff.write("Statistics for program ImportUniRefTitan:\nInput files: " +
							"\nUniref 100 file: " + uniref100File.getName() +
							"\nUniref 90 file: " + uniref90File.getName() +
							"\nUniref 50 file: " + uniref50File.getName()
							+ "\nThe following number of entries was parsed:\n"
							+ "Uniref 100 --> " + uniref100EntryCounter + " entries\n"
							+ "Uniref 90 --> " + uniref90EntryCounter + " entries\n"
							+ "Uniref 50 --> " + uniref50EntryCounter + " entries\n"
							+ "The elapsed time was: " + hours + "h " + minutes + "m " + seconds + "s\n");

					//---closing stats writer---
					statsBuff.close();

				} catch (Exception e) {
					logger.log(Level.SEVERE, e.getMessage());
					StackTraceElement[] trace = e.getStackTrace();
					for (StackTraceElement stackTraceElement : trace) {
						logger.log(Level.SEVERE, stackTraceElement.toString());
					}
				}

			}

			System.out.println("Program finished!! :D");


		}
	}

	private static String getRepresentantAccession(Element elem) {
		String result = null;
		Element dbReference = elem.getChild("dbReference");
		List<Element> properties = dbReference.getChildren("property");
		for (Element prop : properties) {
			if (prop.getAttributeValue("type").equals("UniProtKB accession")) {
				result = prop.getAttributeValue("value");
			}
		}

		return result;
	}

	private int importUnirefFile(UniRefGraph<I,RV,RVT,RE,RET> uniRefGraph,
	                                    File unirefFile,
	                                    int unirefClusterNumber) throws Exception {

		StringBuilder entryStBuilder = new StringBuilder();

		BufferedReader reader = new BufferedReader(new FileReader(unirefFile));
		String line;

		int entryCounter = 0;
		int limitForPrintingOut = 10000;

		while ((line = reader.readLine()) != null) {
			//----we reached a entry line-----
			if (line.trim().startsWith("<" + ENTRY_TAG_NAME)) {

				while (!line.trim().startsWith("</" + ENTRY_TAG_NAME + ">")) {
					entryStBuilder.append(line);
					line = reader.readLine();
				}
				//organism last line
				entryStBuilder.append(line);

				XMLElement entryXMLElem = new XMLElement(entryStBuilder.toString());
				entryStBuilder.delete(0, entryStBuilder.length());

				ArrayList<String> membersAccessionList = new ArrayList<String>();
				Element representativeMember = entryXMLElem.asJDomElement().getChild("representativeMember");
				String representantAccession = getRepresentantAccession(representativeMember);

				if(unirefClusterNumber == 50){
					UniRef50Cluster<I,RV,RVT,RE,RET> cluster = uniRefGraph.UniRef50Cluster().from(uniRefGraph.raw().addVertex(null));
					cluster.set(uniRefGraph.UniRef50Cluster().id, representantAccession);
				}else if(unirefClusterNumber == 90){
					UniRef90Cluster<I,RV,RVT,RE,RET> cluster = uniRefGraph.UniRef90Cluster().from(uniRefGraph.raw().addVertex(null));
					cluster.set(uniRefGraph.UniRef90Cluster().id, representantAccession);
				}else if(unirefClusterNumber == 100){
					UniRef100Cluster<I,RV,RVT,RE,RET> cluster = uniRefGraph.UniRef100Cluster().from(uniRefGraph.raw().addVertex(null));
					cluster.set(uniRefGraph.UniRef100Cluster().id, representantAccession);
				}

			}

			entryCounter++;
			if ((entryCounter % limitForPrintingOut) == 0) {
				logger.log(Level.INFO, (entryCounter + " entries parsed!!"));
			}

		}
		reader.close();

		return entryCounter;
	}
}