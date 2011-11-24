package edu.indiana.d2i.datacatalog.dashboard.util;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Grep {
    // Charset and decoder for ISO-8859-15
    private static Charset charset = Charset.forName("ISO-8859-15");

    private static CharsetDecoder decoder = charset.newDecoder();

    // Pattern used to parse lines
    private static Pattern linePattern = Pattern.compile(".*\r?\n");

    // The input pattern that we're looking for
    private static Pattern pattern;

    // Compile the pattern from the command line
    //
    private static void compile(String pat) {
        try {
            pattern = Pattern.compile(pat);
        } catch (PatternSyntaxException x) {
            System.err.println(x.getMessage());
            System.exit(1);
        }
    }

    // Use the linePattern to break the given CharBuffer into lines, applying
    // the input pattern to each line to see if we have a match
    //
    private static void grep(File f, CharBuffer cb) {
        Matcher lm = linePattern.matcher(cb); // Line matcher
        Matcher pm = null; // Pattern matcher
        int lines = 0;
        while (lm.find()) {
            lines++;
            CharSequence cs = lm.group(); // The current line
            if (pm == null)
                pm = pattern.matcher(cs);
            else
                pm.reset(cs);
            if (pm.find())
                System.out.print(f + ":" + lines + ":" + cs);
            if (lm.end() == cb.limit())
                break;
        }
    }

    // Search for occurrences of the input pattern in the given file
    //
    private static void grep(File f) throws IOException {

        // Open the file and then get a channel from the stream
        FileInputStream fis = new FileInputStream(f);
        FileChannel fc = fis.getChannel();

        // Get the file's size and then map it into memory
        int sz = (int) fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);

        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);

        // Perform the search
        grep(f, cb);

        // Close the channel and the stream
        fc.close();
    }

    public static void main(String[] args) {
        Runtime r = Runtime.getRuntime();

        try {
            /*
            * Here we are executing the UNIX command ls for directory listing.
            * The format returned is the long format which includes file
            * information and permissions.
            */
            Process p = r.exec("grep Cataloger /Users/milindapathirage/Workspace/IU/data-catalog/dashboard/grep-test/performance.log");
            InputStream in = p.getInputStream();
            BufferedInputStream buf = new BufferedInputStream(in);
            InputStreamReader inread = new InputStreamReader(buf);
            BufferedReader bufferedreader = new BufferedReader(inread);

            // Read the ls output
            String line;
            while ((line = bufferedreader.readLine()) != null) {
                System.out.println(line);
            }
            // Check for ls failure
            try {
                if (p.waitFor() != 0) {
                    System.err.println("exit value = " + p.exitValue());
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            } finally {
                // Close the InputStream
                bufferedreader.close();
                inread.close();
                buf.close();
                in.close();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
