package model;

public abstract class Empresa {
    protected int id;
    protected String nome;
    protected String endereco;
    protected String tipoCozinha;
    protected int idDono;

    public Empresa(int id, String nome, String endereco, String tipoCozinha, int idDono) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.tipoCozinha = tipoCozinha;
        this.idDono = idDono;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getTipoCozinha() { return tipoCozinha; }
    public int getIdDono() { return idDono; }

    public abstract String getTipo();

    protected String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public String toJson() {
        return "{\"id\":" + id + ",\"tipo\":\"" + getTipo() + "\",\"nome\":\"" + escapeJson(nome) +
               "\",\"endereco\":\"" + escapeJson(endereco) + "\",\"tipoCozinha\":\"" + escapeJson(tipoCozinha) +
               "\",\"idDono\":" + idDono + "}";
    }
}
