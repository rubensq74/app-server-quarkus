package com.example.appserver.config;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@ApplicationScoped
public class BooksConfig {
    @ConfigProperty(name="parametros.config.valor1")
    String valor1;
    @ConfigProperty(name="parametros.config.valor2")
    String valor2;
    @ConfigProperty(name="parametros.config.valor3")
    String valor3;
}
