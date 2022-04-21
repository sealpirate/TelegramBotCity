import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherJson {
    private final static Map<String, String> weatherIcons = new HashMap<>();

    static {
        weatherIcons.put("Clear", "Ясно ☀");
        weatherIcons.put("Rain", "Дождь ☔");
        weatherIcons.put("Snow", "Снег ❄");
        weatherIcons.put("Clouds", "Облачно ☁");
    }

    private String main;
    private String description;
    private String icon;
    private Double temperature;

    public WeatherJson(String main, Double temperature, String description, String icon) {
        this.main = main;
        this.temperature = temperature;
        this.description = description;
        this.icon = icon;
    }

    public static WeatherJson parseWeatherJson(String weatherJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonNode mainNode = objectMapper.readTree(weatherJson).get("main");
        String temperature = mainNode.get("temp").toString();
        String weatherNode = objectMapper.readTree(weatherJson).get("weather").toString();
        List<WeatherJson> weatherJsonList = objectMapper.readValue(weatherNode, new TypeReference<List<WeatherJson>>() {
        });
        String weatherDescription = weatherIcons.get(weatherJsonList.get(0).getMain());

        WeatherJson weather = new WeatherJson(mainNode.toString(), Double.parseDouble(temperature), weatherJsonList.get(0).getMain(), weatherDescription);
        return weather;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(String main) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
