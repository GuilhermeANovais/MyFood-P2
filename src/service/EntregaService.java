package service;

import model.Entrega;
import model.Pedido;
import model.Produto;
import model.Empresa;
import model.Entregador;
import repository.EntregaRepository;
import repository.EntregadorEmpresaRepository;
import repository.PedidoRepository;
import repository.EmpresaRepository;
import repository.UsuarioRepository;

import java.util.ArrayList;
import java.util.List;

public class EntregaService {
    private EntregaRepository repository;
    private EntregadorEmpresaRepository entregadorEmpresaRepository;
    private PedidoRepository pedidoRepository;
    private EmpresaRepository empresaRepository;
    private UsuarioRepository usuarioRepository;

    public EntregaService(EntregaRepository repository, EntregadorEmpresaRepository entregadorEmpresaRepository,
                         PedidoRepository pedidoRepository, EmpresaRepository empresaRepository,
                         UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.entregadorEmpresaRepository = entregadorEmpresaRepository;
        this.pedidoRepository = pedidoRepository;
        this.empresaRepository = empresaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void cadastrarEntregador(int empresa, int entregador) throws Exception {
        if (!isEntregador(entregador)) {
            throw new Exception("Usuario nao e um entregador");
        }
        if (entregadorEmpresaRepository.existe(empresa, entregador)) {
            throw new Exception("Entregador ja cadastrado nesta empresa");
        }
        entregadorEmpresaRepository.adicionar(empresa, entregador);
    }

    private boolean isEntregador(int id) {
        return usuarioRepository.buscarPorId(id) instanceof Entregador;
    }

    public String getEntregadores(int empresa) throws Exception {
        StringBuilder sb = new StringBuilder("{[");
        boolean first = true;
        List<Integer> entregadores = entregadorEmpresaRepository.getEntregadoresDaEmpresa(empresa);
        for (Integer entregadorId : entregadores) {
            Entregador ent = (Entregador) usuarioRepository.buscarPorId(entregadorId);
            if (ent != null) {
                if (!first) sb.append(", ");
                sb.append(ent.getEmail());
                first = false;
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public String getEmpresas(int entregador) throws Exception {
        if (!isEntregador(entregador)) {
            throw new Exception("Usuario nao e um entregador");
        }
        StringBuilder sb = new StringBuilder("{[[");
        boolean first = true;
        List<Integer> empresas = entregadorEmpresaRepository.getEmpresasDoEntregador(entregador);
        for (Integer empresaId : empresas) {
            Empresa emp = empresaRepository.buscarPorId(empresaId);
            if (emp != null) {
                if (!first) sb.append("], [");
                sb.append(emp.getNome()).append(", ").append(emp.getEndereco());
                first = false;
            }
        }
        sb.append("]]}");
        return sb.toString();
    }

    public void liberarPedido(int numero) throws Exception {
        Pedido pedido = pedidoRepository.buscarPorNumero(numero);
        if (pedido == null) {
            throw new Exception("Pedido nao encontrado");
        }
        if ("pronto".equals(pedido.getEstado())) {
            throw new Exception("Pedido ja liberado");
        }
        if (!"preparando".equals(pedido.getEstado())) {
            throw new Exception("Nao e possivel liberar um produto que nao esta sendo preparado");
        }
        pedido.setEstado("pronto");
    }

    public int obterPedido(int entregador) throws Exception {
        if (!isEntregador(entregador)) {
            throw new Exception("Usuario nao e um entregador");
        }

        // Get companies where this entregador works
        List<Integer> empresasDoEntregador = entregadorEmpresaRepository.getEmpresasDoEntregador(entregador);

        if (empresasDoEntregador.isEmpty()) {
            throw new Exception("Entregador nao estar em nenhuma empresa.");
        }

        // Find ready orders from these companies
        // First, find pharmacy orders (priority)
        Pedido pharmacyPedido = null;
        Pedido otherPedido = null;

        for (Pedido p : pedidoRepository.listarTodos()) {
            if (!"pronto".equals(p.getEstado())) continue;
            if (!empresasDoEntregador.contains(p.getIdEmpresa())) continue;

            // Check if order already has an active delivery
            if (repository.buscarPorPedido(p.getNumero()) != null) continue;

            Empresa emp = empresaRepository.buscarPorId(p.getIdEmpresa());
            if (emp != null && "farmacia".equals(emp.getTipo())) {
                if (pharmacyPedido == null ||
                    (pharmacyPedido != null && p.getNumero() < pharmacyPedido.getNumero())) {
                    pharmacyPedido = p;
                }
            } else {
                if (otherPedido == null ||
                    (otherPedido != null && p.getNumero() < otherPedido.getNumero())) {
                    otherPedido = p;
                }
            }
        }

        // Pharmacy has priority
        if (pharmacyPedido != null) {
            return pharmacyPedido.getNumero();
        }
        if (otherPedido != null) {
            return otherPedido.getNumero();
        }

        throw new Exception("Nao existe pedido para entrega");
    }

    public int criarEntrega(int pedido, int entregador, String destino, UsuarioService usuarioService,
                           EmpresaService empresaService, ProdutoService produtoService) throws Exception {
        Pedido p = pedidoRepository.buscarPorNumero(pedido);
        if (p == null) {
            throw new Exception("Pedido nao encontrado");
        }
        if (!"pronto".equals(p.getEstado())) {
            throw new Exception("Pedido nao esta pronto para entrega");
        }
        if (!isEntregador(entregador)) {
            throw new Exception("Nao e um entregador valido");
        }

        // Check if entregador is already in a delivery
        for (Entrega e : repository.listarTodas()) {
            if (e.getEntregador() == entregador && "entregando".equals(getEstadoPedido(e.getPedido()))) {
                throw new Exception("Entregador ainda em entrega");
            }
        }

        String clienteNome = usuarioService.buscarPorId(p.getIdCliente()).getNome();
        String empresaNome = empresaService.buscarPorId(p.getIdEmpresa()).getNome();

        List<String> produtosNome = new ArrayList<>();
        for (int prodId : p.getProdutos()) {
            Produto prod = produtoService.buscarPorId(prodId);
            if (prod != null) {
                produtosNome.add(prod.getNome());
            }
        }

        String destinoFinal = destino;
        if (destinoFinal == null || destinoFinal.isEmpty()) {
            destinoFinal = usuarioService.buscarPorId(p.getIdCliente()).getEndereco();
        }

        int id = repository.getNextId();
        Entrega entrega = new Entrega(id, pedido, entregador, destinoFinal, clienteNome, empresaNome, produtosNome);
        repository.adicionar(entrega);

        p.setEstado("entregando");

        return id;
    }

    private String getEstadoPedido(int numero) {
        Pedido p = pedidoRepository.buscarPorNumero(numero);
        return p != null ? p.getEstado() : null;
    }

    public String getEntrega(int id, String atributo) throws Exception {
        Entrega entrega = repository.buscarPorId(id);
        if (entrega == null) {
            throw new Exception("Entrega nao encontrada");
        }
        if (atributo == null || atributo.isEmpty()) {
            throw new Exception("Atributo invalido");
        }
        switch (atributo.toLowerCase()) {
            case "cliente": return entrega.getCliente();
            case "empresa": return entrega.getEmpresa();
            case "pedido": return String.valueOf(entrega.getPedido());
            case "entregador":
                Entregador ent = (Entregador) usuarioRepository.buscarPorId(entrega.getEntregador());
                return ent != null ? ent.getNome() : "";
            case "destino": return entrega.getDestino();
            case "produtos":
                StringBuilder sb = new StringBuilder("{[");
                List<String> prods = entrega.getProdutos();
                for (int i = 0; i < prods.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(prods.get(i));
                }
                sb.append("]}");
                return sb.toString();
            default:
                throw new Exception("Atributo nao existe");
        }
    }

    public int getIdEntrega(int pedido) throws Exception {
        Entrega entrega = repository.buscarPorPedido(pedido);
        if (entrega == null) {
            throw new Exception("Nao existe entrega com esse id");
        }
        return entrega.getId();
    }

    public void entregar(int entregaId) throws Exception {
        Entrega entrega = repository.buscarPorId(entregaId);
        if (entrega == null) {
            throw new Exception("Nao existe nada para ser entregue com esse id");
        }
        Pedido pedido = pedidoRepository.buscarPorNumero(entrega.getPedido());
        if (pedido != null) {
            pedido.setEstado("entregue");
        }
    }

    public void save() {
        repository.save();
        entregadorEmpresaRepository.save();
    }

    public void clear() {
        repository.clear();
        entregadorEmpresaRepository.clear();
    }
}