package simpleHL7Batcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import us.ne.state.ocio.publichealth.utilities.SimpleHL7Batcher;

public class TestRebatching extends TestCase {
	private static final Logger log = LogManager.getLogger(TestRebatching.class);
	
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
	private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	protected void setUp() throws Exception {
		super.setUp();
		DirectoryStream<Path> glob = Files.newDirectoryStream(Paths.get("d:/projects/rebatcher/setup/"), "*");
		for (Path p : glob) {
			log.debug(p);
			Files.copy(p,Paths.get("d:/projects/rebatcher/input/" + p.getFileName()));
		}
		System.setOut(new PrintStream(outContent));
		System.setErr(new PrintStream(errContent));
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		System.setOut(null);
		System.setErr(null);
	}
	
	@Test
	public void testRebatching() throws IOException {
		String[] args = {"--archive" ,"D:/projects/rebatcher/archive"
						,"--input","D:/projects/rebatcher/input"
						,"--output","D:/projects/rebatcher/output"
						,"--rebatch"};
		SimpleHL7Batcher.main(args);
		assertEquals("Need to use the countLines function to test the output files...",
				"Does each output file have <batchsize> or less messages ?");

	}

	//From http://stackoverflow.com/questions/453018/number-of-lines-in-a-file-in-java#453067
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
}
