package io.curity.identityserver.plugin.authenticationaction.date_time


import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

class DateDenyAuthenticationActionTest extends Specification {

    @Unroll
    def "Test if authentication is allowed before or after a configured time stamp." () {
        given:
        DateDenyAuthenticationActionConfiguration config = dc(configTime, timeZone, when)
        DateDenyAuthenticationAction action = new DateDenyAuthenticationAction(config);

        when:
        AuthenticationActionResult result = action.apply(null, null, null, null);

        ZoneId configZone = ZoneIdUtil.getZoneId(timeZone);
        ZonedDateTime utcConfigTime = ZonedDateTime.of(configTime, configZone).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime utcNow = ZonedDateTime.of(loginTime, ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC"));

        then:
        if (success) {
            assert result instanceof AuthenticationActionResult.SuccessAuthenticationActionResult : "Now: $utcNow, Deny $when $utcConfigTime ($configTime)"
        } else {
            assert result instanceof AuthenticationActionResult.FailedAuthenticationActionResult : "Now: $utcNow, Deny $when $utcConfigTime ($configTime)"
        }

        where:
        loginTime           | configTime                           | timeZone                     | when                                                 | success
        LocalDateTime.now() | loginTime.minusHours(1) | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.AFTER | false //no access after time stamp in past
        LocalDateTime.now() | loginTime               | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.AFTER | false //no access from now
        LocalDateTime.now() | loginTime.plusHours(1)   | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.AFTER | true  //no access after time stamp in future
        LocalDateTime.now() | loginTime.minusHours(1).minusDays(1) | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.BEFORE| true //no access until time stamp in past
        LocalDateTime.now() | loginTime                            | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.BEFORE| true //no access until now
        LocalDateTime.now() | loginTime.plusHours(1).plusDays(2)   | TimeZoneDisplay.SYSTEM_TIME  | DateDenyAuthenticationActionConfiguration.When.BEFORE| false //no access until time stamp in future
        LocalDateTime.now() | loginTime.plusHours(1).plusDays(2)   | TimeZoneDisplay.Europe_Stockholm | DateDenyAuthenticationActionConfiguration.When.AFTER | true //no access after time stamp in future (config in different time zone)
        LocalDateTime.now() | loginTime.minusHours(1).plusDays(2)  | TimeZoneDisplay.Europe_Stockholm | DateDenyAuthenticationActionConfiguration.When.BEFORE | true //no access until time stamp in past (config in different time zone)
    }

    DateDenyAuthenticationActionConfiguration dc(LocalDateTime localTime, TimeZoneDisplay configTimeZone, DateDenyAuthenticationActionConfiguration.When when) {
        ZoneId zoneId;
        if (configTimeZone == TimeZoneDisplay.SYSTEM_TIME) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = ZoneId.of(configTimeZone.getTimeZone());
        }
        ZonedDateTime confTime = ZonedDateTime.of(localTime, zoneId);

        return new DateDenyAuthenticationActionConfiguration() {
            @Override
            TimeZoneDisplay getTimeZone() {
                return configTimeZone;
            }

            @Override
            int getYear() {
                confTime.getYear();
            }

            @Override
            int getMonth() {
                confTime.getMonthValue();
            }

            @Override
            int getDay() {
                confTime.getDayOfMonth();
            }

            @Override
            int getHour() {
                confTime.getHour();
            }

            @Override
            int getMinute() {
                return confTime.getMinute();
            }

            @Override
            DateDenyAuthenticationActionConfiguration.When getDenyBeforeOrAfter() {
                return when
            }

            @Override
            String id() {
                return null;
            }
        }
    }
}
