package bot.smileone.Controller;

import bot.smileone.SmileOneApplication;
import bot.smileone.entity.Player;
import bot.smileone.entity.Request;
import bot.smileone.entity.SmileOneBot;
import bot.smileone.entity.Voucher;
import bot.smileone.model.Inboxes;
import bot.smileone.model.Outboxes;
import bot.smileone.pojo.ResponsePojo;
import bot.smileone.pojo.VoucherPojo;
import bot.smileone.utility.DBUtilInboxes;
import bot.smileone.utility.DBUtilOutboxes;
import bot.smileone.utility.InputValidator;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TransactionController {
    private static final String STATUS_FAILED = "Failed";
    private static final String STATUS_SUCCESS = "Success";

    private static final String MSG_ERROR_TRX_PLAYER_ID = "TrxID/PlayerID Tidak Valid";
    private static final String MSG_ERROR_TRX_ID_EXISTS = "TrxID Sudah Terdapat Di Database";
    private static final String MSG_ERROR_DENOM_INVALID = "Denom Tidak Valid";

    private final Map<String, String> denomsMap = getDenoms();

    private String trxId, playerId, denom;

    private Integer inboxId;

    private ResponsePojo responsePojo = new ResponsePojo();
    private VoucherPojo voucherPojo = new VoucherPojo();

    public static TransactionController ofRequest(Request request) {
        return new TransactionController(request.getTrxId(), request.getPlayerId(), request.getDenom());
    }

    public TransactionController(String trxId, String playerId, String denom) {
        this.trxId = trxId;
        this.playerId = playerId;
        this.denom = denom;
    }

    public ResponsePojo getResponsePojo() {
        initResponsePojo(trxId);
        initVoucherPojo(playerId, denom);

        try {
            validateRequestAndStartTransaction();
        } catch (Exception e) {
            e.printStackTrace();
            SmileOneApplication.logger.info("Transaction Failed");
        }

        printResponseJsonAndSaveToOutbox(new JSONObject(responsePojo));

        return responsePojo;
    }

    private void validateRequestAndStartTransaction() {
        if (!areTrxIdAndPlayerIdValid()) {
            updateResponseMessage(MSG_ERROR_TRX_PLAYER_ID);
        } else {
            if (isTrxIdAlreadyExists(responsePojo.getTrxId()))
                updateResponseMessage(MSG_ERROR_TRX_ID_EXISTS);
            else {
                if (!isDenomValid(voucherPojo.getDenom()))
                    updateResponseMessage(MSG_ERROR_DENOM_INVALID);
                else {
                    saveToInbox(createNewInbox(trxId, playerId, denom));
                    checkInboxisSavedAndStartTransaction();
                }
            }
        }
    }

    private boolean areTrxIdAndPlayerIdValid() {
        return (InputValidator.isInputNumeric(responsePojo.getTrxId()) && InputValidator.isInputNumeric(voucherPojo.getPlayerId()));
    }

    private void initResponsePojo(String trxId) {
        responsePojo.setTrxId(trxId);
        responsePojo.setMessage("");
        responsePojo.setStatus(STATUS_FAILED);
    }

    private void initVoucherPojo(String playerId, String denom) {
        voucherPojo.setPlayerId(playerId);
        voucherPojo.setDenom(denom);
    }

    private void printRequest(Request request) {
        SmileOneApplication.logger.info("Incoming Request " + request.toString());
    }

    private void printResponseJsonAndSaveToOutbox(JSONObject jsonObject) {
        SmileOneApplication.logger.info("Returning JSON : ");
        SmileOneApplication.logger.info(jsonObject.toString(4));
        if (inboxId != null)
            saveToOutbox(createNewBoutbox());
    }

    private void checkInboxisSavedAndStartTransaction() {
        if (isSaveToInboxSucceed()) {
            Player player = Player.byMergedPlayerAndZoneId(voucherPojo.getPlayerId());
            Voucher voucher = Voucher.byPlayerAndDenom(player, denomsMap.get(voucherPojo.getDenom()));
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

    private Inboxes createNewInbox(String trxId, String playerId, String denom) {
        String message = trxId + "#" + denom + "#" + playerId;
        return new Inboxes(message, getRequesParams(), 0, getJavaUtilDate(), trxId);
    }

    private String getRequesParams() {
        return playerId + "/" + denom + "/" + trxId;
    }

    private void saveToOutbox(Outboxes newBoutbox) {
        try {
            DBUtilOutboxes.saveOutbox(newBoutbox);
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
        return new Outboxes(new JSONObject(responsePojo).toString(), null, getJavaUtilDate(), inboxId);
    }

    private boolean isSaveToInboxSucceed() {
        return inboxId != null;
    }

    private boolean isDenomValid(String denom) {
        return denomsMap.containsKey(denom);
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
