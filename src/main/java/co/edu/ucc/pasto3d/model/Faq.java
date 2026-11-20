package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;

@Entity
@Table(name = "faq")
public class Faq {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String category;
    private String question;
    private String answer;
    @Column(name = "keywords") private String keywords;

    public Integer getId() { return id; }
    public String getCategory() { return category; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getKeywords() { return keywords; }
}


