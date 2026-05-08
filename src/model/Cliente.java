package model;

public class Cliente extends Usuario {

    public Cliente(int id, String nome, String email, String senha, String endereco) {
        super(id, nome, email, senha, endereco);
    }

    @Override
    public String getTipo() {
        return "cliente";
    }

    @Override
    public String toJson() {
        return super.toJson() + "}";
    }
}
