package model;

import java.util.ArrayList;
import java.util.List;
import service.ProdutoService;

public class Pedido {
    private int numero;
    private int idCliente;
    private int idEmpresa;
    private String estado;
    private List<Integer> produtos; // List of product IDs

    public Pedido(int numero, int idCliente, int idEmpresa) {
        this.numero = numero;
        this.idCliente = idCliente;
        this.idEmpresa = idEmpresa;
        this.estado = "aberto";
        this.produtos = new ArrayList<>();
    }

    public int getNumero() { return numero; }
    public int getIdCliente() { return idCliente; }
    public int getIdEmpresa() { return idEmpresa; }
    public String getEstado() { return estado; }
    public List<Integer> getProdutos() { return produtos; }

    public void fechar() {
        this.estado = "preparando";
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void adicionarProduto(int idProduto) {
        produtos.add(idProduto);
    }

    public void removerProduto(String nomeProduto, ProdutoService produtoService) {
        for (int i = 0; i < produtos.size(); i++) {
            Produto p = produtoService.buscarPorId(produtos.get(i));
            if (p != null && p.getNome().equals(nomeProduto)) {
                produtos.remove(i);
                return;
            }
        }
    }

    public float calcularValor(ProdutoService produtoService) {
        float total = 0;
        for (int idProduto : produtos) {
            Produto p = produtoService.buscarPorId(idProduto);
            if (p != null) {
                total += p.getValor();
            }
        }
        return total;
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"numero\":").append(numero);
        sb.append(",\"idCliente\":").append(idCliente);
        sb.append(",\"idEmpresa\":").append(idEmpresa);
        sb.append(",\"estado\":\"").append(estado).append("\"");
        sb.append(",\"produtos\":");
        sb.append("[");
        for (int i = 0; i < produtos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(produtos.get(i));
        }
        sb.append("]}");
        return sb.toString();
    }
}
