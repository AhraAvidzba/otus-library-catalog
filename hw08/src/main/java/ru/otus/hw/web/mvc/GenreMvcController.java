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
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;
import ru.otus.hw.web.mvc.dto.GenreForm;


@Controller
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreMvcController {

    private final GenreService genreService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("genres", genreService.findAll());
        return "lib/genre/genrelist";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("form", new GenreForm());
        return "lib/genre/genreform";
    }


    @PostMapping("/create")
    public String create(@ModelAttribute("form") GenreForm form) {
        genreService.create(form.getName());
        return "redirect:/genres";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") String id, Model model) {
        Genre genre = genreService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Genre not found"));

        GenreForm form = new GenreForm();
        form.setId(genre.getId());
        form.setName(genre.getName());
        model.addAttribute("form", form);
        return "lib/genre/genreform";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") String id, @ModelAttribute("form") GenreForm form) {
        genreService.update(id, form.getName());
        return "redirect:/genres";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id) {
        genreService.deleteById(id);
        return "redirect:/genres";
    }

}
