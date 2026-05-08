package service;

import model.Empresa;
import model.Restaurante;
import model.Mercado;
import model.Farmacia;
import repository.EmpresaRepository;

import java.util.ArrayList;
import java.util.List;

public class EmpresaService {
    private EmpresaRepository repository;

    public EmpresaService(EmpresaRepository repository) {
        this.repository = repository;
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha, UsuarioService usuarioService) throws Exception {
        return criarEmpresa(tipoEmpresa, dono, nome, endereco, tipoCozinha, null, null, null, false, 0, usuarioService);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String abre, String fecha, String tipoMercado, UsuarioService usuarioService) throws Exception {
        return criarEmpresa(tipoEmpresa, dono, nome, endereco, null, abre, fecha, tipoMercado, false, 0, usuarioService);
    }

    public int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, boolean aberto24Horas, int numeroFuncionarios, UsuarioService usuarioService) throws Exception {
        return criarEmpresa(tipoEmpresa, dono, nome, endereco, null, null, null, null, aberto24Horas, numeroFuncionarios, usuarioService);
    }

    private int criarEmpresa(String tipoEmpresa, int dono, String nome, String endereco, String tipoCozinha,
                             String abre, String fecha, String tipoMercado, boolean aberto24Horas, int numeroFuncionarios,
                             UsuarioService usuarioService) throws Exception {
        if (tipoEmpresa == null || tipoEmpresa.isEmpty()) throw new Exception("Tipo de empresa invalido");
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (endereco == null || endereco.isEmpty()) throw new Exception("Endereco da empresa invalido");
        if (!usuarioService.isDonoEmpresa(dono)) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }

        // Validate mercado specific fields
        if ("mercado".equals(tipoEmpresa)) {
            if (tipoMercado == null || tipoMercado.isEmpty()) {
                throw new Exception("Tipo de mercado invalido");
            }
            // Call validation - it throws the appropriate exception
            validarHorarioCriar(abre, fecha);
        }

        // Check if another owner already has a company with the same name
        for (Empresa e : repository.listarTodas()) {
            if (e.getNome().equals(nome) && e.getIdDono() != dono) {
                throw new Exception("Empresa com esse nome ja existe");
            }
        }

        // Check if same owner has company with same name and address
        for (Empresa e : repository.buscarPorDono(dono)) {
            if (e.getNome().equals(nome) && e.getEndereco().equals(endereco)) {
                throw new Exception("Proibido cadastrar duas empresas com o mesmo nome e local");
            }
        }

        int id = repository.getNextId();
        Empresa empresa = null;

        if ("restaurante".equals(tipoEmpresa)) {
            empresa = new Restaurante(id, nome, endereco, tipoCozinha, dono);
        } else if ("mercado".equals(tipoEmpresa)) {
            empresa = new Mercado(id, nome, endereco, abre, fecha, tipoMercado, dono);
        } else if ("farmacia".equals(tipoEmpresa)) {
            empresa = new Farmacia(id, nome, endereco, aberto24Horas, numeroFuncionarios, dono);
        } else {
            throw new Exception("Tipo de empresa invalido");
        }

        repository.adicionar(empresa);
        return id;
    }

    private boolean validarHorarioCriar(String abre, String fecha) throws Exception {
        // Check for null first - throws Horario invalido
        if (abre == null) {
            throw new Exception("Horario invalido");
        }
        if (fecha == null) {
            throw new Exception("Horario invalido");
        }

        // Validate format (includes empty string check) - throws Formato de hora invalido
        if (!validarFormatoHora(abre)) {
            throw new Exception("Formato de hora invalido");
        }
        if (!validarFormatoHora(fecha)) {
            throw new Exception("Formato de hora invalido");
        }

        // Parse hours and minutes
        String[] abreParts = abre.split(":");
        String[] fechaParts = fecha.split(":");

        int abreHour = Integer.parseInt(abreParts[0]);
        int abreMin = Integer.parseInt(abreParts[1]);
        int fechaHour = Integer.parseInt(fechaParts[0]);
        int fechaMin = Integer.parseInt(fechaParts[1]);

        // Check if time is valid (0-23 for hours, 0-59 for minutes)
        if (abreHour < 0 || abreHour > 23 || abreMin < 0 || abreMin > 59 ||
            fechaHour < 0 || fechaHour > 23 || fechaMin < 0 || fechaMin > 59) {
            throw new Exception("Horario invalido");
        }

        // For mercado, closing time must be AFTER opening time (no overnight hours)
        int abreMinutes = abreHour * 60 + abreMin;
        int fechaMinutes = fechaHour * 60 + fechaMin;

        if (fechaMinutes <= abreMinutes) {
            throw new Exception("Horario invalido");
        }

        return true;
    }

    private boolean validarHorarioAlterar(String abre, String fecha) throws Exception {
        // First check for empty strings - throws Horario invalido
        if (abre == null || abre.isEmpty() || fecha == null || fecha.isEmpty()) {
            throw new Exception("Horario invalido");
        }

        // Validate format HH:MM - throws Formato de hora invalido
        if (!validarFormatoHora(abre) || !validarFormatoHora(fecha)) {
            throw new Exception("Formato de hora invalido");
        }

        // Parse hours and minutes
        String[] abreParts = abre.split(":");
        String[] fechaParts = fecha.split(":");

        int abreHour = Integer.parseInt(abreParts[0]);
        int abreMin = Integer.parseInt(abreParts[1]);
        int fechaHour = Integer.parseInt(fechaParts[0]);
        int fechaMin = Integer.parseInt(fechaParts[1]);

        // Check if time is valid (0-23 for hours, 0-59 for minutes)
        if (abreHour < 0 || abreHour > 23 || abreMin < 0 || abreMin > 59 ||
            fechaHour < 0 || fechaHour > 23 || fechaMin < 0 || fechaMin > 59) {
            throw new Exception("Horario invalido");
        }

        // For mercado, closing time must be AFTER opening time (no overnight hours)
        int abreMinutes = abreHour * 60 + abreMin;
        int fechaMinutes = fechaHour * 60 + fechaMin;

        if (fechaMinutes <= abreMinutes) {
            throw new Exception("Horario invalido");
        }

        return true;
    }

    private boolean validarHorario(String abre, String fecha) throws Exception {
        if (abre == null || abre.isEmpty() || fecha == null || fecha.isEmpty()) {
            throw new Exception("Horario invalido");
        }

        // Validate format HH:MM
        if (!validarFormatoHora(abre) || !validarFormatoHora(fecha)) {
            throw new Exception("Horario invalido");
        }

        // Parse hours and minutes
        String[] abreParts = abre.split(":");
        String[] fechaParts = fecha.split(":");

        int abreHour = Integer.parseInt(abreParts[0]);
        int abreMin = Integer.parseInt(abreParts[1]);
        int fechaHour = Integer.parseInt(fechaParts[0]);
        int fechaMin = Integer.parseInt(fechaParts[1]);

        // Check if time is valid (0-23 for hours, 0-59 for minutes)
        if (abreHour < 0 || abreHour > 23 || abreMin < 0 || abreMin > 59 ||
            fechaHour < 0 || fechaHour > 23 || fechaMin < 0 || fechaMin > 59) {
            throw new Exception("Horario invalido");
        }

        // For mercado, closing time must be AFTER opening time (no overnight hours)
        int abreMinutes = abreHour * 60 + abreMin;
        int fechaMinutes = fechaHour * 60 + fechaMin;

        if (fechaMinutes <= abreMinutes) {
            throw new Exception("Horario invalido");
        }

        return true;
    }

    private boolean validarFormatoHora(String hora) {
        if (hora == null || hora.isEmpty()) return false;
        if (hora.length() != 5) return false;
        if (hora.charAt(2) != ':') return false;
        for (int i = 0; i < 5; i++) {
            if (i == 2) continue;
            if (!Character.isDigit(hora.charAt(i))) return false;
        }
        return true;
    }

    public String getEmpresasDoUsuario(int idDono, UsuarioService usuarioService) throws Exception {
        if (!usuarioService.isDonoEmpresa(idDono)) {
            throw new Exception("Usuario nao pode criar uma empresa");
        }
        List<Empresa> empresas = repository.buscarPorDono(idDono);
        StringBuilder sb = new StringBuilder("{[");
        for (int i = 0; i < empresas.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append("[").append(empresas.get(i).getNome()).append(", ").append(empresas.get(i).getEndereco()).append("]");
        }
        sb.append("]}");
        return sb.toString();
    }

    public String getAtributoEmpresa(int empresa, String atributo, UsuarioService usuarioService) throws Exception {
        Empresa emp = repository.buscarPorId(empresa);
        if (emp == null) {
            throw new Exception("Empresa nao cadastrada");
        }
        if (atributo == null || atributo.isEmpty()) {
            throw new Exception("Atributo invalido");
        }
        switch (atributo.toLowerCase()) {
            case "nome": return emp.getNome();
            case "endereco": return emp.getEndereco();
            case "tipocozinha": return emp.getTipoCozinha();
            case "dono":
                return usuarioService.buscarPorId(emp.getIdDono()).getNome();
            case "aberto24horas":
                if (emp instanceof Farmacia) {
                    return String.valueOf(((Farmacia) emp).isAberto24Horas());
                }
                throw new Exception("Atributo invalido");
            case "numerofuncionarios":
                if (emp instanceof Farmacia) {
                    return String.valueOf(((Farmacia) emp).getNumeroFuncionarios());
                }
                throw new Exception("Atributo invalido");
            case "abre":
                if (emp instanceof Mercado) {
                    return ((Mercado) emp).getAbre();
                }
                throw new Exception("Atributo invalido");
            case "fecha":
                if (emp instanceof Mercado) {
                    return ((Mercado) emp).getFecha();
                }
                throw new Exception("Atributo invalido");
            case "tipomercado":
                if (emp instanceof Mercado) {
                    return ((Mercado) emp).getTipoMercado();
                }
                throw new Exception("Atributo invalido");
            default:
                throw new Exception("Atributo invalido");
        }
    }

    public int getIdEmpresa(int idDono, String nome, int indice) throws Exception {
        if (nome == null || nome.isEmpty()) throw new Exception("Nome invalido");
        if (indice < 0) throw new Exception("Indice invalido");

        List<Empresa> empresasDoNome = new ArrayList<>();
        for (Empresa e : repository.buscarPorDono(idDono)) {
            if (e.getNome().equals(nome)) {
                empresasDoNome.add(e);
            }
        }
        if (empresasDoNome.isEmpty()) {
            throw new Exception("Nao existe empresa com esse nome");
        }
        if (indice >= empresasDoNome.size()) {
            throw new Exception("Indice maior que o esperado");
        }
        return empresasDoNome.get(indice).getId();
    }

    public Empresa buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public boolean isMercado(int empresaId) {
        Empresa emp = repository.buscarPorId(empresaId);
        return emp instanceof Mercado;
    }

    public boolean isFarmacia(int empresaId) {
        Empresa emp = repository.buscarPorId(empresaId);
        return emp instanceof Farmacia;
    }

    public void alterarFuncionamento(int mercado, String abre, String fecha) throws Exception {
        Empresa emp = repository.buscarPorId(mercado);
        if (emp == null) {
            throw new Exception("Empresa nao cadastrada");
        }
        if (!(emp instanceof Mercado)) {
            throw new Exception("Nao e um mercado valido");
        }
        validarHorarioAlterar(abre, fecha);
        ((Mercado) emp).setAbre(abre);
        ((Mercado) emp).setFecha(fecha);
    }

    public void save() {
        repository.save();
    }

    public void clear() {
        repository.clear();
    }
}
