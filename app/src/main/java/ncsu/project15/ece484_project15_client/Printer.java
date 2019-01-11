package ncsu.project15.ece484_project15_client;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.Layout;
import android.util.JsonReader;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

// Printer objects that will be received by server and updated on the map
public class Printer {
    private String printer_id;
    private String user_id;
    private double lat;
    private double lng;
    private int status;
    private int color;
    private String printer_name;
    private double price;
    private String address;
    private double distance;
    private String printer_type;

    Printer() {}

    Printer setPrinterId(String printer_id) {
        this.printer_id = printer_id;
        return this;
    }

    public String getPrinterId() {
        return this.printer_id;
    }

    Printer setUserId(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getUserId() {
        return this.user_id;
    }

    Printer setLocation(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
        return this;
    }

    LatLng getLocation() {
        return new LatLng(this.lat, this.lng);
    }

    JsonObject getJsonObject() {
        Gson gson = new Gson();
        return new JsonParser().parse(gson.toJson(this)).getAsJsonObject();
    }

    String getPrinterType() {
        return this.printer_type;
    }

    Printer setStatus(boolean status) {
        if (status) {
            this.status = 1;
        } else {
            this.status = 0;
        }
        return this;
    }

    Boolean getStatus() {
        if (this.status != 0) {
            return true;
        } else {
            return false;
        }
    }

    Printer setColor(boolean color) {
        if (color) {
            this.color = 1;
        } else {
            this.color = 0;
        }
        return this;
    }

    Boolean getColor() {
        if (this.color != 0) {
            return true;
        } else {
            return false;
        }
    }

    Printer setName(String name) {
        this.printer_name = name;
        return this;
    }

    String getName() {
        return this.printer_name;
    }

    Printer setPrice(double price) {
        this.price = price;
        return this;
    }

    Double getPrice() {
        return this.price;
    }

    Printer setAddress(String address) {
        this.address = address;
        return this;
    }

    String getAddress() {
        return this.address;
    }

    Printer setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    Double getDistance() {
        return this.distance;
    }

    Printer setPrinterType(String type) {
        this.printer_type = type;
        return this;
    }

    static Printer[] getPrinterList(String printerJSON) {
        Printer[] printers;
        Gson gson = new Gson();
        printers = gson.fromJson(printerJSON, Printer[].class);
//
//        try {
//            printers = readJsonStream(printerJSON);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return printers;
    }

    private static List<Printer> readJsonStream(String in) throws IOException {
        JsonReader reader = new JsonReader(new StringReader(in));
        List<Printer> printers;
        try {
            printers = readMessagesArray(reader);
        } finally {
            reader.close();
        }
        return printers;
    }

    private static List<Printer> readMessagesArray(JsonReader reader) throws IOException {
        List<Printer> messages = new ArrayList<>();

        reader.beginArray();
        while (reader.hasNext()) {
            messages.add(readMessage(reader));
        }
        reader.endArray();
        return messages;
    }

    private static Printer readMessage(JsonReader reader) throws IOException {
        String printerName = null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
            }
            if (name.equals("name")) {
                printerName = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        Printer printer = new Printer();
        printer.setName(printerName);
        return printer;
    }
}
