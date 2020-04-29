package io.curity.identityserver.plugin.authenticationaction.date_time

import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DateDenyAuthenticationActionTest extends Specification {

    static private Instant loginTime = Instant.now().truncatedTo(ChronoUnit.MINUTES);

    @Unroll
    def "Deny authentication #when (#configTime) #timeZone" () {
        given: "Configuration and action"
        DateDenyAuthenticationActionConfiguration config = dc(configTime, timeZone, when)
        DateDenyAuthenticationAction action = new DateDenyAuthenticationAction(config);

        when: "Authentication is checked against action configuration"
        AuthenticationActionResult result = action.apply(null, null, null, null);


        then: "Allow or deny authentication as expected"
            ZoneId configZone = ZoneIdUtil.getZoneId(timeZone);
            ZonedDateTime zonedConfigTime = ZonedDateTime.ofInstant(configTime, configZone);
            ZonedDateTime zonedLoginTime = ZonedDateTime.ofInstant(loginTime, ZoneId.systemDefault());

            String loginTimeUtc = zonedLoginTime.format(DateTimeFormatter.ISO_INSTANT);
            String configTimeUtc = zonedConfigTime.format(DateTimeFormatter.ISO_INSTANT);
            String configTimeOffset = zonedConfigTime.format(DateTimeFormatter.ISO_DATE_TIME);

            assert success == (result instanceof AuthenticationActionResult.SuccessAuthenticationActionResult) : "Now: $loginTimeUtc, Deny $when $configTimeUtc ($configTimeOffset)"

        where:
            configTime                           | timeZone                       | when                                                  | success
            loginTime.minus(1, ChronoUnit.HOURS) | TimeZoneDisplay.SYSTEM_TIME    | DateDenyAuthenticationActionConfiguration.When.AFTER  | false //no access after time stamp in past
            loginTime                            | TimeZoneDisplay.SYSTEM_TIME    | DateDenyAuthenticationActionConfiguration.When.AFTER  | false //no access from now
            loginTime.plus(1, ChronoUnit.HOURS)  | TimeZoneDisplay.SYSTEM_TIME    | DateDenyAuthenticationActionConfiguration.When.AFTER  | true  //no access after time stamp in future
            loginTime.minus(1, ChronoUnit.DAYS)  | TimeZoneDisplay.SYSTEM_TIME    | DateDenyAuthenticationActionConfiguration.When.BEFORE | true //no access until time stamp in past
            loginTime                            | TimeZoneDisplay.SYSTEM_TIME    | DateDenyAuthenticationActionConfiguration.When.BEFORE | true //no access until now
            loginTime.plus(2, ChronoUnit.DAYS)   | TimeZoneDisplay.US_Pacific_New | DateDenyAuthenticationActionConfiguration.When.BEFORE | false //no access until time stamp in future
            loginTime.plus(1, ChronoUnit.HOURS)  | TimeZoneDisplay.US_Pacific_New | DateDenyAuthenticationActionConfiguration.When.AFTER  | true //no access after time stamp in future (config in different time zone)
            loginTime.minus(1, ChronoUnit.HOURS) | TimeZoneDisplay.US_Pacific_New | DateDenyAuthenticationActionConfiguration.When.BEFORE | true //no access until time stamp in past (config in different time zone)
    }

    DateDenyAuthenticationActionConfiguration dc(Instant timeStamp, TimeZoneDisplay configTimeZone, DateDenyAuthenticationActionConfiguration.When when) {
        ZoneId zoneId;
        if (configTimeZone == TimeZoneDisplay.SYSTEM_TIME) {
            zoneId = ZoneId.systemDefault();
        } else {
            zoneId = ZoneId.of(configTimeZone.getTimeZone());
        }

        ZonedDateTime confTime = ZonedDateTime.ofInstant(timeStamp, zoneId);

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
