package model;

public class DonoEmpresa extends Usuario {
    private String cpf;

    public DonoEmpresa(int id, String nome, String email, String senha, String endereco, String cpf) {
        super(id, nome, email, senha, endereco);
        this.cpf = cpf;
    }

    public String getCpf() { return cpf; }

    @Override
    public String getTipo() {
        return "dono";
    }

    @Override
    public String toJson() {
        return super.toJson() + ",\"cpf\":\"" + escapeJson(cpf) + "\"}";
    }
}
