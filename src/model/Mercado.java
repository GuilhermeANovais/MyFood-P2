package model;

public class Mercado extends Empresa {
    private String abre;
    private String fecha;
    private String tipoMercado;

    public Mercado(int id, String nome, String endereco, String abre, String fecha, String tipoMercado, int idDono) {
        super(id, nome, endereco, null, idDono);
        this.abre = abre;
        this.fecha = fecha;
        this.tipoMercado = tipoMercado;
    }

    public String getAbre() { return abre; }
    public String getFecha() { return fecha; }
    public String getTipoMercado() { return tipoMercado; }

    @Override
    public String getTipo() {
        return "mercado";
    }

    public void setAbre(String abre) { this.abre = abre; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    @Override
    public String toJson() {
        return "{\"id\":" + id + ",\"tipo\":\"" + getTipo() + "\",\"nome\":\"" + escapeJson(nome) +
               "\",\"endereco\":\"" + escapeJson(endereco) + "\",\"abre\":\"" + escapeJson(abre) +
               "\",\"fecha\":\"" + escapeJson(fecha) + "\",\"tipoMercado\":\"" + escapeJson(tipoMercado) +
               "\",\"idDono\":" + idDono + "}";
    }
}