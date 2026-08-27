package org.aquasensus.analytics.web;

import java.util.List;
import org.aquasensus.analytics.application.CarteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/water-points")
public class CarteController {

    private final CarteService carte;

    public CarteController(CarteService carte) {
        this.carte = carte;
    }

    @GetMapping("/map")
    public List<CarteService.MarqueurCarte> carte() {
        return carte.marqueurs();
    }
}
