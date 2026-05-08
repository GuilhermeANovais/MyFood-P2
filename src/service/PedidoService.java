package service;

import model.Pedido;
import model.Produto;
import repository.PedidoRepository;

import java.util.List;
import java.util.Locale;

public class PedidoService {
    private PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public int criarPedido(int cliente, int empresa, UsuarioService usuarioService, EmpresaService empresaService) throws Exception {
        if (usuarioService.isDonoEmpresa(cliente)) {
            throw new Exception("Dono de empresa nao pode fazer um pedido");
        }

        // Check if client already has an open order for this company
        if (repository.buscarPedidoAbertoPorClienteEEmpresa(cliente, empresa) != null) {
            throw new Exception("Nao e permitido ter dois pedidos em aberto para a mesma empresa");
        }

        int numero = repository.getNextId();
        Pedido pedido = new Pedido(numero, cliente, empresa);
        repository.adicionar(pedido);
        return numero;
    }

    public void adicionarProduto(int numero, int produto, ProdutoService produtoService, EmpresaService empresaService) throws Exception {
        Pedido pedido = repository.buscarPorNumero(numero);
        if (pedido == null) {
            throw new Exception("Nao existe pedido em aberto");
        }
        if (!"aberto".equals(pedido.getEstado())) {
            throw new Exception("Nao e possivel adcionar produtos a um pedido fechado");
        }

        Produto prod = produtoService.buscarPorId(produto);
        if (prod == null || prod.getIdEmpresa() != pedido.getIdEmpresa()) {
            throw new Exception("O produto nao pertence a essa empresa");
        }

        pedido.adicionarProduto(produto);
    }

    public String getPedidos(int numero, String atributo, UsuarioService usuarioService, EmpresaService empresaService, ProdutoService produtoService) throws Exception {
        Pedido pedido = repository.buscarPorNumero(numero);
        if (pedido == null) {
            throw new Exception("Pedido nao encontrado");
        }
        if (atributo == null || atributo.isEmpty()) {
            throw new Exception("Atributo invalido");
        }
        switch (atributo.toLowerCase()) {
            case "cliente":
                return usuarioService.buscarPorId(pedido.getIdCliente()).getNome();
            case "empresa":
                return empresaService.buscarPorId(pedido.getIdEmpresa()).getNome();
            case "estado":
                return pedido.getEstado();
            case "produtos":
                List<Integer> produtoIds = pedido.getProdutos();
                StringBuilder sb = new StringBuilder("{[");
                for (int i = 0; i < produtoIds.size(); i++) {
                    if (i > 0) sb.append(", ");
                    Produto p = produtoService.buscarPorId(produtoIds.get(i));
                    sb.append(p.getNome());
                }
                sb.append("]}");
                return sb.toString();
            case "valor":
                return String.format(Locale.US, "%.2f", pedido.calcularValor(produtoService));
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    public void fecharPedido(int numero) throws Exception {
        Pedido pedido = repository.buscarPorNumero(numero);
        if (pedido == null) {
            throw new Exception("Pedido nao encontrado");
        }
        pedido.fechar();
    }

    public void removerProduto(int pedidoNumero, String produtoNome, ProdutoService produtoService) throws Exception {
        if (produtoNome == null || produtoNome.isEmpty()) {
            throw new Exception("Produto invalido");
        }
        Pedido pedido = repository.buscarPorNumero(pedidoNumero);
        if (pedido == null) {
            throw new Exception("Pedido nao encontrado");
        }
        if (!"aberto".equals(pedido.getEstado())) {
            throw new Exception("Nao e possivel remover produtos de um pedido fechado");
        }

        // Check if product exists in order
        boolean found = false;
        for (int idProd : pedido.getProdutos()) {
            Produto p = produtoService.buscarPorId(idProd);
            if (p != null && p.getNome().equals(produtoNome)) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new Exception("Produto nao encontrado");
        }

        pedido.removerProduto(produtoNome, produtoService);
    }

    public int getNumeroPedido(int cliente, int empresa, int indice) throws Exception {
        List<Pedido> pedidos = repository.buscarPorClienteEEmpresa(cliente, empresa);
        if (indice < 0 || indice >= pedidos.size()) {
            throw new Exception("Indice invalido");
        }
        return pedidos.get(indice).getNumero();
    }

    public void save() {
        repository.save();
    }

    public void clear() {
        repository.clear();
    }
}
