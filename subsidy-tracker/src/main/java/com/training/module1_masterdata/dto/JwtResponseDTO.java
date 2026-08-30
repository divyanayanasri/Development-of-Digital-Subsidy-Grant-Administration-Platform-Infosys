package com.training.module1_masterdata.dto;

public class JwtResponseDTO {
    private String token;
    private Long id;
    private String email;
    private String role;
    private String name;
    private Long regionId;

    public JwtResponseDTO() {}

    public JwtResponseDTO(String token, Long id, String email, String role, String name, Long regionId) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
        this.regionId = regionId;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getRegionId() { return regionId; }
    public void setRegionId(Long regionId) { this.regionId = regionId; }

    public static JwtResponseDTOBuilder builder() {
        return new JwtResponseDTOBuilder();
    }

    public static class JwtResponseDTOBuilder {
        private String token;
        private Long id;
        private String email;
        private String role;
        private String name;
        private Long regionId;

        public JwtResponseDTOBuilder token(String token) { this.token = token; return this; }
        public JwtResponseDTOBuilder id(Long id) { this.id = id; return this; }
        public JwtResponseDTOBuilder email(String email) { this.email = email; return this; }
        public JwtResponseDTOBuilder role(String role) { this.role = role; return this; }
        public JwtResponseDTOBuilder name(String name) { this.name = name; return this; }
        public JwtResponseDTOBuilder regionId(Long regionId) { this.regionId = regionId; return this; }

        public JwtResponseDTO build() {
            return new JwtResponseDTO(token, id, email, role, name, regionId);
        }
    }
}