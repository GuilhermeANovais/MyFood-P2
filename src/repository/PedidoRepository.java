package repository;

import model.Pedido;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {
    private List<Pedido> pedidos;
    private int nextId;
    private String filePath;

    public PedidoRepository(String dataPath) {
        this.filePath = dataPath + "/pedidos.json";
        this.pedidos = new ArrayList<>();
        this.nextId = 1;
        load();
    }

    public void adicionar(Pedido pedido) {
        pedidos.add(pedido);
    }

    public Pedido buscarPorNumero(int numero) {
        for (Pedido p : pedidos) {
            if (p.getNumero() == numero) return p;
        }
        return null;
    }

    public List<Pedido> buscarPorCliente(int idCliente) {
        List<Pedido> result = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getIdCliente() == idCliente) result.add(p);
        }
        return result;
    }

    public List<Pedido> buscarPorClienteEEmpresa(int idCliente, int idEmpresa) {
        List<Pedido> result = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getIdCliente() == idCliente && p.getIdEmpresa() == idEmpresa) {
                result.add(p);
            }
        }
        return result;
    }

    public Pedido buscarPedidoAbertoPorClienteEEmpresa(int idCliente, int idEmpresa) {
        for (Pedido p : pedidos) {
            if (p.getIdCliente() == idCliente && p.getIdEmpresa() == idEmpresa && "aberto".equals(p.getEstado())) {
                return p;
            }
        }
        return null;
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos);
    }

    public int getNextId() {
        return nextId++;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            nextId = JsonUtil.parseInt(content, "nextId");
            List<String> objs = JsonUtil.parseObjectArray(content, "pedidos");
            pedidos.clear();
            for (String obj : objs) {
                int numero = JsonUtil.parseInt(obj, "numero");
                int idCliente = JsonUtil.parseInt(obj, "idCliente");
                int idEmpresa = JsonUtil.parseInt(obj, "idEmpresa");
                String estado = JsonUtil.parseString(obj, "estado");
                List<Integer> produtos = JsonUtil.parseIntArray(obj, "produtos");

                Pedido pedido = new Pedido(numero, idCliente, idEmpresa);
                if (estado != null && !estado.equals("aberto")) {
                    pedido.setEstado(estado);
                }
                for (int idProd : produtos) {
                    pedido.adicionarProduto(idProd);
                }
                pedidos.add(pedido);
            }
        } catch (IOException e) {
            pedidos.clear();
            nextId = 1;
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"pedidos\":[");
        for (int i = 0; i < pedidos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(pedidos.get(i).toJson());
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        pedidos.clear();
        nextId = 1;
        save();
    }
}
