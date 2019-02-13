package ink.plink.plinkApp.databaseObjects;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
    private double price_color;
    private String address;
    private double distance;
    private String printer_type;

    public Printer() {}

    public Printer setPrinterId(String printer_id) {
        this.printer_id = printer_id;
        return this;
    }

    public String getPrinterId() {
        return this.printer_id;
    }

    public Printer setUserId(String user_id) {
        this.user_id = user_id;
        return this;
    }

    public String getUserId() {
        return this.user_id;
    }

    public Printer setLocation(LatLng location) {
        this.lat = location.latitude;
        this.lng = location.longitude;
        return this;
    }

    public LatLng getLocation() {
        return new LatLng(this.lat, this.lng);
    }

    public JsonObject getJsonObject() {
        Gson gson = new Gson();
        return new JsonParser().parse(gson.toJson(this)).getAsJsonObject();
    }

    public String getPrinterType() {
        return this.printer_type;
    }

    public Printer setStatus(boolean status) {
        if (status) {
            this.status = 1;
        } else {
            this.status = 0;
        }
        return this;
    }

    public boolean getStatus() {
        if (this.status != 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getStatusAsString() {
        if (this.status != 0) {
            return "Active";
        } else {
            return "Offline";
        }
    }

    public Printer setColor(boolean color) {
        if (color) {
            this.color = 1;
        } else {
            this.color = 0;
        }
        return this;
    }

    public Boolean getColor() {
        if (this.color != 0) {
            return true;
        } else {
            return false;
        }
    }

    public Printer setName(String name) {
        this.printer_name = name;
        return this;
    }

    public String getName() {
        return this.printer_name;
    }

    public Printer setPrice(double price) {
        this.price = price;
        return this;
    }

    public double getPrice() {
        return this.price;
    }

    public Printer setColorPrice(double price_color) {
        this.price_color = price_color;
        return this;
    }

    public double getColorPrice() {
        return this.price_color;
    }

    public Printer setAddress(String address) {
        this.address = address;
        return this;
    }

    public String getAddress() {
        return this.address;
    }

    public Printer setDistance(double distance) {
        this.distance = distance;
        return this;
    }

    public Double getDistance() {
        return this.distance;
    }

    public Printer setPrinterType(String type) {
        this.printer_type = type;
        return this;
    }

    public String getJsonAsString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Printer[] getPrinterList(String printerJSON) {
        Printer[] printers;
        Gson gson = new Gson();
        //JsonElement json = new JsonParser().parse(printerJSON);
        printers = gson.fromJson(printerJSON, Printer[].class);
        return printers;
    }
}
