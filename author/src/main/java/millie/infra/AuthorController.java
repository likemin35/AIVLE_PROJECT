package millie.infra;

import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import millie.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
// @RequestMapping(value="/authors")
@Transactional
public class AuthorController {

    @Autowired
    AuthorRepository authorRepository;

    @RequestMapping(
        value = "/authors/{id}/approveauthor",
        method = RequestMethod.PATCH,
        produces = "application/json;charset=UTF-8"
    )
    public Author approveAuthor(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /author/approveAuthor  called #####");
        Optional<Author> optionalAuthor = authorRepository.findById(id);

        optionalAuthor.orElseThrow(() -> new Exception("No Entity Found"));
        Author author = optionalAuthor.get();
        ApproveAuthorCommand cmd = new ApproveAuthorCommand();
        cmd.setIsApprove(true);

        author.approveAuthor(cmd);
        authorRepository.save(author);
        return author;
    }

    @RequestMapping(
        value = "/authors/{id}/disapproveauthor",
        method = RequestMethod.PATCH,
        produces = "application/json;charset=UTF-8"
    )
    public Author disapproveAuthor(
        @PathVariable(value = "id") Long id,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws Exception {
        System.out.println("##### /author/disapproveAuthor  called #####");
        Optional<Author> optionalAuthor = authorRepository.findById(id);

        optionalAuthor.orElseThrow(() -> new Exception("No Entity Found"));
        Author author = optionalAuthor.get();
        DisapproveAuthorCommand cmd = new DisapproveAuthorCommand();
        cmd.setIsApprove(false);

        author.disapproveAuthor(cmd);
        authorRepository.save(author);
        return author;
    }
}
//>>> Clean Arch / Inbound Adaptor
