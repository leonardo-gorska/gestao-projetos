package br.com.gestaoprojetos.util;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Validador {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    // CPF simples: 11 dígitos (apenas formato). Se quiser validação dos dígitos verificadores, eu implemento.
    private static final String CPF_REGEX = "\\d{11}";

    public static boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public static boolean isEmailValido(String email) {
        if (isVazio(email)) return false;
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isCPFValido(String cpf) {
        if (isVazio(cpf)) return false;
        String somenteDigitos = cpf.replaceAll("\\D", "");
        return Pattern.matches(CPF_REGEX, somenteDigitos);
    }

    public static boolean isPeriodoValido(LocalDate inicio, LocalDate termino) {
        if (inicio == null && termino == null) return true; // se ambos nulos, considerar válido (opcional)
        if (inicio == null || termino == null) return false;
        return !termino.isBefore(inicio);
    }
}
