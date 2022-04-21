import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    public final static Map<String, String> weatherIcons = new HashMap<>();
    private static final Logger log = Logger.getLogger(Bot.class);

    static {
        weatherIcons.put("Clear", "Ясно ☀");
        weatherIcons.put("Rain", "Дождь ☔");
        weatherIcons.put("Snow", "Снег ❄");
        weatherIcons.put("Clouds", "Облачно ☁");
    }

    final int RECONNECT_PAUSE = 10000;
    public WeatherJson weatherJson;
    // Contact BotFather https://telegram.me/BotFather
    String userName = "getTelegramBotUserName";
    String token = "getTelegramBotToken";
    WebServices webServices = new WebServices();
    // Клавиатура
    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
    Utils utils = new Utils();
    // Получить API можно на https://openweathermap.org/appid
    private String weatherApiKey = "getApiFromOpenWeatherMap";

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Look for messages");
        } catch (TelegramApiRequestException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("Receive new Update. updateID: " + update.getUpdateId());
        Long chatId = update.getMessage().getChatId();
        String inputText = update.getMessage().getText();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        getMessage(inputText, sendMessage);
    }

    private String getCovidStats() {

        // TODO проверить на наличие каждого из файлов
        // TODO скачать недостающее
        // TODO распарсить каждый из файлов? но если уже скачан и уже использовали,
        // TODO можно ли сохранять в переменную, чтобы не парсить?

        return utils.processCSVFile();
    }

    private WeatherJson getWeatherReport() throws IOException {
        String weatherURL = "https://api.openweathermap.org/data/2.5/weather?q=Saratov&units=metric&APPID=" + weatherApiKey;
        HttpURLConnection weatherConnection = webServices.getURLConnection(weatherURL);
        String weatherLine = webServices.getFileContent(weatherConnection);


        return weatherJson.parseWeatherJson(weatherLine);
    }

    private String getNews() throws IOException, FeedException {
        URL feedSource = new URL("https://sarnovosti.ru/feed/");
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedSource));
        SyndEntry firstEntry = (SyndEntry) feed.getEntries().get(0);
        SyndEntry entry;
        String result = "";
        for (int i = 0; i < 3; i++) {
            entry = (SyndEntry) feed.getEntries().get(i);

            result += entry.getTitle() + "\n" + entry.getLink() + "\n";
        }

        return result;
    }

    private void getMessage(String msg, SendMessage sendMessage) {
        ArrayList<KeyboardRow> buttons = new ArrayList<>();
        KeyboardRow keyBoardFirstRow = new KeyboardRow();
        KeyboardRow keyBoardSecondRow = new KeyboardRow();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        msg = msg.toLowerCase();

        if (msg.equals("меню")) {
            buttons.clear();
            keyBoardFirstRow.clear();
            keyBoardFirstRow.add("Погода");
            keyBoardFirstRow.add("Новости");
            keyBoardSecondRow.add("Статистика по COVID-19");
            buttons.add(keyBoardFirstRow);
            buttons.add(keyBoardSecondRow);
            replyKeyboardMarkup.setKeyboard(buttons);
        }
        if (msg.equals("погода")) {
            try {
                WeatherJson weatherReport = getWeatherReport();
                sendMessage.setText(weatherReport.getIcon() + "    " + weatherReport.getTemperature() + "\t°С");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (msg.equals("новости")) {
            try {
                sendMessage.setText(getNews());
            } catch (IOException | FeedException e) {
                e.printStackTrace();
            }
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (msg.equals("статистика по covid-19")) {
            sendMessage.setText("Число заболевших в Саратовской области  " + getCovidStats());
            try {
                execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }
}