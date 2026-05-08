package service;

import model.Produto;
import repository.ProdutoRepository;

import java.util.List;
import java.util.Locale;

public class ProdutoService {
    private ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public int criarProduto(int empresa, String nome, float valor, String categoria, EmpresaService empresaService) throws Exception {
        if (empresaService.buscarPorId(empresa) == null) {
            throw new Exception("Empresa nao encontrada");
        }
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (valor < 0) throw new Exception("Valor invalido");
        if (categoria == null || categoria.isEmpty()) throw new Exception("Categoria invalido");

        // Check if product with same name already exists for this company
        if (repository.buscarPorNomeEEmpresa(nome, empresa) != null) {
            throw new Exception("Ja existe um produto com esse nome para essa empresa");
        }

        int id = repository.getNextId();
        Produto produto = new Produto(id, nome, valor, categoria, empresa);
        repository.adicionar(produto);
        return id;
    }

    public void editarProduto(int produto, String nome, float valor, String categoria) throws Exception {
        Produto prod = repository.buscarPorId(produto);
        if (prod == null) {
            throw new Exception("Produto nao cadastrado");
        }
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (valor < 0) throw new Exception("Valor invalido");
        if (categoria == null || categoria.isEmpty()) throw new Exception("Categoria invalido");

        // Update product in place
        repository.atualizar(new Produto(produto, nome, valor, categoria, prod.getIdEmpresa()));
    }

    public String getProduto(String nome, int empresa, String atributo) throws Exception {
        Produto prod = repository.buscarPorNomeEEmpresa(nome, empresa);
        if (prod == null) {
            throw new Exception("Produto nao encontrado");
        }
        if (atributo == null || atributo.isEmpty()) {
            throw new Exception("Atributo nao existe");
        }
        switch (atributo.toLowerCase()) {
            case "valor":
                return String.format(Locale.US, "%.2f", prod.getValor());
            case "categoria":
                return prod.getCategoria();
            case "empresa":
                // This needs to return the company name, handled by Facade
                return String.valueOf(prod.getIdEmpresa());
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    public String listarProdutos(int empresa, EmpresaService empresaService) throws Exception {
        if (empresaService.buscarPorId(empresa) == null) {
            throw new Exception("Empresa nao encontrada");
        }
        List<Produto> produtos = repository.buscarPorEmpresa(empresa);
        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < produtos.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(produtos.get(i).getNome());
        }
        sb.append("]}");
        return sb.toString();
    }

    public Produto buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public Produto buscarPorNomeEEmpresa(String nome, int idEmpresa) {
        return repository.buscarPorNomeEEmpresa(nome, idEmpresa);
    }

    public void save() {
        repository.save();
    }

    public void clear() {
        repository.clear();
    }
}
