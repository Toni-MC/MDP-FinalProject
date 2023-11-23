package dte.masteriot.mdp.mdp_events_app.model;

public class HistoryItem {
    private String clientID;
    private String clientUsername;
    private String msg;
    private String timestampLocal;

    public HistoryItem(String clientID, String clientUsername, String msg, String timestampLocal){
        this.clientID=clientID;
        this.clientUsername=clientUsername;
        this.msg=msg;
        this.timestampLocal=timestampLocal;
    }

    public String getClientID() {
        return clientID;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    public String getMsg() {
        return msg;
    }

    public String getTimestampLocal() {
        return timestampLocal;
    }
}
