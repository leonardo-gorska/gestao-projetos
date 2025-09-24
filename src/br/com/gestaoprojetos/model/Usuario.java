package br.com.gestaoprojetos.model;

public class Usuario {
    private int id;
    private String nome;
    private String cpf;
    private String email;
    private String login;
    private String senha;
    private Cargo cargo;
    private Perfil perfil;
    private boolean ativo; // ⚡ novo campo

    public Usuario() {}
    public Usuario(int id, String nome) { this.id = id; this.nome = nome; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    // ⚡ novo getter e setter
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    // --- Métodos de permissão ---
    public boolean isAdmin() {
        return perfil != null && "Administrador".equalsIgnoreCase(perfil.getDescricao());
    }

    public boolean isGerente() {
        return perfil != null && "Gerente".equalsIgnoreCase(perfil.getDescricao());
    }

    public boolean podeCriarProjeto() {
        return isAdmin() || isGerente();
    }

    public boolean podeEditarProjeto() {
        return isAdmin() || isGerente();
    }

    public boolean podeDeletarProjeto() {
        return isAdmin();
    }

    public boolean podeCriarTarefa() {
        return isAdmin() || isGerente() || (perfil != null && "Colaborador".equalsIgnoreCase(perfil.getDescricao()));
    }

    public boolean podeEditarTarefa() {
        return isAdmin() || isGerente() || (perfil != null && "Colaborador".equalsIgnoreCase(perfil.getDescricao()));
    }

    public boolean podeDeletarTarefa() {
        return isAdmin() || isGerente();
    }

    public boolean podeCriarEquipe() {
        return isAdmin() || isGerente();
    }

    public boolean podeEditarEquipe() {
        return isAdmin() || isGerente();
    }

    public boolean podeDeletarEquipe() {
        return isAdmin();
    }

    public boolean podeCriarProjetoOuTarefa() {
        return podeCriarProjeto() || podeCriarTarefa();
    }

    @Override
    public String toString() {
        return nome != null ? nome : "";
    }
}
