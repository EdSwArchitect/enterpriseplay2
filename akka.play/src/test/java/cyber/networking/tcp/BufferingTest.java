package cyber.networking.tcp;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

/**
 * Created by ebrown on 2/17/2017.
 */
public class BufferingTest {
    @Test
    public void testBuffering() {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/test_data.txt");

            FileChannel channel = fis.getChannel();

            String preSynch = null;
            String postSynch = null;

            MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size());

            Charset utf8 = Charset.forName("utf-8");

            CharBuffer buf = utf8.decode(mbb);
            String data = buf.toString();

            channel.close();

            boolean synching = true;

            StringBuilder buffer = new StringBuilder();

            // skip to new lines
            if (synching) {
                int recSepIndex = data.indexOf("\n\n");

                if (recSepIndex >= 0) {
                    preSynch = data.substring(0, recSepIndex);

                    data = data.substring(recSepIndex+2);
                    synching = false;

                    System.out.println("***>>> Synchronized new lines");
                } // if (recSepIndex >= 0) {

            } // if (synching) {

            // only do the rest if you are not dropping data
            if (!synching) {
                buffer.append(data);

                data = buffer.toString();

                buffer.setLength(0);

                int endIndex = data.indexOf("\n\n");
                int startIndex = 0;

                while (endIndex >= 0) {
                    String line = data.substring(startIndex, endIndex);

                    System.out.println("line: '" + line + "'");

                    startIndex = endIndex+2;
                    endIndex = data.indexOf("\n\n", startIndex);
                } // while (endIndex >= 0) {

                if (endIndex < 0) {
                    buffer.append(data.substring(startIndex));
                }
            }

            System.out.println("\n\nPreSynch: '" + preSynch + "'");
            System.out.println("Leftover: '" + buffer.toString() + "'");
        } catch (FileNotFoundException e) {

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            System.err.println("Current directory: " + System.getProperty("user.dir"));

            Assert.fail(sw.toString());
        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            Assert.fail(sw.toString());
        }
    }
}
