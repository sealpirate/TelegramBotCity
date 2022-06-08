import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utils {
    WebServices webServices = new WebServices();

    private Date getPastDate(int day) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        return cal.getTime();
    }

    //reading telegram bot name, telegram token, weather API key from the file
    public String[] initAPITokens() throws FileNotFoundException {
        String[] APITokens = new String[3];
        FileInputStream fis = new FileInputStream("APItokens.txt");
        Scanner sc = new Scanner(fis);
        if (sc.hasNextLine())
            APITokens[0] = sc.nextLine();
        if (sc.hasNextLine())
            APITokens[1] = sc.nextLine();
        if (sc.hasNextLine())
            APITokens[2] = sc.nextLine();
        sc.close();
        return APITokens;
}

    //removing old csv files
    public void cleanDirectoryFromCSV() {
        try (Stream<Path> walk = Files.walk(Paths.get(""))) {
            walk.map(x -> x.toString())
                    .filter(f -> f.endsWith(".csv")).collect(Collectors.toList())
                    .forEach(f -> {
                        try {
                            Files.deleteIfExists(Paths.get(f));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String processCSVFile() {
        String previousDay;
        String twoDaysBefore;
        String csvFormat = ".csv";
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        DateFormat dateFormatOutput = new SimpleDateFormat("dd.MM.yy");
        String resultDate = dateFormatOutput.format(getPastDate(1));

        previousDay = dateFormat.format(getPastDate(1)) + csvFormat;
        File previousDayFile = new File(previousDay);
        twoDaysBefore = dateFormat.format(getPastDate(2)) + csvFormat;
        File twoDaysBeforeFile = new File(twoDaysBefore);
        webServices.downloadCSVFile(previousDayFile);
        webServices.downloadCSVFile(twoDaysBeforeFile);
        Integer parsingPrevDay = Integer.parseInt(parseCSVFile(previousDayFile));
        Integer parsingTwoDaysBefore = Integer.parseInt(parseCSVFile(twoDaysBeforeFile));
        if ((parsingPrevDay != null) && (parsingTwoDaysBefore != null)) {
            return resultDate + " :   " + String.valueOf(parsingPrevDay - parsingTwoDaysBefore);
        } else {
            return resultDate + ": Нет информации";
        }
    }

    public String parseCSVFile(File file) {
        Scanner sc = null;
        try {
            sc = new Scanner(file);
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                if (line.startsWith(",,Saratov Oblast,")) {
                    String[] words = line.split(",");
                    return words[7];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }
        // Если нет информации в одном файле
        return "Нет информации";
    }
}
