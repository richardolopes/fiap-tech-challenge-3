package com.hospital.shared.domain.entities;

import com.hospital.shared.domain.enums.UserType;
import java.time.LocalDateTime;
import java.util.Objects;

public class User {
	private Long id;
	private String name;
	private String email;
	private String password;
	private UserType userType;
	private String crm; 
	private String coren; 
	private String cpf; 
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private boolean active;

	public User(String name, String email, String password, UserType userType) {
		this.name = validateName(name);
		this.email = validateEmail(email);
		this.password = password;
		this.userType = Objects.requireNonNull(userType, "Tipo de usuário é obrigatório");
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
		this.active = true;
	}

	public User(Long id, String name, String email, String password, UserType userType,
			String crm, String coren, String cpf, LocalDateTime createdAt,
			LocalDateTime updatedAt, boolean active) {
		this.id = id;
		this.name = validateName(name);
		this.email = validateEmail(email);
		this.password = password;
		this.userType = Objects.requireNonNull(userType, "Tipo de usuário é obrigatório");
		this.crm = crm;
		this.coren = coren;
		this.cpf = cpf;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.active = active;
	}

	private String validateName(String name) {
		if (name == null || name.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome não pode ser vazio");
		}
		if (name.length() < 2) {
			throw new IllegalArgumentException("Nome deve ter pelo menos 2 caracteres");
		}
		return name.trim();
	}

	private String validateEmail(String email) {
		if (email == null || email.trim().isEmpty()) {
			throw new IllegalArgumentException("Email não pode ser vazio");
		}
		if (!email.contains("@") || !email.contains(".")) {
			throw new IllegalArgumentException("Email deve ter formato válido");
		}
		return email.trim().toLowerCase();
	}

	public void updateInfo(String name, String email) {
		this.name = validateName(name);
		this.email = validateEmail(email);
		this.updatedAt = LocalDateTime.now();
	}

	public void setCrm(String crm) {
		if (this.userType != UserType.MEDICO) {
			throw new IllegalArgumentException("CRM só pode ser definido para médicos");
		}
		this.crm = crm;
		this.updatedAt = LocalDateTime.now();
	}

	public void setCoren(String coren) {
		if (this.userType != UserType.ENFERMEIRO) {
			throw new IllegalArgumentException("COREN só pode ser definido para enfermeiros");
		}
		this.coren = coren;
		this.updatedAt = LocalDateTime.now();
	}

	public void setCpf(String cpf) {
		if (this.userType != UserType.PACIENTE) {
			throw new IllegalArgumentException("CPF só pode ser definido para pacientes");
		}
		this.cpf = cpf;
		this.updatedAt = LocalDateTime.now();
	}

	public void deactivate() {
		this.active = false;
		this.updatedAt = LocalDateTime.now();
	}

	public void activate() {
		this.active = true;
		this.updatedAt = LocalDateTime.now();
	}

	public boolean canViewAllConsultations() {
		return userType == UserType.MEDICO || userType == UserType.ENFERMEIRO;
	}

	public boolean canEditConsultations() {
		return userType == UserType.MEDICO || userType == UserType.ENFERMEIRO;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public UserType getUserType() {
		return userType;
	}

	public String getCrm() {
		return crm;
	}

	public String getCoren() {
		return coren;
	}

	public String getCpf() {
		return cpf;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public boolean isActive() {
		return active;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(email, user.email);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, email);
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", email='" + email + '\'' +
				", userType=" + userType +
				", active=" + active +
				'}';
	}
}
