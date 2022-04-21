import org.apache.http.ConnectionClosedException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

public class WebServices {
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static String urlStats = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";

    static long download(String url, String fileName) throws IOException {
        try (InputStream in = URI.create(url).toURL().openStream()) {
            return Files.copy(in, Paths.get(fileName));
        }
    }

    private Date yesterday(int day) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        return cal.getTime();
    }

    public void downloadCSVFile(File file) {
        if (!(file.exists())) {
            try {
                download(urlStats + file.getName(), file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public HttpURLConnection getURLConnection(String requestedURL) throws IOException {
        URL urlObject = new URL(requestedURL);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode == 404) {
            throw new ConnectionClosedException("Ошибка 404");
        }
        return connection;
    }

    public String getFileContent(HttpURLConnection weatherConnection) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(weatherConnection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
