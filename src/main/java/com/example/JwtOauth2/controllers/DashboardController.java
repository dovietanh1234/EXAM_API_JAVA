package com.example.JwtOauth2.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping("/welcome-message")
    public ResponseEntity<String> getFirstWelcomeMessage(Authentication authentication){
        return ResponseEntity.ok("welcome ... " + authentication.getName());
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER')")
    @GetMapping("/manager-message")
    public ResponseEntity<String> getManagerData(Principal principal){
        return ResponseEntity.ok("Manager " + principal.getName());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/admin-message")
    public ResponseEntity<String> getAdminData(@RequestParam("message") String data, Principal principal){
        return ResponseEntity.ok("Admin" + principal.getName() + "has this data is: "+ data);
    }

}
