package com.bio4j.model.ncbiTaxonomy.programs;

import java.io.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.bio4j.model.ncbiTaxonomy.NCBITaxonomyGraph;
import com.bio4j.model.ncbiTaxonomy.vertices.NCBITaxon;
import com.bio4j.angulillos.UntypedGraph;


/**
 * @author <a href="mailto:ppareja@era7.com">Pablo Pareja Tobes</a>
 */
public abstract class ImportNCBITaxonomy<I extends UntypedGraph<RV,RVT,RE,RET>,RV,RVT,RE,RET> {

	private static final Logger logger = Logger.getLogger("ImportNCBITaxonomy");
	private static FileHandler fh;

	protected abstract NCBITaxonomyGraph<I,RV,RVT,RE,RET> config(String dbFolder, String propertiesFile);

	public void importNCBITaxonomy(String[] args) {

		if (args.length != 5) {
			System.out.println("This program expects the following parameters: \n"
					+ "1. Nodes DMP filename \n"
					+ "2. Names DMP filename \n"
					+ "3. Merged DMP filename \n"
					+ "4. Bio4j DB folder \n"
					+ "5. DB Properties file (.properties)");
		} else {

			long initTime = System.nanoTime();

			int taxonCounter = 0;
			int limitForTransaction = 1000;

			BufferedWriter statsBuff = null;

			File nodesDumpFile = new File(args[0]);
			File namesDumpFile = new File(args[1]);
			String dbFolder = args[3];
			String propertiesFile = args[4];

			logger.log(Level.INFO, "creating manager...");

			NCBITaxonomyGraph<I,RV,RVT,RE,RET> ncbiTaxonomyGraph = config(dbFolder, propertiesFile);


			try {

				// This block configure the logger with handler and formatter
				fh = new FileHandler("ImportNCBITaxonomy.log", true);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);
				logger.addHandler(fh);
				logger.setLevel(Level.ALL);

				//---creating writer for stats file-----
				statsBuff = new BufferedWriter(new FileWriter(new File("ImportNCBITaxonomyStats.txt")));

				BufferedReader reader = new BufferedReader(new FileReader(nodesDumpFile));
				String line;


				HashMap<String, String> nodeParentMap = new HashMap<>();

				logger.log(Level.INFO, "reading nodes file...");

				while ((line = reader.readLine()) != null) {
					if (line.trim().length() > 0) {
						String[] columns = line.split("\\|");

						String id = columns[0].trim();
						String taxonomicRankSt = columns[2].trim();

						NCBITaxon<I,RV,RVT,RE,RET> ncbiTaxon = ncbiTaxonomyGraph.addVertex(ncbiTaxonomyGraph.NCBITaxon());
						ncbiTaxon.set(ncbiTaxonomyGraph.NCBITaxon().id, id);
						ncbiTaxon.set(ncbiTaxonomyGraph.NCBITaxon().taxonomicRank, taxonomicRankSt);
						//node.setEmblCode(columns[3].trim()); TODO add emblCode??

						//saving the parent of the node for later
						nodeParentMap.put(id, columns[1].trim());

						taxonCounter++;

						if((taxonCounter % limitForTransaction) == 0){
							ncbiTaxonomyGraph.raw().commit();
						}
					}
				}
				reader.close();
				ncbiTaxonomyGraph.raw().commit();
				logger.log(Level.INFO, "done!");

				logger.log(Level.INFO, "reading names file...");
				//------------reading names file-----------------
				reader = new BufferedReader(new FileReader(namesDumpFile));
				int linesCounter = 0;
				while ((line = reader.readLine()) != null) {

					String[] columns = line.split("\\|");

					if (columns[columns.length - 1].trim().equals("scientific name")) {

						String taxId = columns[0].trim();
						String scientificNameSt = columns[1].trim();

						Optional<NCBITaxon<I,RV,RVT,RE,RET>> optionalTaxon = ncbiTaxonomyGraph.nCBITaxonIdIndex().getVertex(taxId);
						if(optionalTaxon.isPresent()){
							NCBITaxon<I,RV,RVT,RE,RET> taxon = optionalTaxon.get();
							taxon.set(ncbiTaxonomyGraph.NCBITaxon().scientificName, scientificNameSt);
						}else{
							Logger.getLogger(ImportNCBITaxonomy.class.getName()).log(Level.INFO, "Taxon with id: " + taxId + " was not found and its name could not be added... :(");
						}

						linesCounter++;
						if((linesCounter % limitForTransaction) == 0){
							ncbiTaxonomyGraph.raw().commit();
						}

					}

				}
				reader.close();
				ncbiTaxonomyGraph.raw().commit();
				logger.log(Level.INFO, "done!");

				logger.log(Level.INFO, "storing relationships...");

				linesCounter = 0;
				Set<String> nodesSet = nodeParentMap.keySet();
				for (String nodeTaxId : nodesSet) {

					String parentTaxId = nodeParentMap.get(nodeTaxId);

					Optional<NCBITaxon<I,RV,RVT,RE,RET>> optionalTaxon = ncbiTaxonomyGraph.nCBITaxonIdIndex().getVertex(nodeTaxId);
					NCBITaxon<I,RV,RVT,RE,RET> taxon = null;

					if(!optionalTaxon.isPresent()){
						Logger.getLogger(ImportNCBITaxonomy.class.getName()).log(Level.INFO, "Taxon with id: " + nodeTaxId + " could not be found... :(");
					}else{
						taxon = optionalTaxon.get();
						if (!nodeTaxId.equals(parentTaxId)) {
							Optional<NCBITaxon<I,RV,RVT,RE,RET>> optionalParentTaxon = ncbiTaxonomyGraph.nCBITaxonIdIndex().getVertex(parentTaxId);
							NCBITaxon<I,RV,RVT,RE,RET> parentTaxon;
							if(!optionalParentTaxon.isPresent()){
								Logger.getLogger(ImportNCBITaxonomy.class.getName()).log(Level.INFO, "Taxon with id: " + nodeTaxId + " could not be found... :(");
							}else{
								parentTaxon = optionalParentTaxon.get();
								parentTaxon.addOutEdge(ncbiTaxonomyGraph.NCBITaxonParent(), taxon);
							}

						}

						linesCounter++;
						if((linesCounter % limitForTransaction) == 0){
							ncbiTaxonomyGraph.raw().commit();
						}
					}

				}
				ncbiTaxonomyGraph.raw().commit();

				logger.log(Level.INFO, "Done!");


			} catch (Exception ex) {
				Logger.getLogger(ImportNCBITaxonomy.class.getName()).log(Level.SEVERE, null, ex);

			} finally {

				//committing last transaction
				ncbiTaxonomyGraph.raw().commit();
				//closing logger file handler
				fh.close();
				logger.log(Level.INFO, "Closing up inserter and index service....");
				// shutdown, makes sure all changes are written to disk
				ncbiTaxonomyGraph.raw().shutdown();

				try {

					//-----------------writing stats file---------------------
					long elapsedTime = System.nanoTime() - initTime;
					long elapsedSeconds = Math.round((elapsedTime / 1000000000.0));
					long hours = elapsedSeconds / 3600;
					long minutes = (elapsedSeconds % 3600) / 60;
					long seconds = (elapsedSeconds % 3600) % 60;

					statsBuff.write("Statistics for program ImportNCBITaxonomy:\nInput file: " + nodesDumpFile.getName()
							+ "\nThere were " + taxonCounter + " taxonomic units inserted.\n"
							+ "The elapsed time was: " + hours + "h " + minutes + "m " + seconds + "s\n");

					//---closing stats writer---
					statsBuff.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

}