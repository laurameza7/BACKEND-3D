package co.edu.ucc.pasto3d.repository;

import co.edu.ucc.pasto3d.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    /** Last conversations of a registered user (their history with Coopi). */
    List<Conversation> findTop20ByUserIdOrderByIdDesc(Integer userId);
}
