package ru.otus.hw.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.web.mvc.dto.AuthorForm;


@Controller
@RequestMapping("/authors")
@RequiredArgsConstructor
public class AuthorMvcController {

    private final AuthorService authorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("authors", authorService.findAll());
        return "lib/author/authorlist";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("form", new AuthorForm());
        return "lib/author/authorform";
    }


    @PostMapping("/create")
    public String create(@ModelAttribute("form") AuthorForm form) {
        authorService.create(form.getName());
        return "redirect:/authors";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") String id, Model model) {
        Author author = authorService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Author not found"));

        AuthorForm form = new AuthorForm();
        form.setId(author.getId());
        form.setName(author.getFullName());
        model.addAttribute("form", form);
        return "lib/author/authorform";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") String id, @ModelAttribute("form") AuthorForm form) {
        authorService.update(id, form.getName());
        return "redirect:/authors";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable String id, RedirectAttributes ra) {
        try {
            authorService.deleteById(id);
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("errorMessage",
                    "Нельзя удалить автора: сначала удалите его книги (или назначьте книгам другого автора).");
        }
        return "redirect:/authors";
    }

}
