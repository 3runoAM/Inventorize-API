package edu.infnet.inventorize.controllers;

import edu.infnet.inventorize.dto.request.AuthenticationRequestDTO;
import edu.infnet.inventorize.dto.response.AuthenticationResponseDTO;
import edu.infnet.inventorize.dto.response.UserResponseDTO;
import edu.infnet.inventorize.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Validated
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Controller de Autenticação", description = "Endpoints para autenticação de usuários")
public class AuthenticationController {
    private final AuthenticationService authenticationService;


    /**
     * Endpoint para autenticar um usuário.
     *
     * @param userData Dyo contendo os dados de autenticação do usuário.
     * @return Resposta com os dados do usuário autenticado.
     */
    @Operation(
            summary = "Autentica um usuário",
            description = "Autentica usuário com e-mail e senha, retornando um token JWT e informações do usuário para acesso ao sistema"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário logado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "158d6a25-4e6a-4357-8643-c4053e2f2a7b",
                                              "email": "user_mail@gmail.com"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ausentes",
                    content = @Content(
                            mediaType = "json/application",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "Erro de validação",
                                                      "errorDetails": [
                                                        "A senha deve ter entre 8 e 16 caracteres",
                                                        "A senha não pode ser vazia",
                                                        "Email não pode ser vazio"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Acesso negado",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 401,
                                                      "message": "Credenciais inválidas",
                                                      "errorDetails": [
                                                          "Usuário inexistente ou senha inválida"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDTO> login(@RequestBody @Valid AuthenticationRequestDTO userData) {
        AuthenticationResponseDTO authenticatedUser = authenticationService.authenticate(userData);

        return ResponseEntity.status(HttpStatus.CREATED).body(authenticatedUser);
    }

    /**
     * Endpoint para registrar um novo usuário.
     *
     * @param userData Dados do usuário a ser registrado.
     * @return Resposta com os dados do usuário registrado.
     */
    @Operation(
            summary = "Registra um novo usuário",
            description = "Registra um novo usuário com email e senha, retornando os dados do usuário registrado"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos ou ausentes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "status": 400,
                                                      "message": "Erro de validação",
                                                      "errorDetails": [
                                                        "A senha não pode ser vazia",
                                                        "Email não pode ser vazio",
                                                        "A senha deve ter entre 8 e 16 caracteres"
                                                      ],
                                                      "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                                    }"""
                                    )
                            }
                    )),
            @ApiResponse(
                    responseCode = "409",
                    description = "Dados já cadastrados ou conflitantes",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "status": 409,
                                              "message": "Email já cadastrado",
                                              "errorDetails": [
                                                "Email: username@email.com já cadastrado"
                                              ],
                                                "timestamp": "YYYY-dd-mmTHH:MM:ss"
                                            }"""
                            ))),
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário registrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "id": "7c3479e0-4ec5-4126-b54a-bef4ccdd32fb",
                                              "email": "user_mail@gmail.com",
                                              "_links" : {
                                                "login": {
                                                  "href": "http://localhost:8080/auth/login"
                                                }
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<EntityModel<UserResponseDTO>> register(@RequestBody @Valid AuthenticationRequestDTO userData) {
        UserResponseDTO savedUser = authenticationService.register(userData);

        EntityModel<UserResponseDTO> resource = EntityModel.of(savedUser,
                linkTo(methodOn(AuthenticationController.class).login(userData)).withRel("login"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
}