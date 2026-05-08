package br.ufal.ic.myfood;

import br.ufal.ic.myfood.exceptions.UsuarioJaExisteException;
import br.ufal.ic.myfood.exceptions.UsuarioNaoExisteException;
import br.ufal.ic.myfood.models.Usuario;
import br.ufal.ic.myfood.models.UsuarioManager;

import java.util.ArrayList;
import java.util.List;

public class Facade {

    UsuarioManager userManager;


    public void zerarSistema() {
        this.userManager = new UsuarioManager();
    }

    public String getAtributoUsuario(String id, String atributo) throws Exception {
        return this.userManager.getAtributoUsuario(id, atributo);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco)
            throws UsuarioJaExisteException {
        criarUsuario(nome, email, senha, endereco, null);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf)
            throws UsuarioJaExisteException {
        this.userManager.criarUsuario(nome, email, senha, endereco, cpf);
    }

    public String login(String email, String senha) {
        return this.userManager.login(email, senha);
    }

}
