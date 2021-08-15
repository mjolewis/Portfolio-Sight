package edu.bu.cs673.stockportfolio.domain.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountLineRepository extends JpaRepository<AccountLine, Long> {
    void deleteAllByAccount_Id(Long accountId);
}
