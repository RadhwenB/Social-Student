package socialstudent.eurecom.fr.socialstudent;

public class RelationEventUser {

    private String key ;
    private String eventKey ;
    private String userKey ;

    public RelationEventUser() {
    }

    public RelationEventUser(String key, String eventKey, String userKey) {
        this.key = key;
        this.eventKey = eventKey;
        this.userKey = userKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
