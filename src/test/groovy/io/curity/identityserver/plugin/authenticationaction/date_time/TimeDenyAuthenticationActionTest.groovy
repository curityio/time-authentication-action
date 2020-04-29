package io.curity.identityserver.plugin.authenticationaction.date_time


import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult
import spock.lang.Specification
import spock.lang.Unroll

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class TimeDenyAuthenticationActionTest extends Specification {

    static private Instant loginTime = Instant.now().truncatedTo(ChronoUnit.MINUTES);

    @Unroll
    def "#message" () {
        given: "Action configuration"
            TimeDenyAuthenticationActionConfiguration config = tdc(notBefore, notAfter, timeZone);
            TimeDenyAuthenticationAction actionToTest = new TimeDenyAuthenticationAction(config);

        when:
        AuthenticationActionResult result = actionToTest.apply(null, null, null, null);

        then:
            DateTimeFormatter simpleTime = DateTimeFormatter.ofPattern("HH:mm");

            String noAccessBeforeSystemTime = notBefore.atZone(ZoneId.systemDefault()).format(simpleTime);
            String noAccessAfterSystemTime = notAfter.atZone(ZoneId.systemDefault()).format(simpleTime);
            String noAccessBeforeConfigTime = notBefore.atZone(ZoneIdUtil.getZoneId(timeZone)).format(simpleTime);
            String noAccessAfterConfigTime = notAfter.atZone(ZoneIdUtil.getZoneId(timeZone)).format(simpleTime);
            String loginSystemTime = loginTime.atZone(ZoneId.systemDefault()).format(simpleTime);

            assert success == (result instanceof AuthenticationActionResult.SuccessAuthenticationActionResult): "Login at $loginSystemTime ($noAccessBeforeSystemTime - $noAccessAfterSystemTime) configured as [$noAccessBeforeConfigTime - $noAccessAfterConfigTime in zone $timeZone]"

        where:
            notBefore                              | notAfter                               | timeZone                       | success | message
            loginTime.minus(1, ChronoUnit.HOURS)   | loginTime.plus(1, ChronoUnit.HOURS)    | TimeZoneDisplay.SYSTEM_TIME    | true    | "Authentication between notBefore and notAfter"
            loginTime.minus(1, ChronoUnit.HOURS)   | loginTime.plus(1, ChronoUnit.HOURS)    | TimeZoneDisplay.US_Pacific_New | true    | "Authentication between notBefore and notAfter, configuration in different time zone"
            loginTime.plus(1, ChronoUnit.MINUTES)  | loginTime.plus(2, ChronoUnit.MINUTES)  | TimeZoneDisplay.SYSTEM_TIME    | false   | "Authentication before notBefore"
            loginTime.plus(1, ChronoUnit.MINUTES)  | loginTime.plus(2, ChronoUnit.MINUTES)  | TimeZoneDisplay.US_Pacific_New | false   | "Authentication before notBefore, configuration in different time zone"
            loginTime.minus(2, ChronoUnit.MINUTES) | loginTime.minus(1, ChronoUnit.MINUTES) | TimeZoneDisplay.SYSTEM_TIME    | false   | "Authentication after notAfter"
            loginTime.minus(2, ChronoUnit.MINUTES) | loginTime.minus(1, ChronoUnit.MINUTES) | TimeZoneDisplay.US_Pacific_New | false   | "Authentication after notAfter, configuration in different time zone"
    }

    private TimeDenyAuthenticationActionConfiguration tdc(Instant notBefore, Instant notAfter, TimeZoneDisplay timezone) {

        return new TimeDenyAuthenticationActionConfiguration() {
            ZonedDateTime noAccessBeforeTime = ZonedDateTime.ofInstant(notBefore, ZoneIdUtil.getZoneId(timezone));
            ZonedDateTime noAccessAfterTime = ZonedDateTime.ofInstant(notAfter, ZoneIdUtil.getZoneId(timezone));

            @Override
            TimeZoneDisplay getTimeZone() {
                return timezone;
            }

            @Override
            TimeConfiguration getNoAccessBefore() {
                return new TimeConfiguration() {
                    @Override
                    int getHour() {
                        return noAccessBeforeTime.getHour();
                    }

                    @Override
                    int getMinutes() {
                        return noAccessBeforeTime.getMinute();
                    }

                    @Override
                    String toString() {
                        return String.format("%2d:%2d", getHour(), getMinutes());
                    }
                }
            }

            @Override
            TimeConfiguration getNoAccessAfter() {
                return new TimeConfiguration() {
                    @Override
                    int getHour() {
                        return noAccessAfterTime.getHour();
                    }

                    @Override
                    int getMinutes() {
                        return noAccessAfterTime.getMinute();
                    }

                    @Override
                    String toString() {
                        return String.format("%2d:%2d", getHour(), getMinutes());
                    }
                }
            }

            @Override
            String id() {
                return null
            }
        }
    }
}
