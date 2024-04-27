package cz.test.test1;

import org.json.JSONException;
import org.json.JSONObject;

public class AppSettings {
    private boolean speak;
    private boolean lanEn;
    private boolean clRegistered;

    private String Useremail;
    private String Userusername;

    // Constructor
    public AppSettings(String Useremail, String Userusername, boolean clRegistered, boolean speak, boolean lanEn) {
        this.Useremail = Useremail;
        this.Userusername = Userusername;
        this.clRegistered = clRegistered;
        this.speak = speak;
        this.lanEn = lanEn;


    }

    // Getters and Setters

    public boolean isSpeak() {
        return speak;
    }

    public void setSpeak(boolean speak) {
        this.speak = speak;
    }

    public boolean isLanEn() {
        return lanEn;
    }
    public void setLanEn(boolean lanEn) {
        this.lanEn = lanEn;
    }


    public boolean isclRegistered() {
        return clRegistered;
    }
    public void setclRegistered(boolean clRegistered) {
        this.lanEn = clRegistered;
    }







    public String isUseremail() {
        return Useremail;
    }
    public void setUseremail(String Useremail) {
        this.Useremail = Useremail;
    }

    public String isUserusername() {
        return Userusername;
    }
    public void setisUserusername(String Userusername) {
        this.Userusername = Userusername;
    }




    // Convert AppSettings to JSON
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("Useremail", Useremail);
            json.put("Userusername", Userusername);
            json.put("clRegistered", clRegistered);
            json.put("speak", speak);
            json.put("lanEn", lanEn);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    // Create AppSettings from JSON
    public static AppSettings fromJson(JSONObject json) {
        try {
            boolean speak = json.getBoolean("speak");
            boolean lanEn = json.getBoolean("lanEn");
            boolean clRegistered = json.getBoolean("clRegistered");
            String Useremail = json.getString("Useremail");
            String Userusername = json.getString("Userusername");

            //return new AppSettings(speak, lanEn, clRegistered, Useremail, Userusername);
            return new AppSettings(Useremail, Userusername, clRegistered, speak, lanEn);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
