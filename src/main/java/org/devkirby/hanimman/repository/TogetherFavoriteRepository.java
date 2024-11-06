package org.devkirby.hanimman.repository;

import org.devkirby.hanimman.entity.Together;
import org.devkirby.hanimman.entity.TogetherFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TogetherFavoriteRepository extends JpaRepository<TogetherFavorite, Integer> {
    List<TogetherFavorite> findByUserId(Integer userId);

    List<TogetherFavorite> findByParent(Together parent);
}