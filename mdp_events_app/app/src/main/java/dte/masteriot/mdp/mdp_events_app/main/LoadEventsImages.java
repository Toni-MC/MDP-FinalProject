package dte.masteriot.mdp.mdp_events_app.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dte.masteriot.mdp.mdp_events_app.model.Dataset;
import dte.masteriot.mdp.mdp_events_app.model.Item;

public class LoadEventsImages implements Runnable {
    // Class to download a text-based content (e.g. HTML, XML, JSON, ...) from a URL
    // and populate a String with it that will be sent in a Message

    private String expectedContent_type;
    private String string_URL;
    Handler creator;
    private Dataset dataset;



    public LoadEventsImages(Handler handler, Dataset data) {
        // The constructor accepts 3 arguments:
        // The handler to the creator of this object
        // The content type expected (e.g. "application/vnd.google-earth.kml+xml").
        // The URL to load.
        creator = handler;
        dataset = data;
        expectedContent_type = "text/html";
    }

    @SuppressLint("LongLogTag")
    @Override
    public void run() {
        Message msg;
        Bundle msg_data;

//        Dictionary<String, String> dic_default_images = new Hashtable<>();
//        dic_default_images.put("deporte", "https://www.madrid.es/UnidadesDescentralizadas/Educacion_Ambiental/EspecialesInformativos/HabitatMadridActividadesAmbientales/Imagenes/Exposiciones/Expo100A%C3%B1osMestaCasaCampo.jpg");
//        dic_default_images.put("expo_arte", "/Exposiciones,/ActividadesCalleArteUrbano");
//        dic_default_images.put("musica", "/Musica");
//        dic_default_images.put("teatro_av", "/TeatroPerformance,/DanzaBaile,/CineActividadesAudiovisuales" +
//                "/CircoMagia,/CuentacuentosTiteresMarionetas");

        for (int i = 0; i < dataset.getSize(); i++) {
            Item item = dataset.getItemAtPosition(i);
            string_URL = item.getLink();
            string_URL = string_URL.replace("http", "https");
            String html = get_html();

            String image_url = get_image_url(html);

            if(image_url == "NA"){
                image_url = "https://www.madrid.es/UnidadesDescentralizadas/Educacion_Ambiental/EspecialesInformativos/HabitatMadridActividadesAmbientales/Imagenes/Exposiciones/Expo100A%C3%B1osMestaCasaCampo.jpg";
            }
            item.setImageLink(image_url);

            msg = creator.obtainMessage(); // message to send to the UI thread
            msg_data = msg.getData(); // message data
            msg_data.putInt("progress", i+1); // (key, value = progress)
            msg.sendToTarget(); // send the message to the target

        }
    }

    public String get_html() {

        Message msg = creator.obtainMessage();
        Bundle msg_data = msg.getData();

        StringBuilder response = new StringBuilder();
        HttpURLConnection urlConnection;

        // Build the string with thread and Class names (used in logs):
        String threadAndClass = "Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);


        try {
//            URL url = new URL("https://www.madrid.es/sites/v/index.jsp?vgnextchannel=ca9671ee4a9eb410VgnVCM100000171f5a0aRCRD&vgnextoid=e9b6122b8241b810VgnVCM1000001d4a900aRCRD");
            URL url = new URL(string_URL);
            urlConnection = (HttpURLConnection) url.openConnection();
            String actualContentType = urlConnection.getContentType();// content-type header from HTTP server
            InputStream is = urlConnection.getInputStream();

            // Extract MIME type and subtype (get rid of the possible parameters present in the content-type header
            // Content-type: type/subtype;parameter1=value1;parameter2=value2...
            if ((actualContentType != null) && (actualContentType.contains(";"))) {
                int beginparam = actualContentType.indexOf(";", 0);
                actualContentType = actualContentType.substring(0, beginparam);
            }

            if ("text/html".equals(actualContentType)) {
                // We check that the actual content type got from the server is the expected one
                // and if it is, download text
                InputStreamReader reader = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(reader);
                // We read the text contents line by line and add them to the response:
                String line = in.readLine();
                while (line != null) {
                    response.append(line);
                    line = in.readLine();
                }
            } else { // content type not supported
                response.append("Actual content type different from expected (" +
                        actualContentType + " vs " + expectedContent_type + ")");
            }
            urlConnection.disconnect();
        } catch (Exception e) {
            response.append(e);
        }

        return response.toString();

    }

    public String get_image_url(String html){
        String str_im;
        Document doc = Jsoup.parse(html);
        Elements image_link = doc.select("#readspeaker > div > div:nth-child(2) > div.tramites-content > div.image-content.ic-right > img"); // a with href
        String str_img = image_link.toString();

        Pattern pattern = Pattern.compile("src=\"");
        Matcher matcher = pattern.matcher(str_img);

        if (matcher.find()) {
            int b = matcher.end();
            str_im = str_img.substring(b);
            str_im = "https://www.madrid.es" + str_im;
            str_im = str_im.substring(0, str_im.length() - 2);
        }
        else{
            str_im =  "NA";
        }
        return str_im;
    }
}

