package model;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String email;
    protected String senha;
    protected String endereco;

    public Usuario(int id, String nome, String email, String senha, String endereco) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.endereco = endereco;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getSenha() { return senha; }
    public String getEndereco() { return endereco; }

    public abstract String getTipo();

    public String toJson() {
        return "{\"id\":" + id + ",\"tipo\":\"" + getTipo() + "\",\"nome\":\"" + escapeJson(nome) +
               "\",\"email\":\"" + escapeJson(email) + "\",\"senha\":\"" + escapeJson(senha) +
               "\",\"endereco\":\"" + escapeJson(endereco) + "\"";
    }

    protected String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
