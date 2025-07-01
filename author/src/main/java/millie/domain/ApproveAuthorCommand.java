package millie.domain;

import java.time.LocalDate;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class ApproveAuthorCommand {

    private Boolean isApprove;
    public void setIsApprove(boolean isApprove) { this.isApprove = isApprove; }
    public boolean getIsApprove() { return isApprove; }
}
