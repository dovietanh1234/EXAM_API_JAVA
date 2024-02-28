package com.example.JwtOauth2.config.jwtConfig;

import com.example.JwtOauth2.DTO.TokenType;
import com.example.JwtOauth2.config.RSAKeyRecord;
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
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAccessTokenFilter extends OncePerRequestFilter {

    private final RSAKeyRecord rsaKeyRecord;
    private final JwtTokenUtils jwtTokenUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // filterchain -> instance's servlet container -> dung de goi filter next trong chuoi or tai nguyen o cuoi chuoi
        try{
            log.info("[JwtAccessTokenFilter:doFilterInternal] :: Started ");
            log.info("[JwtAccessTokenFilter:doFilterInternal]Filtering the Http Request:{}", request.getRequestURI());

            //lay ra header chua token
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // dua ma public key vao doi tuong NimbusJwtDecoder -> jwtDecoder
            JwtDecoder jwtDecoder = NimbusJwtDecoder.withPublicKey(rsaKeyRecord.rsaPublicKey()).build(); // Builder Pattern
// NimbusJwtDecoder -> giup giai ma va xac minh chu ky so cua JSON WEB TOKEN Nó su dung cau hinh Nimbus de giai ma
            if( !authHeader.startsWith(TokenType.Bearer.name()) ){
                filterChain.doFilter(request, response);
                return;
            }

            final String token = authHeader.substring(7);
            final Jwt jwtToken = jwtDecoder.decode(token);



            // lay ra ten trong token sau khi da duoc Decode cua doi tuong jwtDecoder
            final String username =  jwtTokenUtils.getUserName(jwtToken);

            if( !username.isEmpty() && SecurityContextHolder.getContext().getAuthentication() == null){
// SecurityContextHolder -> class support spring security cung cap quyen va ngu canh bao mat
// no se save ngu canh bao mat ( no luon san sang cho cac methods trong cung mot luong thuc thi ).
                UserDetails userDetails = jwtTokenUtils.userDetails(username);

                // check xem du lieu trong token  == data trong Db hay ko?
                if( jwtTokenUtils.isTokenValid( jwtToken, userDetails ) ){
                    // kiem tra xem 2 token nay co khac nhau hay ko?

                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken createToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    createToken.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
                    securityContext.setAuthentication( createToken );
                    SecurityContextHolder.setContext(securityContext);


                }

            }

            log.info( "[JwtAccessTokenFilter:doFilterInternal] Completed" );
            filterChain.doFilter(request, response);
            //Phương thức doFilter(request, response) được gọi để chuyển yêu cầu và phản hồi đến bộ lọc tiếp theo trong chuỗi


        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, e.getMessage());
        }
    }

    //






}
