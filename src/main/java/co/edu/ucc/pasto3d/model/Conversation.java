package co.edu.ucc.pasto3d.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation")
public class Conversation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String question;
    private String answer;
    private String provider;
    @Column(name = "user_id") private Integer userId;
    private String action;
    @Column(name = "created_at") private LocalDateTime createdAt;

    protected Conversation() { }

    public Conversation(String question, String answer, String provider) {
        this(question, answer, provider, null, null);
    }

    public Conversation(String question, String answer, String provider, Integer userId, String action) {
        this.question = question;
        this.answer = answer;
        this.provider = provider;
        this.userId = userId;
        this.action = action;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getId() { return id; }
    public String getQuestion() { return question; }
    public String getAnswer() { return answer; }
    public String getProvider() { return provider; }
    public Integer getUserId() { return userId; }
    public String getAction() { return action; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}


