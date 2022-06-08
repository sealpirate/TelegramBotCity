import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class WeatherJsonTest {
    private final WeatherJson weatherJson = new WeatherJson();

    @Test
    void testParseJson() throws IOException {
        String line = "{\"coord\":{\"lon\":46.0333,\"lat\":51.5667},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"base\":\"stations\",\"main\":{\"temp\":17.35,\"feels_like\":16.79,\"temp_min\":17.35,\"temp_max\":17.35,\"pressure\":1021,\"humidity\":63,\"sea_level\":1021,\"grnd_level\":1004},\"visibility\":10000,\"wind\":{\"speed\":3.27,\"deg\":83,\"gust\":4.07},\"clouds\":{\"all\":48},\"dt\":1653908433,\"sys\":{\"country\":\"RU\",\"sunrise\":1653871569,\"sunset\":1653930057},\"timezone\":14400,\"id\":498677,\"name\":\"Saratov\",\"cod\":200}";
        WeatherJson weather = weatherJson.parseWeatherJson(line);
        System.out.println(weather.getDescription());
        String mainJson = "{\"temp\":17.35,\"feels_like\":16.79,\"temp_min\":17.35,\"temp_max\":17.35,\"pressure\":1021,\"humidity\":63,\"sea_level\":1021,\"grnd_level\":1004}";
        assertEquals(mainJson, weatherJson.parseWeatherJson(line).getMain());
        assertEquals(17.35, weatherJson.parseWeatherJson(line).getTemperature());
        assertEquals("Clouds", weatherJson.parseWeatherJson(line).getDescription());

    }


}
