package br.com.fiap.Floodless.model.entities;

import br.com.fiap.Floodless.model.enums.NivelRisco;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "TB_FLOODLESS_REGIAO")
public class Regiao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_regiao")
    private Long id;

    @Column(nullable = false, length = 100, name = "nm_regiao")
    private String nome;

    @Column(nullable = false, length = 50, name = "estado_regiao")
    private String estado;

    @Column(nullable = false, length = 50, name = "cidade_regiao")
    private String cidade;

    @Column(nullable = false, length = 50, name = "bairro_regiao")
    private String bairro;

    @Column(name = "cep_regiao", length = 50)
    private String cep;

    @Column(name = "nivel_risco")
    @Enumerated(EnumType.STRING)
    private NivelRisco nivelRisco = NivelRisco.BAIXO;

    @Column(name = "nivel_chuva")
    private Double nivelChuva;

    @Column(name = "temp_regiao")
    private Double temperatura;

    @Column(name = "area_risco")
    private Boolean areaRisco = false;

    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao = LocalDateTime.now();

    @OneToMany(mappedBy = "regiao")
    private List<Usuario> moradores;

    @OneToMany(mappedBy = "regiao")
    private List<Abrigo> abrigos;

    public Regiao() {}

    public Regiao(Long id, String nome, String estado, String cidade, String bairro, String cep, NivelRisco nivelRisco, Double nivelChuva, Double temperatura, Boolean areaRisco, LocalDateTime ultimaAtualizacao, List<Usuario> moradores, List<Abrigo> abrigos) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.nivelRisco = nivelRisco;
        this.nivelChuva = nivelChuva;
        this.temperatura = temperatura;
        this.areaRisco = areaRisco;
        this.ultimaAtualizacao = ultimaAtualizacao;
        this.moradores = moradores;
        this.abrigos = abrigos;
    }

    public Regiao(Long id, String nome, String estado, String cidade, String bairro, String cep, NivelRisco nivelRisco, Double nivelChuva, Double temperatura, Boolean areaRisco, LocalDateTime ultimaAtualizacao, List<Usuario> moradores) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.nivelRisco = nivelRisco;
        this.nivelChuva = nivelChuva;
        this.temperatura = temperatura;
        this.areaRisco = areaRisco;
        this.ultimaAtualizacao = ultimaAtualizacao;
        this.moradores = moradores;
    }

    public Regiao(Long id, String nome, String estado, String cidade, String bairro, String cep, NivelRisco nivelRisco, Double nivelChuva, Double temperatura, Boolean areaRisco, LocalDateTime ultimaAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.estado = estado;
        this.cidade = cidade;
        this.bairro = bairro;
        this.cep = cep;
        this.nivelRisco = nivelRisco;
        this.nivelChuva = nivelChuva;
        this.temperatura = temperatura;
        this.areaRisco = areaRisco;
        this.ultimaAtualizacao = ultimaAtualizacao;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public Double getNivelChuva() {
        return nivelChuva;
    }

    public void setNivelChuva(Double nivelChuva) {
        this.nivelChuva = nivelChuva;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Boolean getAreaRisco() {
        return areaRisco;
    }

    public void setAreaRisco(Boolean areaRisco) {
        this.areaRisco = areaRisco;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public List<Usuario> getMoradores() {
        return moradores;
    }

    public void setMoradores(List<Usuario> moradores) {
        this.moradores = moradores;
    }

    public List<Abrigo> getAbrigos() {
        return abrigos;
    }

    public void setAbrigos(List<Abrigo> abrigos) {
        this.abrigos = abrigos;
    }
}