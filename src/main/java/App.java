import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;

public class App {
    private static final Logger log = Logger.getLogger(App.class);

    public static void main(String[] args) {
        log.info("Инициализация API");
        ApiContextInitializer.init();
        Bot saratovBot = new Bot();
        saratovBot.botConnect();
    }
}