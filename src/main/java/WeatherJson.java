import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherJson{
    private String main;
    private String description;
    private String icon;
    private Double temperature;
    private final static Map<String, String> weatherIcons = new HashMap<>();

    public WeatherJson(String main, Double temperature, String description, String icon) {
        this.main = main;
        this.temperature = temperature;
        this.description = description;
        this.icon = icon;
    }

    static {
        weatherIcons.put("Clear", "Ясно ☀");
        weatherIcons.put("Rain", "Дождь ☔");
        weatherIcons.put("Snow", "Снег ❄");
        weatherIcons.put("Clouds", "Облачно ☁");
    }

    public WeatherJson(){

    }

    public String getMain() { return main; }

    public Double getTemperature () { return temperature; }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public void setTemperature(String main) {
        this.temperature = temperature;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public static WeatherJson parseWeatherJson(String weatherJson) throws IOException {
        //String line = "\"coord\":{\"lon\":46.0333,\"lat\":51.5667},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"base\":\"stations\",\"main\":{\"temp\":17.35,\"feels_like\":16.79,\"temp_min\":17.35,\"temp_max\":17.35,\"pressure\":1021,\"humidity\":63,\"sea_level\":1021,\"grnd_level\":1004},\"visibility\":10000,\"wind\":{\"speed\":3.27,\"deg\":83,\"gust\":4.07},\"clouds\":{\"all\":48},\"dt\":1653908433,\"sys\":{\"country\":\"RU\",\"sunrise\":1653871569,\"sunset\":1653930057},\"timezone\":14400,\"id\":498677,\"name\":\"Saratov\",\"cod\":200}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode mainNode = objectMapper.readTree(weatherJson).get("main");
        String temperature = mainNode.get("temp").toString();
        String weatherNode = objectMapper.readTree(weatherJson).get("weather").toString();
        List<WeatherJson> weatherJsonList = objectMapper.readValue(weatherNode, new TypeReference<List<WeatherJson>>(){});
        String weatherDescription = weatherIcons.get(weatherJsonList.get(0).getMain());
        WeatherJson weather = new WeatherJson(mainNode.toString(), Double.parseDouble(temperature), weatherJsonList.get(0).getMain(), weatherDescription);
        return weather;
    }

    public boolean equals(WeatherJson w1){
        if ((this.getMain().equals(w1.getMain())) && (this.getTemperature() == w1.getTemperature())
                && (this.getDescription().equals(w1.getDescription())) && (this.getIcon().equals(w1.getIcon())))
                return true;
        return false;
    }


}
