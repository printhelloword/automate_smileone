package bot.smileone.utility;

import bot.smileone.SmileOneApplication;

public class InputValidator {

    public static boolean isInputNumeric(String input) {
        boolean status = false;
        try {
            Long.parseLong(input);
            status = true;
        } catch (Exception e) {
            SmileOneApplication.logger.info(e.getMessage());
        }
        return status;
    }
}
