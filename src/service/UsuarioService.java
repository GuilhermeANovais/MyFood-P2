package service;

import model.Usuario;
import model.Cliente;
import model.DonoEmpresa;
import model.Entregador;
import repository.UsuarioRepository;

import java.util.List;

public class UsuarioService {
    private UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public void criarUsuario(String nome, String email, String senha, String endereco) throws Exception {
        validarDadosUsuario(nome, email, senha, endereco);
        if (repository.buscarPorEmail(email) != null) {
            throw new Exception("Conta com esse email ja existe");
        }
        int id = repository.getNextId();
        repository.adicionar(new Cliente(id, nome, email, senha, endereco));
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) throws Exception {
        validarDadosUsuario(nome, email, senha, endereco);
        if (cpf == null || cpf.isEmpty() || cpf.length() != 14) {
            throw new Exception("CPF invalido");
        }
        if (repository.buscarPorEmail(email) != null) {
            throw new Exception("Conta com esse email ja existe");
        }
        int id = repository.getNextId();
        repository.adicionar(new DonoEmpresa(id, nome, email, senha, endereco, cpf));
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String veiculo, String placa) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (email == null || email.isEmpty() || !email.contains("@")) throw new Exception("Email invalido");
        if (senha == null || senha.isEmpty()) throw new Exception("Senha invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco invalido");
        if (veiculo == null || veiculo.isEmpty()) throw new Exception("Veiculo invalido");
        if (placa == null || placa.isEmpty() || placa.length() != 8) throw new Exception("Placa invalido");
        if (repository.buscarPorPlaca(placa) != null) {
            throw new Exception("Placa invalido");
        }
        if (repository.buscarPorEmail(email) != null) {
            throw new Exception("Conta com esse email ja existe");
        }
        int id = repository.getNextId();
        repository.adicionar(new Entregador(id, nome, email, senha, endereco, veiculo, placa));
    }

    private void validarDadosUsuario(String nome, String email, String senha, String endereco) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (email == null || email.isEmpty() || !email.contains("@")) throw new Exception("Email invalido");
        if (senha == null || senha.isEmpty()) throw new Exception("Senha invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco invalido");
    }

    public int login(String email, String senha) throws Exception {
        if (email == null || email.isEmpty() || senha == null || senha.isEmpty()) {
            throw new Exception("Login ou senha invalidos");
        }
        Usuario usuario = repository.buscarPorEmail(email);
        if (usuario == null || !usuario.getSenha().equals(senha)) {
            throw new Exception("Login ou senha invalidos");
        }
        return usuario.getId();
    }

    public String getAtributoUsuario(int id, String atributo) throws Exception {
        Usuario usuario = repository.buscarPorId(id);
        if (usuario == null) {
            throw new Exception("Usuario nao cadastrado.");
        }
        if (atributo == null || atributo.isEmpty()) {
            throw new Exception("Atributo invalido");
        }
        switch (atributo.toLowerCase()) {
            case "nome": return usuario.getNome();
            case "email": return usuario.getEmail();
            case "senha": return usuario.getSenha();
            case "endereco": return usuario.getEndereco();
            case "cpf":
                if (usuario instanceof DonoEmpresa) {
                    return ((DonoEmpresa) usuario).getCpf();
                }
                throw new Exception("Atributo invalido");
            case "veiculo":
                if (usuario instanceof Entregador) {
                    return ((Entregador) usuario).getVeiculo();
                }
                throw new Exception("Atributo invalido");
            case "placa":
                if (usuario instanceof Entregador) {
                    return ((Entregador) usuario).getPlaca();
                }
                throw new Exception("Atributo invalido");
            default:
                throw new Exception("Atributo invalido");
        }
    }

    public Usuario buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public boolean isDonoEmpresa(int id) {
        Usuario usuario = repository.buscarPorId(id);
        return usuario instanceof DonoEmpresa;
    }

    public boolean isEntregador(int id) {
        Usuario usuario = repository.buscarPorId(id);
        return usuario instanceof Entregador;
    }

    public void save() {
        repository.save();
    }

    public void clear() {
        repository.clear();
    }
}
