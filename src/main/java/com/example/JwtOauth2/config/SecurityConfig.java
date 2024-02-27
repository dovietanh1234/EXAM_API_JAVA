package com.example.JwtOauth2.config;

/*
sau khi tao xong Bean
* Thi đây sẽ là class dau tiên tuong tac voi API của ta
* */


import com.example.JwtOauth2.config.userConfig.UserInfoManagerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserInfoManagerConfig userInfoManagerConfig;

    @Order(1)
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/api/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .userDetailsService(userInfoManagerConfig)
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Order(2)
    @Bean
    public SecurityFilterChain h2ConsoleSecurityFilterChainConfig(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher(("/h2-console/**")))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*")))
                .headers( headers -> headers.frameOptions(Customizer.withDefaults()).disable())
                .build();
    }

@Bean
PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
}

/*
* HttpSecurity => cho phep cấu hing bảo mật cho các yêu cầu HTTP cụ thể. Bạn co thể su dụng method requestMatcher()
* để hạn chế cấu hình bảo mật cho một endpoint HTTP cụ thể.
* CẤU HIH BẢO MẬT CHO HTTP CỤ THE
*
* Cấu hình xác thực và uỷ quyền: HttpSecurity cung cấp các methods để cấu hình xác thực và uỷ quyền cho các yêu cau
* HTTP vi dụ ta có thể xác thực dựa trên vai trò với method hasRole().
*
* SecurityFilterChain: HttpSecurity
*
* */



    /*
    * giai thich:
    * _ @Order(1) -> Annotation nay dat thu tu uu tien cho "SecurityFilterChain" neu co nhieu "SecurityFilterChain"
    * thi cai nao thu tu thap hon se duoc ap dung truoc.
    *
    * _ @Bean -> danh dau method "apiSecurityFilterChain" de spring tao ra 1 bean tu gia tri tra ve cua no.
    *
    * _ httpSecurity.securityMatcher(new AntPathRequestMatcher("/api/**")) -> Chuỗi filter bao mat nay chỉ áp dụng
    * cho các yêu cầu Http đến các đường bắt đầu bằng "/api/".
    * -> no cho phep tuy chinh cau hình bao mật cho các URl cụ thể trong ứng dụng của bạn.
    *
    * .csrf(AbstractHttpConfigurer::disable) -> vo hieu hoa bao ve chong tan con CSRF
    *
    * .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()) -> y/c Http phai duoc xac thuc.
    *
    * .userDetailsService(userInfoManagerConfig) -> Dat dich vu tim kiem thong tin nguoi dung trong qua trinh xac thuc
    * "userInfoManagerConfig" la mot Bean cua loai UserDetailService
    *
    * .formLogin(Customizer.withDefaults()) và .httpBasic(Customizer.withDefaults()) -> kich hoat xac thuc dua tren
    * form va xac thuc co ban voi cac cai dat mac dinh.
    *
    * .build(): Điều này tạo ra SecurityFilterChain từ cấu hình đã cho.
    *
    * */
}



/*
* SecurityConfig -> day la một class ĐỊNH NGHIA CAU HINH BAO MAT cho spring boot.
* 1.  dùng để định nghĩa các quy tắc bảo mật cho các URL cụ thể trong ứng dụng của bạn
* ( ta có the xác thực cho all yêu cầu HTTP ) hoặc là cho mot đường dẫn cụ thể.
*
* 2. cấu hinh chi tiết cách xác thực người dùng
*
* 3. tạo SecurityFilterChain: SecurityConfig thường được su dung de tao mot "SecurityFilterChain"
* day la mot chuoi cac bo filter bảo mật được sắp xếp theo thứ tự nhất định để xử lý cac yêu cầu HTTP
* SecurityFilterChain -> chiu trach nhiem bao ve cac URL cua ung dung. xac thuc account nguoi dung da guu, chuyen huong
* den form dang nhap va nhieu hon nua.
*
*
* @Configuration -> thong báo đây là môt class cau hinh! co the chua cac method @Bean duoc
* spring su dung ĐỂ TẠO VÀ QUẢN LÝ CÁC @Bean
*
* @EnableWebSecurity -> Kich hoat BAO MAT WEB với Spring Security. Nó sẽ tạo ra môt bộ lọc bảo
* mat duoc them vao chuoi bo loc cua servlet, cho phep Ho tro, Xac thuc, Uy quyen.
*
*@EnableMethodSecurity -> Annotation này Kich Hoat BAO MAT METHOD voi Spring Security. Cho
* phep them cac annotation bao mat nhu: @PreAuthorize, @PostAuthorize VÀO CÁC PHƯƠNG THỨC TRONG
* ỨNG DỤNG CỦA CHÚNG TA.
*
* Tsao phai bao mat web & method?  -> giup bao ve ứng dụng khỏi nhũng cuoc tấn công đảm bảo chỉ
* người dùng có quyền hạn mới có quyền truy cap vao tài nguyên or đoạn mã nguồn cua minh.
* Dac biet khi ta kich hoat bao mat method: ta co them cac annotation bao mat nhu: @PreAuthorize
* @PostAuthorize Vao các methods trong ứng dụng của ta.
*
* Tìm hieu qua ve @Bean:
* Tsao @Bean lai can khai bao trong class Cau hinh:
* Spring framework cac class cau hinh ( @Configuration ) duoc su dụng để định nghĩa các bean(@Bean)
* Mỗi method @Bean tạo ra một đối tượng (bean) & Spring sẽ quản lý doi tuong nay -> No cho phep
* chen cac cau hinh config ( inject ) o cac nơi khác trong ứng dụng( nghĩa là khi ta config nó
* có thể áp dụng ở nhưng nơi khác trong du án.)
*
* @Bean: sẽ return vè một instance (bean) Spring sẽ quản lý trong container của mình. Doi tuong
* sau nay co the chen (inject) ở những noi khac trong ung dung thong qua Dependency injection
*
* Việc chèn( inject ): tự dộng cung cấp các phụ thuộc cho các đối tượng. Khi một bean được chèn
* vào đối tượng khác( thì đối tượng đó sẽ sử dụng bean để thực hiện một số chức năng )
*
* ví dụ về cách sử dụng @Bean:
*
* Class1:
* @Configuration
* public class AppConfig(){
*   @Bean
*   public UserRepository userRepository(){
*   return new UserRepository;
* }}
*
* Class2:
* @Service
* public class UserService{
*    private final UserRepository userRepository;
*
* @Autowired
* public UserService( UserRepository userRepository ){
*      this.userRepository = userRepository;
* }
* }
*
* -> trong vidu vua roi Spring auto tao moi doi tuong userRepository, quan ly no nhu 1 bean
* va chen no vao UserService. Dieu nay giam bot su phuc tap khi quan ly cac phu thuoc lam cho
* doan ma cua ban tro len de dang thay doi hon.
*
* Neu ko khai bao anh xa @Bean: ko khai bao @Bean trong class cau hinh Spring se ko biet su
* ton tai cua bean do! nghia la ta ko the chen (inject) bean do vao class khac nhu vi du tren!
*
*
*
*
*
* */