import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class Utils {
    WebServices webServices = new WebServices();

    private Date getPastDate(int day) {
        final Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -day);
        return cal.getTime();
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
