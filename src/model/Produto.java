package model;

public class Produto {
    private int id;
    private String nome;
    private float valor;
    private String categoria;
    private int idEmpresa;

    public Produto(int id, String nome, float valor, String categoria, int idEmpresa) {
        this.id = id;
        this.nome = nome;
        this.valor = valor;
        this.categoria = categoria;
        this.idEmpresa = idEmpresa;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public float getValor() { return valor; }
    public String getCategoria() { return categoria; }
    public int getIdEmpresa() { return idEmpresa; }

    public String toJson() {
        return "{\"id\":" + id + ",\"nome\":\"" + escapeJson(nome) +
               "\",\"valor\":" + valor + ",\"categoria\":\"" + escapeJson(categoria) +
               "\",\"idEmpresa\":" + idEmpresa + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
