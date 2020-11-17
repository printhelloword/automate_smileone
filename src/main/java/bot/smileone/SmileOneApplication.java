package bot.smileone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmileOneApplication {
//	public static Logger logger = LogManager.getRootLogger();
public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SmileOneApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SmileOneApplication.class, args);
	}

}
