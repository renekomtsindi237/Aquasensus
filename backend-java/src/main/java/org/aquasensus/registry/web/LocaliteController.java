package org.aquasensus.registry.web;

import java.util.List;
import org.aquasensus.registry.domain.Localite;
import org.aquasensus.registry.domain.LocaliteRepository;
import org.aquasensus.registry.domain.NiveauLocalite;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/localites")
public class LocaliteController {

    private final LocaliteRepository localites;

    public LocaliteController(LocaliteRepository localites) {
        this.localites = localites;
    }

    @GetMapping
    public List<Localite> lister(@RequestParam(required = false) NiveauLocalite niveau) {
        return localites.toutes().stream()
                .filter(l -> niveau == null || l.niveau() == niveau)
                .toList();
    }
}
