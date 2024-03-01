package com.example.JwtOauth2.config;

/*
sau khi tao xong Bean
* Thi đây sẽ là class dau tiên tuong tac voi API của ta
* */


import com.example.JwtOauth2.config.jwtConfig.JwtAccessTokenFilter;
import com.example.JwtOauth2.config.jwtConfig.JwtRefreshTokenFilter;
import com.example.JwtOauth2.config.jwtConfig.JwtTokenUtils;
import com.example.JwtOauth2.config.userConfig.UserInfoManagerConfig;
import com.example.JwtOauth2.repository.IRefreshToken;
import com.example.JwtOauth2.service.LogoutHandlerService;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    //Signin --> username, password ... -> accessToken( PERMISSION ) JWT TOKEN
    private final UserInfoManagerConfig userInfoManagerConfig;

    // tao 2 bien cho cau hinh jwt "jwtEncoder"
    private final RSAKeyRecord rsaKeyRecord;

    // cau hinh cho phep api khi xoa tai khoan tren DB token cua no ko the hoat dong dc
    private final JwtTokenUtils jwtTokenUtils;

    // order(3)
    private final IRefreshToken iRefreshToken;

    private final LogoutHandlerService logoutHandlerService;

    // CONFIG MIDDLEWARE JWT
    @Order(1) // dat thu tu uu tien cho SecurityFilterChain
    @Bean  // tao 1 instance cho phep cac class khac inject ...
    public SecurityFilterChain signInSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/sign-in/**")) // chuoi bo loc nay chi ap dung cho cac duong dan HTTP la "/sign-in/"
                .csrf(AbstractHttpConfigurer::disable) // vo hieu hoa tan cong CSRF
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated()) // yeu cau all HTTP phai duoc XAC THUC
                .userDetailsService(userInfoManagerConfig) // dat dich vu de tim kiem thong tin nguoi dung THONG QUA qua trinh xac thuc
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // cau hinh chinh sach tao session. KO su dung session de save trang thai nguoi dung.
                .exceptionHandling(ex -> {
                    ex.authenticationEntryPoint((request, response, authenticationException) ->
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage()));
                }) // neu loi xac thuc se thong bao loi guu ve cho client
                .httpBasic(Customizer.withDefaults()) // kich hoat xac thuc co ban voi cac cai dat mac dinh
                .build(); // tao ra mot SecurityFilterChain tu cau hinh da cho.
    }

    // CONFIG Security Authentication
    @Order(2)
    @Bean
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/api/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
              //  .userDetailsService(userInfoManagerConfig) //thay doi: duyet qua token chu ko can account nua
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // duyet token thi moi co the chay API
                .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy( SessionCreationPolicy.STATELESS ))
                .exceptionHandling(ex -> {
                     log.error("[SecurityConfig:apiSecurityFilterChain] Exception due to :{}", ex);
                     ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
                     ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
                })
                .httpBasic(Customizer.withDefaults())
                .build();
    }

    @Order(3)
    @Bean
    public SecurityFilterChain refreshTokenSecurityFilterChain( HttpSecurity httpSecurity ) throws Exception {
            return httpSecurity
                    .securityMatcher(new AntPathRequestMatcher("/refresh-token/**"))
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(new JwtRefreshTokenFilter(rsaKeyRecord, jwtTokenUtils, iRefreshToken), UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling(ex -> {
                        log.error("[SecurityConfig:refreshTokenSecurityFilterChain] Exception due to :{}",ex);
                        ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
                        ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
                    })
                    .httpBasic(Customizer.withDefaults())
                    .build();
    }

    @Order(4)
    @Bean
    public SecurityFilterChain logoutSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/logout/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAccessTokenFilter(rsaKeyRecord, jwtTokenUtils), UsernamePasswordAuthenticationFilter.class)
                .logout( logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandlerService)
                        .logoutSuccessHandler( ((request, response, authentication) -> SecurityContextHolder.clearContext()) )

                )
                .exceptionHandling(ex -> {
                    log.error("[SecurityConfig:logoutSecurityFilterChain] Exception due to :{}",ex);
                    ex.authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint());
                    ex.accessDeniedHandler(new BearerTokenAccessDeniedHandler());
                })
                .build();
    }

    // Môi config nay sẽ gán vói một method trong controller
    @Order(5)
    public SecurityFilterChain registerSecurityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher("/sign-in/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }

    // CONFIG Security authentication
    @Order(6)
    @Bean
    public SecurityFilterChain h2ConsoleSecurityFilterChainConfig(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .securityMatcher(new AntPathRequestMatcher(("/h2-console/**"))) // bo loc nay chi ap dung cac yeu cau HTTP den cac duong dan bat dau bang "/h2-console/"
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // ap dung cho all yeu cau HTTP ko yeu cau xac thuc.
                .csrf(csrf -> csrf.ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/*"))) // vo hieu hoa tan cong CSRF cho cac yeu cau den "/h2-console"
                .headers( headers -> headers.frameOptions(Customizer.withDefaults()).disable()) //vo hieu hoa tieu de X-Frame-Options  -> h2 console hoat dong trong 1 iframe mot so Browser se chan cac yeu cau iframe neu tieu de X-Frame-Options duoc dat.
                .build();
    }

@Bean
PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
}



    // su dung khoa cong khai tu RSA tu rsaKeyRecord
@Bean
JwtDecoder jwtDecoder(){ // giai ma va xac minh JWT
    return NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();
    // tao mot JwtDecoder -> su dung khoa cong Khai RSA. NumbusJwtDecoder la 1 class cu the
    //cua JwtDecoder duoc cung cap boi thu vien Nimbus.
}

@Bean
    JwtEncoder jwtEncoder(){ // su dung khoa cong khai va khoa rieng tu RSA tu rsaKeyRecord
                             // jwtEncoder -> dung ma hoa va ky cac JWT
    JWK jwk = new RSAKey.Builder(rsaKeyRecord.rsaPublicKey()).privateKey(rsaKeyRecord.rsaPrivateKey()).build();
    // tao ra mot RSAKey su dung khoa cong khai va khoa rieng tu RSA cung cap.

    JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
    // tao mot JWKSet (JSON WEB KEY Set) tu RSAKey da tao. Sau do dong goi vao ImmutableJWKSet
    // => no giup tao ra mot JwtEncoder su dung JWKSet da tao

    return new NimbusJwtEncoder(jwkSource);
    // tao mot JWTEncoder su dung JWKSet
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