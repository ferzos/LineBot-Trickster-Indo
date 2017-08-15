
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
    String startMessage = "Silahkan ketik \"start <nomor soal>\" untuk memulai permainan\nContoh: start 1\n\nKode soal:\n" +
            "1 --> Nasi kucing 1000, Teh gratis\n" +
            "2 --> Yang ketiga\n" +
            "3 --> Tepok Nyamuk\n" +
            "4 --> Tahu bulat\n" +
            "5 --> Lantai Hotel\n\n" +
            "Ketik \"bye tido\" untuk mengeluarkan tido dari grup";
    String headerMessage = "============\nGame Dimulai \n============\n\n";
    String footMessage = "Game berakhir, Terima kasih sudah bermain :)";
    String endMessage = "Ketik \"soal\" untuk meminta kembali soal\nKetik \"end game\" untuk mengakhiri permainan";
    HashMap<String, HashMap<String, Object>> relativeValueMap = new HashMap<>();

    TreeMap<Integer, String[]> soal1;
    ArrayList<String> soal2Pertama;
    ArrayList<String> soal2Kedua;
    ArrayList<String> soal3;
    TreeMap<Integer, String[]> soal4;
    TreeMap<Integer, String> soal5;

    public LineBotController() {
        /*SOAL 1*/
        soal1 = new TreeMap<>();
        String[] soal1Gratis = new String[10];
        soal1Gratis[0] = "Gudeg";
        soal1Gratis[1] = "Cireng";
        soal1Gratis[2] = "Perkedel";
        soal1Gratis[3] = "Cilok";
        soal1Gratis[4] = "Sekoteng";
        soal1Gratis[5] = "Pempek";
        soal1Gratis[6] = "Kue cubit";
        soal1Gratis[7] = "Pecel lele";
        soal1Gratis[8] = "Pepes";
        soal1Gratis[9] = "Cendol";

        String[] soal1Seribu = new String[10];
        soal1Seribu[0] = "Surabi";
        soal1Seribu[1] = "Bajigur";
        soal1Seribu[2] = "Rawon";
        soal1Seribu[3] = "Rendang";
        soal1Seribu[4] = "Bakso";
        soal1Seribu[5] = "Jamu";
        soal1Seribu[6] = "Wedang ronde";
        soal1Seribu[7] = "Ketoprak";
        soal1Seribu[8] = "Kerak telor";
        soal1Seribu[9] = "Seblak";

        String[] soal1DuaRibu = new String[10];
        soal1DuaRibu[0] = "Nasi ulam";
        soal1DuaRibu[1] = "Gado-gado";
        soal1DuaRibu[2] = "Opor ayam";
        soal1DuaRibu[3] = "Otak-otak";
        soal1DuaRibu[4] = "Papeda";
        soal1DuaRibu[5] = "Soda gembira";
        soal1DuaRibu[6] = "Wedang Jahe";
        soal1DuaRibu[7] = "Cah kangkung";
        soal1DuaRibu[8] = "Soto ayam";
        soal1DuaRibu[9] = "Sate kambing";

        String[] soal1TigaRibu = new String[10];
        soal1TigaRibu[0] = "Gulai ikan patin";
        soal1TigaRibu[1] = "Gulai belacan";
        soal1TigaRibu[2] = "Teh manis hangat";
        soal1TigaRibu[3] = "Martabak telor";
        soal1TigaRibu[4] = "Pisang bakar";
        soal1TigaRibu[5] = "Ikan bakar";
        soal1TigaRibu[6] = "Sate ayam";
        soal1TigaRibu[7] = "Es kelapa muda";
        soal1TigaRibu[8] = "Martabak";

        soal1.put(0, soal1Gratis);
        soal1.put(1, soal1Seribu);
        soal1.put(2, soal1DuaRibu);
        soal1.put(3, soal1TigaRibu);

        /*SOAL 2*/
        soal2Pertama = new ArrayList<>();
        soal2Kedua = new ArrayList<>();
        soal2Pertama.add("Ojek");soal2Kedua.add("Bemo");
        soal2Pertama.add("Borobudur");soal2Kedua.add("Prambanan");
        soal2Pertama.add("Delman");soal2Kedua.add("Becak");
        soal2Pertama.add("Tari saman");soal2Kedua.add("Tari kecak");
        soal2Pertama.add("Petruk");soal2Kedua.add("Semar");
        soal2Pertama.add("Angklung");soal2Kedua.add("Kolintang");
        soal2Pertama.add("Keris");soal2Kedua.add("Batik");
        soal2Pertama.add("Rasa sayange");soal2Kedua.add("Jali-jali");
        soal2Pertama.add("Komodo");soal2Kedua.add("Orangutan");
        soal2Pertama.add("Merapi");soal2Kedua.add("Semeru");


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
        String[] soal4Nol = new String[5];
        soal4Nol[0] = "1+3";
        soal4Nol[1] = "7+5+3";
        soal4Nol[2] = "1+7+4+4";
        soal4Nol[3] = "1+1+1+1+1";
        soal4Nol[4] = "1+3+5+7+5+3+1";

        String[] soal4Satu = new String[5];
        soal4Satu[0] = "6+4";
        soal4Satu[1] = "1+9+4";
        soal4Satu[2] = "4+3+9+7";
        soal4Satu[3] = "1+1+2+7+6";
        soal4Satu[4] = "4+7+7+9+4+3";

        String[] soal4Dua = new String[5];
        soal4Dua[0] = "8+2";
        soal4Dua[1] = "6+2+9";
        soal4Dua[2] = "4+9+9+4";
        soal4Dua[3] = "1+4+7+2+8";
        soal4Dua[4] = "1+1+3+8+7+4";

        String[] soal4Tiga = new String[5];
        soal4Tiga[0] = "8+6";
        soal4Tiga[1] = "8+7+6";
        soal4Tiga[2] = "6+7+9+3+6";
        soal4Tiga[3] = "8+1+7+4+2+6";
        soal4Tiga[4] = "9+8+1+7+5+5+1";

        String[] soal4Empat = new String[5];
        soal4Empat[0] = "8+8";
        soal4Empat[1] = "8+1+8";
        soal4Empat[2] = "6+6+9+9";
        soal4Empat[3] = "6+4+9+7+8";
        soal4Empat[4] = "8+5+6+3+9+1";

        soal4.put(0, soal4Nol);
        soal4.put(1, soal4Satu);
        soal4.put(2, soal4Dua);
        soal4.put(3, soal4Tiga);
        soal4.put(4, soal4Empat);

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
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Tido. Mari kita bermain :)\nKetik \"tido help\" untuk bantuan permainan");
            }
            if (payload.events[0].source.type.equals("room")){
                replyToUser(payload.events[0].replyToken, "Halo semuanya, namaku Tido. Mari kita bermain :)\nKetik \"tido help\" untuk bantuan permainan");
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
                msgText = msgText.trim();

                if (!msgText.contains("bye tido")){
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
            if (message.equalsIgnoreCase("tido help")) {
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
                            int soalNumber = (int) (Math.random() * 3);
                            String[] kumpulanSoal = soal1.get(soalNumber);
                            int soalChooser = (int)(Math.random() * kumpulanSoal.length);
                            String soal = kumpulanSoal[soalChooser];
                            String soal1Answer = soalNumber + "000";
                            variables.put("soal1Answer", soal1Answer);
                            String soalBundle = headerMessage + "Nasi kucing 1000, kalo Teh gratis. \n" + soal + " berapa ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 2) {
                            variables.put("flagSoal",2);
                            int soalNumber = (int) (Math.random() * 10);
                            String soalPertama = soal2Pertama.get(soalNumber);
                            String soalKedua = soal2Kedua.get(soalNumber);
                            String soalBundle = headerMessage + "Jawab dengan bahasa indonesia yang baik dan benar\nYang pertama " + soalPertama + ", Yang kedua " + soalKedua + "\nyang ketiga apa ?\n\n" + endMessage;
                            variables.put("soalBundle", soalBundle);
                            replyToUser(replyToken, soalBundle);
                            variables.put("start", true);
                        } else if (Integer.parseInt(arrInput[1]) == 3) {
                            variables.put("flagSoal",3);
                            int soalNumber = (int) (Math.random() * 7);
                            int jumlahTepokan = (int) (Math.random() * 10);
                            String plok = "Plok!! ";
                            for (int i = 0; i < jumlahTepokan ; i++) {
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
                            int soalNumber = (int) (Math.random() * 4);
                            String[] kumpulanSoal = soal4.get(soalNumber);
                            int soalChooser = (int)(Math.random() * (kumpulanSoal.length));
                            String soal = kumpulanSoal[soalChooser];
                            String soal4Answer = soalNumber + "";
                            variables.put("soal4Answer", soal4Answer);
                            String soalBundle = headerMessage + "Tahu itu bulat\n" + soal + " berapa ?\n\n" + endMessage;
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
