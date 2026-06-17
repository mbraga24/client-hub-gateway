package com.clienthub.gateway.client;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/clients")
public class ClientController {
    
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Void> addClient(@RequestBody ClientRequest request) { 
        log.info("POST /api/v1/clients");
        Long clientId = clientService.addClient(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(clientId)
                .toUri();
        return ResponseEntity.created(location).build();
    }


}
