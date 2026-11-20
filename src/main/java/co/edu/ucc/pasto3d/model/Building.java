package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;

@Entity
@Table(name = "building")
public class Building {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String code;
    private String name;
    private String description;
    @Column(name = "pos_x") private Double posX;
    @Column(name = "pos_z") private Double posZ;
    private Double width;
    private Double depth;
    private Integer floors;
    private Integer basements;
    private String color;
    private String type;

    public Integer getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPosX() { return posX; }
    public Double getPosZ() { return posZ; }
    public Double getWidth() { return width; }
    public Double getDepth() { return depth; }
    public Integer getFloors() { return floors; }
    public Integer getBasements() { return basements == null ? 0 : basements; }
    public String getColor() { return color; }
    public String getType() { return type; }
}



