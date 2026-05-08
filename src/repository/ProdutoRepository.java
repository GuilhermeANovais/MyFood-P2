
package repository;

import model.Produto;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {
    private List<Produto> produtos;
    private int nextId;
    private String filePath;

    public ProdutoRepository(String dataPath) {
        this.filePath = dataPath + "/produtos.json";
        this.produtos = new ArrayList<>();
        this.nextId = 1;
        load();
    }

    public void adicionar(Produto produto) {
        produtos.add(produto);
    }

    public void atualizar(Produto produtoAtualizado) {
        for (int i = 0; i < produtos.size(); i++) {
            if (produtos.get(i).getId() == produtoAtualizado.getId()) {
                produtos.set(i, produtoAtualizado);
                return;
            }
        }
    }

    public Produto buscarPorId(int id) {
        for (Produto p : produtos) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    public Produto buscarPorNomeEEmpresa(String nome, int idEmpresa) {
        for (Produto p : produtos) {
            if (p.getNome().equals(nome) && p.getIdEmpresa() == idEmpresa) return p;
        }
        return null;
    }

    public List<Produto> buscarPorEmpresa(int idEmpresa) {
        List<Produto> result = new ArrayList<>();
        for (Produto p : produtos) {
            if (p.getIdEmpresa() == idEmpresa) result.add(p);
        }
        return result;
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public int getNextId() {
        return nextId++;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            nextId = JsonUtil.parseInt(content, "nextId");
            List<String> objs = JsonUtil.parseObjectArray(content, "produtos");
            produtos.clear();
            for (String obj : objs) {
                int id = JsonUtil.parseInt(obj, "id");
                String nome = JsonUtil.parseString(obj, "nome");
                float valor = JsonUtil.parseFloat(obj, "valor");
                String categoria = JsonUtil.parseString(obj, "categoria");
                int idEmpresa = JsonUtil.parseInt(obj, "idEmpresa");
                produtos.add(new Produto(id, nome, valor, categoria, idEmpresa));
            }
        } catch (IOException e) {
            produtos.clear();
            nextId = 1;
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"produtos\":[");
        for (int i = 0; i < produtos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(produtos.get(i).toJson());
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        produtos.clear();
        nextId = 1;
        save();
    }
}
