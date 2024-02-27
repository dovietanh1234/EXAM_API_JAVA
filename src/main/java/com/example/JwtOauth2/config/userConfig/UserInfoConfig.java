package com.example.JwtOauth2.config.userConfig;


import com.example.JwtOauth2.entities.UserInfoEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
@RequiredArgsConstructor
public class UserInfoConfig implements UserDetails {

    /*
    * như thầy đã nói ở đoạn code này: có 2 cách để khởi tạo userInfoEntity
    * 1. la Autowired( ko dược khuyen khich )
    * 2. la khoi tao trong constructor( duoc khuyen khich nhieu hon ) no su dung qua anh xa "@RequiredArgsConstructor"
    *
    * */
    private final UserInfoEntity userInfoEntity;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays
                .stream( userInfoEntity.getRoles().split(",") )
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
    /*
    * getAuthorities() -> cong viẹc cua no la:
    * xử lý chuỗi & Tạo ra một danh sách đối tượng "SimpleGrantedAuthority"
    *
    * userInfoEntity.getRole().split(",") -> lấy chuỗi từ class "userInfoEntity" tách chuỗi này
    * thành một mảng nhỏ dựa trên dấu phẩy, mỗi chuỗi đại diện cho mot vai tro.
    * ví dụ: string a = "hn,hp, th, nd, hcm, dn"
    * string[] cities = a.split(","); -
    * -> tách thành các chuỗi con và đưa vào trong một mảng citties
    *
    * Arrays.stream(...) -> chuyển đổi mảng thành một Stream
    * -> Stream la mot INSTANCE giup thuc hiẹn thao tác trên dữ liệu mot cach de dang hieu qua.
    *
    * .map(SimpleGrantedAuthority::new) -> áp dụng phương thức map lặp qua tất cả các phần tu.
    * phương thức map này bien dổi mỗi phần tử trong Stream thành một óoi tượng SimpleGrantedAuthority moi.
    *  ĐỐI TƯỢNG "SimpleGrantedAuthority" là mot lop cu the cua GrantedAuthority mot giao dien
    * trong Spring boot dung de bieu dien quyen han nguoi dung.
    * -> Khi bạn biến đổi mỗi phần tử trong Stream thành một đối tượng SimpleGrantedAuthority mới la
    * ban dang tao ra mot list cac quyen han ma nguoi dung co.
    * -> Spring security se su dung danh sách các "GrantedAuthority" để quyết định client có
    * quyền truy cap vao cac tai nguyen cụ the hay ko?
    *
    * .toList() -> chuyen doi Stream thanh mot danh sach.
    * */

    @Override
    public String getPassword() {
        return userInfoEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userInfoEntity.getEmailId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

/*
* class UserInfoConfig -> đang thực hiện interface UserDetails
* -> interface được sử dụng để cung cấp thông tin chi tiết về người dunng cho Spring Security
* => Giup no xac thuc & uy quyền nguoi dung mot cach chinh xac
*
* các methods trong userDetails bao gồm:
* getAuthorities() -> tra ve 1 Collection của các quyen hạn đươc cấp cho người dùng
* getPassword() -> tra ve mat khau cua nguoi dung
* getUsername() -> tra ve ten nguoi dung
* isAccountNoExpired() -> check tai khoan nguoi dung co het han hay ko?
* isAccountNonLocked() -> check tai khoan nguoi dung co bi khoa hay ko?
* isCredentialsNonExpired() -> check thong tin xac thuc nguoi dung co het han hay ko?
* isEnabled() -> check nguoi dung co duoc kich hoat hay ko?
*
* neu all methods deu return ve false or null -> dieu nay gay ra error, dieu nay gay ra loi
* TA phai CUNG CAP THONG TIN cho cac METHODS nay! de Spring Security hoat dong chinh xac.
*
*
* */