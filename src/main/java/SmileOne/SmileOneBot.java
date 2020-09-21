package SmileOne;

import SmileOne.controller.TransactionController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SmileOneBot {

    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SmileOneBot.class);

        public static void main(String[] args) {
            SpringApplication.run(SmileOneBot.class, args);
        }

    }