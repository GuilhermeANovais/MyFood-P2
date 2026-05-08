package model;

public class Farmacia extends Empresa {
    private boolean aberto24Horas;
    private int numeroFuncionarios;

    public Farmacia(int id, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios, int idDono) {
        super(id, nome, endereco, null, idDono);
        this.aberto24Horas = aberto24Horas;
        this.numeroFuncionarios = numeroFuncionarios;
    }

    public boolean isAberto24Horas() { return aberto24Horas; }
    public int getNumeroFuncionarios() { return numeroFuncionarios; }

    @Override
    public String getTipo() {
        return "farmacia";
    }

    @Override
    public String toJson() {
        return "{\"id\":" + id + ",\"tipo\":\"" + getTipo() + "\",\"nome\":\"" + escapeJson(nome) +
               "\",\"endereco\":\"" + escapeJson(endereco) + "\",\"aberto24Horas\":" + aberto24Horas +
               ", \"numeroFuncionarios\":" + numeroFuncionarios + ",\"idDono\":" + idDono + "}";
    }
}