package bot.smileone.Controller;

import bot.smileone.SmileOneApplication;
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

    private static final String STATUS_FAILED = "Failed";
    private static final String STATUS_SUCCESS = "Success";
    private static final String TAG_STATUS_DEFAULT = STATUS_FAILED;

    private static final String TAG_TRX_ID = "Trx ID";
    private static final String TAG_PLAYER_ID = "Player ID";

    private static final String MSG_ERROR_TRX_PLAYER_ID = "TrxID/PlayerID Tidak Valid";
    private static final String MSG_ERROR_TRX_ID_EXISTS = "TrxID Sudah Terdapat Di Database";
    private static final String MSG_ERROR_DENOM_INVALID = "Denom Tidak Valid";
    private static final String MSG_ERROR_INBOX_UNSAVED = "Failed Save to Inbox";
    private static final String MSG_ERROR_OUTBOX_UNSAVED = "Failed Save to Outbox";
    private static final String MSG_ERROR_UNKNOWN = "Unknown";

    private static final String DENOM_TYPE_DIAMOND = "diamond";
    private static final String DENOM_TYPE_MEMBER = "member";

    private final Map<String, String> denomsMap = getDenoms();
    // ... insert stuff into map
    // eg: map.add("something", new MyObject());

    private SmileOneBot republic;

    private static final boolean STATUS_DEFAULT_TRUE = true;
    private static final boolean STATUS_DEFAULT_FALSE = false;

    private Integer inboxId;

    private ResponsePojo responsePojo;
    private VoucherPojo voucherPojo;
    private String response;

    private String playerId;

    private JSONObject jsonObject;

    @GetMapping("/trx/{playerId}/{denom}/{trxId}")
    public String topUp(
            @PathVariable String playerId,
            @PathVariable String denom,
            @PathVariable String trxId,
            HttpServletRequest request)
            throws IOException {

        printRequest(request.getRequestURI());
        initResponsePojo(trxId, TAG_STATUS_DEFAULT, MSG_ERROR_TRX_PLAYER_ID);
        initVoucherPojo(playerId, denom);

        /*try {
            if (!areTrxIdAndPlayerIdNumeric(trxId, playerId)) {
                responsePojo = ResponsePojo.byWithTrxIdStatusMessage(trxId, TAG_STATUS_DEFAULT, MSG_ERROR_TRX_PLAYER_ID);
            } else {
                responsePojo = ResponsePojo.byWithTrxIdStatusMessage(trxId, TAG_STATUS_DEFAULT, MSG_ERROR_UNKNOWN);
                if (isTrxIdAlreadyExists(trxId))
                    updateResponseMessage(MSG_ERROR_TRX_ID_EXISTS);
                else {
                    if (!isDenomValid(denom))
                        updateResponseMessage(MSG_ERROR_DENOM_INVALID);
                    else {
                        voucherPojo = VoucherPojo.byPlayerIdAndDenom(playerId, denom);
                        saveToInbox(createNewInbox(trxId, playerId, denom, request));
                        CheckInboxisSavedAndStartTransaction();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            SmileOneApplication.logger.info("Transaction Failed");
        }*/

        try {
            if (!areTrxIdAndPlayerIdValid()){
                updateResponseMessage(MSG_ERROR_TRX_PLAYER_ID);
            }else{
                if (isTrxIdAlreadyExists(responsePojo.getTrxId()))
                    updateResponseMessage(MSG_ERROR_TRX_ID_EXISTS);
                else {
                    if (!isDenomValid(voucherPojo.getDenom()))
                        updateResponseMessage(MSG_ERROR_DENOM_INVALID);
                    else {
                        saveToInbox(createNewInbox(trxId, playerId, denom, request));
                        checkInboxisSavedAndStartTransaction();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            SmileOneApplication.logger.info("Transaction Failed");
        }

        jsonObject = new JSONObject(responsePojo);
        response = jsonObject.toString();

        printResponseLogAndSaveOutbox();

        return response;
    }

    private boolean areTrxIdAndPlayerIdValid() {
        return (InputValidator.isInputNumeric(responsePojo.getTrxId()) && InputValidator.isInputNumeric(voucherPojo.getPlayerId()));
    }

    private void initVoucherPojo(String playerId, String denom) {
        voucherPojo = VoucherPojo.byPlayerIdAndDenom(playerId, denom);
    }

    private void initResponsePojo(String trxId, String defaultStatus, String defaultMessage) {
        responsePojo = ResponsePojo.byWithTrxIdStatusMessage(trxId, defaultStatus, defaultMessage);

    }

    private void printRequest(String requestURI) {
        SmileOneApplication.logger.info("Incoming Request " + requestURI);
    }

    private void printResponseLogAndSaveOutbox() {
        SmileOneApplication.logger.info("Returning JSON : ");
        SmileOneApplication.logger.info(jsonObject.toString(4));
        saveToOutbox(createNewBoutbox());
    }

    private void checkInboxisSavedAndStartTransaction() {
        if (isSaveToInboxSucceed()) {
            Player player = Player.byMergedPlayerAndZoneId(voucherPojo.getPlayerId());
            Voucher voucher = Voucher.byPlayerAndDenom(player, denomsMap.get(voucherPojo.getDenom()));
            SmileOneApplication.logger.info("Denom Element Locator " + denomsMap.get(voucherPojo.getDenom()));
            SmileOneBot smileOneBot = SmileOneBot.withVoucher(voucher);
            Map<Boolean, String> transactionResult = smileOneBot.processTopTupAndGetMessage();
            if (transactionResult != null) {
                responsePojo.setVoucher(voucherPojo);
                for (Map.Entry<Boolean, String> entry : transactionResult.entrySet()) {
                    responsePojo.setStatus((entry.getKey()) ? STATUS_SUCCESS : STATUS_FAILED);
                    responsePojo.setMessage(entry.getValue());
                }
            }
        }
    }

    private Inboxes createNewInbox(String trxId, String playerId, String denom, HttpServletRequest request) {
        String message = trxId + "#" + denom + "#" + playerId;
        return new Inboxes(message, request.getRequestURI(), 0, getJavaUtilDate(), trxId);
    }

    private void saveToOutbox(Outboxes newBoutbox) {
        try {
            Integer outboxId = DBUtilOutboxes.saveOutbox(newBoutbox);
        } catch (Exception e) {
            SmileOneApplication.logger.info(e.getMessage());
            SmileOneApplication.logger.info("Failed Save To Outbox");
        }
    }

    private void saveToInbox(Inboxes newInbox) {
        try {
            inboxId = DBUtilInboxes.saveInbox(newInbox);
        } catch (Exception e) {
            e.printStackTrace();
            SmileOneApplication.logger.info("Failed Save To DB");
        }
    }

    private Outboxes createNewBoutbox() {
        return new Outboxes(response, null, getJavaUtilDate(), inboxId);
    }

    private boolean isSaveToInboxSucceed() {
        return inboxId != null;
    }

    private boolean isDenomValid(String denom) {
        if (denomsMap.containsKey(denom))
            return true;
        else return false;
    }

    private java.util.Date getJavaUtilDate() {
        return new java.util.Date();
    }

//    private java.sql.Date getjavaSqlDate(){
//        return new java.sql.Date(new java.util.Date().getTime());
//    }

    private boolean isTrxIdAlreadyExists(String trxId) {
        return DBUtilInboxes.isTrxIdExists(trxId);
    }

    private boolean areTrxIdAndPlayerIdNumeric(String trxId, String playerId) {
        return (InputValidator.isInputNumeric(trxId) && InputValidator.isInputNumeric(playerId));
    }

    private void updateResponseMessage(String message) {
        responsePojo.setMessage(message);
    }

    private static Map<String, String> getDenoms() {
        Map<String, String> denomsMap = new HashMap<>();

        denomsMap.put("86", ".PcliF-em2");
        denomsMap.put("172", ".PcliS-em2");
        denomsMap.put("257", ".PcliT-em2");
        denomsMap.put("706", ".PcliFo-em2");
        denomsMap.put("2195", ".PcliFif-em2");
        denomsMap.put("3688", ".PcliSix-em2");
        denomsMap.put("5532", ".PcliSev-em2");
        denomsMap.put("9288", ".PcliEig-em2");
        denomsMap.put("twilight", ".PcliEle-em2");
        denomsMap.put("starlight", ".PcliNin-em2");
        denomsMap.put("starlightplus", ".PcliTwl-em2");

        return denomsMap;
    }

}
