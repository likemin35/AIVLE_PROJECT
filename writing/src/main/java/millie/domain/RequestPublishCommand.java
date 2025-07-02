package millie.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;

@Data
public class RequestPublishCommand {

    private String status;
    private Boolean isApprove;

    public RequestPublishCommand() {}
}
