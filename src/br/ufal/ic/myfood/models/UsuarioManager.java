package br.ufal.ic.myfood.models;

import br.ufal.ic.myfood.exceptions.UsuarioJaExisteException;
import br.ufal.ic.myfood.exceptions.UsuarioNaoExisteException;

import java.util.ArrayList;
import java.util.List;

public class UsuarioManager {

    List<Usuario> usuarioList;

    public UsuarioManager() {
        this.usuarioList = new ArrayList<Usuario>();
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf)
            throws UsuarioJaExisteException {
        for (Usuario usuario : this.usuarioList) {
            if (usuario.getEmail().equals(email)) {
                throw new UsuarioJaExisteException();
            }
        }

        this.usuarioList.add(new Usuario(nome, email, senha, endereco, cpf));
    }

    public String getAtributoUsuario(String id, String atributo) throws Exception {
        for (Usuario usuario : this.usuarioList) {
            if (usuario.getId().equals(id)) {
                if (atributo.equalsIgnoreCase("nome")) {
                    return usuario.getNome();
                }
            }
        }
        throw new UsuarioNaoExisteException();
    }

    public String login(String email, String senha) {
        for (Usuario usuario : this.usuarioList) {
            if (usuario.getEmail().equals(email)) {
                if (usuario.getSenha().equals(senha)) {
                    return usuario.getId();
                }
            }
        }
        return null;
    }

}
