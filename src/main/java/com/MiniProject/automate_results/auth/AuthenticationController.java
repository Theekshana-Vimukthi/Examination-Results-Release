package com.MiniProject.automate_results.auth;

import com.MiniProject.automate_results.dto.Roles;
import com.MiniProject.automate_results.service.exception.BadRequestException;
import com.MiniProject.automate_results.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

//  @PostMapping("/register")
//  public ResponseEntity<AuthenticationResponse> register(
//      @RequestBody RegisterRequest request
//  ) throws BadRequestException {
//    return ResponseEntity.ok(service.register(request));
//  }

  @PostMapping("/register/depSecretary")
  public ResponseEntity<AuthenticationResponse> registerSec(
          @RequestBody RegisterRequest request
  ) throws BadRequestException {
    return ResponseEntity.ok(service.register(request, Role.SECRETARY));
  }

  @PostMapping("/register/lecture")
  public ResponseEntity<AuthenticationResponse> registerLec(
          @RequestBody RegisterRequest request
  ) throws BadRequestException {
    return ResponseEntity.ok(service.register(request, Role.LECTURE));
  }

  @PostMapping("/register/hod")
  public ResponseEntity<AuthenticationResponse> registerHod(
          @RequestBody RegisterRequest request
  ) throws BadRequestException {
    return ResponseEntity.ok(service.register(request, Role.HOD));
  }

  @PostMapping("/register/dean")
  public ResponseEntity<AuthenticationResponse> registerDean(
          @RequestBody RegisterRequest request
  ) throws BadRequestException {
    return ResponseEntity.ok(service.register(request, Role.DEAN));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


}
