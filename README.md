# Time Based Deny Authentication Action

[![Build Status](https://travis-ci.org/curityio/time-authentication-action.svg?branch=master)](https://travis-ci.org/curityio/time-authentication-action)

This repository contains two plugins for time based Actions in the Authentication flow.

## System Requirements

Curity Identity Server 5.0.0 or later and its [system requirements](https://developer.curity.io/docs/latest/system-admin-guide/system-requirements.html).

## Build and deploy the Plugins

Both plugins will be packaged in one single jar.

* Run `mvn package`
* Create a directory in `/usr/share/plugins/`. Ex. `time`
* Copy the generated .jar file in `target/usr/share/plugins/authenticationactions.timedeny/` to the newly created folder `/usr/share/plugins/time`
* Restart the Curity Identity Server

## Configuration

### Deny based on a time span
* Create an Action of type `Time Deny`
* Add the action to the authenticator as a login action.
* Configure the `No Access Before` and `No Access After` parameters to define when access should not be allowed.
* Select a time zone that this action should apply to. The default is `system-time` and will use the System Time of the Curity server.

The `No Access Before` denotes the start time of any particular day in which access should be granted; the `No Access 
After` denotes the end point on the same day. 

### Deny based on a given date and time
* Create an Action of type `Date Time Deny`
* Add the action to the authenticator as a login action.
* Set `Date and Time`.
* Choose if access should be denied before or after the configured date and time.

This action restricts access until a certain point on the timeline or, conversely, allows it until a certain point.

## License

This plugin and its associated documentation is listed under the [Apache 2 license](LICENSE).

## More Information

Please visit [curity.io](https://curity.io/) for more information about the Curity Identity Server.

Copyright (C) 2020 Curity AB.
