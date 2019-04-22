package ink.plink.plinkApp.databaseObjects;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Job {

    private String job_id;
    private String printer_name;
    private String status;
    private double price;

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

    public static List<Job> getJobsList(String json) {
        ArrayList<Job> jobs;
        Gson gson = new Gson();
        //JsonElement json = new JsonParser().parse(printerJSON);
        jobs = new ArrayList<>(Arrays.asList(gson.fromJson(json, Job[].class)));
        return jobs;
    }

}
