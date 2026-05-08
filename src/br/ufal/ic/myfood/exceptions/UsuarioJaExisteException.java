package br.ufal.ic.myfood.exceptions;

public class UsuarioJaExisteException extends Exception {

    public UsuarioJaExisteException() {
        super("Conta com esse email ja existe");
    }

}
