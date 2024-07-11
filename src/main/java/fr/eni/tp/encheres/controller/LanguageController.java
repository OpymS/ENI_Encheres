package fr.eni.tp.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class LanguageController {

    @GetMapping("/changeLanguage")
    public RedirectView changeLanguage(HttpServletRequest request, @RequestParam("language") String language) {
        String referer = request.getHeader("Referer");
        StringBuilder newUrl = new StringBuilder(referer.split("\\?")[0] + "?language=" + language);

        String queryString = referer.split("\\?").length > 1 ? referer.split("\\?")[1] : "";
        if (!queryString.isEmpty()) {
            String[] params = queryString.split("&");
            for (String param : params) {
                if (!param.startsWith("language=")) {
                    newUrl.append("&").append(param);
                }
            }
        }

        RedirectView redirectView = new RedirectView();
        redirectView.setUrl(newUrl.toString());
        return redirectView;
    }
}
