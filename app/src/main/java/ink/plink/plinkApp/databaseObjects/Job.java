package ink.plink.plinkApp.databaseObjects;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Job {

    private String job_id;
    private String printer_name;
    private String status;
    private double price;
    private long time;
    private String document_title;

    public Job(String job_id) {
        this.job_id = job_id;
    }

    public String getId() {
        return this.job_id;
    }

    public String getPrinterName() {
        return printer_name;
    }

    public double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
        return sdf.format(date);
    }

    public String getDocuentTitle() {
        if (document_title != null) {
            return document_title;
        } else {
            return "Document Title";
        }
    }

    public static List<Job> getJobsList(String json) {
        ArrayList<Job> jobs;
        Gson gson = new Gson();
        //JsonElement json = new JsonParser().parse(printerJSON);
        jobs = new ArrayList<>(Arrays.asList(gson.fromJson(json, Job[].class)));
        return jobs;
    }

}
