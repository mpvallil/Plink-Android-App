package ink.plink.plinkApp.databaseObjects;

import java.util.ArrayList;
import java.util.List;

public class Job {

    private String job_id;

    public Job(String job_id) {
        this.job_id = job_id;
    }

    public String getId() {
        return this.job_id;
    }

    public static List<Job> getJobsList(String json) {
        return new ArrayList<>();
    }

}
