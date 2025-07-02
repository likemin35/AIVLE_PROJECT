package millie.domain;

import lombok.Data;

@Data
public class RequestPublishCommand {

    private String status;
    private Boolean isApprove;

    public RequestPublishCommand() {}

    public Boolean getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(Boolean isApprove) {
        this.isApprove = isApprove;
    }
}
