package lk.ghanim.api.repository;

import lk.ghanim.api.entity.User;
import lk.ghanim.api.entity.UserAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserAddressrepository extends JpaRepository<UserAddress, Long> {
    List<UserAddress> findByUserOrderByIsDefaultDescCreatedAtDesc(User user);

    Optional<UserAddress> findByUserAndIsDefaultTrue(User user);

    long countByUser(User user);

    @Modifying
    @Query("Update UserAddress a SET a.isDefault = false WHERE a.user = :user")
    void clearDefaultForUser(User user);
}
