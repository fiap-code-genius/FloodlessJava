package br.com.fiap.Floodless.model.entities;

import br.com.fiap.Floodless.model.enums.TipoNotificacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_FLOODLESS_NOTIFICACAO")
public class Notificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacao")
    private Long id;

    @Column(nullable = false, name = "titulo_notificacao")
    private String titulo;

    @Column(nullable = false, name = "ms_noticacao")
    private String mensagem;

    @Column(name = "tipo_notificacao")
    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_regiao")
    private Regiao regiao;

    public Notificacao() {}

    public Notificacao(Long id, String titulo, String mensagem, TipoNotificacao tipo, LocalDateTime dataCriacao, Usuario usuario, Regiao regiao) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
        this.usuario = usuario;
        this.regiao = regiao;
    }

    public Notificacao(Long id, String titulo, String mensagem, TipoNotificacao tipo, LocalDateTime dataCriacao, Usuario usuario) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
        this.usuario = usuario;
    }

    public Notificacao(Long id, String titulo, String mensagem, TipoNotificacao tipo, LocalDateTime dataCriacao, Regiao regiao) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
        this.regiao = regiao;
    }

    public Notificacao(Long id, String titulo, String mensagem, TipoNotificacao tipo, LocalDateTime dataCriacao) {
        this.id = id;
        this.titulo = titulo;
        this.mensagem = mensagem;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoNotificacao tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }

    @PrePersist
    protected void aoCriar() {
        dataCriacao = LocalDateTime.now();
    }
}

