package com.aja.mediaevents.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnquiryResponseDTO {

	 private Long id;
	    private String name;
	    private String phone;
	    private String occasion;
	    private Integer guests;
	    private LocalDate date;
	    private String location;
	    private String status;
	    private LocalDateTime createdAt;
	    
	    
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getOccasion() {
			return occasion;
		}
		public void setOccasion(String occasion) {
			this.occasion = occasion;
		}
		public Integer getGuests() {
			return guests;
		}
		public void setGuests(Integer guests) {
			this.guests = guests;
		}
		public LocalDate getDate() {
			return date;
		}
		public void setDate(LocalDate date) {
			this.date = date;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
    
}