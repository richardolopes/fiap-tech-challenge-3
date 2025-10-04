package com.hospital.shared.domain.enums;

public enum UserType {
	MEDICO("MEDICO", "Médico"),
	ENFERMEIRO("ENFERMEIRO", "Enfermeiro"),
	PACIENTE("PACIENTE", "Paciente");

	private final String code;
	private final String description;

	UserType(String code, String description) {
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
