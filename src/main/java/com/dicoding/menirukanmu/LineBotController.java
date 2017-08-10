
package com.dicoding.menirukanmu;

import com.google.gson.Gson;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping(value="/linebot")
public class LineBotController
{
    boolean isStart = false;
    String startMessage = "Silahkan ketik \"start <kode soal 1-10>\" untuk memulai permainan";
    String endMessage = "Game berakhir, Terima kasih sudah bermain :)";
    ArrayList<String> soal;

    @Autowired
    @Qualifier("com.linecorp.channel_secret")
    String lChannelSecret;
    
    @Autowired
    @Qualifier("com.linecorp.channel_access_token")
    String lChannelAccessToken;

    @RequestMapping(value="/callback", method=RequestMethod.POST)
    public ResponseEntity<String> callback(
        @RequestHeader("X-Line-Signature") String aXLineSignature,
        @RequestBody String aPayload)
    {
        final String text=String.format("The Signature is: %s",
            (aXLineSignature!=null && aXLineSignature.length() > 0) ? aXLineSignature : "N/A");
        System.out.println(text);
        final boolean valid=new LineSignatureValidator(lChannelSecret.getBytes()).validateSignature(aPayload.getBytes(), aXLineSignature);
        System.out.println("The signature is: " + (valid ? "valid" : "tidak valid"));
        if(aPayload!=null && aPayload.length() > 0)
        {
            System.out.println("Payload: " + aPayload);
        }
        Gson gson = new Gson();
        Payload payload = gson.fromJson(aPayload, Payload.class);

        String msgText = " ";
        String idTarget = " ";
        String eventType = payload.events[0].type;

        if (eventType.equals("join")){
            if (payload.events[0].source.type.equals("group")){
                replyToUser(payload.events[0].replyToken, "Hello Group");
            }
            if (payload.events[0].source.type.equals("room")){
                replyToUser(payload.events[0].replyToken, "Hello Room");
            }
        } else if (eventType.equals("message")){
            if (payload.events[0].source.type.equals("group")){
                idTarget = payload.events[0].source.groupId;
            } else if (payload.events[0].source.type.equals("room")){
                idTarget = payload.events[0].source.roomId;
            } else if (payload.events[0].source.type.equals("user")){
                idTarget = payload.events[0].source.userId;
            }

            if (!payload.events[0].message.type.equals("text")){
                replyToUser(payload.events[0].replyToken, "Unknown message");
            } else {
                msgText = payload.events[0].message.text;
                msgText = msgText.toLowerCase();

                if (!msgText.contains("bot leave")){
                    try {
//                      getMessageData(msgText, idTarget);
                        getMessageData(msgText, payload.events[0].replyToken);
                    } catch (IOException e) {
                        System.out.println("Exception is raised ");
                        e.printStackTrace();
                    }
                } else {
                    if (payload.events[0].source.type.equals("group")){
                        leaveGR(payload.events[0].source.groupId, "group");
                    } else if (payload.events[0].source.type.equals("room")){
                        leaveGR(payload.events[0].source.roomId, "room");
                    }
                }

            }
        }
         
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    private void getMessageData(String message, String targetID) throws IOException{
        String[] arrInput = message.split(" ");

        // Game udah dimulai
        if (isStart) {
            replyToUser(targetID, "Game Dimulai nih :)");
        }
        // Game belum dimulai
        else {
            // User ketik "help"
            if(message.equalsIgnoreCase("help")) {
                replyToUser(targetID, startMessage);
            }
            // User ketik "start <something>"
            else if (arrInput[0].equalsIgnoreCase("start")) {
                // Yang diketik number
                if (NumberUtils.isDigits(arrInput[1])) {
                    int kodeSoal = Integer.parseInt(arrInput[1]);
                    // Numbernya pada range yang benar
                    if ( kodeSoal > 0 && Integer.parseInt(arrInput[1]) < 11) {
                        // CRUMBLES
                        if (kodeSoal == 1) {
                            soal = new ArrayList<>();
                            soal.add("Luffy");
                            soal.add("Roronoa");
                            soal.add("Nami");
                            soal.add("Usopp");
                            soal.add("Sanji");
                            replyToUser(targetID, "Game Dimulai \n ============");
                            isStart = true;
                        }
                    }
                    // Number pada range yang salah
                    else {
                        replyToUser(targetID, "Tidak ada soal dengan nomor itu :(");
                    }
                }
                // Yang diketik bukan number
                else
                {
                    replyToUser(targetID, startMessage);
                }
            } else if (arrInput[0].equalsIgnoreCase("end the game")) {
                isStart = false;
                soal.clear();
                replyToUser(targetID, endMessage);
            }
        }
//        if (isStart) {
//            if (message.equalsIgnoreCase("end the game")) {
//                isStart = !isStart;
//                replyToUser(targetID, endMessage);
//            } else if (NumberUtils.isDigits(message)) {
//                replyToUser(targetID, message);
//            }
//            else {
//                replyToUser(targetID, startMessage);
//            }
//        } else {
//            if (message.equalsIgnoreCase("start")) {
//                isStart = !isStart;
//                replyToUser(targetID, startMessage);
//            } else {
//                replyToUser(targetID, "Silahkan ketik \"start\" untuk memulai permainan ");
//            }
//        }

//        if (message!=null){
////            pushMessage(targetID, message);
//            replyToUser(targetID, message);
//        }
    }

    private void replyToUser(String rToken, String messageToUser){
        TextMessage textMessage = new TextMessage(messageToUser);
        ReplyMessage replyMessage = new ReplyMessage(rToken, textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
                .create(lChannelAccessToken)
                .build()
                .replyMessage(replyMessage)
                .execute();
            System.out.println("Reply Message: " + response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    private void pushMessage(String sourceId, String txt){
        TextMessage textMessage = new TextMessage(txt);
        PushMessage pushMessage = new PushMessage(sourceId,textMessage);
        try {
            Response<BotApiResponse> response = LineMessagingServiceBuilder
            .create(lChannelAccessToken)
            .build()
            .pushMessage(pushMessage)
            .execute();
            System.out.println(response.code() + " " + response.message());
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }

    private void leaveGR(String id, String type){
        try {
            if (type.equals("group")){
                Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .leaveGroup(id)
                    .execute();
                System.out.println(response.code() + " " + response.message());
            } else if (type.equals("room")){
                Response<BotApiResponse> response = LineMessagingServiceBuilder
                    .create(lChannelAccessToken)
                    .build()
                    .leaveRoom(id)
                    .execute();
                System.out.println(response.code() + " " + response.message());
            }
        } catch (IOException e) {
            System.out.println("Exception is raised ");
            e.printStackTrace();
        }
    }
}
