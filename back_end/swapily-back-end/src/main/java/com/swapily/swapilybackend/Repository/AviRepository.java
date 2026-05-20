package com.swapily.swapilybackend.Repository;

import com.swapily.swapilybackend.Entity.Avis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AviRepository extends JpaRepository<Avis, Long> {

    List<Avis> findAllByOrderByIdDesc();
}
