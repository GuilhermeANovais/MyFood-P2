package model;

public class Entregador extends Usuario {
    private String veiculo;
    private String placa;

    public Entregador(int id, String nome, String email, String senha, String endereco, String veiculo, String placa) {
        super(id, nome, email, senha, endereco);
        this.veiculo = veiculo;
        this.placa = placa;
    }

    public String getVeiculo() { return veiculo; }
    public String getPlaca() { return placa; }

    @Override
    public String getTipo() {
        return "entregador";
    }

    @Override
    public String toJson() {
        return super.toJson() + ",\"veiculo\":\"" + escapeJson(veiculo) +
               "\",\"placa\":\"" + escapeJson(placa) + "\"}";
    }
}