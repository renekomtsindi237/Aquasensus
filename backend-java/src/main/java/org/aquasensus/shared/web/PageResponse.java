package org.aquasensus.shared.web;

import java.util.List;

public record PageResponse<T>(List<T> elements, int page, int taille) {}
