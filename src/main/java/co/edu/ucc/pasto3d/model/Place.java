package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;

@Entity
@Table(name = "place")
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String type;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id")
    private Building building;
    private Integer floor;
    private String description;
    private String schedule;
    private String phone;
    private String email;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public Building getBuilding() { return building; }
    public Integer getFloor() { return floor; }
    public String getDescription() { return description; }
    public String getSchedule() { return schedule; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}


