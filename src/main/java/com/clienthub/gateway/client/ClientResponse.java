package com.clienthub.gateway.client;

public record ClientResponse (
    Long id,
    String email,
    String firstName,
    String lastName,
    String phoneNumber
) {}
