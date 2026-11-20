package co.edu.ucc.pasto3d.repository;

import co.edu.ucc.pasto3d.model.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository pattern: abstracts data access for Faq. */
public interface FaqRepository extends JpaRepository<Faq, Integer> {
}

