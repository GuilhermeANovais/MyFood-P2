package repository;

import model.Usuario;
import model.Cliente;
import model.DonoEmpresa;
import model.Entregador;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {
    private List<Usuario> usuarios;
    private int nextId;
    private String filePath;

    public UsuarioRepository(String dataPath) {
        this.filePath = dataPath + "/usuarios.json";
        this.usuarios = new ArrayList<>();
        this.nextId = 1;
        load();
    }

    public void adicionar(Usuario usuario) {
        usuarios.add(usuario);
    }

    public Usuario buscarPorId(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) return u;
        }
        return null;
    }

    public Usuario buscarPorPlaca(String placa) {
        for (Usuario u : usuarios) {
            if (u instanceof Entregador && ((Entregador) u).getPlaca().equals(placa)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }

    public int getNextId() {
        return nextId++;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            nextId = JsonUtil.parseInt(content, "nextId");
            List<String> objs = JsonUtil.parseObjectArray(content, "usuarios");
            usuarios.clear();
            for (String obj : objs) {
                String tipo = JsonUtil.parseString(obj, "tipo");
                int id = JsonUtil.parseInt(obj, "id");
                String nome = JsonUtil.parseString(obj, "nome");
                String email = JsonUtil.parseString(obj, "email");
                String senha = JsonUtil.parseString(obj, "senha");
                String endereco = JsonUtil.parseString(obj, "endereco");

                if ("dono".equals(tipo)) {
                    String cpf = JsonUtil.parseString(obj, "cpf");
                    usuarios.add(new DonoEmpresa(id, nome, email, senha, endereco, cpf));
                } else if ("entregador".equals(tipo)) {
                    String veiculo = JsonUtil.parseString(obj, "veiculo");
                    String placa = JsonUtil.parseString(obj, "placa");
                    usuarios.add(new Entregador(id, nome, email, senha, endereco, veiculo, placa));
                } else {
                    usuarios.add(new Cliente(id, nome, email, senha, endereco));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, start fresh
            usuarios.clear();
            nextId = 1;
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"usuarios\":[");
        for (int i = 0; i < usuarios.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(usuarios.get(i).toJson());
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        usuarios.clear();
        nextId = 1;
        save();
    }
}
