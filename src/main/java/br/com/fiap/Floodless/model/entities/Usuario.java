package br.com.fiap.Floodless.model.entities;

import br.com.fiap.Floodless.dto.UsuarioRequestDTO;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_FLOODLESS_USUARIO")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false, length = 100, name = "nm_usuario")
    private String nome;

    @Column(nullable = false, unique = true, name = "ds_email")
    private String email;

    @Column(nullable = false, name = "ds_senha")
    private String senha;

    @Column(name = "ds_telefone")
    private String telefone;

    @Column(name = "receber_notificacoes")
    private Boolean receberNotificacoes = true;

    @Column(name = "receber_alertas")
    private Boolean receberAlertas = true;

    @Column(name = "data_cadastro")
    private LocalDateTime dataCadastro;

    @Column(name = "ultimo_login")
    private LocalDateTime ultimoLogin;

    @ManyToOne
    @JoinColumn(name = "id_regiao")
    private Regiao regiao;

    @OneToMany(mappedBy = "usuario")
    private List<Notificacao> notificacoes;

    public Usuario() {}

    public Usuario(UsuarioRequestDTO dto) {
        this.nome = dto.nome();
        this.email = dto.email();
        this.senha = dto.senha();
        this.telefone = dto.telefone();
        this.receberNotificacoes = dto.receberNotificacoes() != null ? dto.receberNotificacoes() : true;
        this.receberAlertas = dto.receberAlertas() != null ? dto.receberAlertas() : true;
        this.dataCadastro = LocalDateTime.now();
        this.ultimoLogin = LocalDateTime.now();
        // A região será definida pelo serviço
        // As notificações serão gerenciadas separadamente
    }

    public Usuario(String email, Long id, String nome, String senha, String telefone, Regiao regiao, List<Notificacao> notificacoes) {
        this.email = email;
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
        this.regiao = regiao;
        this.notificacoes = notificacoes;
    }

    public Usuario(String email, Long id, String nome, String senha, String telefone, Regiao regiao) {
        this.email = email;
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
        this.regiao = regiao;
    }

    public Usuario(String email, Long id, String nome, String senha, String telefone) {
        this.email = email;
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
    }

    public Usuario(String email, Long id, String nome, String senha, String telefone, List<Notificacao> notificacoes) {
        this.email = email;
        this.id = id;
        this.nome = nome;
        this.senha = senha;
        this.telefone = telefone;
        this.notificacoes = notificacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Boolean getReceberNotificacoes() {
        return receberNotificacoes;
    }

    public void setReceberNotificacoes(Boolean receberNotificacoes) {
        this.receberNotificacoes = receberNotificacoes;
    }

    public Boolean getReceberAlertas() {
        return receberAlertas;
    }

    public void setReceberAlertas(Boolean receberAlertas) {
        this.receberAlertas = receberAlertas;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    public List<Notificacao> getNotificacoes() {
        return notificacoes;
    }

    public void setNotificacoes(List<Notificacao> notificacoes) {
        this.notificacoes = notificacoes;
    }
}