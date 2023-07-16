package com.pisces.framework.web.token;

import com.pisces.framework.core.entity.AccountData;
import com.pisces.framework.core.utils.AppUtils;
import com.pisces.framework.core.utils.lang.ObjectUtils;
import com.pisces.framework.web.config.WebProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.Map;

/**
 * jwt标记辅助
 *
 * @author jason
 * @date 2022/12/08
 */
@Getter
public class JwtTokenHelper {
    private static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS256;
    private static final String CLAIM_KEY_ACCOUNT_DATA = "accountData";
    private static final String CLAIM_KEY_CREATED = "created";

    private String subject;
    private AccountData accountData;
    private Boolean expiration;

    private static final class AudienceHolder {
        private static final WebProperties AUDIENCE = AppUtils.getBean(WebProperties.class);
    }

    private static WebProperties getAudience() {
        return AudienceHolder.AUDIENCE;
    }

    private static Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + (long) (getAudience().getExpiresSecond()) * 1000);
    }

    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.decodeBase64(getAudience().getBase64Secret());//自定义
        return new SecretKeySpec(encodedKey, SIGNATURE_ALGORITHM.getJcaName());
    }

    public static String generateToken(AccountData accountData) {
        Claims claims = Jwts.claims().setSubject(accountData.getAccount());
        claims.put(CLAIM_KEY_ACCOUNT_DATA, accountData);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    private static String generateToken(Claims claims) {
        return Jwts.builder().setHeaderParam("typ", "JWT")
                .setClaims(claims)
                .setIssuer(getAudience().getClientId())
                .setAudience(getAudience().getAudience())
                .setExpiration(generateExpirationDate())
                .signWith(generalKey(), SIGNATURE_ALGORITHM)
                .compact();
    }

    public static String refreshToken(String token) {
        final Claims claims = getClaimsFromToken(token);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    private static Claims getClaimsFromToken(String token) {
        SecretKey key = generalKey();
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public static JwtTokenHelper parseToken(String token) throws Exception {
        final Claims claims = getClaimsFromToken(token);

        JwtTokenHelper helper = new JwtTokenHelper();
        helper.subject = claims.getSubject();
        Map<String, Object> data = claims.get(CLAIM_KEY_ACCOUNT_DATA, Map.class);
        if (data != null) {
            helper.accountData = ObjectUtils.mapToBean(data, AccountData.class);
        }
        helper.expiration = claims.getExpiration().before(new Date());
        return helper;
    }
}

