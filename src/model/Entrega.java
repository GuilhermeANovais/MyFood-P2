package model;

import java.util.ArrayList;
import java.util.List;

public class Entrega {
    private int id;
    private int pedido;
    private int entregador;
    private String destino;
    private String cliente;
    private String empresa;
    private List<String> produtos;

    public Entrega(int id, int pedido, int entregador, String destino, String cliente, String empresa, List<String> produtos) {
        this.id = id;
        this.pedido = pedido;
        this.entregador = entregador;
        this.destino = destino;
        this.cliente = cliente;
        this.empresa = empresa;
        this.produtos = produtos;
    }

    public int getId() { return id; }
    public int getPedido() { return pedido; }
    public int getEntregador() { return entregador; }
    public String getDestino() { return destino; }
    public String getCliente() { return cliente; }
    public String getEmpresa() { return empresa; }
    public List<String> getProdutos() { return produtos; }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\":").append(id);
        sb.append(",\"pedido\":").append(pedido);
        sb.append(",\"entregador\":").append(entregador);
        sb.append(",\"destino\":\"").append(destino != null ? destino : "").append("\"");
        sb.append(",\"cliente\":\"").append(cliente != null ? cliente : "").append("\"");
        sb.append(",\"empresa\":\"").append(empresa != null ? empresa : "").append("\"");
        sb.append(",\"produtos\":[");
        for (int i = 0; i < produtos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("\"").append(produtos.get(i)).append("\"");
        }
        sb.append("]}");
        return sb.toString();
    }
}