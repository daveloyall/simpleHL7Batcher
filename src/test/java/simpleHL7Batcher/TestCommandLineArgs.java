package simpleHL7Batcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;
import us.ne.state.ocio.publichealth.utilities.SimpleHL7Batcher;

//TODO there are no meaningful tests in here.
public class TestCommandLineArgs extends TestCase {
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
	public void testFooBar() throws IOException {
		String[] args = {"foo","bar"};
		SimpleHL7Batcher.main(args);
		assertEquals("",outContent.toString());
		assertEquals("Was passed main parameter 'foo' but no main parameter was defined\r\n"+
				"Try passing --help for usage information.\r\n",errContent.toString());
	}
	
	@Test
	public void testEmpty() throws IOException {
		String[] args = {};
		SimpleHL7Batcher.main(args);
		assertEquals("Usage: java -jar simpleHL7Batcher.jar [options]\n" +
				"  Options:\n" +
				"    --archive\n" +
				"       Archive directory\n" +
				"    --batchsize\n" +
				"       Number of files per batch\n" +
				"       Default: 200\n" +
				"    --cooldown\n" +
				"       Ignore files modified less than [cooldown] seconds ago. (This avoids\n" +
				"       partial files.)\n" +
				"       Default: 30\n" +
				"    --filter\n" +
				"       Filename filter\n" +
				"    --input\n" +
				"       Input directory\n" +
				"    --output\n" +
				"       Output directory\n\r\n",outContent.toString());
		assertEquals("",errContent.toString());
	}
	
	@Test
	public void testFoo() throws IOException {
		String[] args = {"foo"};
		SimpleHL7Batcher.main(args);
		assertEquals("",outContent.toString());
		assertEquals("Was passed main parameter 'foo' but no main parameter was defined\r\n"+
				"Try passing --help for usage information.\r\n",errContent.toString());
		
	}
	
	//TODO Let's parameterize this somehow!
	//There are two ways to think about parameterizing this.
	//One: pass in strings from a config file rather than ""+"" string inline.
	//Two: That output is generated, anyway...  Just check for the known
	//     command line options.  Like, output.matches("--archive"), etc.
	@Test
	public void testHelp() throws IOException {
		String[] args = {"--help"};
		SimpleHL7Batcher.main(args);
		assertEquals("Usage: java -jar simpleHL7Batcher.jar [options]\n" +
				"  Options:\n" +
				"    --archive\n" +
				"       Archive directory\n" +
				"    --batchsize\n" +
				"       Number of files per batch\n" +
				"       Default: 200\n" +
				"    --cooldown\n" +
				"       Ignore files modified less than [cooldown] seconds ago. (This avoids\n" +
				"       partial files.)\n" +
				"       Default: 30\n" +
				"    --filter\n" +
				"       Filename filter\n" +
				"    --input\n" +
				"       Input directory\n" +
				"    --output\n" +
				"       Output directory\n\r\n"
				,outContent.toString());
		assertEquals("",errContent.toString());
	}

}
