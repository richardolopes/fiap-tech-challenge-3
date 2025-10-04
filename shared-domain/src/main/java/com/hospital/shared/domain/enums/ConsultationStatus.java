package com.hospital.shared.domain.enums;

public enum ConsultationStatus {
	AGENDADA("AGENDADA", "Consulta agendada"),
	CONCLUIDA("CONCLUIDA", "Consulta concluída"),
	CANCELADA("CANCELADA", "Consulta cancelada");

	private final String code;
	private final String description;

	ConsultationStatus(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}
}
