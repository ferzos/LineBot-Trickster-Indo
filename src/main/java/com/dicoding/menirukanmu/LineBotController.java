
package com.dicoding.menirukanmu;

import com.google.gson.Gson;
import com.linecorp.bot.client.LineMessagingServiceBuilder;
import com.linecorp.bot.client.LineSignatureValidator;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.ReplyMessage;
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
    int JUMLAH_SOAL = 5;
    String startMessage = "Silahkan ketik \"start <kode soal>\" untuk memulai permainan\nKode soal:\n" +
            "1 --> Fanta dan Sprite\n" +
            "2 --> Yang ketiga\n" +
            "3 --> Tepok Nyamuk\n" +
            "4 --> Nol itu Satu\n" +
            "5 --> Lantai Hotel";
    String headerMessage = "============\nGame Dimulai \n============\n\n";
    String footMessage = "Game berakhir, Terima kasih sudah bermain :)";
    String endMessage = "Ketik \"soal\" untuk meminta kembali soal\nKetik \"end game\" untuk mengakhiri permainan";
    HashMap<String, HashMap<String, Object>> relativeValueMap = new HashMap<>();

    TreeMap<Integer, String> soal1;
    ArrayList<String> soal2Pertama;
    ArrayList<String> soal2Kedua;
    ArrayList<String> soal3;
    TreeMap<Integer, String> soal4;
    TreeMap<Integer, String> soal5;

    public LineBotController() {
        /*SOAL 1*/
        soal1 = new TreeMap<>();
        soal1.put(0, "Pepsi");
        soal1.put(1, "Root beer A&W");
        soal1.put(2, "Coca-cola");
        soal1.put(3, "Coklat Panas");
        soal1.put(4, "Jus mangga + Jus alpukat ");

        /*SOAL 2*/
        soal2Pertama = new ArrayList<>();
        soal2Kedua = new ArrayList<>();
        soal2Pertama.add("Emas");soal2Kedua.add("Perak");
        soal2Pertama.add("Mobil");soal2Kedua.add("Motor");
        soal2Pertama.add("Baju");soal2Kedua.add("Celana");
        soal2Pertama.add("Tikus");soal2Kedua.add("Kerbau");
        soal2Pertama.add("Langit");soal2Kedua.add("Laut");

        /*SOAL 3*/
        soal3 = new ArrayList<>();
        soal3.add("Berapa");
        soal3.add("Berapa yang mati");
        soal3.add("Ada berapa nyamuk yang mati");
        soal3.add("Hayo berapa yang mati");
        soal3.add("Berapa nyamuk yang mati");
        soal3.add("Nyamuknya berapa yang mati");
        soal3.add("Coba itung berapa nyamuk yang mati");
        soal3.add("Coba tebak ada berapa nyamuk yang mati");

        /*SOAL 4*/
        soal4 = new TreeMap<>();
        soal4.put(0, "7+5+3");
        soal4.put(1, "9+1+4");
        soal4.put(2, "8+2");
        soal4.put(3, "6+9+0+4+1+3");
        soal4.put(4, "8+2+1+7+3+9+6+4");

        /*SOAL 5*/
        soal5 = new TreeMap<>();
        soal5.put(1, "Kamu berada di lantai 2, belok kiri");
        soal5.put(2, "Kamu berada di lantai 3, belok kiri");
        soal5.put(3, "Kamu berada di lantai 9, naik dua lantai");
        soal5.put(4, "Kamu berada di lantai 4, turun satu lantai, belok kanan, naik satu lantai, belok kiri");
        soal5.put(5, "Kamu berada di lantai 7, belok kanan, naik satu lantai");
        soal5.put(6, "Kamu berada di lantai 8, naik satu lantai, belok kanan");
        soal5.put(7, "Kamu berada di lantai 6, turun satu lantai, belok kiri dua kali");
        soal5.put(8, "Kamu berada di lantai 1, turun dua lantai, belok kanan");
        soal5.put(9, "Kamu berada di lantai 5, belok kanan, turun satu lantai");
    }

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
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Trido. Mari kita bermain :)\nKetik \"trido help\" untuk memulai permainan");
            }
            if (payload.events[0].source.type.equals("room")){
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Trido. Mari kita bermain :)\nKetik \"trido help\" untuk memulai permainan");
            }
        } else if (eventType.equals("message")){
            if (payload.events[0].source.type.equals("group")){
                idTarget = payload.events[0].source.groupId;
                if (!relativeValueMap.containsKey(idTarget)){
                    relativeValueMap.put(idTarget, makeNewRelativeValue(idTarget));
                }
            } else if (payload.events[0].source.type.equals("room")){
                idTarget = payload.events[0].source.roomId;
                if (!relativeValueMap.containsKey(idTarget)){
                    relativeValueMap.put(idTarget, makeNewRelativeValue(idTarget));
                }
            } else if (payload.events[0].source.type.equals("user")){
                idTarget = payload.events[0].source.userId;
                if (!relativeValueMap.containsKey(idTarget)){
                    relativeValueMap.put(idTarget, makeNewRelativeValue(idTarget));
                }
            }

            if (!payload.events[0].message.type.equals("text")){
                // DO NOTHING
                // replyToUser(payload.events[0].replyToken, "Unknown message");
            } else {
                msgText = payload.events[0].message.text;
                msgText = msgText.toLowerCase();

                if (!msgText.contains("bye trido")){
                    try {
                        // Ini ori
                        // getMessageData(msgText, idTarget);

                        getMessageData(msgText, payload.events[0].replyToken, idTarget);

                    } catch (IOException e) {
                        System.out.println("Exception is raised ");
                        e.printStackTrace();
                    }
                } else {
                    resetState(relativeValueMap.get(idTarget));
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

    private void getMessageData(String message, String replyToken, String targetId) throws IOException {
        String[] arrInput = message.split(" ");
        HashMap<String, Object> variables = relativeValueMap.get(targetId);

        // Game dimulai
        if ((boolean)variables.get("start")) {
            // User menghentikan permainan
            if (message.equalsIgnoreCase("end game")) {
                resetState(relativeValueMap.get(targetId));
                replyToUser(replyToken, "Game berakhir, Terima kasih sudah bermain :)");
            }
            // User minta soal
            else if (message.equalsIgnoreCase("soal")) {
                replyToUser(replyToken, variables.get("soalBundle")+"");
            }
            // User masukin input
            else {
                // Input ada
                if ((int)variables.get("flagSoal") == 1) {
                    String soal1Answer = variables.get("soal1Answer")+"";
                    if (message.equalsIgnoreCase("gratis") && soal1Answer.equalsIgnoreCase("0000")) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar\nJawabannya adalah gratis\n\n" + footMessage);
                    }
                    if (message.equalsIgnoreCase(soal1Answer)) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar\nJawabannya adalah " + soal1Answer + "\n\n" + footMessage);
                    } else {
                        replyToUser(replyToken, "Salah !!");
                    }
                } else if ((int)variables.get("flagSoal") == 2) {
                    if (arrInput[0].equalsIgnoreCase("yang") && arrInput[1].equalsIgnoreCase("ketiga") && arrInput[2] != null) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar, " + message + "\n\n" + footMessage);
                    } else {
                        replyToUser(replyToken, "Salah !!");
                    }
                } else if ((int)variables.get("flagSoal") == 3) {
                    String soal3Answer = variables.get("soal3Answer")+"";
                    if (message.equalsIgnoreCase(soal3Answer)) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar\nJawabannya adalah " + soal3Answer + "\n\n" + footMessage);
                    } else {
                        replyToUser(replyToken, "Salah !!");
                    }
                } else if ((int)variables.get("flagSoal") == 4) {
                    String soal4Answer = variables.get("soal4Answer")+"";
                    if (message.equalsIgnoreCase(soal4Answer)) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar\nJawabannya adalah " + soal4Answer + "\n\n" + footMessage);
                    } else {
                        replyToUser(replyToken, "Salah !!");
                    }
                } else if ((int)variables.get("flagSoal") == 5) {
                    String soal5Answer = variables.get("soal5Answer")+"";
                    if (message.equalsIgnoreCase(soal5Answer)) {
                        resetState(relativeValueMap.get(targetId));
                        replyToUser(replyToken, "Ya kamu benar\nKamu berada di lantai " + soal5Answer + "\n\n" + footMessage);
                    } else {
                        replyToUser(replyToken, "Salah !!");
                    }
                }
            }
        }
        // Game belum dimulai
        else {
            // User ketik "help"
            if (message.equalsIgnoreCase("trido help")) {
                replyToUser(replyToken, startMessage);
            }
            // User ketik "start <something>"
            else if (arrInput[0].equalsIgnoreCase("start")) {
                // Yang diketik number
                if (NumberUtils.isDigits(arrInput[1])) {
                    int kodeSoal = Integer.parseInt(arrInput[1]);
                    // Numbernya pada range yang benar
                    if (kodeSoal > 0 && Integer.parseInt(arrInput[1]) <= JUMLAH_SOAL) {
                        if (Integer.parseInt(arrInput[1]) == 1) {
                            variables.put("flagSoal",1);
                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soal = soal1.get(soalNumber);
                            String soal1Answer = soalNumber + "000";
                            variables.put("soal1Answer", soal1Answer);
                            String soalBundle = headerMessage + "Fanta 2000, kalo Sprite gratis. \n" + soal + " berapa ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 2) {
                            variables.put("flagSoal",2);
                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soalPertama = soal2Pertama.get(soalNumber);
                            String soalKedua = soal2Kedua.get(soalNumber);
                            String soalBundle = headerMessage + "Yang pertama " + soalPertama + ", Yang kedua " + soalKedua + "\nyang ketiga apa ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 3) {
                            variables.put("flagSoal",3);
                            int soalNumber = (int) (Math.random() * (7 - 0));
                            int jumlahTepokan = (int) (Math.random() * 10);
                            String plok = "";
                            for (int i = 0; i < jumlahTepokan; i++) {
                                plok += "Plok!! ";
                            }
                            String soalBundle = headerMessage + plok + "\n" + soal3.get(soalNumber) + " ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            String soal3Answer = soal3.get(soalNumber).split(" ").length + "";
                            variables.put("soal3Answer", soal3Answer);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 4) {
                            variables.put("flagSoal",4);
                            int soalNumber = (int) (Math.random() * (4 - 0));
                            String soal = soal4.get(soalNumber);
                            String soal4Answer = soalNumber + "";
                            variables.put("soal4Answer", soal4Answer);
                            String soalBundle = headerMessage + "Nol itu satu\n" + soal + " berapa ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 5) {
                            variables.put("flagSoal",5);
                            int soalNumber = (int) (Math.random() * (9 - 1));
                            String soal = soal5.get(soalNumber);
                            String soal5Answer = soalNumber+"";
                            variables.put("soal5Answer", soal5Answer);
                            String soalBundle = headerMessage + "Kamu berada di hotel digital\n" + soal + " di lantai berapa kamu sekarang ? (Jawab hanya dengan menggunakan angka)\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        }
                    }
                    // Number pada range yang salah
                    else {
                        replyToUser(replyToken, "Tidak ada soal dengan nomor itu :(");
                    }
                }
                // Yang diketik bukan number
                else {
                    replyToUser(replyToken, startMessage);
                }
            }
        }
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

    private HashMap<String, Object> makeNewRelativeValue(String idTarget) {
        HashMap<String, Object> initValue = new HashMap<>();

        initValue.put("id", idTarget);
        initValue.put("start", false);
        initValue.put("flagSoal", 0);
        initValue.put("soalBundle", "");
        initValue.put("soal1Answer", "");
        initValue.put("soal3Answer", "");
        initValue.put("soal4Answer", "");
        initValue.put("soal5Answer", "");

        return initValue;
    }

    private void resetState (HashMap<String, Object> variables) {
        variables.put("start", false);
        variables.put("flagSoal", 0);
        variables.put("soalBundle", "");
        variables.put("soal1Answer", "");
        variables.put("soal3Answer", "");
        variables.put("soal4Answer", "");
        variables.put("soal5Answer", "");
    }

}
