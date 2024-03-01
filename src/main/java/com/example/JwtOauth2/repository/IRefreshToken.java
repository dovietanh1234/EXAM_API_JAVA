package com.example.JwtOauth2.repository;

import com.example.JwtOauth2.entities.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IRefreshToken extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

  /*
  *  @Query(value = "SELECT rt.* FROM REFRESH_TOKENS rt " +
              "INNER JOIN USER_DETAILS ud ON rt.user_id = ud.id " +
              "WHERE ud.EMAIL = :userEmail and rt.revoked = false ", nativeQuery = true)
      List<RefreshTokenEntity> findAllRefreshTokenByUserEmailId(String userEmail);
  * */
}

/*
* Optional là một kiểu dữ lệu container có thể chứa 1 giá trị or không có giá trị nào
* Nó được bieu dien sự có mặt or vắng mat cua mot gia tri. Thay vi su dung null -> thi ta su dung Optional
*
* Collection: Collection là một interface trong Java, được sử dụng để biểu diễn một nhóm các đối tượng như một đơn vị duy nhất
* No sẽ có nhieu methods thao tac voi du lieu hon so với Array ... ( nhưng chậm hơn Array )
* thằng này ko chứa các dữ liệu kiểu nguyen thuỷ
*
*
* Array: Trong Java, Array là một nhóm các biến cùng kiểu được gọi bởi một tên chung
* -> Bạn nên sử dụng Array khi bạn cần lưu trữ nhiều giá trị trong một biến duy nhất, thay vì khai báo các biến riêng lẻ cho mỗi giá trị:
* ex: int[] a = {1, 2, 3} thay vi phai khai bao tung gia tri mot.
* Co thể lưu các dữ liệu kiểu nguyên thuỷ.
*
* */