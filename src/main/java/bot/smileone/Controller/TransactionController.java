package bot.smileone.Controller;

import bot.smileone.SmileOneApplication;
import bot.smileone.entity.Request;
import bot.smileone.entity.SmileOneBot;
import bot.smileone.entity.Player;
import bot.smileone.entity.Voucher;
import bot.smileone.model.Inboxes;
import bot.smileone.model.Outboxes;
import bot.smileone.pojo.ResponsePojo;
import bot.smileone.pojo.VoucherPojo;
import bot.smileone.utility.DBUtilInboxes;
import bot.smileone.utility.DBUtilOutboxes;
import bot.smileone.utility.InputValidator;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    public TransactionController() {
    }

    @GetMapping("/trx/{playerId}/{denom}/{trxId}")
    public String topUp(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId)
            throws IOException {

        Request request = Request.ofParams(trxId, denom, playerId);

        ProcessRequest processRequest = ProcessRequest.ofRequest(request);
        ResponsePojo responsePojo = processRequest.getResponsePojo();

        JSONObject jsonObject = new JSONObject(responsePojo);
        String response = jsonObject.toString();

        return response;
    }



}
