package im.im1020.model.bean;

/**
 * Created by Mancy_Lin on 2017-02-16.
 */
public class GroupInfo {

    private String groupName;

    private String groupid;

    private String inviteperson;

    public GroupInfo() {

    }

    public GroupInfo(String groupName, String groupid, String inviteperson) {
        this.groupName = groupName;
        this.groupid = groupid;
        this.inviteperson = inviteperson;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getInviteperson() {
        return inviteperson;
    }

    public void setInviteperson(String inviteperson) {
        this.inviteperson = inviteperson;
    }
}
