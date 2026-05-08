package facade;

import model.Empresa;
import model.Produto;
import repository.*;
import service.*;

public class Facade {

    private UsuarioRepository usuarioRepository;
    private EmpresaRepository empresaRepository;
    private ProdutoRepository produtoRepository;
    private PedidoRepository pedidoRepository;
    private EntregaRepository entregaRepository;
    private EntregadorEmpresaRepository entregadorEmpresaRepository;

    private UsuarioService usuarioService;
    private EmpresaService empresaService;
    private ProdutoService produtoService;
    private PedidoService pedidoService;
    private EntregaService entregaService;

    private static final String DATA_PATH = "data";

    public Facade() {
        initRepositories();
        initServices();
    }

    private void initRepositories() {
        usuarioRepository = new UsuarioRepository(DATA_PATH);
        empresaRepository = new EmpresaRepository(DATA_PATH);
        produtoRepository = new ProdutoRepository(DATA_PATH);
        pedidoRepository = new PedidoRepository(DATA_PATH);
        entregaRepository = new EntregaRepository(DATA_PATH);
        entregadorEmpresaRepository = new EntregadorEmpresaRepository(DATA_PATH);
    }

    private void initServices() {
        usuarioService = new UsuarioService(usuarioRepository);
        empresaService = new EmpresaService(empresaRepository);
        produtoService = new ProdutoService(produtoRepository);
        pedidoService = new PedidoService(pedidoRepository);
        entregaService = new EntregaService(entregaRepository, entregadorEmpresaRepository, pedidoRepository, empresaRepository, usuarioRepository);
    }

    public void zerarSistema() {
        usuarioService.clear();
        empresaService.clear();
        produtoService.clear();
        pedidoService.clear();
        entregaService.clear();
    }

    public void encerrarSistema() {
        usuarioService.save();
        empresaService.save();
        produtoService.save();
        pedidoService.save();
        entregaService.save();
    }

    // ==================== USUARIOS ====================

    public void criarUsuario(String nome, String email, String senha, String endereco) throws Exception {
        usuarioService.criarUsuario(nome, email, senha, endereco);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String cpf) throws Exception {
        usuarioService.criarUsuario(nome, email, senha, endereco, cpf);
    }

    public void criarUsuario(String nome, String email, String senha, String endereco, String veiculo, String placa) throws Exception {
        usuarioService.criarUsuario(nome, email, senha, endereco, veiculo, placa);
    }

    public int login(String email, String senha) throws Exception {
        return usuarioService.login(email, senha);
    }

    public String getAtributoUsuario(int id, String atributo) throws Exception {
        return usuarioService.getAtributoUsuario(id, atributo);
    }

    // ==================== EMPRESAS ====================

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha) throws Exception {
        return empresaService.criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha, usuarioService);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String abre, String fecha, String tipoMercado) throws Exception {
        return empresaService.criarEmpresa(tipoEmpresa, dono, nome, endereco, abre, fecha, tipoMercado, usuarioService);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios) throws Exception {
        return empresaService.criarEmpresa(tipoEmpresa, dono, nome, endereco, aberto24Horas, numeroFuncionarios, usuarioService);
    }

    public String getEmpresasDoUsuario(int idDono) throws Exception {
        return empresaService.getEmpresasDoUsuario(idDono, usuarioService);
    }

    public String getAtributoEmpresa(int empresa, String atributo) throws Exception {
        return empresaService.getAtributoEmpresa(empresa, atributo, usuarioService);
    }

    public int getIdEmpresa(int idDono, String nome, int indice) throws Exception {
        return empresaService.getIdEmpresa(idDono, nome, indice);
    }

    public void alterarFuncionamento(int mercado, String abre, String fecha) throws Exception {
        empresaService.alterarFuncionamento(mercado, abre, fecha);
    }

    // ==================== PRODUTOS ====================

    public int criarProduto(int empresa, String nome, float valor, String categoria) throws Exception {
        return produtoService.criarProduto(empresa, nome, valor, categoria, empresaService);
    }

    public void editarProduto(int produto, String nome, float valor, String categoria) throws Exception {
        produtoService.editarProduto(produto, nome, valor, categoria);
    }

    public String getProduto(String nome, int empresa, String atributo) throws Exception {
        if ("empresa".equalsIgnoreCase(atributo)) {
            Produto prod = produtoService.buscarPorNomeEEmpresa(nome, empresa);
            if (prod == null) {
                throw new Exception("Produto nao encontrado");
            }
            return empresaService.buscarPorId(prod.getIdEmpresa()).getNome();
        }
        return produtoService.getProduto(nome, empresa, atributo);
    }

    public String listarProdutos(int empresa) throws Exception {
        return produtoService.listarProdutos(empresa, empresaService);
    }

    // ==================== PEDIDOS ====================

    public int criarPedido(int cliente, int empresa) throws Exception {
        return pedidoService.criarPedido(cliente, empresa, usuarioService, empresaService);
    }

    public void adicionarProduto(int numero, int produto) throws Exception {
        pedidoService.adicionarProduto(numero, produto, produtoService, empresaService);
    }

    public String getPedidos(int numero, String atributo) throws Exception {
        return pedidoService.getPedidos(numero, atributo, usuarioService, empresaService, produtoService);
    }

    public void fecharPedido(int numero) throws Exception {
        pedidoService.fecharPedido(numero);
    }

    public void removerProduto(int pedido, String produto) throws Exception {
        pedidoService.removerProduto(pedido, produto, produtoService);
    }

    public int getNumeroPedido(int cliente, int empresa, int indice) throws Exception {
        return pedidoService.getNumeroPedido(cliente, empresa, indice);
    }

    // ==================== ENTREGAS ====================

    public void cadastrarEntregador(int empresa, int entregador) throws Exception {
        entregaService.cadastrarEntregador(empresa, entregador);
    }

    public String getEntregadores(int empresa) throws Exception {
        return entregaService.getEntregadores(empresa);
    }

    public String getEmpresas(int entregador) throws Exception {
        return entregaService.getEmpresas(entregador);
    }

    public void liberarPedido(int numero) throws Exception {
        entregaService.liberarPedido(numero);
    }

    public int obterPedido(int entregador) throws Exception {
        return entregaService.obterPedido(entregador);
    }

    public int criarEntrega(int pedido, int entregador, String destino) throws Exception {
        return entregaService.criarEntrega(pedido, entregador, destino, usuarioService, empresaService, produtoService);
    }

    public String getEntrega(int id, String atributo) throws Exception {
        return entregaService.getEntrega(id, atributo);
    }

    public int getIdEntrega(int pedido) throws Exception {
        return entregaService.getIdEntrega(pedido);
    }

    public void entregar(int entrega) throws Exception {
        entregaService.entregar(entrega);
    }
}
