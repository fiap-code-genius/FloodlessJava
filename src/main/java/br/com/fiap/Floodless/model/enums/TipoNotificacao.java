package br.com.fiap.Floodless.model.enums;

public enum TipoNotificacao {
    ALERTA_RISCO,           // Alerta de risco de enchente
    MUDANCA_NIVEL_RISCO,    // Mudança no nível de risco de uma região
    ABRIGO_DISPONIVEL,      // Novo abrigo disponível ou vagas liberadas
    ABRIGO_LOTADO,         // Abrigo atingiu capacidade máxima
    EVACUACAO,             // Ordem de evacuação
    INFORMATIVO            // Informações gerais
}

