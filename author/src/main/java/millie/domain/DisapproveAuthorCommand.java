package millie.domain;

import java.time.LocalDate;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class DisapproveAuthorCommand {

    private Boolean isApprove;
}
