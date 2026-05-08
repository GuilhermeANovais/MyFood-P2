package br.ufal.ic.myfood.exceptions;

public class UsuarioNaoExisteException extends Exception {

    public UsuarioNaoExisteException() {
        super("Usuario nao cadastrado.");
    }

}
