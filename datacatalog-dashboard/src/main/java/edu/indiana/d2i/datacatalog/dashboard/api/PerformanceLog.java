package edu.indiana.d2i.datacatalog.dashboard.api;

import edu.indiana.d2i.datacatalog.dashboard.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.Properties;

@Path("/perflog")
public class PerformanceLog {
    private static Log log = LogFactory.getLog(PerformanceLog.class);

    @Context
    ServletContext context;

    @GET
    @Path("currentstate")
    @Produces(MediaType.TEXT_PLAIN)
    public String getCurrentState() throws IOException {
        String dashboardConfFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");

        Properties dashboardProps = new Properties();
        dashboardProps.load(new FileInputStream(new File(dashboardConfFilePath)));

        if (dashboardProps.get(Constants.PROP_DATACAT_PERF_LOG) != null) {
            String tail = tail(new File(((String) dashboardProps.get(Constants.PROP_DATACAT_PERF_LOG)).trim()));
            if (tail != null) {
                String[] tailElements = tail.split("\\|");

                return "In the " + tailElements[1] + " queue, processing data product " + tailElements[4] +
                        " of catalog " + tailElements[3];
            }

            return "Error reading performance log.";
        }

        return "Cannot find performance log file in configuration.";
    }

    @GET
    @Path("history")
    @Produces(MediaType.APPLICATION_JSON)
    public String getDataCrawlingHistory() throws IOException {
        JSONObject crawlHistory = new JSONObject();
        crawlHistory.put("dateTimeFormat", "Gregorian");
        org.json.simple.JSONArray events = new JSONArray();

        String dashboardConfFilePath = context.getRealPath("/WEB-INF/conf/dashboard-conf.properties");

        Properties dashboardProps = new Properties();
        dashboardProps.load(new FileInputStream(new File(dashboardConfFilePath)));

        if (dashboardProps.get(Constants.PROP_DATACAT_PERF_LOG) != null) {
            //String tail = tail2(new File(((String) dashboardProps.get(Constants.PROP_DATACAT_PERF_LOG)).trim()), 2);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            tail3(new File(((String) dashboardProps.get(Constants.PROP_DATACAT_PERF_LOG)).trim()), bos, 100);
            String tail = bos.toString("utf-8");
            if (tail != null) {
                log.info(tail);
                String[] tailElements = tail.split("\n");
                for(String element : tailElements){
                    String[] items = element.split("\\|");
                    events.add(createEvent(items[0], "In queue:" + items[1] + " processing data product " + items[4] + " of catalog:" + items[3]));
                }
                crawlHistory.put("events", events);
                return crawlHistory.toJSONString();
            }

            return "{'error': 'Error reading performance log.'}";
        }

        return "{'error': 'Cannot find performance log file in configuration.'}";
    }

    private JSONObject createEvent(String date, String title){
        JSONObject event = new JSONObject();
        event.put("start", date);
        event.put("title", title);
        event.put("durationEvent", false);
        return event;
    }

    public String tail(File file) {
        try {
            RandomAccessFile fileHandler = new RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (filePointer == fileLength) {
                        continue;
                    } else {
                        break;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer == fileLength - 1) {
                        continue;
                    } else {
                        break;
                    }
                }

                sb.append((char) readByte);
            }

            return sb.reverse().toString();
        } catch (java.io.FileNotFoundException e) {
            log.error("Log file not found.", e);
            return null;
        } catch (java.io.IOException e) {
            log.error("Error reading log file.", e);
            return null;
        }
    }

    public String tail2(File file, int lines) {
        try {
            java.io.RandomAccessFile fileHandler = new java.io.RandomAccessFile(file, "r");
            long fileLength = file.length() - 1;
            StringBuilder sb = new StringBuilder();
            int line = 0;

            for (long filePointer = fileLength; filePointer != -1; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();

                if (readByte == 0xA) {
                    if (line == lines) {
                        if (filePointer == fileLength) {
                            continue;
                        } else {
                            break;
                        }
                    }
                } else if (readByte == 0xD) {
                    line = line + 1;
                    if (line == lines) {
                        if (filePointer == fileLength - 1) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
                sb.append((char) readByte);
            }

            sb.deleteCharAt(sb.length() - 1);
            String lastLine = sb.reverse().toString();
            return lastLine;
        } catch (java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void tail3(File src, OutputStream out, int maxLines) throws FileNotFoundException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(src));
        String[] lines = new String[maxLines];
        int lastNdx = 0;
        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            if (lastNdx == lines.length) {
                lastNdx = 0;
            }
            lines[lastNdx++] = line;
        }

        OutputStreamWriter writer = new OutputStreamWriter(out);
        for (int ndx = lastNdx; ndx != lastNdx - 1; ndx++) {
            if (ndx == lines.length) {
                ndx = 0;
            }
            writer.write(lines[ndx]);
            writer.write("\n");
        }

        writer.flush();
    }
}
