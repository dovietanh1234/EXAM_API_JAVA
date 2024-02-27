package com.example.JwtOauth2.config.userConfig;

import com.example.JwtOauth2.config.userConfig.UserInfoConfig;
import com.example.JwtOauth2.repository.IUserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserInfoManagerConfig implements UserDetailsService {

    private final IUserInfoEntity iUserInfoEntity;

    // tim kiem thong tin nguoi dung dua tren email neu tim thay tao ra doi tuong UserDetails
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        return iUserInfoEntity
                .findByEmailId(emailId)
                .map(UserInfoConfig::new)
                .orElseThrow( ()-> new UsernameNotFoundException("not found")  );
    }

    // lay user trong Authentication form object return UserDetails

    //HTTP -> UserInfoManagerConfig( load data ) -> UserInfoConfig( has data )

}

/*
* class "UserInfoManagerConfig" sẽ implement interface "UserDetailsService" từ spring security
* interface nay dinh nghia 1 method "loadUserByUsername" => no giup Spring Security su dụng
* để tải thông tin nguoi dung! dưa tren ten nguoi dung.
*
* vidu: khi co 1 nguoi dang nhap! Spring Security se gọi method "loadUserByUsername"
* với tên người dùng được cung câp. method này se tra ve Instance "UserDetails" chua thong
* tin nguoi dung ( username, password, role ...  )
*
* iUserInfoEntity.findByEmailId(emailId) -> goi vao findByEmailId trên iUserInfoEntity
* voi "emailId" duoc cung cap method return ve "Optional<UserInfoEntity>" nghia la:
* Neu email ton tai in DB thi nó se chứa môt entity "UserInfoEntity"
*
* .map(UserInfoConfig::new) -> chuyen đổi môt entity "UserInfoEntity" thành mot doi tuong
* moi "userDetails" { userDetails cung cap thong tin chi tiet ve client cho spring security }
*
* .orElseThrow( ()-> new UsernameNotFoundException("not found") ) -> tha exception neu ko tim
* thay email.
*
* @Service & "@Component" đều quản lý như một Bean cho phép class khác thể (inject đối tượng)
*
* Sử dụng @Service thay vì @Component giúp làm rõ ràng hơn về mục đích của class và cũng hỗ trợ
* tốt hơn cho các tính năng cụ thể của Spring liên quan đến logic nghiệp vụ,
 * */



