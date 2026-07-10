package com.hugogonzalez.polentracker.core.collection;
import jakarta.validation.Valid; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/collections") public class CollectionController {private final CollectionService service; public CollectionController(CollectionService s){service=s;}
 @PostMapping ResponseEntity<CollectionEntity> start(@Valid @RequestBody StartCollectionCommand command){return ResponseEntity.accepted().body(service.start(command));} @GetMapping List<CollectionEntity> all(){return service.findAll();} @GetMapping("/{id}") ResponseEntity<CollectionEntity> one(@PathVariable UUID id){return ResponseEntity.of(service.find(id));}}
