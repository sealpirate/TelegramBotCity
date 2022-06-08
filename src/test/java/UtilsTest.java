import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {
    private final WebServices webServices = new WebServices();
    private final File csvFile = new File("05-27-2022.csv");
    private final Utils utils = new Utils();


    @BeforeEach
    public void downloadCSV(){
        webServices.downloadCSVFile(csvFile);
    }

    @Test
    public void parseCSVFileTest(){
        assertEquals("252017", utils.parseCSVFile(csvFile));
    }

}
