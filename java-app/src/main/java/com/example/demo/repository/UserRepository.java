package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * User エンティティ用のリポジトリインターフェース。
 * Spring Data JPA により、自動的に基本的な CRUD 操作が提供される。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを検索する。
     * ログイン認証やユーザー確認処理に使用。
     *
     * @param email ユーザーのメールアドレス
     * @return 該当ユーザーが存在すれば Optional<User>、いなければ空
     */
    Optional<User> findByEmail(String email);
}
