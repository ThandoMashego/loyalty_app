package com.mukuru.model;

public class Model {
}
package com.mukuru.model;

public class User {
    private Long id;
    private String phone;
    private String name = "Customer";
    private int points = 0;
    private String tier  = "Bronze";

    public User() {}
    public User(Long id, String phone, String name) {
        this.id = id; this.phone = phone; this.name = name == null ? "Customer" : name;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }
}
