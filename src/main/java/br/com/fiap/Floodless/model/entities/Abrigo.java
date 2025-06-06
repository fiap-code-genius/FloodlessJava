package br.com.fiap.Floodless.model.entities;

import br.com.fiap.Floodless.dto.AbrigoRequestDTO;
import jakarta.persistence.*;

@Entity
@Table(name = "TB_FLOODLESS_ABRIGO")
public class Abrigo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_abrigo")
    private Long id;

    @Column(nullable = false, name = "nm_abrigo")
    private String nome;

    @Column(name = "ds_abrigo")
    private String descricao;

    @Column(nullable = false, name = "ds_endereco")
    private String endereco;

    @Column(name = "capacidade_maxima", nullable = false)
    private Integer capacidadeMaxima;

    @Column(name = "ocupacao_atual")
    private Integer ocupacaoAtual;

    @Column(name = "telefone_contato")
    private String telefoneContato;

    @Column(name = "email_contato")
    private String emailContato;

    @Column(name = "ativo")
    private Boolean ativo = true;

    @Column(name = "disponivel")
    private Boolean disponivel = true;

    @ManyToOne
    @JoinColumn(name = "id_regiao")
    private Regiao regiao;

    public Abrigo() {}

    public Abrigo(AbrigoRequestDTO dto) {
        this.nome = dto.nome();
        this.descricao = dto.descricao();
        this.endereco = dto.endereco();
        this.capacidadeMaxima = dto.capacidadeMaxima();
        this.ocupacaoAtual = dto.ocupacaoAtual();
        this.telefoneContato = dto.telefoneContato();
        this.emailContato = dto.emailContato();
        this.ativo = dto.ativo() != null ? dto.ativo() : true;
        this.disponivel = dto.disponivel() != null ? dto.disponivel() : true;
    }

    public Abrigo(Long id, String nome, String descricao, String endereco, Integer capacidadeMaxima, Integer ocupacaoAtual, String telefoneContato, String emailContato, Boolean ativo, Boolean disponivel, Regiao regiao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ocupacaoAtual = ocupacaoAtual;
        this.telefoneContato = telefoneContato;
        this.emailContato = emailContato;
        this.ativo = ativo;
        this.disponivel = disponivel;
        this.regiao = regiao;
    }

    public Abrigo(Long id, String nome, String descricao, String endereco, Integer capacidadeMaxima, Integer ocupacaoAtual, String telefoneContato, String emailContato, Boolean ativo, Boolean disponivel) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.endereco = endereco;
        this.capacidadeMaxima = capacidadeMaxima;
        this.ocupacaoAtual = ocupacaoAtual;
        this.telefoneContato = telefoneContato;
        this.emailContato = emailContato;
        this.ativo = ativo;
        this.disponivel = disponivel;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public void setCapacidadeMaxima(Integer capacidadeMaxima) {
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public Integer getOcupacaoAtual() {
        return ocupacaoAtual;
    }

    public void setOcupacaoAtual(Integer ocupacaoAtual) {
        this.ocupacaoAtual = ocupacaoAtual;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(Boolean disponivel) {
        this.disponivel = disponivel;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public void setRegiao(Regiao regiao) {
        this.regiao = regiao;
    }
}
