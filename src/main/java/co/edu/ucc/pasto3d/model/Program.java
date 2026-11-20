package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;

@Entity
@Table(name = "program")
public class Program {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String faculty;
    private String level;
    private String modality;
    @Column(name = "duration_semesters") private Integer durationSemesters;
    private String degreeTitle;
    private String description;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "building_id")
    private Building building;
    /** Place where the faculty of the program is (offices, dean's office). */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "place_id")
    private Place place;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getFaculty() { return faculty; }
    public String getLevel() { return level; }
    public String getModality() { return modality; }
    public Integer getDurationSemesters() { return durationSemesters; }
    public String getDegreeTitle() { return degreeTitle; }
    public String getDescription() { return description; }
    public Building getBuilding() { return building; }
    public Place getPlace() { return place; }
}



