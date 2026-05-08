package repository;

import model.Entrega;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntregaRepository {
    private List<Entrega> entregas;
    private int nextId;
    private String filePath;

    public EntregaRepository(String dataPath) {
        this.filePath = dataPath + "/entregas.json";
        this.entregas = new ArrayList<>();
        this.nextId = 1;
        load();
    }

    public void adicionar(Entrega entrega) {
        entregas.add(entrega);
    }

    public Entrega buscarPorId(int id) {
        for (Entrega e : entregas) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public Entrega buscarPorPedido(int pedido) {
        for (Entrega e : entregas) {
            if (e.getPedido() == pedido) return e;
        }
        return null;
    }

    public List<Entrega> listarTodas() {
        return new ArrayList<>(entregas);
    }

    public int getNextId() {
        return nextId++;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            nextId = JsonUtil.parseInt(content, "nextId");
            List<String> objs = JsonUtil.parseObjectArray(content, "entregas");
            entregas.clear();
            for (String obj : objs) {
                int id = JsonUtil.parseInt(obj, "id");
                int pedido = JsonUtil.parseInt(obj, "pedido");
                int entregador = JsonUtil.parseInt(obj, "entregador");
                String destino = JsonUtil.parseString(obj, "destino");
                String cliente = JsonUtil.parseString(obj, "cliente");
                String empresa = JsonUtil.parseString(obj, "empresa");
                List<String> produtos = JsonUtil.parseStringArray(obj, "produtos");
                entregas.add(new Entrega(id, pedido, entregador, destino, cliente, empresa, produtos));
            }
        } catch (IOException e) {
            entregas.clear();
            nextId = 1;
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"entregas\":[");
        for (int i = 0; i < entregas.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(entregas.get(i).toJson());
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        entregas.clear();
        nextId = 1;
        save();
    }
}