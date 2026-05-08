package repository;

import model.Empresa;
import model.Restaurante;
import model.Mercado;
import model.Farmacia;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EmpresaRepository {
    private List<Empresa> empresas;
    private int nextId;
    private String filePath;

    public EmpresaRepository(String dataPath) {
        this.filePath = dataPath + "/empresas.json";
        this.empresas = new ArrayList<>();
        this.nextId = 1;
        load();
    }

    public void adicionar(Empresa empresa) {
        empresas.add(empresa);
    }

    public Empresa buscarPorId(int id) {
        for (Empresa e : empresas) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    public List<Empresa> buscarPorDono(int idDono) {
        List<Empresa> result = new ArrayList<>();
        for (Empresa e : empresas) {
            if (e.getIdDono() == idDono) result.add(e);
        }
        return result;
    }

    public Empresa buscarPorNomeEDono(String nome, int idDono) {
        for (Empresa e : empresas) {
            if (e.getNome().equals(nome) && e.getIdDono() == idDono) return e;
        }
        return null;
    }

    public Empresa buscarPorNome(String nome) {
        for (Empresa e : empresas) {
            if (e.getNome().equals(nome)) return e;
        }
        return null;
    }

    public List<Empresa> listarTodas() {
        return new ArrayList<>(empresas);
    }

    public int getNextId() {
        return nextId++;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            nextId = JsonUtil.parseInt(content, "nextId");
            List<String> objs = JsonUtil.parseObjectArray(content, "empresas");
            empresas.clear();
            for (String obj : objs) {
                int id = JsonUtil.parseInt(obj, "id");
                String nome = JsonUtil.parseString(obj, "nome");
                String endereco = JsonUtil.parseString(obj, "endereco");
                String tipoCozinha = JsonUtil.parseString(obj, "tipoCozinha");
                int idDono = JsonUtil.parseInt(obj, "idDono");
                String tipo = JsonUtil.parseString(obj, "tipo");

                if ("restaurante".equals(tipo)) {
                    empresas.add(new Restaurante(id, nome, endereco, tipoCozinha, idDono));
                } else if ("mercado".equals(tipo)) {
                    String abre = JsonUtil.parseString(obj, "abre");
                    String fecha = JsonUtil.parseString(obj, "fecha");
                    String tipoMercado = JsonUtil.parseString(obj, "tipoMercado");
                    empresas.add(new Mercado(id, nome, endereco, abre, fecha, tipoMercado, idDono));
                } else if ("farmacia".equals(tipo)) {
                    boolean aberto24Horas = JsonUtil.parseBoolean(obj, "aberto24Horas");
                    int numeroFuncionarios = JsonUtil.parseInt(obj, "numeroFuncionarios");
                    empresas.add(new Farmacia(id, nome, endereco, aberto24Horas, numeroFuncionarios, idDono));
                }
            }
        } catch (IOException e) {
            empresas.clear();
            nextId = 1;
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"nextId\":").append(nextId).append(",\"empresas\":[");
        for (int i = 0; i < empresas.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(empresas.get(i).toJson());
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        empresas.clear();
        nextId = 1;
        save();
    }
}
