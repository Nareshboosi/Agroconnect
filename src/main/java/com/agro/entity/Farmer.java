package com.agro.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.agro.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "farmers")
public class Farmer {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(nullable = false)
	    private String name;

	    @Column(nullable = false, unique = true)
	    private String email;

	    @Column(nullable = false)
	    @JsonIgnore
	    private String password;

	    @Column
	    private String phone;
	    @Column
	    private String address;
	    
	    public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		@OneToMany(mappedBy = "farmer")
	    @JsonIgnore
	    private List<Crop> crops;
	    
	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false)
	    private Role role;



	    public List<Crop> getCrops() {
			return crops;
		}

		public void setCrops(List<Crop> crops) {
			this.crops = crops;
		}

		public Role getRole() {
			return role;
		}

		public void setRole(Role farmer) {
			this.role = farmer;
		}

		// 🔹 GETTERS
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

	    public String getPhone() {
	        return phone;
	    }
	   

	    // 🔹 SETTERS
	    public void setId(Long id) {
	        this.id = id;
	    }

	    public void setName(String name) {
	        this.name = name;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public void setPhone(String phone) {
	        this.phone = phone;
	    }
	   
}
