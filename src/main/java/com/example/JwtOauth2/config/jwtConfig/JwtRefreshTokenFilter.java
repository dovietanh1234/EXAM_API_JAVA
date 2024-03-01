package com.example.JwtOauth2.config.jwtConfig;

import com.example.JwtOauth2.config.RSAKeyRecord;
import com.example.JwtOauth2.repository.IRefreshToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtRefreshTokenFilter extends OncePerRequestFilter {

// CLASS NAY se duoc dua vao file SecurityConfig -> o Order(3)

private final RSAKeyRecord rsaKeyRecord;
private final JwtTokenUtils jwtTokenUtils;
private final IRefreshToken iRefreshToken;

/*
*  việc tạo lớp JwtRefreshTokenFilter kế thừa từ OncePerRequestFilter và ghi đè
* phương thức doFilterInternal giúp bạn tạo ra một bộ lọc HTTP tùy chỉnh để xử lý
* JWT làm mới trong ứng dụng Spring Security
*
* viec tao ra mot filter HTTP tuy chinh -> cho phep ta thao tac kiem tra bo sung tren cac yeu cau HTTP
* truoc khi chung duoc xu ly boi cac phan khac cua ung dung.
* -> Xac thuc uy quyen
* -> Kiem soat truy cap
* -> ghi log
* -> chong tan cong CSRF
 * */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("[JwtRefreshTokenFilter:doFilterInternal] :: Started ");
            log.info("[JwtRefreshTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build();

            if( !authHeader.startsWith("Bearer ") ){
                filterChain.doFilter(request, response);
                return;
            }


            final String token = authHeader.substring(7);
            final Jwt jwtRefreshToken = jwtDecoder.decode(token);

            final String username = jwtTokenUtils.getUserName(jwtRefreshToken);

            if( !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
                // check if refreshToken isPresent in db and is valid
                var isRefreshTokenValidInDatabase = iRefreshToken.findByRefreshToken(jwtRefreshToken.getTokenValue())
                        .map(refreshTokenEntity -> !refreshTokenEntity.isRevoked())
                        .orElse(false);
                // check token in DB

                UserDetails userDetails = jwtTokenUtils.userDetails(username);
                // gan data vao UserDetails cua spring security

                if( jwtTokenUtils.isTokenValid(jwtRefreshToken, userDetails) && isRefreshTokenValidInDatabase ){
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                    // cau lenh tao ra mot SecurityContext( mot interface trong spring security ) chứa thong
                    // tin nguoi dung da xac thuc.

                    UsernamePasswordAuthenticationToken createToken  = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    // UsernamePasswordAuthenticationToken la mot class cua Authentication duoc su dung de bieu dien
                    //và thể hiện thong tin xac thuc cho viec dang nhap bang ten nguoi dung

                    createToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                    // --> một lệnh thiết lập chi tiết xác thực từ yêu cầu HTTP vào token xác thực.

                    securityContext.setAuthentication(createToken); // thiết lập token xác thực SecurityContext.
                    SecurityContextHolder.setContext(securityContext); // thiết lập SecurityContext vào SecurityContextHolder.
                    //SecurityContextHolder là một lớp trợ giúp trong Spring Security, cung cấp quyền truy cập vào SecurityContext.

                    //=> Điêu nay cho phép Spring security truy cap vào thông tin xác thực của người dùng trong Xuốt quá
                    // trình xử lý yêu cầu HTTP.
                }


            }

            log.info("[JwtRefreshTokenFilter:doFilterInternal] Completed");
            filterChain.doFilter(request, response);

        }catch (JwtValidationException jwtValidationException){
            log.error("[JwtRefreshTokenFilter:doFilterInternal] Exception due to :{}",jwtValidationException.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE,jwtValidationException.getMessage());
        }
    }
}
