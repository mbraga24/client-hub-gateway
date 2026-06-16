package com.clienthub.gateway.client;

public record ClientRequest (
    String email,
    String firstName,
    String lastName,
    String phoneNumber
) {}
