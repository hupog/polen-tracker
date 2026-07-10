package com.hugogonzalez.polentracker.core.adapter.in.web;
import com.hugogonzalez.polentracker.core.application.model.StartCollectionCommand;
import com.hugogonzalez.polentracker.core.application.port.in.*;
import com.hugogonzalez.polentracker.core.domain.model.PollenCollection;
import com.hugogonzalez.polentracker.domain.GeographicLocation;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/collections")
public class CollectionController {
    private final StartCollectionUseCase start; private final QueryCollectionsUseCase query;
    public CollectionController(StartCollectionUseCase start, QueryCollectionsUseCase query) { this.start=start; this.query=query; }
    @PostMapping ResponseEntity<PollenCollection> start(@Valid @RequestBody StartCollectionRequest request) {
        var l=request.location(); var command=new StartCollectionCommand(request.sourceType(),request.dateFrom(),request.dateTo(),new GeographicLocation(l.name(),l.latitude(),l.longitude()));
        return ResponseEntity.accepted().body(start.start(command));
    }
    @GetMapping List<PollenCollection> all(){ return query.findAll(); }
    @GetMapping("/{id}") ResponseEntity<PollenCollection> one(@PathVariable UUID id){ return ResponseEntity.of(query.find(id)); }
}
