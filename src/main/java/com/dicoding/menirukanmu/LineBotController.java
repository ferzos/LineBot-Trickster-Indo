
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
import java.util.*;

@RestController
@RequestMapping(value="/linebot")
public class LineBotController
{
    boolean isStart = false;
    int flagSoal = 0;
    int JUMLAH_SOAL = 4;
    String startMessage = "Silahkan ketik \"start <kode soal>\" untuk memulai permainan\nKode soal:\n" +
            "1 --> Mata-Mati-Mitu\n" +
            "2 --> Yang ketiga\n" +
            "3 --> Tepok Nyamuk\n" +
            "4 --> Nol itu Satu";
    String endMessage = "Game berakhir, Terima kasih sudah bermain :)";
//    String soal1Message = "Sebutkan 5 kru topi jerami pada anime One Piece dengan harga tertinggi";
//    ArrayList<String> soal;
//    ArrayList<Boolean> answerStatus;
//    StringBuilder builder = new StringBuilder();
    String soalBundle = "";
    TreeMap<Integer, String> soal1;
    String soal1Answer = "";
    ArrayList<String> soal2Pertama;
    ArrayList<String> soal2Kedua;
    ArrayList<String> soal3;
    String soal3answer = "";
    TreeMap<Integer, String> soal4;
    String soal4Answer = "";

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
        String userId = "";

        if (eventType.equals("join")){
            if (payload.events[0].source.type.equals("group")){
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Trido. Mari kita bermain :)");
            }
            if (payload.events[0].source.type.equals("room")){
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Trido. Mari kita bermain :)");
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
                userId = payload.events[0].source.userId;

