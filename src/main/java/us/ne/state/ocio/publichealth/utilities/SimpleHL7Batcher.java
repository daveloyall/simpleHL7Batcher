package us.ne.state.ocio.publichealth.utilities;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class SimpleHL7Batcher {
	private static final Logger log = LogManager.getLogger(SimpleHL7Batcher.class);

	public static void main(String[] args) throws IOException {
		SimpleHL7Batcher shb = new SimpleHL7Batcher();
		JCommander jc = new JCommander(shb, args);
		jc.setProgramName("java -jar simpleHL7Batcher.jar");
		if (shb.run() == 42)
			jc.usage();
	}

	@Parameter(names = "--input", description = "Input directory")
	private String input;

	@Parameter(names = "--output", description = "Output directory")
	private String output;

	@Parameter(names = "--archive", description = "Archive directory")
	private String archive;

	@Parameter(names = "--filter", description = "Filename filter")
	private String fileSpec;
	
	@Parameter(names = "--cooldown", description = "Ignore files modified less than [cooldown] seconds ago. (This avoids partial files.)")
	private int coolDownInSeconds = 30;

	@Parameter(names = "--batchsize", description = "Number of files per batch")
	private int batchSize = 200;

	@Parameter(names = "--help", help = true, description = "This help message.", hidden = true)
	private boolean help;

	private int run() throws IOException {
		if (help || anyNull(input, output, archive)) {
			return 42; //magic number tells caller to display usage;
		}

		TreeSet<Path> files = new TreeSet<Path>(); //defined in this file.
		
		long cutoffTimeInMillis = System.currentTimeMillis() - coolDownInSeconds * 1000;

		long duration = System.currentTimeMillis();
		
		for(Path p : fileSpec != null ? Files.newDirectoryStream(Paths.get(input),fileSpec) : Files.newDirectoryStream(Paths.get(input))) {
			if (!Files.isDirectory(p) && Files.getLastModifiedTime(p).toMillis() < cutoffTimeInMillis)	files.add(p);
		}
		duration = System.currentTimeMillis() - duration;
		log.info("Found {} suitable files in {} and it took {} millis.",files.size(),input,duration);
		
		int inputCounter = 0;
		int outputCounter = 0;
		Path outputPath = null;
		long batchNumber = 0;
		
		Path archivePath = null;
		for (Path p : files) {
			if(inputCounter >= batchSize) inputCounter = 0;
			if(inputCounter == 0) {
				batchNumber = System.currentTimeMillis();
				archivePath = Files.createDirectory(Paths.get(archive + FileSystems.getDefault().getSeparator() + batchNumber));
				outputPath=getNewOutputFile(batchNumber);
			}
			try {
				Files.write(outputPath, Files.readAllLines(p, StandardCharsets.UTF_8), StandardCharsets.UTF_8,
		            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				//TODO what happens when the read and write work but the move doesn't?				
				Files.move(p, archivePath.resolve(p.getFileName()));
				outputCounter++;
				inputCounter++;
			} catch (MalformedInputException e) {
				log.error("Error reading file '{}'.  Leaving it there!",p.getFileName());
			}
		}
		log.debug("created {} batch files in {}.",outputCounter,output);
		return 0;
	}

	private Path getNewOutputFile(long batchNumber) {
		return Paths.get(output + FileSystems.getDefault().getSeparator() + "batch." + batchNumber + ".hl7");
	}

	private static boolean anyNull(Object... args) {
		for (Object o : args) {
			if (o == null)
				return true;
		}
		return false;
	}
	
	//TODO it's slow.  One order of magnitude slower than without this comparator.
	class ModTimeThenFilenameComparator<T> implements Comparator<Path> {
		@Override
		public int compare(Path o1, Path o2) {
	        try {
	        	int firstTry = Files.getLastModifiedTime(o1).compareTo(Files.getLastModifiedTime(o2));

	        	if (firstTry != 0)
	        		return firstTry; 
	        	else
	        		return o1.getFileName().compareTo(o2.getFileName());
	        } catch (IOException e) {
	            log.error("Sorting file list failed.",e);
	            return 0;
	        }
	    }

	}		
}

