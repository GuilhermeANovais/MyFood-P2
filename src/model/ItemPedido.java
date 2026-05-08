package model;

public class ItemPedido {
    private int idProduto;
    private int quantidade;

    public ItemPedido(int idProduto, int quantidade) {
        this.idProduto = idProduto;
        this.quantidade = quantidade;
    }

    public int getIdProduto() { return idProduto; }
    public int getQuantidade() { return quantidade; }
}