                if (!msgText.contains("bye trido")){
                    try {
                        // Ini ori
                        // getMessageData(msgText, idTarget);

                        getMessageData(msgText, payload.events[0].replyToken);

                    } catch (IOException e) {
                        System.out.println("Exception is raised ");
                        e.printStackTrace();
                    }
                } else {
                    isStart = false;
                    flagSoal = 0;
                    soalBundle = "";
                    soal1.clear();
                    soal1Answer = "";
                    soal2Pertama.clear();
                    soal2Kedua.clear();
                    soal3.clear();
                    soal3answer = "";
                    soal4.clear();
                    soal4Answer = "";
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

        // Game dimulai
        if (isStart) {
            // User menghentikan permainan
            if (message.equalsIgnoreCase("end the game")) {
                isStart = false;
                flagSoal = 0;
                soalBundle = "";
                soal1.clear();
                soal1Answer = "";
                soal2Pertama.clear();
                soal2Kedua.clear();
                soal3.clear();
                soal3answer = "";
                soal4.clear();
                soal4Answer = "";
                replyToUser(targetID, endMessage);
            }
            // User minta soal
            else if (message.equalsIgnoreCase("soal")) {
                replyToUser(targetID, soalBundle);
            }
            // User masukin input
            else {
                // Input ada
                if (flagSoal == 1) {
                    if(message.equalsIgnoreCase("gratis") && soal1Answer.equalsIgnoreCase("0000")) {
                        isStart = false;
                        flagSoal = 0;
                        soalBundle = "";
                        soal1.clear();
                        soal1Answer = "";
                        replyToUser(targetID, "Ya kamu benar\nJawabannya adalah gratis\n\n" +endMessage);
                    } if(message.equalsIgnoreCase(soal1Answer)) {
                        isStart = false;
                        flagSoal = 0;
                        soalBundle = "";
                        soal1.clear();
                        replyToUser(targetID, "Ya kamu benar\nJawabannya adalah " + soal1Answer + "\n\n" +endMessage);
                        soal1Answer = "";
                    } else {
                        replyToUser(targetID, "Salah !!");
                    }
                } else if (flagSoal == 2) {
                    if (arrInput[0].equalsIgnoreCase("yang") && arrInput[1].equalsIgnoreCase("ketiga")) {
                        isStart = false;
                        flagSoal = 0;
                        soalBundle = "";
                        soal2Pertama.clear();
                        soal2Kedua.clear();
                        replyToUser(targetID, "Ya kamu benar, " + message + "\n\n" +endMessage);
                    } else {
                        replyToUser(targetID, "Salah !!");
                    }
                } else if (flagSoal == 3) {
                    if (message.equalsIgnoreCase(soal3answer)){
                        isStart = false;
                        flagSoal = 0;
                        soalBundle = "";
                        soal3.clear();
                        replyToUser(targetID, "Ya kamu benar\nJawabannya adalah " + soal3answer + "\n\n" +endMessage);
                        soal3answer = "";
                    } else {
                        replyToUser(targetID, "Salah !!");
                    }
                } else if (flagSoal == 4) {
                    if(message.equalsIgnoreCase(soal4Answer)) {
                        isStart = false;
                        flagSoal = 0;
                        soalBundle = "";
                        soal4.clear();
                        replyToUser(targetID, "Ya kamu benar\nJawabannya adalah " + soal4Answer + "\n\n" +endMessage);
                        soal4Answer = "";
                    } else {
                        replyToUser(targetID, "Salah !!");
                    }
                }
            }
        }
        // Game belum dimulai
        else {
            // User ketik "help"
            if(message.equalsIgnoreCase("trido help")) {
                replyToUser(targetID, startMessage);
            }
            // User ketik "start <something>"
            else if (arrInput[0].equalsIgnoreCase("start")) {
                // Yang diketik number
                if (NumberUtils.isDigits(arrInput[1])) {
                    int kodeSoal = Integer.parseInt(arrInput[1]);
                    // Numbernya pada range yang benar
                    if ( kodeSoal > 0 && Integer.parseInt(arrInput[1]) <= JUMLAH_SOAL) {
                        if (Integer.parseInt(arrInput[1]) == 1) {
                            flagSoal = 1;
                            // Buat soal dan mulai permainan
                            soal1 = new TreeMap<>();
                            soal1.put(0, "Bensin");
                            soal1.put(1, "Pagi");
                            soal1.put(2, "Kura-kura");
                            soal1.put(3, "Amanah");
                            soal1.put(4, "Kacamata");
                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soal = soal1.get(soalNumber);
                            soal1Answer = soalNumber + "000";
                            soalBundle = "Game Dimulai \n============\n\nMata 2000, Mati 1000, Mitu gratis. \n"+ soal + " berapa ?";
                            replyToUser(targetID, soalBundle);
                            isStart = true;
                        } else if (Integer.parseInt(arrInput[1]) == 2) {
                            flagSoal = 2;
                            soal2Pertama = new ArrayList<>();
                            soal2Kedua = new ArrayList<>();

                            soal2Pertama.add("Emas");
                            soal2Kedua.add("Perak");

                            soal2Pertama.add("Mobil");
                            soal2Kedua.add("Motor");

                            soal2Pertama.add("Baju");
                            soal2Kedua.add("Celana");

                            soal2Pertama.add("Tikus");
                            soal2Kedua.add("Kerbau");

                            soal2Pertama.add("Langit");
                            soal2Kedua.add("Laut");

                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soalPertama = soal2Pertama.get(soalNumber);
                            String soalKedua = soal2Kedua.get(soalNumber);
                            soalBundle = "Game Dimulai \n============\n\nYang pertama " + soalPertama + ", Yang kedua " + soalKedua +"\nyang ketiga apa ?";
                            replyToUser(targetID, soalBundle);
                            isStart = true;
                        } else if (Integer.parseInt(arrInput[1]) == 3) {
                            flagSoal = 3;

                            soal3 = new ArrayList<>();
                            soal3.add("Berapa");
                            soal3.add("Berapa yang mati");
                            soal3.add("Ada berapa nyamuk yang mati");
                            soal3.add("Hayo berapa yang mati");
                            soal3.add("Coba itung berapa nyamuk yang mati");

                            int soalNumber = (int) (Math.random() * (4 - 0));
                            soalBundle = "Game Dimulai \n============\n\nPlok !! Plok !! Plok !! Plok !! Plok !! \n" + soal3.get(soalNumber) + " ?";
                            soal3answer = soal3.get(soalNumber).split(" ").length + "";
                            replyToUser(targetID, soalBundle);
                            isStart = true;
                        } else if (Integer.parseInt(arrInput[1]) == 4) {
                            flagSoal = 4;

                            soal4 = new TreeMap<>();
                            soal4.put(0,"7+5+3");
                            soal4.put(1,"9+1+4");
                            soal4.put(2,"8+2");
                            soal4.put(3,"6+9+0+4+1+3");
                            soal4.put(4,"8+2+1+7+3+9+6+4");

                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soal = soal4.get(soalNumber);
                            soal4Answer = soalNumber+"";
                            soalBundle = "Game Dimulai \n============\n\nNol itu satu\n"+ soal + " berapa ?";

                            replyToUser(targetID, soalBundle);
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
            }
        }
//        // Game udah dimulai
//        if (isStart) {
//            // User menghentikan permainan
//            if (message.equalsIgnoreCase("end the game")) {
//                isStart = false;
//                soal.clear();
//                answerStatus.clear();
//                replyToUser(targetID, endMessage);
//            }
//            // User masukin input
//            else {
//                // Input ada
//                if(soal.contains(message.toLowerCase())) {
//                    replyToUser(targetID, "ada nih, bentar gw cariin indexnya");
//                    for (int i = 0 ; i < soal.size() ; i++) {
//                        if(soal.get(i).equalsIgnoreCase(message)){
//                            answerStatus.set(i, Boolean.TRUE);
//                            replyToUser(targetID, "indexnya ini bro: " + i+1);
//                        } else {
//                            replyToUser(targetID, "gatau bro indexnya");
//                        }
//                    }
////                    for (int i = 0 ; i < soal.size() ; i++) {
////                        if(answerStatus.get(i)){
////                            builder.append(i+1 + ". " + soal.get(i));
////                        } else {
////                            builder.append(i+1 + ". ");
////                        }
////                    }
////                    replyToUser(targetID, builder.toString());
//                }
//            }
//        }
//        // Game belum dimulai
//        else {
//            // User ketik "help"
//            if(message.equalsIgnoreCase("help")) {
//                replyToUser(targetID, startMessage);
//            }
//            // User ketik "start <something>"
//            else if (arrInput[0].equalsIgnoreCase("start")) {
//                // Yang diketik number
//                if (NumberUtils.isDigits(arrInput[1])) {
//                    int kodeSoal = Integer.parseInt(arrInput[1]);
//                    // Numbernya pada range yang benar
//                    if ( kodeSoal > 0 && Integer.parseInt(arrInput[1]) < 11) {
//                        // Buat soal dan mulai permainan
//                        if (kodeSoal == 1) {
//                            soal = new ArrayList<>();
//                            soal.add("luffy");
//                            soal.add("roronoa");
//                            soal.add("usopp");
//                            soal.add("sanji");
//                            soal.add("robin");
//                            answerStatus = new ArrayList<>(soal.size());
//                            Collections.fill(answerStatus, Boolean.FALSE);
//                            replyToUser(targetID, "Game Dimulai \n============\n\n" + soal1Message);
//                            isStart = true;
//                        }
//                    }
//                    // Number pada range yang salah
//                    else {
//                        replyToUser(targetID, "Tidak ada soal dengan nomor itu :(");
//                    }
//                }
//                // Yang diketik bukan number
//                else
//                {
//                    replyToUser(targetID, startMessage);
//                }
//            }
//        }
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
