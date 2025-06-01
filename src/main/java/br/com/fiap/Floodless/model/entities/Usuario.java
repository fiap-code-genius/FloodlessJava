package br.com.fiap.Floodless.model.entities;

import jakarta.persistence.*;

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

    @ManyToOne
    @JoinColumn(name = "id_regiao")
    private Regiao regiao;

    @OneToMany(mappedBy = "usuario")
    private List<Notificacao> notificacoes;

    public Usuario() {}

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