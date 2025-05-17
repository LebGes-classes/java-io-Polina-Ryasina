public class GroupSchedule<T> extends Schedule<T> {
    private String groupId;

    public GroupSchedule() {}

    public GroupSchedule(String groupId) {
        super();
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }

}
