package bot.smileone.Controller;

import bot.smileone.SmileOneApplication;
import bot.smileone.entity.Request;
import bot.smileone.pojo.ResponsePojo;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RequestListener {

    @GetMapping("/trx/{playerId}/{denom}/{trxId}")
    public String processRequestAndAndGetResponseByTransaction(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId)
            throws IOException {

        Request request = Request.ofParams(trxId, denom, playerId);
        printRequest(request);
        TransactionController processRequest = TransactionController.ofRequest(request);
        ResponsePojo responsePojo = processRequest.getResponsePojo();

        return new JSONObject(responsePojo).toString();
    }

    private void printRequest(Request request) {
        SmileOneApplication.logger.info("======Incoming Request========= : trx/" + request.toString());
    }



}
