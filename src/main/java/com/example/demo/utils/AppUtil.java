/**
 * Created By shoh@n
 * Date: 8/18/2022
 */

package com.example.demo.utils;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

public class AppUtil {

    public static Date getCalculatedExpirationTime(int expirationTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expirationTime);
        return new Date(calendar.getTime().getTime());
    }

    public static String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder
                .fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

    public static String getApiBaseUrl(HttpServletRequest request) {
        return getBaseUrl(request) + "/api/v1";
    }
}
