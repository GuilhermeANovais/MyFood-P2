package repository;

import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntregadorEmpresaRepository {
    private List<EntregadorEmpresa> relacionamentos;
    private String filePath;

    public EntregadorEmpresaRepository(String dataPath) {
        this.filePath = dataPath + "/entregador_empresa.json";
        this.relacionamentos = new ArrayList<>();
        load();
    }

    public void adicionar(int empresa, int entregador) {
        for (EntregadorEmpresa ee : relacionamentos) {
            if (ee.empresaId == empresa && ee.entregadorId == entregador) {
                return; // already exists
            }
        }
        relacionamentos.add(new EntregadorEmpresa(empresa, entregador));
    }

    public boolean existe(int empresa, int entregador) {
        for (EntregadorEmpresa ee : relacionamentos) {
            if (ee.empresaId == empresa && ee.entregadorId == entregador) {
                return true;
            }
        }
        return false;
    }

    public List<Integer> getEmpresasDoEntregador(int entregador) {
        List<Integer> result = new ArrayList<>();
        for (EntregadorEmpresa ee : relacionamentos) {
            if (ee.entregadorId == entregador) {
                result.add(ee.empresaId);
            }
        }
        return result;
    }

    public List<Integer> getEntregadoresDaEmpresa(int empresa) {
        List<Integer> result = new ArrayList<>();
        for (EntregadorEmpresa ee : relacionamentos) {
            if (ee.empresaId == empresa) {
                result.add(ee.entregadorId);
            }
        }
        return result;
    }

    public void load() {
        try {
            String content = JsonUtil.readFile(filePath);
            List<String> objs = JsonUtil.parseObjectArray(content, "relacionamentos");
            relacionamentos.clear();
            for (String obj : objs) {
                int empresa = JsonUtil.parseInt(obj, "empresa");
                int entregador = JsonUtil.parseInt(obj, "entregador");
                relacionamentos.add(new EntregadorEmpresa(empresa, entregador));
            }
        } catch (IOException e) {
            relacionamentos.clear();
        }
    }

    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"relacionamentos\":[");
        for (int i = 0; i < relacionamentos.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append("{\"empresa\":").append(relacionamentos.get(i).empresaId);
            sb.append(",\"entregador\":").append(relacionamentos.get(i).entregadorId).append("}");
        }
        sb.append("]}");
        try {
            JsonUtil.writeFile(filePath, sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        relacionamentos.clear();
        save();
    }

    private static class EntregadorEmpresa {
        int empresaId;
        int entregadorId;

        EntregadorEmpresa(int empresaId, int entregadorId) {
            this.empresaId = empresaId;
            this.entregadorId = entregadorId;
        }
    }
}