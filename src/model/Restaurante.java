package model;

public class Restaurante extends Empresa {

    public Restaurante(int id, String nome, String endereco, String tipoCozinha, int idDono) {
        super(id, nome, endereco, tipoCozinha, idDono);
    }

    @Override
    public String getTipo() {
        return "restaurante";
    }
}
